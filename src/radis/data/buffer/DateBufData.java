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
import radis.types.Date;

/**
 * Buffer containing DATE data, backed by a FloatBuffer.
 */
public class DateBufData extends BufferedImpl implements Boxed<Float> {
	private final FloatBuffer buf;

	public DateBufData(ExecContext exec, FloatBuffer buf) {
		super(exec, tok.DATE);
		this.buf = buf;
	}

	public DateBufData(ExecContext exec) {
		super(exec, tok.DATE);
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

	@Override
	public NumBufData toNum() {
		// no conversion required when going from DATE to FLOAT
		return new NumBufData(exec, buf.duplicate());
	}

	@Override
	public DateBufData toDate() {
		return this;
	}

	@Override
	public BoolBufData toBool() {
		rewind();
		return BoolBufData.make(exec, () -> Date.toBool(get()));
	}

	@Override
	public StringBufData toText() {
		rewind();
		return StringBufData.make(exec, () -> Date.toText(get()));
	}

	/**
	 * Creates a new buffer for DATE data, populating its records from the given
	 * source.
	 *
	 * @param exec
	 * @param source
	 * @return a new DATE buffer
	 */
	public static DateBufData make(ExecContext exec, Supplier<Float> source) {
		DateBufData newData = new DateBufData(exec, FloatBuffer.allocate(exec.getMaxRecNum()));
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
	public DateBufData duplicate() {
		return new DateBufData(exec, buf.duplicate());
	}

	@Override
	public boolean hasMore() {
		return buf.hasRemaining();
	}

	@Override
	public boolean isValid() {
		return Date.isValid(get());
	}

	@Override
	public Float getInvalidBoxed() {
		return Date.INVALID_VALUE;
	}

}
