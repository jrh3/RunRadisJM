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
import radis.data.DataImpl;
import radis.data.constant.ConstData;

/**
 * Implementation of buffered data.
 */
public abstract class BufferedImpl extends DataImpl implements Buffered {

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 * @param type token type (i.e., NUM, BOOL, STR, DATE) of the data contained
	 *             within this buffer
	 */
	public BufferedImpl(ExecContext exec, int type) {
		super(exec, type, false);
	}

	/**
	 * Converts the buffer to a constant.
	 *
	 * @throws UnsupportedOperationException if this buffer does not represent a
	 *                                       constant
	 */
	public ConstData toConst() {
		throw new UnsupportedOperationException(getClass().getName());
	}

	/**
	 * Converts this to fully buffered data.
	 */
	public Buffered toBuf() {
		return this;
	}
}
