/*
	RunRadisJM: Run RadiScript screens
	Copyright (C) 2009-2023  James Hahn

	This file is part of RunRadisJM.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package radis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import radis.context.Context;
import radis.context.ExecContext;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.RadisIdData;
import radis.data.buffer.TextBufData;
import radis.datadef.Period;
import radis.interp.Statement;
import radis.op.OpItem;
import radis.parser.Parser;
import radis.stats.DrawDown;
import radis.stats.GeoStdDev;
import radis.stats.Statistic;
import radis.types.Date;
import radis.types.Num;

/**
 * Screen back-tester. For a list of options, run:
 * <p/>
 * <code>
 * radis.bt -?
 * </code>
 */
public class bt {
	private Context ctx;
	private Parser parser;
	private final ExecContext exec;
	private final Corrections corrections;
	private final TextBufData tickers;
	private final NumBufData prices;
	private final DateBufData priceDates;
	private final NumBufData splitFactors;
	private final DateBufData splitDates;
	private final RadisIdData radisIds;
	private final List<Period> periods;
	private boolean[] retain;
	private int prevYear = 0;

	/**
	 * Constructs the object.
	 *
	 * @throws IOException
	 */
	public bt() throws IOException {
		var dir = System.getenv("MMAP");
		if (dir == null) {
			throw new IOException("missing environment variable MMAP");
		}

		this.ctx = new Context(dir);
		this.parser = new Parser();

		parser.parseAll(Args.files);

		if (Args.screen == null) {
			Args.screen = parser.firstScreen;
		}

		this.exec = new ExecContext(ctx, pickPeriods());
		this.tickers = exec.getData(Util.TKR_VAR_NM).toBuf().toText();
		this.prices = exec.getData("si price").toBuf().toNum();
		this.priceDates = exec.getData("si price date").toBuf().toDate();
		this.splitFactors = exec.getData("si split factor").toBuf().toNum();
		this.splitDates = exec.getData("si split date").toBuf().toDate();
		this.radisIds = exec.getRadisIds();
		this.corrections = new Corrections();
		this.retain = exec.retain;

		periods = exec.getPeriods();
		Statement stmt = new Statement(exec);

		OpItem basicAction = parser.getBasicScreenAction();
		if (basicAction != null) {
			stmt.eval(basicAction);
		}

		stmt.eval(parser.getAction(Args.screen.toLowerCase()));

		if (Args.emax > 0) {
			stmt.evalLimit(Args.emax);
		}
	}

	/**
	 * @param args
	 */
	static public void main(String[] args) {
		Args.Spec[] specs = { Args.beg_spec, Args.end_spec, Args.months_spec, Args.emin_spec, Args.emax_spec,
				Args.screen_spec, Args.bias_spec, Args.individ_ret_spec };

		Args.parse(args, specs, Args.add_file);

		if (Args.files.isEmpty()) {
			System.err.println("missing source file name");
			System.exit(1);
		}

		try {
			var bt = new bt();

			if (Args.indiv) {
				bt.showIndiv();
			} else {
				bt.run();
			}

		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace(); // so we can get stack trace
		}
	}

	/**
	 * @return a list of the periods to be included in the back-test
	 */
	private List<Period> pickPeriods() {
		List<Period> periods = new ArrayList<>(ctx.getPeriods().size());

		int nmonths = 0;
		for (var per : ctx.getPeriods()) {
			String datestr = Date.toText(per.getDate());
			if (datestr.compareTo(Args.dbegin) < 0) {
				continue;
			}

			if (datestr.compareTo(Args.dend) <= 0 && nmonths % Args.months == 0) {
				periods.add(per);
			}

			++nmonths;
		}

		return periods;
	}

	/**
	 * Runs the back-test.
	 */
	public void run() {
		List<Statistic> stats = new ArrayList<>(2);
		stats.add(new DrawDown());
		stats.add(new GeoStdDev());

		System.out.println("PERIOD\t\t   CUR\t   YTD\t  CAGR\tPASSING");

		final float dtstart = periods.get(0).getDate();
		Period pprev = null;
		double yearRet = 1.0;
		double cumRet = 1.0;
		int nprev = 0; // number of tickers passing the screen in the previous period
		for (var per : periods) {
			if (isNewYear(per)) {
				yearRet = 1.0;
				stats.forEach(Statistic::finishYear);
			}

			double perRet = computeReturn(exec, pprev, per);
			yearRet *= perRet;
			cumRet *= perRet;
			double cagr = (pprev == null ? 1.0 : Math.pow(cumRet, 365.25 / (per.getDate() - dtstart)));

			System.out.format("%s\t%6.1f\t%6.1f\t%6.1f\t%d\n", Date.toText(per.getDate()), Util.asPercent(perRet),
					Util.asPercent(yearRet), Util.asPercent(cagr), nprev);

			stats.forEach(stat -> stat.addPer(perRet));

			pprev = per;
			nprev = per.countRecords(retain);
		}

		System.out.println();
		stats.forEach(Statistic::print);
	}

	/**
	 * Computes the return for the tickers passing the screen during the previous
	 * period.
	 *
	 * @param exec
	 * @param pprev previous period
	 * @param per   current period
	 * @return the return for the previous period
	 */
	private double computeReturn(ExecContext exec, Period pprev, Period per) {
		corrections.correctPrices(Date.toText(per.getDate()));

		if (pprev == null) {
			return 1.0;
		}

		Map<Integer, Integer> radis2recnum = mapRadis(per);

		double sum = 0.0;
		int nrecs = 0;
		int endrec = pprev.endRecord();
		for (int oldRecNum = pprev.beginRecord(); oldRecNum < endrec; ++oldRecNum) {
			if (retain[oldRecNum]) {
				++nrecs;

				// get the radis company ID associated with the old period
				int radis = radisIds.get(oldRecNum);

				// get the record, from the new period, containing that ID
				var newRecNum = radis2recnum.get(radis);

				if (newRecNum == null) {
					// ticker doesn't exist in the current period
					sum += Args.bias;

				} else {
					float prevpr = getSplitAdjustedPrice(oldRecNum, pprev.getDate(), newRecNum);
					float curpr = getCorrectedPrice(newRecNum, corrections::getCurPrice);

					float ret = curpr / prevpr;

					if (Num.isInvalid(ret)) {
						// something is wrong with this ticker's price information
						sum += Args.bias;
					} else {
						sum += ret;
					}
				}
			}
		}

		int nneeded = Math.max(0, Args.emin - nrecs);
		sum += nneeded;
		nrecs += nneeded;

		return (nrecs == 0 ? 1.0 : sum / nrecs);
	}

	/**
	 * Runs the back-test, printing the results for individual tickers.
	 */
	public void showIndiv() {
		System.out.println("PERIOD\t\tPASSING\tINDIVIDUAL RETURNS...");

		Period pprev = null;
		int nprev = 0; // number of tickers passing the screen in the previous period
		for (var per : periods) {
			if (pprev != null) {
				System.out.format("%s\t%d", Date.toText(per.getDate()), nprev);
				showReturn(exec, pprev, per);
				System.out.println();
			}

			pprev = per;
			nprev = per.countRecords(retain);
		}
	}

	/**
	 * Prints the returns, for the previous period, for individual tickers.
	 *
	 * @param exec
	 * @param pprev previous period
	 * @param per   current period
	 */
	private void showReturn(ExecContext exec, Period pprev, Period per) {
		corrections.correctPrices(Date.toText(per.getDate()));

		Map<Integer, Integer> radis2recnum = mapRadis(per);

		int nrecs = 0;
		int endrec = pprev.endRecord();
		for (int oldRecNum = pprev.beginRecord(); oldRecNum < endrec; ++oldRecNum) {
			if (retain[oldRecNum]) {
				++nrecs;

				// get the radis company ID associated with the old period
				int radis = radisIds.get(oldRecNum);

				// get the record, from the new period, containing that ID
				var newRecNum = radis2recnum.get(radis);

				if (newRecNum == null) {
					// ticker doesn't exist in the current period
					System.out.format("\t%6.1f", Util.asPercent(Args.bias));

				} else {
					float prevpr = getSplitAdjustedPrice(oldRecNum, pprev.getDate(), newRecNum);
					float curpr = getCorrectedPrice(newRecNum, corrections::getCurPrice);

					float ret = curpr / prevpr;

					if (Num.isInvalid(ret)) {
						// something is wrong with this ticker's price information
						System.out.format("\t%6.1f", Util.asPercent(Args.bias));
					} else {
						System.out.format("\t%6.1f", Util.asPercent(ret));
					}
				}
			}
		}

		for (var x = nrecs; x < Args.emin; ++x) {
			System.out.format("\t%6.1f", Util.asPercent(1.0));
		}
	}

	/**
	 * @param oldRecNum   record number of the ticker within the previous period
	 * @param prevPerDate data associated with the previous period
	 * @param newRecNum   record number of the ticker within the current period
	 * @return the split-adjusted purchase price of a ticker
	 */
	private float getSplitAdjustedPrice(int oldRecNum, float prevPerDate, int newRecNum) {
		float prevpr = getCorrectedPrice(oldRecNum, corrections::getPrevPrice);

		var splitDate = splitDates.get(newRecNum);
		if (Date.isInvalid(splitDate) || splitDate < prevPerDate) {
			// split occurred BEFORE the purchase - no adjustment
			return prevpr;
		}

		return prevpr / splitFactors.get(newRecNum);
	}

	/**
	 * Corrects a price based on data in the "price corrections" file.
	 *
	 * @param recnum         record number of the ticker of interest
	 * @param correctedPrice function that takes a ticker and returns the price from
	 *                       the "price corrections" file
	 * @return the corrected price
	 */
	private float getCorrectedPrice(int recnum, Function<String, Float> correctedPrice) {
		float price = correctedPrice.apply(tickers.get(recnum));
		if (!Num.isInvalid(price)) {
			// have a corrected price - just return it
			return price;
		}

		// check date from the "price dates" mmap file
		var date = priceDates.get(recnum);
		if (Date.isInvalid(date) || date == 0.0f) {
			return Num.INVALID_VALUE;
		}

		// just get the price from the "prices" mmap file
		return prices.get(recnum);
	}

	/**
	 * Gets a map of the radis company ID, found in the given period, to the record
	 * number (within the mmap files) with which it's associated.
	 *
	 * @param per period of interest
	 * @return a map of the radis company ID to the mmap record number
	 */
	private Map<Integer, Integer> mapRadis(Period per) {
		Map<Integer, Integer> radis2recnum = new HashMap<>();

		int endrec = per.endRecord();
		for (int recnum = per.beginRecord(); recnum < endrec; ++recnum) {
			radis2recnum.put(radisIds.get(recnum), recnum);
		}

		return radis2recnum;
	}

	/**
	 * @param per period of interest
	 * @return {@code true} if the given period is not in the same year as
	 *         {@link #prevYear}
	 */
	private boolean isNewYear(Period per) {

		/*
		 * Date.toStr() generates a string of the form YYYYMMDD.
		 */
		int date = Integer.parseInt(Date.toText(per.getDate()));

		int year = date / 10000;
		if (year == prevYear) {
			return false;
		}

		int monthDay = date % 10000;
		if (monthDay >= 107) {
			// past Jan 6
			prevYear = year;
			return true;
		}

		return false;
	}
}
