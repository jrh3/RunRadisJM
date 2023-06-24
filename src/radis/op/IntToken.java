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

package radis.op;

/**
 * An integer token.
 */
public class IntToken extends OpItem {

	/**
	 * Value associated with the token.
	 */
	private final int value;

	/**
	 * Creates the object.
	 *
	 * @param typ token type (typically INTCON)
	 * @param val token value
	 */
	public IntToken(int typ, int val) {
		super(typ);
		value = val;
	}

	@Override
	public int intval() {
		return value;
	}

	@Override
	public float floatval() {
		return value;
	}

	/**
	 * Compares this object with another.
	 *
	 * @param p object with which to compare this
	 * @return true if they have the same token type and value, false otherwise
	 */
	@Override
	public boolean equals(Object p) {
		if (this == p) {
			return true;

		} else if (!(p instanceof IntToken)) {
			return false;

		} else {
			IntToken t = (IntToken) p;
			return (type() == t.type() && value == t.value);
		}
	}

	/**
	 * Generates hash code for this object.
	 */
	@Override
	public int hashCode() {
		return (value * 31 + super.hashCode());
	}

}
