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

package radis.data.constant;

import radis.context.ExecContext;
import radis.data.buffer.Buffered;
import radis.data.buffer.DateBufData;
import radis.parser.tok;
import radis.types.Date;

/**
 * Constant DATE value.
 */
public class DateConstData extends ConstDataImpl implements ConstData {
	private final Float value;

	public DateConstData(ExecContext exec, float value) {
		super(exec, tok.DATE);
		this.value = value;
	}

	@Override
	public boolean isValid() {
		return Date.isValid(value);
	}

	@Override
	public Buffered toBuf() {
		return DateBufData.make(exec, () -> value);
	}

	@Override
	public float toNum() {
		return Date.toNum(value);
	}

	@Override
	public float toDate() {
		return value;
	}

	@Override
	public byte toBool() {
		return Date.toBool(value);
	}

	@Override
	public String toText() {
		return Date.toText(value);
	}

}
