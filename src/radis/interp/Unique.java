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

import java.util.HashMap;
import java.util.Map;

import radis.context.ExecContext;
import radis.data.buffer.Boxed;
import radis.datadef.Period;
import radis.exception.SyntaxException;
import radis.op.OpItem;
import radis.parser.tok;

/**
 * Evaluator for "unique" statement.
 *
 * @param <T> result type
 */
public class Unique<T> extends Rank {

	/**
	 * {@code True} if to pick the last value, {@code false} to pick the first.
	 */
	private final boolean pickLast;

	private final Boxed<T> buf;

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 * @param p
	 * @param buf
	 */
	private Unique(ExecContext exec, OpItem p, Boxed<T> buf) {
		super(exec);
		this.pickLast = !p.arg(1).boolval();
		this.buf = buf;
	}

	/**
	 * Evaluates a "unique(<variable-name>, <picklast-flag>)" statement.
	 *
	 * @param exec
	 * @param p    "unique" statement
	 */
	public static void eval(ExecContext exec, OpItem p) {
		// get the data for the specified variable
		var varnm = p.arg(0).strval();
		var data = exec.getData(varnm);
		if (data.isConst()) {
			throw new SyntaxException("cannot 'unique' a constant: " + varnm);
		}

		switch (data.getType()) {
		case tok.NUM:
			new Unique<Float>(exec, p, data.toBuf().toNum()).eval();

		case tok.DATE:
			new Unique<Float>(exec, p, data.toBuf().toDate()).eval();

		case tok.BOOL:
			new Unique<Byte>(exec, p, data.toBuf().toBool()).eval();

		case tok.STR:
			new Unique<String>(exec, p, data.toBuf().toText()).eval();
		}
	}

	/**
	 * Evaluates this statement.
	 */
	private void eval() {
		exec.applyPeriods(this::selectUnique);
		exec.applyPeriods(this::updateRank);
	}

	/**
	 * Selects rows containing unique variable values for the given period. Sets
	 * {@code retain[recnum]} to {@code false} for every row to be discarded.
	 *
	 * @param period
	 */
	private void selectUnique(Period period) {
		Map<T, Integer> value2recnum = new HashMap<>();
		int endrec = period.endRecord();

		for (int index = period.beginRecord(); index < endrec; ++index) {
			// recOrder identifies the order in which to walk the rows
			int recnum = recOrder[index];

			if (!retain[recnum]) {
				continue;
			}

			T value = buf.getBoxed(recnum);

			value2recnum.compute(value, (key, oldrec) -> {
				if (pickLast) {
					if (oldrec != null) {
						// discard the previous record
						retain[oldrec] = false;
					}

				} else {
					if (oldrec != null) {
						// discard the new record, keep the old
						retain[recnum] = false;
						return oldrec;
					}
				}

				return recnum;
			});
		}
	}

	@Override
	boolean equals(int p, int q) {
		return buf.getBoxed(p).equals(buf.getBoxed(q));
	}
}
