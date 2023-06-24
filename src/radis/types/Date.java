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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Manipulators for DATA data types. DATE values are stored as "float" values,
 * representing the number of days since 1-1-1970.
 */
public class Date {
	public static final float INVALID_VALUE = Float.NaN;

	// possible date formats
	static final SimpleDateFormat DATE_FMT1 = new SimpleDateFormat("yyyyMMdd");
	static final SimpleDateFormat DATE_FMT2 = new SimpleDateFormat("MM/dd/yy");

	static {
		DATE_FMT1.setTimeZone(TimeZone.getTimeZone("GMT"));
		DATE_FMT2.setTimeZone(TimeZone.getTimeZone("GMT"));

		DATE_FMT1.setLenient(false);
		DATE_FMT2.setLenient(false);
	}

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
	 * Converts a DATE to a FLOAT.
	 *
	 * @param value
	 * @return the value, converted to a FLOAT (which is also a "float")
	 */
	public static float toNum(float value) {
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

		return DATE_FMT1.format(new java.util.Date((long) (value * (24.0 * 60.0 * 60.0 * 1000.0))));
	}

	/**
	 * Convert a date string of the form YYYYMMDD to a unix time, in days
	 *
	 * @return the date
	 */
	public static float fromText(String dt) {
		if (dt.isEmpty()) {
			return INVALID_VALUE;
		}

		java.util.Date d;
		try {
			d = DATE_FMT1.parse(dt);
		} catch (ParseException ex) {
			try {
				d = DATE_FMT2.parse(dt);
			} catch (ParseException ex2) {
				return INVALID_VALUE;
			}
		}

		return (float) (d.getTime() / (24.0 * 60.0 * 60.0 * 1000.0));
	}
}
