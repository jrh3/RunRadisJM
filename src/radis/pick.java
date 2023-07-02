/*
	RunRadisJM: Run RadiScript screens
	Copyright (C) 2023  James Hahn

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
import java.util.Map;

import radis.context.LoaderContext;
import radis.data.buffer.TextBufData;
import radis.datadef.Period;
import radis.dbf.SiProInstallation;
import radis.direct.DirectExecContext;
import radis.direct.DirectLoaderContext;
import radis.interp.Statement;
import radis.op.OpItem;
import radis.parser.Parser;

/**
 * Displays tickers, from the current SI Pro installation, that pass the screen.
 * For a list of options, run:
 * <p/>
 * <code>
 * radis.pick -?
 * </code>
 */
public class pick {
	private final DirectLoaderContext ctx;
	private final Parser parser;
	private final DirectExecContext exec;
	private final TextBufData tickers;
	private final Period period;
	private boolean[] retain;

	/**
	 * Constructs the object.
	 *
	 * @throws IOException
	 */
	public pick() throws IOException {
		this.ctx = new DirectLoaderContext();
		this.parser = new Parser();

		parser.parseAll(Args.files);

		if (Args.screen == null) {
			Args.screen = parser.firstScreen;
		}

		var siprodir = System.getenv("SI_PRO");
		if (siprodir == null) {
			throw new IOException("missing environment variable SI_PRO");
		}

		var sipro = new SiProInstallation(siprodir) {
			@Override
			protected void saveRadisIds(LoaderContext ctx, Map<String, Integer> sipro2radis) throws IOException {
				// don't save anything
			}
		};

		// extract the date of the data in the SI Pro installation
		var newdt = sipro.getDate();

		sipro.loadData(ctx, newdt);

		this.exec = new DirectExecContext(ctx);
		this.tickers = exec.getData(Util.TKR_VAR_NM).toBuf().toText();
		this.retain = exec.retain;
		this.period = exec.getPeriods().get(0);

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
		Args.Spec[] specs = { Args.emin_spec, Args.emax_spec, Args.screen_spec };

		Args.parse(args, specs, Args.add_file);

		if (Args.files.isEmpty()) {
			System.err.println("missing source file name");
			System.exit(1);
		}

		try {
			Util.printLoaderInfo = false;
			new pick().display();

		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace(); // so we can get stack trace
		}
	}

	/**
	 * Displays the tickers passing the screen, in the last sort order.
	 */
	public void display() {
		int[] recOrder = exec.recOrder;
		int nrecs = 0;
		int endrec = period.endRecord();
		for (int x = period.beginRecord(); x < endrec; ++x) {
			int recnum = recOrder[x];

			if (retain[recnum]) {
				++nrecs;
				System.out.println(tickers.get(recnum));
			}
		}

		if (nrecs < Args.emin) {
			System.err.println("*** not enough companies passed the filter");
		}
	}
}
