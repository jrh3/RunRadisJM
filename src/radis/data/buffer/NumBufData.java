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

import java.nio.FloatBuffer;
import java.util.function.Supplier;

import radis.context.ExecContext;
import radis.parser.tok;
import radis.types.Num;

/**
 * Buffer containing FLOAT data, backed by a FloatBuffer.
 */
public class NumBufData extends BufferedImpl implements Boxed<Float> {
	private final FloatBuffer buf;

	public NumBufData(ExecContext exec, FloatBuffer buf) {
		super(exec, tok.NUM);
		this.buf = buf;
	}

	public NumBufData(ExecContext exec) {
		super(exec, tok.NUM);
		this.buf = FloatBuffer.allocate(exec.getMaxRecNum());
	}

	@Override
	public void rewind() {
		buf.position(exec.getBaseRec());
	}

	@Override
	public void rewind(int begrec) {
		buf.position(begrec);
	}

	public float get() {
		return buf.get();
	}

	public float get(int recnum) {
		return buf.get(recnum);
	}

	@Override
	public Float getBoxed() {
		return buf.get();
	}

	@Override
	public Float getBoxed(int recnum) {
		return buf.get(recnum);
	}

	public void put(float value) {
		buf.put(value);
	}

	public void put(int recnum, float value) {
		buf.put(recnum, value);
	}

	@Override
	public NumBufData toNum() {
		return this;
	}

	@Override
	public DateBufData toDate() {
		// no conversion required when going from FLOAT to DATE
		return new DateBufData(exec, buf.duplicate());
	}

	@Override
	public BoolBufData toBool() {
		rewind();
		return BoolBufData.make(exec, () -> Num.toBool(get()));
	}

	@Override
	public StringBufData toText() {
		rewind();
		return StringBufData.make(exec, () -> Num.toText(get()));
	}

	/**
	 * Creates a new buffer for FLOAT data, populating its records from the given
	 * source.
	 *
	 * @param exec
	 * @param source
	 * @return a new FLOAT buffer
	 */
	public static NumBufData make(ExecContext exec, Supplier<Float> source) {
		NumBufData newData = new NumBufData(exec, FloatBuffer.allocate(exec.getMaxRecNum()));
		newData.populate(source);

		return newData;
	}

	/**
	 * Populates this buffer from the given source.
	 *
	 * @param source
	 */
	private void populate(Supplier<Float> source) {
		rewind();

		while (buf.hasRemaining()) {
			buf.put(source.get());
		}
	}

	@Override
	public NumBufData duplicate() {
		return new NumBufData(exec, buf.duplicate());
	}

	@Override
	public boolean hasMore() {
		return buf.hasRemaining();
	}

	@Override
	public boolean isValid() {
		return Num.isValid(get());
	}

	@Override
	public Float getInvalidBoxed() {
		return Num.INVALID_VALUE;
	}
}
