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
import java.util.function.Function;

import radis.Util;
import radis.context.ExecContext;
import radis.data.Data;
import radis.data.buffer.NumBufData;
import radis.exception.SyntaxException;
import radis.op.OpItem;
import radis.parser.tok;
import radis.types.Num;

/**
 * Evaluator for expressions that may contain non-aggregate functions of
 * variables.
 */
public class VarExpr extends Expression {

	public VarExpr(ExecContext exec) {
		super(exec);
	}

	@Override
	public Data eval(OpItem op) {

		switch (op.type()) {

		case tok.VAR:
			return evalVar(op);

		case tok.AVERAGE:
			return multiSource(op.arg(), VarExpr::average);

		case tok.MEDIAN:
			return multiSource(op.arg(), VarExpr::median);

		case tok.AGGVAR:
		case tok.SUM:
		case tok.COUNT:
		case tok.AGGAVERAGE:
		case tok.AGGMEDIAN:
			throw new SyntaxException("aggregate not allowed in expression: " + op.type());

		default:
			// not a variable-oriented expression, just use the standard evaluator
			return super.eval(op);
		}
	}

	/**
	 * @param sources
	 * @return the average of the values, taking one from each source buffer
	 */
	private static float average(List<NumBufData> sources) {
		int count = 0;
		double sum = 0;

		for (var data : sources) {
			++count;

			// no need to check for invalid - "+=" does that for us
			sum += data.get();
		}

		return (float) (sum / count);
	};

	/**
	 * @param sources
	 * @return the median of the values, taking one from each source buffer
	 */
	private static float median(List<NumBufData> sources) {
		List<Float> vec = new ArrayList<>(sources.size());

		boolean invalid = false;

		for (var data : sources) {
			var value = data.get();
			if (Num.isInvalid(value)) {
				invalid = true;
			} else {
				vec.add(value);
			}
		}

		return (invalid ? Num.INVALID_VALUE : Util.median(vec));
	}

	/**
	 * Combines values from multiple data sources to yield a single value, one per
	 * row.
	 *
	 * @param op      operator
	 * @param combine function to combine values
	 * @return the result of combining each row
	 */
	private Data multiSource(OpItem op, Function<List<NumBufData>, Float> combine) {
		List<NumBufData> results = new ArrayList<>();
		genNumArgs(results, op);
		results.forEach(NumBufData::rewind);

		return NumBufData.make(exec, () -> combine.apply(results));
	}

	/**
	 * Convert an argument list to numeric results.
	 *
	 * @param results where to put the generated rows
	 * @param p       argument list
	 * @throws SyntaxException if the action references a constant or an aggregate
	 *                         variable
	 */
	private void genNumArgs(List<NumBufData> results, OpItem p) {
		if (p.type() == tok.COMMA) {
			// add left & right to the results
			genNumArgs(results, p.left());
			genNumArgs(results, p.right());
			return;
		}

		Data result = eval(p);
		if (result.isConst()) {
			throw new SyntaxException("constant found in average/median");
		}

		// coerce the resulting buffer to a FLOAT buffer
		results.add(result.toBuf().toNum());
	}

}
