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
import java.util.function.Consumer;

import radis.context.ExecContainer;
import radis.context.ExecContext;
import radis.data.buffer.BoolBufData;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.TextBufData;
import radis.datadef.Period;
import radis.exception.InternalException;
import radis.op.OpItem;
import radis.parser.tok;
import radis.types.Bool;
import radis.types.Date;
import radis.types.Num;

/**
 * Evaluator for a "print" statement.
 */
public class Print extends ExecContainer {

	/**
	 * Functions that take a record number and print some data item associated with
	 * the record.
	 */
	private final List<Consumer<Integer>> printers = new ArrayList<>();

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 * @param stmt
	 */
	public Print(ExecContext exec, OpItem stmt) {
		super(exec);
		addPrinter(stmt.arg());
	}

	/**
	 * Evaluates the "print" statement.
	 */
	public void eval() {
		exec.applyPeriods(this::printPeriod);
	}

	/**
	 * Prints data for the given period.
	 *
	 * @param period
	 */
	private void printPeriod(Period period) {
		System.out.println(Date.toText(period.getDate()) + ":");

		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		for (int x = begrec; x < endrec; ++x) {
			int recnum = recOrder[x];

			if (!retain[recnum]) {
				continue;
			}

			// print each relevant data item
			for (var printer : printers) {
				printer.accept(recnum);
			}

			System.out.println();
		}

		System.out.println();
	}

	/**
	 * Adds all of the print expressions to {@link #printers}.
	 *
	 * @param printExpr
	 */
	private void addPrinter(OpItem printExpr) {

		switch (printExpr.type()) {
		case tok.COMMA:
			addPrinter(printExpr.left());
			addPrinter(printExpr.right());
			return;
		}

		// get the data for this expression
		var result = new VarExpr(exec).eval(printExpr);

		Consumer<Integer> printer;
		NumBufData num;
		BoolBufData bool;
		DateBufData date;
		TextBufData text;

		switch (result.getType()) {
		case tok.NUM:
			num = result.toBuf().toNum();
			printer = recnum -> System.out.print(Num.toText(num.get(recnum)));
			break;
		case tok.DATE:
			date = result.toBuf().toDate();
			printer = recnum -> System.out.print(Date.toText(date.get(recnum)));
			break;
		case tok.BOOL:
			bool = result.toBuf().toBool();
			printer = recnum -> System.out.print(Bool.toText(bool.get(recnum)));
			break;
		case tok.STR:
			text = result.toBuf().toText();
			printer = recnum -> System.out.print(text.get(recnum));
			break;
		default:
			throw new InternalException("invalid print field type: " + result.getType());
		}

		printers.add(printer);
	}
}
