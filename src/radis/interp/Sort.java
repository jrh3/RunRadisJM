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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import radis.context.ExecContext;
import radis.data.buffer.BoolBufData;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.TextBufData;
import radis.datadef.Period;
import radis.exception.InternalException;
import radis.op.OpItem;
import radis.parser.tok;

/**
 * Evaluator for a "sort" statement.
 */
public class Sort extends Rank {

	/**
	 * List of functions, one per sort-order, that takes two record numbers and
	 * compares the data found in the respective records.
	 */
	private final List<BiFunction<Integer, Integer, Integer>> ordering = new ArrayList<>();

	public Sort(ExecContext exec, OpItem stmt) {
		super(exec);

		addOrder(stmt.arg());
	}

	public void eval() {
		exec.applyPeriods(this::sort);
		exec.applyPeriods(this::updateRank);
	}

	/**
	 * Adds all of the sort-order expressions to {@link #ordering}.
	 *
	 * @param orderExpr sort-order expression
	 */
	private void addOrder(OpItem orderExpr) {

		switch (orderExpr.type()) {
		case tok.ASCENDING:
		case tok.DESCENDING:
			break;
		case tok.COMMA:
			addOrder(orderExpr.left());
			addOrder(orderExpr.right());
			return;
		default:
			throw new InternalException("invalid sort type: " + orderExpr.type());
		}

		// get the data for this expression
		var result = new VarExpr(exec).eval(orderExpr.arg());
		if (result.isConst()) {
			// no need to sort constants
			return;
		}

		BiFunction<Integer, Integer, Integer> order;
		NumBufData num;
		BoolBufData bool;
		DateBufData date;
		TextBufData text;

		switch (orderExpr.type()) {
		case tok.ASCENDING:
			switch (result.getType()) {
			case tok.NUM:
				num = result.toBuf().toNum();
				order = (p, q) -> Float.compare(num.get(p), num.get(q));
				break;
			case tok.DATE:
				date = result.toBuf().toDate();
				order = (p, q) -> Float.compare(date.get(p), date.get(q));
				break;
			case tok.BOOL:
				bool = result.toBuf().toBool();
				order = (p, q) -> Byte.compare(bool.get(p), bool.get(q));
				break;
			case tok.STR:
				text = result.toBuf().toText();
				order = (p, q) -> text.get(p).compareTo(text.get(q));
				break;
			default:
				throw new InternalException("invalid sort field type: " + result.getType());
			}
			break;

		case tok.DESCENDING:
			// Note: "p" and "q" are reversed
			switch (result.getType()) {
			case tok.NUM:
				num = result.toBuf().toNum();
				order = (q, p) -> Float.compare(num.get(p), num.get(q));
				break;
			case tok.DATE:
				date = result.toBuf().toDate();
				order = (q, p) -> Float.compare(date.get(p), date.get(q));
				break;
			case tok.BOOL:
				bool = result.toBuf().toBool();
				order = (q, p) -> Byte.compare(bool.get(p), bool.get(q));
				break;
			case tok.STR:
				text = result.toBuf().toText();
				order = (q, p) -> text.get(p).compareTo(text.get(q));
				break;
			default:
				throw new InternalException("invalid sort field type: " + result.getType());
			}
			break;

		default:
			throw new InternalException("invalid sort type: " + orderExpr.type());
		}

		ordering.add(order);
	}

	/**
	 * Sorts the records for a given period.
	 *
	 * @param period
	 */
	private void sort(Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();
		List<Integer> recorders = new ArrayList<>(endrec - begrec);

		// build a sortable list of the current record order
		for (int x = begrec; x < endrec; ++x) {
			recorders.add(recOrder[x]);
		}

		// sort all of the record order numbers
		recorders.sort(this::compare);

		// now copy the order numbers back into recOrder
		int count = begrec;
		for (var ordnum : recorders) {
			recOrder[count++] = ordnum;
		}
	}

	/**
	 * Compares all relevant fields of two records.
	 *
	 * @param p first record number
	 * @param q second record number
	 * @return negative, 0, positive, if the first record's data is less than, equal
	 *         to, or greater than the second record's data
	 */
	private int compare(int p, int q) {

		// retained records should appear before un-retained records
		if (retain[p]) {
			if (!retain[q]) {
				// retain p, but not q, thus p goes first
				return -1;
			}

		} else {
			// don't retain p, check q, thus q goes first or they're equal
			return (retain[q] ? 1 : 0);
		}

		// check each ordering function until we get a non-zero value
		int r = 0;

		for (var order : ordering) {
			if ((r = order.apply(p, q)) != 0) {
				break;
			}
		}

		return r;
	}

	/**
	 * Compares two equities for equality, using the current sort order.
	 */
	@Override
	boolean equals(int p, int q) {
		return (compare(p, q) == 0);
	}
}
