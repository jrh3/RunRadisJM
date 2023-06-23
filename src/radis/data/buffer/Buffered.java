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

import radis.data.Data;

/**
 * Data that is accessed via a buffer (e.g., FloatBuffer).
 */
public interface Buffered extends Data {

	/**
	 * Rewinds the buffer to the first record of the execution context.
	 */
	void rewind();

	/**
	 * Rewinds the buffer to the specified record.
	 *
	 * @param begrec record, relative to the start of the buffer
	 */
	void rewind(int begrec);

	/**
	 * @return {@code true} if the buffer contains more records
	 */
	boolean hasMore();

	/*
	 * These methods convert the data in "this" buffer to data of the appropriate
	 * type. If this buffer is already of the appropriate type, then it just returns
	 * itself. Otherwise, a new buffer, of the appropriate type, is constructed and
	 * then populated by converting each record of "this" buffer.
	 */

	NumBufData toNum();

	DateBufData toDate();

	BoolBufData toBool();

	TextBufData toText();
}
