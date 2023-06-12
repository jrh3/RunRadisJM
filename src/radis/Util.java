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

package radis;

import java.util.List;

/**
 * Utility functions and variables
 */
public final class Util {

	/**
	 * Value returned when an invalid parameter is passed to a string function.
	 */
	public static final String BAD_VALUE = "#VALUE!";

	/**
	 * Name prefix for industry variables.
	 */
	public static final String IND_NAME_PREFIX = normalizeName("SI IND ");

	/**
	 * Name prefix for sector variables.
	 */
	public static final String SEC_NAME_PREFIX = normalizeName("SI SEC ");

	/**
	 * "True" string.
	 */
	public static final String TRUE_STR = "TRUE";

	/**
	 * "False" string.
	 */
	public static final String FALSE_STR = "FALSE";

	/**
	 * Ticker variable name.
	 */
	public static final String TKR_VAR_NM = varName("SI Ticker");

	/**
	 * "Rank" variable name.
	 */
	public static final String RANK_VAR_NM = varName("Rank");

	/**
	 * "Tied Rank" variable name.
	 */
	public static final String TIED_RANK_VAR_NM = varName("Tied Rank");

	/**
	 * "Percent Tied Rank" variable name.
	 */
	public static final String PERCENT_TIED_RANK_VAR_NM = varName("Tied Rank %");

	/**
	 * "NOW" variable name.
	 */
	public static final String NOW_VAR_NM = varName("SI Weekly Data Date");

	/**
	 * Prevents the object from being constructed.
	 */
	private Util() {
	}

	/**
	 * Converts return to a percentage.
	 *
	 * @return the return, as a percentage
	 */
	public static double asPercent(double ret) {
		return (ret - 1.0) * 100.0;
	}

	/**
	 * Strips any CR from the end of the line, if there is one.
	 *
	 * @return the line, with any trailing CR removed
	 */
	public static String chomp(String ln) {
		if (ln.endsWith("\r")) {
			return ln.substring(0, ln.length() - 1);
		} else {
			return ln;
		}
	}

	/**
	 * Normalizes variable names so they're more likely to match. Maps them to lower
	 * case and remove dots and extra blanks.
	 *
	 * @param varnm variable name to be normalized
	 * @return the normalized name
	 */
	public static String normalizeName(String varnm) {
		return varnm.replaceAll("[.]", "").replaceAll(" [ ]+", " ").toLowerCase();
	}

	/**
	 * Converts a string to a variable name.
	 *
	 * @param varnm unadorned variable name text
	 * @return fully adorned and normalized variable name
	 */
	public static String varName(String varnm) {
		return normalizeName(varnm);
	}

	/**
	 * @param vec
	 * @return the median value of a vector of numbers
	 */
	public static float median(List<Float> vec) {
		if (vec.isEmpty()) {
			return 0;
		}

		vec.sort(Float::compare);

		int mid = vec.size() / 2;

		if (vec.size() % 2 == 1) {
			// odd number of elements - return the middle one
			return vec.get(mid);

		} else {
			// even number of elements - return the average of the middle two
			return (vec.get(mid - 1) + vec.get(mid)) / 2;
		}
	};
}
