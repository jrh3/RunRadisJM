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

import java.nio.Buffer;
import java.util.List;
import java.util.Map;

import radis.dbf.Dbf;
import radis.dbf.FieldDescriptor;

/**
 * Loader for a field. Wraps a buffer (e., FloatBuffer), into which the values
 * of a field are loaded from an SI Pro DBF file.
 *
 * @param <T> type of buffer that this wraps
 */
public abstract class DataLoader<T extends Buffer> {
	/**
	 * Wrapped buffer.
	 */
	protected final T buf;

	/**
	 * Record size.
	 */
	protected final int recsz;

	/**
	 * Constructs the object.
	 *
	 * @param buf   buffer into which data should be loaded
	 * @param recsz record size
	 */
	public DataLoader(T buf, int recsz) {
		this.buf = buf;
		this.recsz = recsz;
	}

	/**
	 * Loads data for a field from an SI Pro DBF file.
	 *
	 * @param dbf          SI Pro DBF file
	 * @param compdef      descriptor to be used to extract the company ID (or
	 *                     industry or sector code) from the DBF record
	 * @param def          descriptor to be used to extract the field's data from
	 *                     the DBF record
	 * @param nrecords     number of records in the period the period
	 * @param sipro2recnum map from the SI Pro company ID to the relevant mmap
	 *                     record numbers, relative to "begrec"
	 */
	public void loadFieldData(Dbf dbf, FieldDescriptor compdef, FieldDescriptor def, int nrecords,
			Map<String, List<Integer>> sipro2recnum) {

		// zap all of the records
		buf.rewind();
		zap(nrecords);

		// now load the field's data from the DBF file
		buf.rewind();

		dbf.rewind();

		while (dbf.nextRecord()) {
			var compid = dbf.getField(compdef);
			var recnums = sipro2recnum.get(compid);
			if (recnums == null) {
				// this company ID doesn't exist - nothing to load
				continue;
			}

			var data = dbf.getField(def);
			for (var recnum : recnums) {
				put(recnum, data);
			}
		}
	}

	/**
	 * Zaps the records, initializing them with an "INVALID_VALUE" appropriate to
	 * the field's data type.
	 *
	 * @param nrecords number of records to zap
	 */
	public abstract void zap(int nrecords);

	/**
	 * Puts the given data into the specified record of the wrapped buffer.
	 *
	 * @param recnum number of the record into which the data should be placed
	 * @param data   data to be added to the record
	 */
	protected abstract void put(int recnum, String data);
}
