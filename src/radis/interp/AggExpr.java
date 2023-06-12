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

import radis.Util;
import radis.context.ExecContext;
import radis.data.Data;
import radis.data.buffer.Buffered;
import radis.data.buffer.NumBufData;
import radis.datadef.Period;
import radis.exception.SyntaxException;
import radis.op.OpItem;
import radis.parser.tok;
import radis.types.Num;

/**
 * Evaluator for expressions that may contain aggregate functions of variables.
 */
public class AggExpr extends Expression {

	/**
	 * Function to be applied for a period, using a data source
	 *
	 * @param <S> data source type
	 */
	public interface Func<S extends Data> {
		public float apply(S source, Period period);
	}

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 */
	public AggExpr(ExecContext exec) {
		super(exec);
	}

	@Override
	public Data eval(OpItem op) {

		switch (op.type()) {

		case tok.COUNT:
			return applyAggregateBuf(op, this::count);

		case tok.SUM:
			return applyAggregateNum(op, this::sum);

		case tok.AGGAVERAGE:
			return applyAggregateNum(op, this::average);

		case tok.AGGMEDIAN:
			return applyAggregateNum(op, this::median);

		case tok.AGGMAX:
			return applyAggregateNum(op, this::max);

		case tok.VAR:
		case tok.AVERAGE:
		case tok.MEDIAN:
			throw new SyntaxException("non-aggregate not allowed in expression: " + op.type());

		default:
			// not a variable-oriented expression, just use the standard evaluator
			return super.eval(op);
		}
	}

	/**
	 * Applies an aggregate operation to a data buffer of arbitrary type.
	 *
	 * @param op
	 * @param aggregate
	 * @return the result
	 */
	private Data applyAggregateBuf(OpItem op, Func<Buffered> aggregate) {
		var arg = evalAggVar(op);
		return applyAggregate(arg.toBuf(), aggregate);
	}

	/**
	 * Applies an aggregate operation to a numeric data buffer.
	 *
	 * @param op
	 * @param aggregate
	 * @return the result
	 */
	private Data applyAggregateNum(OpItem op, Func<NumBufData> aggregate) {
		var arg = evalAggVar(op);
		return applyAggregate(arg.toBuf().toNum(), aggregate);
	}

	/**
	 * Gets the data associated with a variable, verifying that it is not a
	 * constant.
	 *
	 * @param varexpr variable name expression
	 * @return the data associated with the given variable variable
	 */
	private Data evalAggVar(OpItem varexpr) {
		Data arg = evalVar(varexpr.arg());
		if (arg.isConst()) {
			throw new SyntaxException("cannot aggregate a constant");
		}

		return arg;
	}

	/**
	 * Applies an aggregate operation to a numeric data buffer.
	 *
	 * @param <S>       data source type
	 * @param source    data to which the aggregation operator should be applied
	 * @param aggregate aggregation operator
	 * @return the result
	 */
	public <S extends Data> NumBufData applyAggregate(S source, Func<S> aggregate) {

		NumBufData result = new NumBufData(exec);

		for (var period : exec.getPeriods()) {
			float value = aggregate.apply(source, period);

			int begrec = period.beginRecord();
			int endrec = period.endRecord();

			// store the result in every record
			result.rewind(begrec);

			for (int x = begrec; x < endrec; ++x) {
				result.put(value);
			}
		}

		return result;
	}

	/**
	 * @param source source records
	 * @param period
	 * @return the number of valid, retained source records in the period
	 */
	private float count(Buffered source, Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		boolean[] retain = exec.retain;
		source.rewind(begrec);

		int count = 0;
		for (int x = begrec; x < endrec; ++x) {
			/*
			 * Note: must invoke source.isValid() each time through the loop so it doesn't
			 * get out of sync with "x", thus we test source BEFORE checking retain.
			 */
			if (source.isValid() && retain[x]) {
				++count;
			}
		}

		return count;
	}

	/**
	 * @param source source records
	 * @param period
	 * @return the sum of the valid, retained source records in the period
	 */
	private float sum(NumBufData source, Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		boolean[] retain = exec.retain;
		source.rewind(begrec);

		float sum = 0;
		for (int x = begrec; x < endrec; ++x) {
			float value = source.get();
			if (Num.isValid(value) && retain[x]) {
				sum += value;
			}
		}

		return sum;
	}

	/**
	 * @param source source records
	 * @param period
	 * @return the average of the valid, retained source records in the period
	 */
	private float average(NumBufData source, Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		boolean[] retain = exec.retain;
		source.rewind(begrec);

		int count = 0;
		float sum = 0;
		for (int x = begrec; x < endrec; ++x) {
			float value = source.get();
			if (Num.isValid(value) && retain[x]) {
				++count;
				sum += value;
			}
		}

		return (count == 0 ? 0 : sum / count);
	}

	/**
	 * @param source source records
	 * @param period
	 * @return the median of the valid, retained source records in the period
	 */
	private float median(NumBufData source, Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		boolean[] retain = exec.retain;
		source.rewind(begrec);

		ArrayList<Float> vec = new ArrayList<>(endrec - begrec);

		for (int x = begrec; x < endrec; ++x) {
			float value = source.get();
			if (Num.isValid(value) && retain[x]) {
				vec.add(value);
			}
		}

		return Util.median(vec);
	}

	/**
	 * @param source source records
	 * @param period
	 * @return the maximum of the valid, retained source records in the period
	 */
	private float max(NumBufData source, Period period) {
		int begrec = period.beginRecord();
		int endrec = period.endRecord();

		boolean[] retain = exec.retain;
		source.rewind(begrec);

		float max = 0;
		boolean haveMax = false;
		for (int x = begrec; x < endrec; ++x) {
			float value = source.get();
			if (Num.isValid(value) && retain[x]) {
				if (!haveMax || value > max) {
					max = value;
					haveMax = true;
				}
			}
		}

		return max;
	}
}
