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

/**
 * Manipulators for FLOAT data types.
 */
public class Num {
	public static final float INVALID_VALUE = Float.NaN;

	/**
	 * @param value
	 * @return {@code true} if the value represents an invalid value
	 */
	public static boolean isInvalid(float value) {
		return !Float.isFinite(value);
	}

	/**
	 * @param value
	 * @return {@code true} if the value represents a valid value
	 */
	public static boolean isValid(float value) {
		return Float.isFinite(value);
	}

	/**
	 * @param value
	 * @return the value, converted to a DATE (which is also a "float")
	 */
	public static float toDate(float value) {
		return value;
	}

	/**
	 * @param value
	 * @return the value, converted to a LOGICAL
	 */
	public static byte toBool(float value) {
		if (isInvalid(value)) {
			return Bool.INVALID_VALUE;
		} else {
			return (value == 0 ? Bool.FALSE : Bool.TRUE);
		}
	}

	/**
	 * @param value
	 * @return the value, converted to TEXT
	 */
	public static String toText(float value) {
		if (isInvalid(value)) {
			return Text.INVALID_VALUE;
		}

		String text = Float.toString(value);
		if (text.endsWith(".0")) {
			return text.substring(0, text.length() - 2);
		}

		return text;
	}

	/**
	 * @param text
	 * @return the text, converted to a FLOAT
	 */
	public static float fromText(String text) {
		if (text.isEmpty()) {
			return INVALID_VALUE;
		}

		try {
			return Float.parseFloat(text);

		} catch (NumberFormatException e) {
			return INVALID_VALUE;
		}
	}

	/**
	 * Determines the sign of a value.
	 *
	 * @param value
	 * @return -1, 0, or 1, depending on whether the value is negative, zero, or
	 *         positive, respectively
	 */
	public static float signum(float value) {
		if (isInvalid(value)) {
			return INVALID_VALUE;

		} else if (value < 0) {
			return -1;

		} else if (value > 0) {
			return 1;

		} else {
			return 0;
		}
	}
}
