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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import radis.data.DataLoader;
import radis.data.loader.BoolLoader;
import radis.data.loader.DateLoader;
import radis.data.loader.NumLoader;
import radis.data.loader.TextLoader;
import radis.datadef.FieldDef;
import radis.datadef.Period;
import radis.datadef.SiProIdent;
import radis.datadef.StructFile;
import radis.dbf.Dbf;
import radis.dbf.DdInfo;
import radis.dbf.FieldDescriptor;
import radis.exception.CorruptDbException;
import radis.exception.FieldMismatchException;
import radis.exception.LoaderException;

/**
 * Context used when loading data from the SI Pro installation into the radis
 * DB.
 */
public class LoaderContext extends Context {
	// DBF file name patterns of interest
	private static final Pattern FILE_PAT1 = Pattern.compile("(.*)_([^_0-9]+)([0-9]+)");
	private static final Pattern FILE_PAT2 = Pattern.compile("(.*)([mdyq])([0-9]+)");
	private static final String MMAP_SUFFIX = ".map";

	/**
	 * List of SI Pro company ID / ticker mappings.
	 */
	private final List<SiProIdent> companies;

	/**
	 * Maps the full relative path of an mmap file to its radis field definition.
	 */
	private final Map<String, FieldDef> file2def = new HashMap<>();

	public LoaderContext(String dir) throws IOException {
		super(dir);

		// load the companies from the radis DB
		companies = new StructFile<SiProIdent>(getDir() + SiProIdent.FILE_NAME).read(SiProIdent.RECSZ, SiProIdent::new);

		// populate file2def from long2def
		for (var def : long2def.values()) {
			file2def.put(def.getFileName(), def);
		}
	}

	/**
	 * @return the number of fields in the radis DB
	 */
	public int getNumFields() {
		return file2def.size();
	}

	/**
	 * Saves the load context data to the radis DB.
	 * <p/>
	 * Note: this should be invoked AFTER records have been added to all of the
	 * files containing the contents of the different fields.
	 *
	 * @throws IOException
	 */
	public void save() throws IOException {
		// save the company data
		new StructFile<SiProIdent>(getDir() + SiProIdent.FILE_NAME).write(SiProIdent.RECSZ, companies,
				SiProIdent::write);

		// save the field definitions
		new StructFile<FieldDef>(getDir() + FieldDef.FILE_NAME).write(FieldDef.RECSZ, file2def.values(),
				FieldDef::write);

		/*
		 * save the periods
		 *
		 * Note: this should be the last thing written.
		 */
		new StructFile<Period>(getDir() + Period.FILE_NAME).write(Period.RECSZ, periods, Period::write);
	}

	/**
	 * @return the list of SI Pro company ID / ticker mappings
	 */
	public List<SiProIdent> getCompanies() {
		return companies;
	}

	/**
	 * Adds a field to the context, assigning it an mmap file name within the radis
	 * DB.
	 *
	 * @param info SI Pro Data/field Definition
	 */
	public void addField(DdInfo info) {
		var longnm = info.getLongName();

		var def = long2def.get(longnm);
		if (def != null) {
			// field already exists - ensure that it's still compatible
			validateUnchanged(info, def);
			return;
		}

		var fullnm = makeFieldFileName(info.getFileName(), info.getShortName());
		def = file2def.get(fullnm);
		if (def != null) {
			// file name already exists

			if (longnm.endsWith(" %")) {
				// may have added a trailing "%" to the field name
				var nopct = longnm.substring(0, longnm.length() - 2);

				def = long2def.get(nopct);
				if (def != null) {
					// yes, the old field name has no trailing "%"
					// ensure that it's still compatible
					System.out.println("rename field " + nopct + " TO " + longnm);
					validateUnchanged(info, def);

					def.setType(info.getType());
					def.setRecSize(info.getRecSize());
					def.setLongName(longnm);
					def.setShortName(info.getShortName());
					def.setFileName(fullnm);

					// add new long field name and definition
					long2def.put(longnm, def);

					// replace the file's field definition with the new one
					file2def.put(fullnm, def);
					return;
				}
			}

			throw new LoaderException(
					"duplicate file name " + fullnm + " for field " + longnm + " AND " + def.getLongName());
		}

		// new field - add it
		System.out.println("add field " + longnm + ": " + fullnm);

		def = new FieldDef(info, fullnm);

		long2def.put(longnm, def);
		file2def.put(fullnm, def);
	}

	/**
	 * Adds a new period to the context.
	 *
	 * @param dt       date of the period, from Date.fromText()
	 * @param nrecords number of records in the period
	 */
	public void addPeriod(float dt, int nrecords) {

		if (!periods.isEmpty() && dt <= periods.get(periods.size() - 1).getDate()) {
			throw new LoaderException("periods out of order");
		}

		var per = new Period(dt, nrecords, numRecords());

		periods.add(per);
	}

	/**
	 *
	 * Verifies that a field's definition has not changed in a way that is
	 * incompatible with the current data.
	 *
	 * @param info SI Pro Data/field Definition
	 * @param def  radis field definition
	 * @throws FieldMismatchException
	 */
	private void validateUnchanged(DdInfo info, FieldDef def) throws FieldMismatchException {

		if (def.getType() != info.getType()) {
			throw new FieldMismatchException(
					info.getLongName() + ": type changed from " + def.getType() + " to " + info.getType());
		}

		if (def.recSize() < info.getRecSize()) {
			throw new FieldMismatchException(
					info.getLongName() + ": record size changed from " + def.recSize() + " to " + info.getRecSize());
		}

		/*
		 * Note: it's OK if the new field is SHORTER than the field in radis, because
		 * that means the new data will still fit within the existing mmap file.
		 */

		if (file2def.containsKey(info.getFileName() + "/" + info.getShortName())) {
			throw new FieldMismatchException(info.getLongName() + ": already in use");
		}
	}

	/**
	 * Uses the short field name to create a multi-part file name in which to store
	 * the field's data.
	 *
	 * @param filenm         SI Pro DBF file name (without the ".dbf" extension), as
	 *                       extracted from the SI Pro data dictionary
	 * @param shortFieldName short field name
	 * @return
	 */
	private String makeFieldFileName(String filenm, String shortFieldName) {

		var mat = FILE_PAT1.matcher(shortFieldName);
		if (!mat.matches()) {
			mat = FILE_PAT2.matcher(shortFieldName);
			if (!mat.matches()) {
				return filenm + "/" + shortFieldName + MMAP_SUFFIX;
			}
		}

		var prefix = mat.group(1);
		var middle = mat.group(2);
		var suffix = mat.group(3);

		return filenm + "/" + prefix + "/" + middle + "/" + suffix + MMAP_SUFFIX;
	}

	/**
	 * Loads the data for a field from an SI Pro DBF file into a radis mmap file.
	 *
	 * @param longnm       long field name
	 * @param dbf          SI Pro DBF file
	 * @param compdef      descriptor to be used to extract the company ID (or
	 *                     industry or sector code) from the DBF record
	 * @param def          descriptor to be used to extract the field's data from
	 *                     the DBF record
	 * @param sipro2recnum map from the SI Pro company ID to the relevant mmap
	 *                     record numbers, relative to {@link #beginRecord()}
	 * @throws IOException
	 */
	public void loadFieldData(String longnm, Dbf dbf, FieldDescriptor compdef, FieldDescriptor def,
			Map<String, List<Integer>> sipro2recnum) throws IOException {

		FieldDef pdef = getFieldDef(longnm);
		var begrec = beginRecord();
		var maxrecs = numRecords() - begrec;
		var recsz = pdef.recSize();
		var filenm = getDir() + "/" + pdef.getFileName();

		// data will be loaded into this buffer
		var buf = ByteBuffer.allocate(maxrecs * recsz);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		DataLoader<?> loader;
		switch (pdef.getType()) {
		case TEXT:
			loader = new TextLoader(buf, recsz);
			break;

		case FLOAT:
			loader = new NumLoader(buf);
			break;

		case DATE:
			loader = new DateLoader(buf);
			break;

		case LOGICAL:
			loader = new BoolLoader(buf);
			break;

		default:
			throw new CorruptDbException("invalid type " + pdef.getType() + " for " + longnm);
		}

		// load the data into the buffer
		loader.loadFieldData(dbf, compdef, def, begrec, begrec + maxrecs, sipro2recnum);

		buf.rewind();

		var path = Path.of(filenm);
		Files.createDirectories(path.getParent());

		// write the buffer to the file
		try (var file = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE)) {

			file.position(begrec * recsz);
			file.write(buf);
		}
	}
}
