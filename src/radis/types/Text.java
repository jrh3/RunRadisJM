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

package radis.types;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import radis.exception.LoaderException;

/**
 * Manipulators for TEXT data types.
 */
public class Text {
	public static final String INVALID_VALUE = "#VALUE!";

	/**
	 * @param text
	 * @return {@code true} if the text represents an invalid value
	 */
	public static boolean isInvalid(String text) {
		return INVALID_VALUE.equals(text);
	}

	/**
	 * @param text
	 * @return {@code true} if the text represents a valid value
	 */
	public static boolean isValid(String text) {
		return !INVALID_VALUE.equals(text);
	}

	/**
	 * Reads a fixed number of bytes from a buffer and converts it to text,
	 * truncating at the first '\0'.
	 *
	 * @param buf    buffer from which to read the text
	 * @param nbytes number of bytes to read
	 * @return the text that was read from the buffer
	 */
	public static String getString(ByteBuffer buf, int nbytes) {
		var bytes = new byte[nbytes];
		buf.get(bytes);
		return Text.fromBytes(bytes);
	}

	/**
	 * Writes text to a buffer. The buffer is padded with '\0' out to the specified
	 * number of bytes.
	 *
	 * @param buf    buffer to which the text should be written
	 * @param nbytes number of bytes to write
	 * @param text   text to be written
	 */
	public static void putString(ByteBuffer buf, int nbytes, String text) {
		var bytes = Text.toBytes(text);
		if (bytes.length > nbytes) {
			throw new LoaderException("value is too long: " + text);
		}

		buf.put(bytes);

		for (int x = bytes.length; x < nbytes; ++x) {
			buf.put((byte) 0);
		}
	}

	/**
	 * Converts an array of bytes to text, using the ASCII character set. Stops at
	 * the first '\0'.
	 *
	 * @param bytes bytes to be converted
	 * @return the text encoded by the bytes
	 */
	public static String fromBytes(byte[] bytes) {
		for (int x = 0; x < bytes.length; ++x) {
			if (bytes[x] == 0) {
				return new String(bytes, 0, x, StandardCharsets.US_ASCII);
			}
		}

		return new String(bytes, StandardCharsets.US_ASCII);
	}

	/**
	 * Converts text into an array of bytes, using the ASCII character set. Does not
	 * include any trailing '\0'.
	 *
	 * @param text text to be converted
	 * @return the encoded text
	 */
	public static byte[] toBytes(String text) {
		return text.getBytes(StandardCharsets.US_ASCII);
	}
}
