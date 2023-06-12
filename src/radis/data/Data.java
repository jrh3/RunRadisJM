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

package radis.data;

import radis.data.buffer.Buffered;
import radis.data.constant.ConstData;

/**
 * Data used by statement and expression evaluators.
 */
public interface Data {

	/**
	 * Gets the data type (e.g., tok.NUM, tok.STR).
	 *
	 * @return the data type
	 */
	int getType();

	/**
	 * @return {@code true} if the data is a constant, meaning that every record has
	 *         the same value
	 */
	boolean isConst();

	/**
	 * @throws UnsupportedOperationException if the data is not a constant
	 * @return "this", if the data is a constant
	 */
	ConstData toConst();

	/**
	 * @return "this", if the data is buffered, a new data buffer where every record
	 *         has the same value, if the data is a constant
	 */
	Buffered toBuf();

	/**
	 * Gets the next item and determines if it's valid.
	 *
	 * @return {@code true} if the next item is valid
	 */
	boolean isValid();

	/**
	 * @return a copy of this data
	 */
	Data duplicate();
}
