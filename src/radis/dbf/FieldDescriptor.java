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

package radis.dbf;

/**
 * Field descriptor, which can be used when retrieving the contents of a field from a DBF file.
 */
public class FieldDescriptor {
	private final String name;	// lower case field name
	private final char type;
	private final int length;
	private final int numDecimals;
	private final int offset;

	/**
	 * Constructs the object.
	 * @param name
	 * @param type
	 * @param length
	 * @param numDecimals
	 * @param offset
	 */
	public FieldDescriptor(String name, char type, int length, int numDecimals, int offset) {
		super();
		this.name = name;
		this.type = type;
		this.length = length;
		this.numDecimals = numDecimals;
		this.offset = offset;
	}

	public String getName() {
		return name;
	}

	public char getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public int getNumDecimals() {
		return numDecimals;
	}

	public int getOffset() {
		return offset;
	}
}
