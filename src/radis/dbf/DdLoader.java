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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import radis.Util;
import radis.datadef.FieldDef.Type;
import radis.datadef.SiProIdent;
import radis.exception.LoaderException;

/**
 * Loader for Data Definitions.
 */
public class DdLoader {
	// field names
	public static final String TICKER = "ticker";
	public static final String COMPANY_ID = "company_id";

	// file names containing industry, sector, and "universal" data
	public static final String IND_FILENM = "si_mgav2";
	public static final String SEC_FILENM = "si_mgavg";
	public static final String UNIV_FILENM = "si_avg";

	private final Dbf dbf;

	// descriptors used to extract various fields from the data dictionary
	private final FieldDescriptor ffile;
	private final FieldDescriptor fname;
	private final FieldDescriptor flen;
	private final FieldDescriptor fdesc;
	private final FieldDescriptor ftype;
	private final FieldDescriptor fsitype;

	/**
	 * Maps a long field name to its maximum length.
	 */
	private final Map<String, Integer> textFields = new HashMap<>();

	/**
	 * Identifies fields that contain numeric data.
	 */
	private final Set<String> numFields = new HashSet<>();

	/**
	 * Names of files to be excluded when searching the data dictionary.
	 */
	private final Set<String> excludeFile;

	/**
	 * Names of files to be included, if not previously excluded, when searching the
	 * data dictionary.
	 */
	private final Set<String> includeFile;

	/**
	 * Constructs the object.
	 *
	 * @param dbf
	 */
	public DdLoader(Dbf dbf) {
		this.dbf = dbf;

		this.ffile = dbf.getField("FILE");
		this.fname = dbf.getField("FIELD_NAME");
		this.flen = dbf.getField("FIELD_LEN");
		this.fdesc = dbf.getField("FIELD_DESC");
		this.ftype = dbf.getField("FIELD_TYPE");
		this.fsitype = dbf.getField("SI_TYPE");

		// long names of text fields to retain; all others are discarded
		textFields.put(Util.TKR_VAR_NM, SiProIdent.TKR_LEN);
		textFields.put("si exchange", 1);
		textFields.put("si company name", 36);
		textFields.put("si country", 24);
		textFields.put("si standard and poor stock", 3);

		numFields.add("si sector");
		numFields.add("si industry");

		// names of files to be skipped
		excludeFile = new HashSet<>(Set.of(UNIV_FILENM, "datadict", "setup", "si_udf", "si_utype"));

		// names of files to include, if not excluded
		includeFile = new HashSet<>(Set.of(IND_FILENM, SEC_FILENM));

		// also include those that have a company_id
		dbf.rewind();
		while (dbf.nextRecord()) {
			String name = dbf.getField(fname).toLowerCase();
			if (name.equals(COMPANY_ID)) {
				String filenm = dbf.getField(ffile).toLowerCase();
				includeFile.add(filenm);
			}
		}

		dbf.rewind();
	}

	/**
	 * Loads the data definition from the current DBF record.
	 *
	 * @return the new data definition, or {@code null} if the field associated with
	 *         the current DBF record should be discarded
	 */
	public DdInfo loadInfo() {
		DdInfo info = new DdInfo();

		info.setDbfType(dbf.getField(ftype));
		info.setSiType(dbf.getField(fsitype));

		if (!(detmFileName(info) && detmLongName(info) && detmType(info) && detmRecSize(info))) {
			return null;
		}

		var name = dbf.getField(fname);
		if (name.isEmpty() || name.charAt(0) == '_') {
			// "_" indicates that the record was deleted
			return null;
		}

		info.setShortName(Util.normalizeName(name));

		return info;
	}

	/**
	 * Determines the name of the DBF file.
	 *
	 * @param info data definition to be updated with the radis type
	 * @return {@code true} if the type was successfully determined
	 */
	private boolean detmFileName(DdInfo info) {
		var filenm = dbf.getField(ffile).toLowerCase();
		if (filenm.startsWith("usr")) {
			return false;
		}

		// skip those that are in the "exclude" list
		if (excludeFile.contains(filenm)) {
			return false;
		}

		// skip those that are not in the "include" list
		if (!includeFile.contains(filenm)) {
			return false;
		}

		info.setFileName(filenm);

		return true;
	}

	/**
	 * Determines the long field name.
	 *
	 * @param info data definition to be updated with the long field name
	 * @return {@code true} if the long field name was successfully determined
	 */
	private boolean detmLongName(DdInfo info) {
		var longnm = dbf.getField(fdesc);
		if (longnm.isEmpty()) {
			return false;
		}

		longnm = longnm.toLowerCase();

		if (IND_FILENM.equals(info.getFileName())) {
			if (longnm.startsWith("ind. ")) {
				// remove the "."
				longnm = longnm.substring(0, 3) + longnm.substring(4);
			}

		} else if (SEC_FILENM.equals(info.getFileName())) {
			if (longnm.startsWith("sec. ")) {
				// remove the "."
				longnm = longnm.substring(0, 3) + longnm.substring(4);
			}
		}

		info.setLongName(Util.normalizeName("si " + longnm));

		return true;
	}

	/**
	 * Determines the radis data type to be used when the field is stored in the
	 * mmap file.
	 *
	 * @param info data definition to be updated with the radis type
	 * @return {@code true} if the type was successfully determined
	 */
	private boolean detmType(DdInfo info) {

		if (numFields.contains(info.getLongName())) {
			// this field is known to be a numeric field
			info.setType(Type.FLOAT);

		} else if ("N".equals(info.getDbfType()) || "N".equals(info.getSiType())) {
			info.setType(Type.FLOAT);

		} else if ("D".equals(info.getDbfType()) || "D".equals(info.getSiType())) {
			info.setType(Type.DATE);

		} else if ("L".equals(info.getDbfType()) || "L".equals(info.getSiType())) {
			info.setType(Type.LOGICAL);

		} else {
			info.setType(Type.TEXT);
		}

		return true;
	}

	/**
	 * Determines the record size of the data when it's stored in the mmap file.
	 *
	 * @param info data definition to be updated with the record size
	 * @return {@code true} if the size was successfully determined
	 */
	private boolean detmRecSize(DdInfo info) {
		switch (info.getType()) {
		case LOGICAL:
			info.setRecSize(1);
			return true;

		case TEXT:
			break;

		default:
			info.setRecSize(4);
			return true;
		}

		// only keep text fields if found in the company info file
		if (!"si_ci".equals(info.getFileName())) {
			return false;
		}

		// only keep select text fields
		var num = textFields.get(info.getLongName());
		if (num == null) {
			return false;
		}

		int recsz = Integer.parseInt(dbf.getField(flen));
		if (recsz > num) {
			throw new LoaderException(info.getLongName() + " size changed to " + recsz);
		}

		info.setRecSize(num + 1);

		return true;
	}
}
