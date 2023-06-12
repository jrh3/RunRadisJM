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
import java.util.List;

import radis.context.LoaderContext;
import radis.datadef.Period;
import radis.types.Date;

/**
 * Deletes periods from the radis DB.
 */
public class dbdel {

	/**
	 * Deletes periods from the radis DB. Starts with the given period and deletes
	 * everything after that, inclusive.
	 *
	 * @param dtdel data of first period to be deleted
	 * @throws IOException
	 */
	public dbdel(String dtdel) throws IOException {
		var dir = System.getenv("MMAP");
		if (dir == null) {
			throw new IOException("missing environment variable MMAP");
		}

		var context = new LoaderContext(dir);

		System.out.println("deleting starting with: " + dtdel);

		List<Period> periods = context.getPeriods();
		while (!periods.isEmpty()) {
			var per = periods.remove(periods.size() - 1);
			var dtper = Date.toText(per.getDate());

			if (dtper.compareTo(dtdel) < 0) {
				// put this period back in
				periods.add(per);
				break;
			}

			System.out.println("\t" + dtper);
		}

		System.out.print("Are you sure? (y/n) ");
		var ln = new BufferedReader(new InputStreamReader(System.in)).readLine();

		if (ln.startsWith("y")) {
			System.out.println("Ok");

			// save it
			context.save();
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("arg(s): yyyyMMdd");
			System.exit(1);
		}

		try {
			new dbdel(args[0]);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
