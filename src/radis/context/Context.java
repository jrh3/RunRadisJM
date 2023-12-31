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

package radis.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import radis.datadef.FieldDef;
import radis.datadef.Period;
import radis.datadef.StructFile;

/**
 * Radis DB context.
 */
public class Context {

	/**
	 * Full path to the memory map files.
	 */
	private final String dir;

	/**
	 * All periods in the DB.
	 */
	protected final List<Period> periods;

	/**
	 * Maps a long field name to its definition.
	 */
	protected final Map<String, FieldDef> long2def = new HashMap<>();

	/**
	 * Constructs the object.
	 *
	 * @param dir path to the root directory containing the radis DB
	 * @throws IOException
	 */
	public Context(String dir) throws IOException {
		this.dir = dir;

		// read the period definitions that have been previously loaded
		periods = readPeriods();

		// read the field definitions that have been previously loaded
		readFieldDefs();
	}

	/**
	 * Reads the periods from the radis DB
	 *
	 * @return list of periods that were read
	 * @throws IOException
	 */
	protected List<Period> readPeriods() throws IOException {
		var fper = new StructFile<Period>(dir + Period.FILE_NAME);
		return fper.read(Period.RECSZ, buf -> new Period(buf));
	}

	/**
	 * Reads the field definitions from the radis DB.
	 *
	 * @throws IOException
	 */
	protected void readFieldDefs() throws IOException {
		var fdef = new StructFile<FieldDef>(dir + FieldDef.FILE_NAME);
		for (FieldDef def : fdef.read(FieldDef.RECSZ, buf -> new FieldDef(buf))) {
			long2def.put(def.getLongName(), def);
		}
	}

	public String getDir() {
		return dir;
	}

	/**
	 * @return the starting record number for the next period to be loaded
	 */
	public int beginRecord() {
		return (periods.isEmpty() ? 0 : periods.get(periods.size() - 1).beginRecord());
	}

	/**
	 * @return the number of records currently in the radis DB
	 */
	public int numRecords() {
		if (periods.isEmpty()) {
			return 0;

		} else {
			Period last = periods.get(periods.size() - 1);
			return (last.beginRecord() + last.numRecords());
		}
	}

	/**
	 * @return the periods in the radis DB
	 */
	public List<Period> getPeriods() {
		return periods;
	}

	/**
	 * @return a copy of the list of field definitions in the radis DB
	 */
	public List<FieldDef> getFieldDefs() {
		return new ArrayList<>(long2def.values());
	}

	/**
	 * @param longnm long field name
	 * @return the field definition associated with the given field name, or
	 *         {@code null} if it does not exist
	 */
	public FieldDef getFieldDef(String longnm) {
		return long2def.get(longnm);
	}
}
