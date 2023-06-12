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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import radis.datadef.FieldDef;
import radis.datadef.Period;
import radis.datadef.SiProIdent;
import radis.datadef.StructFile;

/**
 * Initializes a new the radis DB.
 */
public class dbinit {

	/**
	 * Initializes the DB, truncating pre-existing files.
	 *
	 * @throws IOException
	 */
	public dbinit() throws IOException {
		var dir = System.getenv("MMAP");
		if (dir == null) {
			throw new IOException("missing environment variable MMAP");
		}

		System.out.println();
		System.out.println("*** re-initializing the entire DB: " + dir + " ***");
		System.out.println();
		System.out.print("Are you sure? (y/n) ");
		var ln = new BufferedReader(new InputStreamReader(System.in)).readLine();

		if (!ln.startsWith("y")) {
			return;
		}

		Files.createDirectories(Path.of(dir));

		new StructFile<Period>(dir + Period.FILE_NAME).write(Period.RECSZ, List.of(), Period::write);
		new StructFile<FieldDef>(dir + FieldDef.FILE_NAME).write(FieldDef.RECSZ, List.of(), FieldDef::write);
		new StructFile<SiProIdent>(dir + SiProIdent.FILE_NAME).write(SiProIdent.RECSZ, List.of(), SiProIdent::write);
		System.out.println("done");
	}

	public static void main(String[] args) {
		try {
			new dbinit();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
