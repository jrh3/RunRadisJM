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

import java.util.function.BiFunction;
import static radis.dbf.CompanyDbf.TKR_RE;
import static radis.dbf.CompanyDbf.NAME_RE;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import radis.Util;
import radis.context.ExecContext;
import radis.data.Data;
import radis.data.buffer.BoolBufData;
import radis.data.buffer.Boxed;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.StringBufData;
import radis.data.constant.BoolConstData;
import radis.data.constant.NumConstData;
import radis.data.constant.TextConstData;
import radis.exception.InternalException;
import radis.op.OpItem;
import radis.parser.tok;
import radis.types.Bool;
import radis.types.Num;
import radis.types.Text;

/**
 * Expression evaluator.
 */
public class Expression {
	protected final ExecContext exec;

	public Expression(ExecContext exec) {
		this.exec = exec;
	}

	/**
	 * Evaluates the given expression.
	 *
	 * @param op operator
	 * @return the result
	 */
	public Data eval(OpItem op) {
		Data arg;
		Data left;
		Data right;

		switch (op.type()) {

		case tok.INTCON:
			return new NumConstData(exec, op.intval());

		case tok.FLOATCON:
			return new NumConstData(exec, op.floatval());

		case tok.STRCON:
			return new TextConstData(exec, op.strval());

		case tok.NOW:
		case tok.NOW_VAR:
			return exec.getData(Util.NOW_VAR_NM);

		case tok.IF:
			return ifStatement(op);

		case tok.NOT:
			arg = eval(op.arg());
			if (arg.isConst()) {
				return new BoolConstData(exec, Bool.boolNot(arg.toConst().toBool()));
			} else {
				var bool = arg.toBuf().toBool();
				bool.rewind();
				return BoolBufData.make(exec, () -> Bool.boolNot(bool.get()));
			}

		case tok.ADD:
			return numBinary(op, (vleft, vright) -> vleft + vright);

		case tok.SUB:
			return numBinary(op, (vleft, vright) -> vleft - vright);

		case tok.MULT:
			return numBinary(op, (vleft, vright) -> vleft * vright);

		case tok.DIV:
			return numBinary(op, (vleft, vright) -> vleft / vright);

		case tok.OR:
			return boolBinary(op, Bool::boolOr);

		case tok.AND:
			return boolBinary(op, Bool::boolAnd);

		case tok.MIN:
			return numBinary(op, (vleft, vright) -> {
				if (Num.isInvalid(vleft) || Num.isInvalid(vright)) {
					return Num.INVALID_VALUE;
				} else {
					return Math.min(vleft, vright);
				}
			});

		case tok.MAX:
			return numBinary(op, (vleft, vright) -> {
				if (Num.isInvalid(vleft) || Num.isInvalid(vright)) {
					return Num.INVALID_VALUE;
				} else {
					return Math.max(vleft, vright);
				}
			});

		case tok.SIGN:
			arg = eval(op.arg(0));
			if (arg.isConst()) {
				return new NumConstData(exec, Num.signum(arg.toConst().toNum()));
			} else {
				var num = arg.toBuf().toNum();
				num.rewind();
				return NumBufData.make(exec, () -> Num.signum(num.get()));
			}

		case tok.ABS:
			arg = eval(op.arg(0));
			if (arg.isConst()) {
				return new NumConstData(exec, Math.abs(arg.toConst().toNum()));
			} else {
				var num = arg.toBuf().toNum();
				num.rewind();
				return NumBufData.make(exec, () -> Math.abs(num.get()));
			}

		case tok.LENGTH:
			arg = eval(op.arg(0));
			if (arg.isConst()) {
				String value = arg.toConst().toText();
				return new NumConstData(exec, Text.isInvalid(value) ? Num.INVALID_VALUE : value.length());
			} else {
				var text = arg.toBuf().toText();
				text.rewind();
				return NumBufData.make(exec, () -> {
					String value = text.get();
					return Text.isInvalid(value) ? Num.INVALID_VALUE : value.length();
				});
			}

		case tok.EQ:
			left = eval(op.left());
			right = eval(op.right());

			if (left.getType() == tok.STR || right.getType() == tok.STR) {
				return textCmp(left, right, String::equals);
			} else {
				return numCmp(left, right, Float::equals);
			}

		case tok.NEQ:
			// this is always true, because it's enforced when the data is loaded
			if ("si exchange".equals(getVarName(op.left())) && "O".equals(getTextConst(op.right()))) {
				return new BoolConstData(exec, Bool.TRUE);
			}

			left = eval(op.left());
			right = eval(op.right());

			if (left.getType() == tok.STR || right.getType() == tok.STR) {
				return textCmp(left, right, (vleft, vright) -> !vleft.equals(vright));
			} else {
				return numCmp(left, right, (vleft, vright) -> !vleft.equals(vright));
			}

		case tok.LT:
			return numCmp(op, (vleft, vright) -> vleft < vright);

		case tok.LE:
			return numCmp(op, (vleft, vright) -> vleft <= vright);

		case tok.GT:
			return numCmp(op, (vleft, vright) -> vleft > vright);

		case tok.GE:
			return numCmp(op, (vleft, vright) -> vleft >= vright);

		case tok.POW:
			return numBinary(op, (vleft, vright) -> (float) Math.pow(vleft, vright));

		case tok.NEG:
			arg = eval(op.arg(0));
			if (arg.isConst()) {
				return new NumConstData(exec, -arg.toConst().toNum());
			} else {
				var num = arg.toBuf().toNum();
				num.rewind();
				return NumBufData.make(exec, () -> -num.get());
			}

		case tok.MATCH:
			return match(op);

		default:
			throw new InternalException("invalid expression op code: " + op.type());
		}
	}

	private Data ifStatement(OpItem op) {
		var condition = eval(op.arg(0));

		if (condition.isConst()) {
			byte value = condition.toConst().toBool();
			if (Bool.isInvalid(value)) {
				return new NumConstData(exec, Num.INVALID_VALUE);
			} else if (value == Bool.TRUE) {
				return eval(op.arg(1));
			} else {
				return eval(op.arg(2));
			}

		} else {
			var cond = condition.toBuf().toBool();

			var left = eval(op.arg(1)).toBuf();
			var right = eval(op.arg(2)).toBuf();

			if (left.getType() != right.getType()) {
				// left & right are different types - convert both to text first
				return typedIf(cond, left.toText(), right.toText(), StringBufData::make);
			}

			switch (left.getType()) {
			case tok.NUM:
				return typedIf(cond, left.toNum(), right.toNum(), NumBufData::make);
			case tok.DATE:
				return typedIf(cond, left.toDate(), right.toDate(), DateBufData::make);
			case tok.BOOL:
				return typedIf(cond, left.toBool(), right.toBool(), BoolBufData::make);
			case tok.STR:
				return typedIf(cond, left.toText(), right.toText(), StringBufData::make);
			default:
				throw new InternalException("invalid 'if' argument types: " + left.getType());
			}
		}
	}

	/**
	 * Performs the "if" operation.
	 *
	 * @param condition
	 * @param left      result rows generated by the left operand
	 * @param right     result rows generated by the right operand
	 * @param dataMaker function to generate the result for each row
	 * @return the result
	 */
	private <T> Data typedIf(BoolBufData condition, Boxed<T> left, Boxed<T> right,
			BiFunction<ExecContext, Supplier<T>, Data> dataMaker) {

		condition.rewind();
		left.rewind();
		right.rewind();

		/*
		 * Note: to ensure we're always looking at the same rows, we must invoke
		 * getBoxed() for both the left and the right, regardless of which branch is
		 * taken.
		 */

		return dataMaker.apply(exec, () -> {

			var condValue = condition.get();

			if (Bool.isInvalid(condValue)) {
				// discard both left & right, and return an INVALID value
				left.getBoxed();
				right.getBoxed();
				return left.getInvalidBoxed();

			} else if (condValue == Bool.TRUE) {
				// discard right, return left
				right.getBoxed();
				return left.getBoxed();

			} else {
				// discard left, return right
				left.getBoxed();
				return right.getBoxed();
			}
		});
	}

	/**
	 * @param op
	 * @return the variable name if "op" is a variable, {@code null} otherwise
	 */
	private String getVarName(OpItem op) {
		return (op.type() == tok.VAR ? op.strval() : null);
	}

	/**
	 * @param op
	 * @return the text if "op" is a string constant, {@code null} otherwise
	 */
	private String getTextConst(OpItem op) {
		return (op.type() == tok.STRCON ? op.strval() : null);
	}

	/**
	 * Evaluates a "match(<value>, <regular-expression>, <case-sensitive-flag>)"
	 * operation.
	 *
	 * @param op "match" operator
	 * @return the result
	 */
	private Data match(OpItem op) {
		String regex = op.arg(1).strval();

		/*
		 * These cases were applied when the data was first loaded into the memory map
		 * files, thus we can assume the result is already determined.
		 */
		switch (regex) {
		case TKR_RE:
			return new NumConstData(exec, 1);

		case NAME_RE:
			return new NumConstData(exec, 0);
		}

		boolean caseSensitive = op.arg(2).boolval();
		Pattern pat = Pattern.compile(regex, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
		Data text = eval(op.arg(0));

		if (text.isConst()) {
			Matcher mat = pat.matcher(text.toConst().toText());
			if (mat.find()) {
				return new NumConstData(exec, mat.start() + 1);
			} else {
				return new NumConstData(exec, 0);
			}

		} else {
			var buf = text.toBuf().toText();
			buf.rewind();
			return NumBufData.make(exec, () -> {
				Matcher mat = pat.matcher(buf.get());
				return (float) (mat.find() ? mat.start() + 1 : 0);
			});
		}
	}

	/**
	 * Evaluate a "variable" operator.
	 *
	 * @param op
	 * @return the data associated with the variable
	 */
	protected Data evalVar(OpItem op) {
		var varnm = op.strval();
		return exec.getData(varnm);
	}

	/**
	 * Evaluates a numeric, binary operation.
	 *
	 * @param op
	 * @param func function to yield a new value using the left & right values
	 * @return the result
	 */
	private Data numBinary(OpItem op, BiFunction<Float, Float, Float> func) {
		var left = eval(op.left());
		var right = eval(op.right());

		if (left.isConst()) {
			if (right.isConst()) {
				// both are constant - result is a constant
				Float lv = left.toConst().toNum();
				Float rv = right.toConst().toNum();
				return new NumConstData(exec, func.apply(lv, rv));

			} else {
				// only the left is a constant - extract its value just once
				Float lv = left.toConst().toNum();
				var rb = right.toBuf().toNum();
				rb.rewind();
				return NumBufData.make(exec, () -> func.apply(lv, rb.getBoxed()));
			}

		} else {
			if (right.isConst()) {
				// only the right is a constant - extract its value just once
				var lb = left.toBuf().toNum();
				Float rv = right.toConst().toNum();
				lb.rewind();
				return NumBufData.make(exec, () -> func.apply(lb.getBoxed(), rv));

			} else {
				// neither value is constant - we must get the value from each row of both
				var lb = left.toBuf().toNum();
				var rb = right.toBuf().toNum();
				lb.rewind();
				rb.rewind();
				return NumBufData.make(exec, () -> func.apply(lb.getBoxed(), rb.getBoxed()));
			}
		}
	}

	/**
	 * Evaluates a logical, binary operation.
	 *
	 * @param op
	 * @param func function to yield a new value using the left & right values
	 * @return the result
	 */
	private Data boolBinary(OpItem op, BiFunction<Byte, Byte, Byte> func) {
		var left = eval(op.left());
		var right = eval(op.right());

		if (left.isConst()) {
			if (right.isConst()) {
				// both are constant - result is a constant
				Byte lv = left.toConst().toBool();
				Byte rv = right.toConst().toBool();
				return new BoolConstData(exec, func.apply(lv, rv));

			} else {
				// only the left is a constant - extract its value just once
				Byte lv = left.toConst().toBool();
				var rb = right.toBuf().toBool();
				rb.rewind();
				return BoolBufData.make(exec, () -> func.apply(lv, rb.getBoxed()));
			}

		} else {
			if (right.isConst()) {
				// only the right is a constant - extract its value just once
				var lb = left.toBuf().toBool();
				Byte rv = right.toConst().toBool();
				lb.rewind();
				return BoolBufData.make(exec, () -> func.apply(lb.getBoxed(), rv));

			} else {
				// neither value is constant - we must get the value from each row of both
				var lb = left.toBuf().toBool();
				var rb = right.toBuf().toBool();
				lb.rewind();
				rb.rewind();
				return BoolBufData.make(exec, () -> func.apply(lb.getBoxed(), rb.getBoxed()));
			}
		}
	}

	/**
	 * Evaluates a comparison of two FLOAT values.
	 *
	 * @param op
	 * @param func function to compare the two values
	 * @return the result
	 */
	private Data numCmp(OpItem op, BiFunction<Float, Float, Boolean> func) {
		var left = eval(op.left());
		var right = eval(op.right());
		return numCmp(left, right, func);
	}

	/**
	 * Evaluates a comparison of two FLOAT values.
	 *
	 * @param left
	 * @param right
	 * @param func  function to compare the two values
	 * @return the result
	 */
	private Data numCmp(Data left, Data right, BiFunction<Float, Float, Boolean> func) {

		// wrap "func" in another function that will check for valid values
		BiFunction<Float, Float, Byte> func2 = (vleft, vright) -> {
			if (Num.isInvalid(vleft) || Num.isInvalid(vright)) {
				return Bool.INVALID_VALUE;
			} else {
				return (func.apply(vleft, vright) ? Bool.TRUE : Bool.FALSE);
			}
		};

		if (left.isConst()) {
			if (right.isConst()) {
				// both are constant - result is a constant
				Float lv = left.toConst().toNum();
				Float rv = right.toConst().toNum();
				return new BoolConstData(exec, func2.apply(lv, rv));

			} else {
				// only the left is a constant - extract its value just once
				Float lv = left.toConst().toNum();
				var rb = right.toBuf().toNum();
				return BoolBufData.make(exec, () -> func2.apply(lv, rb.getBoxed()));
			}

		} else {
			if (right.isConst()) {
				// only the right is a constant - extract its value just once
				var lb = left.toBuf().toNum();
				Float rv = right.toConst().toNum();
				lb.rewind();
				return BoolBufData.make(exec, () -> func2.apply(lb.getBoxed(), rv));

			} else {
				// neither value is constant - we must get the value from each row of both
				var lb = left.toBuf().toNum();
				var rb = right.toBuf().toNum();
				lb.rewind();
				rb.rewind();
				return BoolBufData.make(exec, () -> func2.apply(lb.getBoxed(), rb.getBoxed()));
			}
		}
	}

	/**
	 * Evaluates a comparison of two TEXT values.
	 *
	 * @param left
	 * @param right
	 * @param func  function to compare the two values
	 * @return the result
	 */
	private Data textCmp(Data left, Data right, BiFunction<String, String, Boolean> func) {

		// wrap "func" in another function that will check for valid values
		BiFunction<String, String, Byte> func2 = (vleft, vright) -> {
			if (Text.isInvalid(vleft) || Text.isInvalid(vright)) {
				return Bool.INVALID_VALUE;
			} else {
				return (func.apply(vleft, vright) ? Bool.TRUE : Bool.FALSE);
			}
		};

		if (left.isConst()) {
			if (right.isConst()) {
				// both are constant - result is a constant
				String lv = left.toConst().toText();
				String rv = right.toConst().toText();
				return new BoolConstData(exec, func2.apply(lv, rv));

			} else {
				// only the left is a constant - extract its value just once
				String lv = left.toConst().toText();
				var rb = right.toBuf().toText();
				return BoolBufData.make(exec, () -> func2.apply(lv, rb.getBoxed()));
			}

		} else {
			if (right.isConst()) {
				// only the right is a constant - extract its value just once
				var lb = left.toBuf().toText();
				String rv = right.toConst().toText();
				lb.rewind();
				return BoolBufData.make(exec, () -> func2.apply(lb.getBoxed(), rv));

			} else {
				// neither value is constant - we must get the value from each row of both
				var lb = left.toBuf().toText();
				var rb = right.toBuf().toText();
				lb.rewind();
				rb.rewind();
				return BoolBufData.make(exec, () -> func2.apply(lb.getBoxed(), rb.getBoxed()));
			}
		}
	}
}
