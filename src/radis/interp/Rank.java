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

package radis.interp;

import radis.Util;
import radis.context.ExecContainer;
import radis.context.ExecContext;
import radis.data.buffer.NumBufData;
import radis.datadef.Period;

/**
 * Evaluator used to re-rank the equities.
 */
public abstract class Rank extends ExecContainer {

	/**
	 * Data for the "rank" variable.
	 */
	private NumBufData rank;

	/**
	 * Data for the "tied rank" variable.
	 */
	private NumBufData tiedRank;

	/**
	 * Data for the "tied rank %" variable.
	 */
	private NumBufData tiedRankPct;

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 */
	public Rank(ExecContext exec) {
		super(exec);
		this.rank = new NumBufData(exec);
		this.tiedRank = new NumBufData(exec);
		this.tiedRankPct = new NumBufData(exec);
	}

	/**
	 * Updates the rank for each equity in the given period.
	 *
	 * @param period
	 */
	protected void updateRank(Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();
		int r = 1; // rank of this equity
		int t = 1; // tied rank of this equity, if it matches the previous
		boolean havePrev = false;
		int prevRec = 0;

		for (int ordnum = begrec; ordnum < endrec; ++ordnum) {
			int recnum = recOrder[ordnum];

			if (!retain[recnum]) {
				continue;
			}

			rank.put(recnum, r);

			if (havePrev && !equals(prevRec, recnum)) {
				// they're different - reset the tied rank to the rank
				t = r;
			}

			tiedRank.put(recnum, t);

			havePrev = true;
			++r;
			prevRec = recnum;
		}

		// now go back through and rank everything as a percent of its rank
		float maxTiedRank = t;
		for (int ordnum = begrec; ordnum < endrec; ++ordnum) {
			int recnum = recOrder[ordnum];

			if (!retain[recnum]) {
				continue;
			}

			float v = tiedRank.get(recnum);
			tiedRankPct.put(recnum, (v * 100) / maxTiedRank);
		}

		// store the data in the variables
		exec.addVar(Util.RANK_VAR_NM, rank);
		exec.addVar(Util.TIED_RANK_VAR_NM, tiedRank);
		exec.addVar(Util.PERCENT_TIED_RANK_VAR_NM, tiedRankPct);
	}

	/**
	 * Compares two equities for equality.
	 *
	 * @param p record number of the first equity
	 * @param q record number of the second equity
	 * @return {@code true}, if the two equities are equal
	 */
	abstract boolean equals(int p, int q);
}
