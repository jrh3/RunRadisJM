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
 * Computes the maximum draw down.
 */
public class DrawDown implements Statistic {
	double maxret = 1.0;
	double cumret = 1.0;
	double dd = 1.0;

	/**
	 * Adds returns from a new period.
	 *
	 * @param ret return for the period
	 */
	@Override
	public void addPer(double ret) {
		cumret *= ret;

		if (cumret > maxret) {
			maxret = cumret;
		}

		dd = Math.min(dd, cumret / maxret);
	}

	/**
	 * Completes a year, wrapping up any statistic that is dependent on year-end.
	 */
	@Override
	public void finishYear() {
	}

	/**
	 * Prints a statistic.
	 */
	@Override
	public void print() {
		System.out.format("MAX DD= %.1f", Util.asPercent(dd));
		System.out.println();
	}
}
