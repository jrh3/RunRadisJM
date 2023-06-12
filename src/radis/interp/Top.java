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
import radis.op.OpItem;

/**
 * Evaluator for "top" statements.
 */
public class Top extends ExecContainer {

	/**
	 * Maximum number of rows to retain.
	 */
	private final double limit;

	/**
	 * {@code True} if "limit" is a percentage
	 */
	private final boolean isPercent;

	/**
	 * "Tied rank" data for each row, or {@code null} if not keeping ties.
	 */
	private final NumBufData tiedRank;

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 * @param p
	 */
	public Top(ExecContext exec, OpItem p) {
		super(exec);
		this.limit = p.arg(0).floatval();
		this.isPercent = p.arg(1).boolval();

		boolean includeTies = p.arg(2).boolval();

		if (includeTies) {
			tiedRank = exec.getData(Util.TIED_RANK_VAR_NM).toBuf().toNum();
		} else {
			tiedRank = null;
		}
	}

	/**
	 * Evaluates the statement.
	 */
	public void eval() {
		if (tiedRank != null) {
			tiedRank.rewind();
		}

		exec.applyPeriods(this::pickTop);
	}

	/**
	 * Picks the top rows for the given period.
	 *
	 * @param period
	 */
	private void pickTop(Period period) {
		int nkeep;
		if (isPercent) {
			nkeep = (int) (period.countRecords(retain) * limit + 0.5);
		} else {
			nkeep = (int) (limit + 0.5);
		}

		int origKeep = nkeep;
		int lastKeptRec = 0;
		int ordnum;

		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		for (ordnum = begrec; ordnum < endrec; ++ordnum) {
			if (nkeep <= 0) {
				// have all the records we want
				break;
			}

			// recOrder identifies the order in which to walk the rows
			int recnum = recOrder[ordnum];

			if (!retain[recnum]) {
				continue;
			}

			lastKeptRec = recnum;
			--nkeep;
		}

		if (tiedRank == null || origKeep == nkeep) {
			// no tied rank, or not keeping any records - discard the remainder
			discard(ordnum, endrec);
			return;
		}

		// keep the remaining records that have the same tied rank as the last one

		// get the "tied rank" from the last record that we've kept (so far)
		float lastTiedRank = tiedRank.get(lastKeptRec);

		while (ordnum < endrec) {
			int recnum = recOrder[ordnum++];

			if (!retain[recnum]) {
				continue;
			}

			if (tiedRank.get(recnum) > lastTiedRank) {
				// tied rank is different - put it back and stop looping
				--ordnum;
				break;
			}
		}

		discard(ordnum, endrec);
	}

	/**
	 * Discards records, setting {@code retain[recnum]} to {@code false}.
	 *
	 * @param begrec number of first record to discard
	 * @param endrec last record (plus one) to discard
	 */
	private void discard(int begrec, int endrec) {
		while (begrec < endrec) {
			int recnum = recOrder[begrec++];
			retain[recnum] = false;
		}
	}

}
