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

package radis.stats;

import radis.Util;

/**
 * Computes the GSD
 */
public class GeoStdDev implements Statistic {
	double ytd = 1;				// YTD return
	boolean processed = true;	// whether or not ytd is included in sums
	double sum_ln_ret = 0;		// sum of ln(year's return)
	double sum_ln_ret_sq = 0;	// sum of square of ln(year's return)
	int count = 0;				// number of items in the sum

	/**
	 * Adds returns from a new period.
	 *
	 * @param ret return for the period
	 */
	@Override
	public void addPer(double ret) {
		ytd *= ret;
		processed = false;
	}

	/**
	 * Completes a year, wrapping up any statistic that is dependent on year-end.
	 */
	@Override
	public void finishYear() {
		if (!processed) {
			processed = true;
			double lnr = Math.log(ytd);
			sum_ln_ret += lnr;
			sum_ln_ret_sq += lnr * lnr;
			++count;
			ytd = 1;
		}
	}

	/**
	 * Prints a statistic.
	 */
	public void print() {
		finishYear();

		if (count <= 1) {
			return;
		}

		double alnr = sum_ln_ret / count;
		double alnrsq = sum_ln_ret_sq / count;
		double r = count / (count - 1.0);
		double sigma = Math.sqrt((alnrsq - alnr * alnr) * r);
		double gsd = Math.exp(sigma);

		System.out.format("GSD= %.1f", Util.asPercent(gsd));
		System.out.println();
	}

}
