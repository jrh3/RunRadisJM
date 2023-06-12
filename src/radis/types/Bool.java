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
 * Manipulators for LOGICAL data types. LOGICAL values are stored as "byte"
 * values, where {@code true} and {@code false} are represented by one and zero,
 * respectively. Invalid values have the second bit (i.e., 0x02) set.
 */
public class Bool {
	public static final byte INVALID_VALUE = 2;
	public static final byte TRUE = 1;
	public static final byte FALSE = 0;

	/**
	 * @param value
	 * @return {@code true} if the value represents an invalid value
	 */
	public static boolean isInvalid(byte value) {
		return ((value & INVALID_VALUE) != 0);
	}

	/**
	 * @param value
	 * @return {@code true} if the value represents a valid value
	 */
	public static boolean isValid(byte value) {
		return ((value & INVALID_VALUE) == 0);
	}

	/**
	 * @param value
	 * @return {@code true} if the value represents {@code false} or is invalid
	 */
	public static boolean isFalseOrInvalid(byte value) {
		return ((value & (INVALID_VALUE | TRUE)) != TRUE);
	}

	/**
	 * @param value
	 * @return the value, negated. If the value was invalid, the resulting value
	 *         will also be invalid
	 */
	public static byte boolNot(byte value) {
		return (byte) ((~value ^ INVALID_VALUE) & (INVALID_VALUE | TRUE));
	}

	/**
	 * @param value1
	 * @param value2
	 * @return the "or-ing" of the two values. If either value is invalid, the
	 *         resulting value will also be invalid
	 */
	public static byte boolOr(byte value1, byte value2) {
		return (byte) (value1 | value2);
	}

	/**
	 * @param value1
	 * @param value2
	 * @return the "and-ing" of the two values. If either value is invalid, the
	 *         resulting value will also be invalid
	 */
	public static byte boolAnd(byte value1, byte value2) {
		return (byte) ((value1 & value2) | ((value1 | value2) & INVALID_VALUE));
	}

	/**
	 * Converts a LOGICAL to a FLOAT.
	 *
	 * @param value
	 * @return the value, converted to a FLOAT
	 */
	public static float toNum(byte value) {
		if (isInvalid(value)) {
			return Num.INVALID_VALUE;
		} else if ((value & TRUE) == 0) {
			return 0.0f;
		} else {
			return 1.0f;
		}
	}

	/**
	 * @param value
	 * @return the value, converted to a DATE
	 */
	public static float toDate(byte value) {
		return toNum(value);
	}

	/**
	 * @param value
	 * @return the value, converted to TEXT, where "T" and "F" are returned for
	 *         {@code true} and {@code false} respectively
	 */
	public static String toText(byte value) {
		if (isInvalid(value)) {
			return Text.INVALID_VALUE;
		} else if ((value & TRUE) == 0) {
			return "F";
		} else {
			return "T";
		}
	}

	/**
	 * @param text
	 * @return the text, converted to a LOGICAL
	 */
	public static byte fromText(String text) {
		if (text.isEmpty()) {
			return INVALID_VALUE;
		}

		switch (text) {
		case "TRUE":
		case "T":
		case "True":
		case "true":
			return TRUE;

		case "FALSE":
		case "F":
		case "False":
		case "false":
			return FALSE;

		default:
			return INVALID_VALUE;
		}
	}
}
