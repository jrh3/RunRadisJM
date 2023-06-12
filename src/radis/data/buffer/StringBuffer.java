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

/**
 * Buffer, similar to FloatBuffer, but contains String values instead of float
 * values.
 */
public class StringBuffer {
	private final String[] buf;
	private int curIndex;

	/**
	 * Constructs the object, allocating a new backing store.
	 *
	 * @param size number of Strings this buffer should contain
	 */
	public StringBuffer(int size) {
		this.buf = new String[size];
		this.curIndex = 0;
	}

	/**
	 * Constructs the object.
	 *
	 * @param buf      backing store
	 * @param curIndex the current index into the backing store
	 */
	private StringBuffer(String[] buf, int curIndex) {
		this.buf = buf;
		this.curIndex = curIndex;
	}

	/**
	 * Makes a duplicate of this buffer, reusing the same backing store.
	 *
	 * @return a new buffer
	 */
	public StringBuffer duplicate() {
		return new StringBuffer(buf, curIndex);
	}

	/**
	 * @return the number of Strings this buffer contains
	 */
	public int limit() {
		return buf.length;
	}

	/**
	 * @return {@code true} if there are more Strings available for retrieval
	 */
	public boolean hasRemaining() {
		return (curIndex < buf.length);
	}

	/**
	 * Sets the current index to the specified record.
	 *
	 * @param begrec
	 */
	public void position(int begrec) {
		curIndex = begrec;
	}

	/**
	 * Gets the String at the current index, and bumps the index.
	 *
	 * @return the current String
	 */
	public String get() {
		return buf[curIndex++];
	}

	/**
	 * @param index
	 * @return the String at the specified index
	 */
	public String get(int index) {
		return buf[index];
	}

	/**
	 * Stores the given value at the current index, and bumps the index.
	 *
	 * @param value
	 */
	public void put(String value) {
		buf[curIndex++] = value;
	}
}
