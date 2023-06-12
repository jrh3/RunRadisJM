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

import java.io.IOException;

import radis.context.Context;
import radis.datadef.FieldDef;

/**
 * Prints the long names of all of the fields found in the radis DB.
 */
public class fields {

	/**
	 * Loads and prints the field names.
	 *
	 * @throws IOException
	 */
	public fields() throws IOException {
		var dir = System.getenv("MMAP");
		if (dir == null) {
			throw new IOException("missing environment variable MMAP");
		}

		var context = new Context(dir);
		var fieldNames = context.getFieldDefs().stream().map(FieldDef::getLongName).sorted().toList();

		for (var fieldnm : fieldNames) {
			System.out.println(fieldnm);
		}
	}

	public static void main(String[] args) {
		try {
			new fields();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
