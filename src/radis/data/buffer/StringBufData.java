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

import java.util.function.Supplier;

import radis.context.ExecContext;

/**
 * Buffer containing TEXT data, backed by a StringBuffer. This is used when
 * accessing data that is <i>not</i> contained within an mmap file; i.e., it's a
 * temporary value.
 */
public class StringBufData extends TextBufData {
	private final StringBuffer buf;

	public StringBufData(ExecContext exec, StringBuffer buf) {
		super(exec);
		this.buf = buf;
	}

	public StringBufData(ExecContext exec) {
		super(exec);
		this.buf = new StringBuffer(exec.getMaxRecNum());
	}

	@Override
	public void rewind() {
		buf.position(exec.getBaseRec());
	}

	@Override
	public void rewind(int begrec) {
		buf.position(begrec);
	}

	@Override
	public String get() {
		return buf.get();
	}

	@Override
	public String get(int recnum) {
		return buf.get(recnum);
	}

	public void put(String value) {
		buf.put(value);
	}

	/**
	 * Creates a new buffer for TEXT data, populating its records from the given
	 * source.
	 *
	 * @param exec
	 * @param source
	 * @return a new TEXT buffer
	 */
	public static StringBufData make(ExecContext exec, Supplier<String> source) {
		StringBufData newData = new StringBufData(exec);
		newData.populate(source);

		return newData;
	}

	/**
	 * Populates this buffer from the given source.
	 *
	 * @param source
	 */
	private void populate(Supplier<String> source) {
		rewind();

		while (buf.hasRemaining()) {
			buf.put(source.get());
		}
	}

	@Override
	public StringBufData duplicate() {
		return new StringBufData(exec, buf.duplicate());
	}

	@Override
	public boolean hasMore() {
		return buf.hasRemaining();
	}

}
