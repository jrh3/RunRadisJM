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

import radis.context.ExecContainer;
import radis.context.ExecContext;
import radis.data.Data;
import radis.data.buffer.BoolBufData;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.StringBufData;
import radis.exception.InternalException;
import radis.exception.SyntaxException;
import radis.op.Op;
import radis.op.OpItem;
import radis.parser.tok;
import radis.types.Bool;
import radis.types.Text;

/**
 * Evaluator for statements statements.
 */
public class Statement extends ExecContainer {

	public Statement(ExecContext exec) {
		super(exec);
	}

	/**
	 * Evaluates a statement.
	 *
	 * @param stmt
	 */
	public void eval(OpItem stmt) {
		switch (stmt.type()) {

		case tok.COMMA:
			eval(stmt.left());
			eval(stmt.right());
			break;

		case tok.USES:
			// do nothing
			break;

		case tok.KEEP:
			keep(stmt);
			break;

		case tok.DEBLANK:
			deblank(stmt.arg());
			break;

		case tok.CREATE:
			createSet(new VarExpr(exec), stmt);
			break;

		case tok.SET:
			createSet(new AggExpr(exec), stmt);
			break;

		case tok.SORT:
			deblank(stmt.arg());
			new Sort(exec, stmt).eval();
			break;

		case tok.UNIQUE:
			deblank(stmt.arg());
			Unique.eval(exec, stmt);
			break;

		case tok.TOP:
			new Top(exec, stmt).eval();
			break;

		case tok.PRINT:
			new Print(exec, stmt).eval();
			break;

		default:
			throw new InternalException("invalid statement op code: " + stmt.type());
		}
	}

	/**
	 * Applies a limit (i.e., maximum number of tickers) to the current execution
	 * context.
	 *
	 * @param limit
	 */
	public void evalLimit(int limit) {
		eval(new Op(tok.TOP, tok.FloatCon(limit), tok.BoolCon(false), // the limit is not a percentage
				tok.BoolCon(false)));
	}

	/**
	 * Evaluates a "keep" statement.
	 *
	 * @param stmt
	 */
	private void keep(OpItem stmt) {

		var result = new VarExpr(exec).eval(stmt.arg());
		if (result.isConst()) {
			byte value = result.toConst().toBool();
			if (Bool.isFalseOrInvalid(value)) {
				// always evaluates to false or invalid - discard everything
				setRetainFalse();
			}

		} else {
			var rb = result.toBuf().toBool();
			rb.rewind();
			for (int x = baseRec; x < maxRecNum; ++x) {
				if (Bool.isFalseOrInvalid(rb.get())) {
					retain[x] = false;
				}
			}
		}
	}

	/**
	 * Evaluates a "deblank" statement.
	 *
	 * @param stmt
	 */
	private void deblank(OpItem stmt) {
		String varnm;
		Data data;

		switch (stmt.type()) {
		case tok.COMMA:
			deblank(stmt.left());
			deblank(stmt.right());
			break;

		case tok.ASCENDING:
		case tok.DESCENDING:
			deblank(stmt.arg());
			break;

		default:
			varnm = stmt.strval();
			data = exec.getData(varnm);
			if (data.getType() == tok.STR) {
				setRetainText(data);
			} else {
				setRetain(data);
			}
			break;
		}
	}

	/**
	 * Evaluates a "create" or "set" statement.
	 *
	 * @param expr expression evaluator
	 * @param stmt
	 */
	private void createSet(Expression expr, OpItem stmt) {

		var varnm = stmt.left().strval();
		if (exec.isField(varnm)) {
			// this variable appears as a field within the DB
			throw new SyntaxException("attempt to overwrite field: " + varnm);
		}

		var result = expr.eval(stmt.right());
		Float vfloat;
		Byte vbyte;
		String vstring;

		if (result.isConst()) {
			// TODO is it necessary to convert a Const to a Buffered?
			switch (result.getType()) {
			case tok.NUM:
				vfloat = result.toConst().toNum();
				result = NumBufData.make(exec, () -> vfloat);
				break;
			case tok.BOOL:
				vbyte = result.toConst().toBool();
				result = BoolBufData.make(exec, () -> vbyte);
				break;
			case tok.DATE:
				vfloat = result.toConst().toDate();
				result = DateBufData.make(exec, () -> vfloat);
				break;
			case tok.STR:
				vstring = result.toConst().toText();
				result = StringBufData.make(exec, () -> vstring);
				break;
			}
		}

		exec.addVar(varnm, result);
		setRetain(varnm);
	}

	/**
	 * Keeps rows where the given variable is valid.
	 *
	 * @param varnm
	 */
	private void setRetain(String varnm) {
		setRetain(exec.getData(varnm));
	}

	/**
	 * Keeps rows where the corresponding data row is valid.
	 *
	 * @param data
	 */
	private void setRetain(Data data) {
		if (data.isConst()) {
			if (!data.isValid()) {
				// never valid - discard everything
				setRetainFalse();
			}

		} else {
			var buf = data.toBuf();
			buf.rewind();
			for (int x = baseRec; x < maxRecNum; ++x) {
				if (!buf.isValid()) {
					retain[x] = false;
				}
			}
		}
	}

	/**
	 * Keeps rows where the corresponding data row is valid.
	 *
	 * @param data TEXT data
	 */
	private void setRetainText(Data data) {

		if (data.isConst()) {
			if (isEmptyOrInvalid(data.toConst().toText())) {
				setRetainFalse();
			}

		} else {
			var buf = data.toBuf().toText();
			buf.rewind();
			for (int x = baseRec; x < maxRecNum; ++x) {
				if (isEmptyOrInvalid(buf.get())) {
					retain[x] = false;
				}
			}
		}
	}

	/**
	 * Discards all records.
	 */
	private void setRetainFalse() {
		throw new SyntaxException("discarding all records");
	}

	/**
	 * @param value
	 * @return {@code true} if the value is empty or invalid
	 */
	private boolean isEmptyOrInvalid(String value) {
		return value.isEmpty() || Text.isInvalid(value);
	}
}
