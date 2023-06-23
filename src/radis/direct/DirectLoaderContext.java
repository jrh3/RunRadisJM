/*
	RunRadisJM: Run RadiScript screens
	Copyright (C) 2023  James Hahn

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

package radis.direct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import radis.context.LoaderContext;
import radis.datadef.Period;
import radis.datadef.SiProIdent;
import radis.dbf.Dbf;
import radis.dbf.FieldDescriptor;

/**
 * Context that loads directly from the SI Pro installation rather than the
 * radis DB.
 */
public class DirectLoaderContext extends LoaderContext {
	private Map<String, Data> long2data = new HashMap<>();

	public DirectLoaderContext(String dir) throws IOException {
		super(dir);
	}

	@Override
	protected List<SiProIdent> readCompanies() throws IOException {
		// don't read anything - just return an empty list
		return new ArrayList<>();
	}

	@Override
	public void save() throws IOException {
		// don't save anything to the DB
	}

	/**
	 * Uses lazy loading, simply recording the parameters so the field's data can be
	 * retrieved when {@link #getFieldData(String)} is called later.
	 */
	@Override
	public ByteBuffer loadFieldData(String longnm, Dbf dbf, FieldDescriptor compdef, FieldDescriptor def,
			Map<String, List<Integer>> sipro2recnum) throws IOException {

		long2data.put(longnm, new Data(dbf, compdef, def, sipro2recnum));
		return null;
	}

	/**
	 * Gets the data for the specified field from the associated DBF file.
	 *
	 * @param longnm long field name
	 * @return the field's data
	 * @throws IOException
	 */
	public ByteBuffer getFieldData(String longnm) throws IOException {
		var data = long2data.get(longnm);
		if (data == null) {
			return null;
		}

		return super.loadFieldData(longnm, data.dbf, data.compdef, data.def, data.sipro2recnum);
	}

	@Override
	protected void saveFieldData(String filenm, int recsz, int begrec, ByteBuffer buf) throws IOException {
		// don't save anything to the DB
	}

	@Override
	protected List<Period> readPeriods() throws IOException {
		// don't read anything - just return an empty list
		return new ArrayList<>();
	}

	@Override
	protected void readFieldDefs() throws IOException {
		// don't read anything
	}

	private class Data {
		private final Dbf dbf;
		private final FieldDescriptor compdef;
		private final FieldDescriptor def;
		private final Map<String, List<Integer>> sipro2recnum;

		public Data(Dbf dbf, FieldDescriptor compdef, FieldDescriptor def, Map<String, List<Integer>> sipro2recnum) {
			this.dbf = dbf;
			this.compdef = compdef;
			this.def = def;
			this.sipro2recnum = sipro2recnum;
		}
	}
}
