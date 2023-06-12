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

import java.nio.ByteBuffer;

import radis.context.ExecContext;
import radis.types.Text;

/**
 * Buffer containing TEXT data, backed by a ByteBuffer. This is typically used
 * when accessing a field within an mmap file.
 */
public class TextByteBufData extends TextBufData {
	private final ByteBuffer buf;

	/**
	 * Temporary buffer used to convert the data to a String.
	 */
	private final byte[] bytes;

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 * @param buf
	 * @param recsz record size
	 */
	public TextByteBufData(ExecContext exec, ByteBuffer buf, int recsz) {
		super(exec);
		this.buf = buf;
		this.bytes = new byte[recsz];
	}

	@Override
	public void rewind() {
		buf.position(bytes.length * exec.getBaseRec());
	}

	@Override
	public void rewind(int begrec) {
		buf.position(bytes.length * begrec);
	}

	@Override
	public String get() {
		buf.get(bytes);
		return Text.fromBytes(bytes);
	}

	@Override
	public String get(int recnum) {
		buf.position(bytes.length * recnum);
		return get();
	}

	@Override
	public TextByteBufData duplicate() {
		return new TextByteBufData(exec, buf.duplicate(), bytes.length);
	}

	@Override
	public boolean hasMore() {
		return buf.hasRemaining();
	}
}
