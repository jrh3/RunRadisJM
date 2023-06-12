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
 * An OP (i.e., operation) item.
 */
public class OpItem {
	/**
	 * Item returned when a child is requested, but the OpItem has no children (or
	 * the child index is too large).
	 */
	public static final OpItem None = new OpItem(1);

	/**
	 * Item's token type.
	 */
	protected int mType;

	/**
	 * Create an object - this is only for use by the parser.
	 *
	 * @param type token type
	 */
	public OpItem() {
		mType = -1;
	}

	/**
	 * Create an object.
	 *
	 * @param type token type
	 */
	public OpItem(int type) {
		mType = type;
	}

	/**
	 * @return the item's type
	 */
	public final int type() {
		return mType;
	}

	/**
	 * @return the first child, or None if there are no children
	 */
	public final OpItem arg() {
		return arg(0);
	}

	/**
	 * @return the left (first) child, or None if there are no children
	 */
	public final OpItem left() {
		return arg(0);
	}

	/**
	 * @return the right (second) child, or None if there are no children
	 */
	public final OpItem right() {
		return arg(1);
	}

	/**
	 * Returns the value stored within the item, provided it's of the appropriate
	 * type.
	 *
	 * @throws IllegalArgumentException if the item cannot provide a value of the
	 *                                  given type
	 * @return a value of the appropriate type
	 */
	public boolean boolval() {
		throw new IllegalArgumentException("not a boolean OpItem");
	}

	public int intval() {
		throw new IllegalArgumentException("not an integer OpItem");
	}

	public float floatval() {
		throw new IllegalArgumentException("not a float OpItem");
	}

	public String strval() {
		throw new IllegalArgumentException("not a string OpItem");
	}

	/**
	 * Gets a particular child item.
	 *
	 * @return the desired child, if available, or None otherwise (even if this is
	 *         not an "Op" object).
	 * @param idx child index, where the first child is idx=0
	 */
	public OpItem arg(int idx) {
		return None;
	}

	/**
	 * Replaces the right (second) child with a new value.
	 *
	 * @param p new child
	 * @throws IllegalArgumentException if the item has no children
	 */
	public void replaceRight(OpItem p) {
		throw new IllegalArgumentException("OpItem has no children");
	}

	/**
	 * Walk the item, and any children, displaying them as we go.
	 */
	public void walk() {
		System.out.print(" " + mType);
	}

	/**
	 * Generates hash code for this object.
	 */
	@Override
	public int hashCode() {
		return mType;
	}

}
