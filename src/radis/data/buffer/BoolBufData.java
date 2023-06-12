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
import java.util.function.Supplier;

import radis.context.ExecContext;
import radis.parser.tok;
import radis.types.Bool;

/**
 * Buffer containing LOGICAL data, backed by a ByteBuffer.
 */
public class BoolBufData extends BufferedImpl implements Boxed<Byte> {
	private final ByteBuffer buf;

	public BoolBufData(ExecContext exec, ByteBuffer buf) {
		super(exec, tok.BOOL);
		this.buf = buf;
	}

	@Override
	public void rewind() {
		buf.position(exec.getBaseRec());
	}

	@Override
	public void rewind(int begrec) {
		buf.position(begrec);
	}

	public byte get() {
		return buf.get();
	}

	public byte get(int recnum) {
		return buf.get(recnum);
	}

	@Override
	public Byte getBoxed() {
		return buf.get();
	}

	@Override
	public Byte getBoxed(int recnum) {
		return buf.get(recnum);
	}

	public void put(byte value) {
		buf.put(value);
	}

	@Override
	public NumBufData toNum() {
		rewind();
		return NumBufData.make(exec, () -> Bool.toNum(get()));
	}

	@Override
	public DateBufData toDate() {
		rewind();
		return DateBufData.make(exec, () -> Bool.toDate(get()));
	}

	@Override
	public BoolBufData toBool() {
		return this;
	}

	@Override
	public StringBufData toText() {
		rewind();
		return StringBufData.make(exec, () -> Bool.toText(get()));
	}

	/**
	 * Creates a new buffer for logical data, populating its records from the given
	 * source.
	 *
	 * @param exec
	 * @param source
	 * @return a new FLOAT buffer
	 */
	public static BoolBufData make(ExecContext exec, Supplier<Byte> source) {
		BoolBufData newData = new BoolBufData(exec, ByteBuffer.allocateDirect(exec.getMaxRecNum()));
		newData.populate(source);

		return newData;
	}

	/**
	 * Populates this buffer from the given source.
	 *
	 * @param source
	 */
	private void populate(Supplier<Byte> source) {
		rewind();

		while (buf.hasRemaining()) {
			buf.put(source.get());
		}
	}

	@Override
	public BoolBufData duplicate() {
		return new BoolBufData(exec, buf.duplicate());
	}

	@Override
	public boolean hasMore() {
		return buf.hasRemaining();
	}

	@Override
	public boolean isValid() {
		return Bool.isValid(get());
	}

	@Override
	public Byte getInvalidBoxed() {
		return Bool.INVALID_VALUE;
	}

}
