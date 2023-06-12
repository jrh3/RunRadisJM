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
import java.util.List;

import radis.context.LoaderContext;
import radis.datadef.Period;
import radis.dbf.SiProInstallation;
import radis.types.Date;

/**
 * Loads the current SI Pro installation into the radis DB.
 */
public class dbload {

	/**
	 * Loads the SI Pro data into the radis DB.
	 *
	 * @throws IOException
	 */
	public dbload() throws IOException {
		var dir = System.getenv("MMAP");
		if (dir == null) {
			throw new IOException("missing environment variable MMAP");
		}

		var context = new LoaderContext(dir);

		System.out.println(context.getPeriods().size() + " periods " + context.getNumFields() + " fields");

		var siprodir = System.getenv("SI_PRO");
		if (siprodir == null) {
			throw new IOException("missing environment variable SI_PRO");
		}

		SiProInstallation sipro = new SiProInstallation(siprodir);

		// extract the date of the data in the SI Pro installation
		var newdt = sipro.getDate();

		List<Period> periods = context.getPeriods();
		if (!periods.isEmpty() && newdt <= periods.get(periods.size() - 1).getDate()) {
			System.out.println("already loaded " + Date.toText(newdt));
			return;
		}

		sipro.loadData(context, newdt);

		context.save();

	}

	public static void main(String[] args) {
		try {
			new dbload();
			System.out.println("Ok");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
