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

package radis.data.buffer;

import radis.context.ExecContext;
import radis.parser.tok;
import radis.types.Bool;
import radis.types.Date;
import radis.types.Num;
import radis.types.Text;

/**
 * Buffer containing TEXT data.
 */
public abstract class TextBufData extends BufferedImpl implements Boxed<String> {

	public TextBufData(ExecContext exec) {
		super(exec, tok.STR);
	}

	public abstract String get();

	public abstract String get(int recnum);

	@Override
	public String getBoxed() {
		return get();
	}

	@Override
	public String getBoxed(int recnum) {
		return get(recnum);
	}

	@Override
	public boolean isValid() {
		return Text.isValid(get());
	}

	@Override
	public NumBufData toNum() {
		rewind();
		return NumBufData.make(exec, () -> Num.fromText(get()));
	}

	@Override
	public DateBufData toDate() {
		rewind();
		return DateBufData.make(exec, () -> Date.fromText(get()));
	}

	@Override
	public BoolBufData toBool() {
		rewind();
		return BoolBufData.make(exec, () -> Bool.fromText(get()));
	}

	@Override
	public TextBufData toText() {
		return this;
	}

	@Override
	public String getInvalidBoxed() {
		return Text.INVALID_VALUE;
	}

}
