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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import radis.Util;
import radis.context.LoaderContext;
import radis.data.buffer.RadisIdData;
import radis.datadef.FieldDef;
import radis.datadef.SiProIdent;
import radis.exception.CorruptDbException;
import radis.exception.FieldNotFoundException;
import radis.exception.InternalException;
import radis.types.Date;

/**
 * Information about an SI Pro DB.
 */
public class SiProInstallation {

	/**
	 * Name of the field containing the equity's industry code.
	 */
	private static final String INDUSTRY_CODE = "indcode";

	/**
	 * Name of the field containing the equity's sector code. Yes, it's "indcode".
	 */
	private static final String SECTOR_CODE = "indcode";

	/**
	 * Directory containing the SI Pro installation.
	 */
	private final String installDir;

	/**
	 * Maps an SI Pro company ID to its corresponding record number in the mmap
	 * files, relative to the first record within the period being loaded.
	 */
	private final Map<String, Integer> compid2recnum = new HashMap<>();

	/**
	 * Maps an SI Pro company ID to a list of corresponding record numbers in the
	 * mmap files. The list only contains one record, which is the record obtained
	 * from {@link #compid2recnum}. The records are all relative to the first record
	 * within the period being loaded. only contains one record number.
	 */
	private final Map<String, List<Integer>> compid2recs = new HashMap<>();

	/**
	 * Maps an SI Pro industry code to a list of mmap records having that industry
	 * code. The records are all relative to the first record within the period
	 * being loaded.
	 */
	private final Map<String, List<Integer>> ind2recs = new HashMap<>();

	/**
	 * Maps an SI Pro sector code to a list of mmap records having that sector code.
	 * The records are all relative to the first record within the period being
	 * loaded.
	 */
	private final Map<String, List<Integer>> sec2recs = new HashMap<>();

	/**
	 * Constructs the object.
	 *
	 * @param installDir directory containing the SI Pro installation
	 */
	public SiProInstallation(String installDir) {
		this.installDir = installDir + "/Professional";
	}

	/**
	 * Reads the date from the setup DBF file of the SI Pro installation.
	 *
	 * @return the date of the period associated with the SI Pro installation
	 * @throws IOException
	 */
	public float getDate() throws IOException {
		String setup = findFileName("setup.dbf");

		Dbf dbf = new Dbf(setup);
		if (!dbf.nextRecord()) {
			throw new IOException("cannot read date from " + setup);
		}

		// get an accessor for the date field
		FieldDescriptor fd;
		try {
			fd = dbf.getField("MONTHDATE");
		} catch (FieldNotFoundException ex) {
			try {
				fd = dbf.getField("MONTH_DATE");
			} catch (FieldNotFoundException ex2) {
				throw new CorruptDbException("no date in sipro database");
			}
		}

		String sdate = dbf.getField(fd);

		System.out.println("date=" + sdate);

		var fdate = Date.fromText(sdate);
		if (Date.isInvalid(fdate)) {
			throw new CorruptDbException("invalid date in sipro database: " + sdate);
		}

		return fdate;
	}

	/**
	 * Loads the data from the SI Pro installation into the radis DB.
	 *
	 * @param ctx
	 * @param perdt date associated with the period being loaded
	 * @throws IOException
	 */
	public void loadData(LoaderContext ctx, float perdt) throws IOException {
		/*
		 * identify fields that already exist within the radis DB
		 *
		 * Note: must do this BEFORE calling loadDd() as loadDd() adds fields to the
		 * context.
		 */
		List<String> existingFields = ctx.getFieldDefs().stream().map(FieldDef::getLongName).toList();

		// load the data dictionary for the SI Pro DB
		Map<String, DdField> long2dd = loadDd(ctx);

		Map<String, List<String>> file2long = reverseMap(long2dd);

		loadCompanyMap(ctx);

		ctx.addPeriod(perdt, compid2recnum.size());

		// add empty records for those fields that no longer exist
		Set<String> deletedFields = new HashSet<>(existingFields);
		deletedFields.removeAll(long2dd.keySet());

		for (var longnm : deletedFields) {
			ctx.zapOldFields(longnm);
		}

		// add empty records for prior periods of new fields
		Set<String> newFields = new HashSet<>(long2dd.keySet());
		newFields.removeAll(existingFields);

		for (var longnm : newFields) {
			ctx.zapNewFields(longnm);
		}

		// now load the data from the files found in the two data directories
		loadData2(ctx, file2long, long2dd, findFileName("dbfs"));
		loadData2(ctx, file2long, long2dd, findFileName("static"));
	}

	/**
	 * Loads the data definition of each field from the SI Pro data dictionary.
	 *
	 * @param ctx
	 * @return a mapping of long field name to its data definition
	 * @throws IOException
	 */
	private Map<String, DdField> loadDd(LoaderContext ctx) throws IOException {

		String dddir = findFileName("datadict");
		String dd = findFileName(dddir, "datadict.dbf");

		Dbf dbf = new Dbf(dd);
		DdLoader loader = new DdLoader(dbf);

		// map long names to (short) file names
		Map<String, DdField> long2dd = new HashMap<>();

		while (dbf.nextRecord()) {
			DdInfo info = loader.loadInfo();
			if (info == null) {
				continue;
			}

			ctx.addField(info);

			long2dd.put(info.getLongName(), info);
		}

		return long2dd;
	}

	/**
	 * Reverses the data definition map.
	 *
	 * @param long2dd maps a long field name to its data definition
	 * @return a map from the name of an SI Pro DBF file to all of the fields that
	 *         it contains
	 */
	private Map<String, List<String>> reverseMap(Map<String, DdField> long2dd) {

		Map<String, List<String>> file2long = new HashMap<>();

		for (var ent : long2dd.entrySet()) {
			file2long.computeIfAbsent(ent.getValue().getFileName(), key -> new ArrayList<>()).add(ent.getKey());
		}

		return file2long;
	}

	/**
	 * Loads the company mappings, merging the radis information with the new
	 * period's information.
	 *
	 * @param ctx loader context whose company data is to be updated
	 * @throws IOException
	 */
	private void loadCompanyMap(LoaderContext ctx) throws IOException {

		CompanyDbf dbf = new CompanyDbf(findFileName(findFileName("static"), "si_ci.dbf"));

		// update the SI Pro Identity vector with the companies in the company DBF file
		remapCompanies(ctx, dbf);

		// add new companies from the company DBF file to the SI Pro Identity vector
		addNewCompanies(ctx, dbf);

		// map the SI Pro company IDs to radis company IDs
		Map<String, Integer> sipro2radis = mapContextToRadis(ctx);

		mapDbfToPeriodRecord(dbf);

		// build maps of IDs to list of records
		buildListMap(); // populates compid2recs
		buildMap(dbf, ind2recs, CompanyDbf::getIndustry);
		buildMap(dbf, sec2recs, CompanyDbf::getSector);

		// save the radis company IDs associated with each record of the period
		saveRadisIds(ctx, sipro2radis);
	}

	/**
	 * Updates the SI Pro Identity vector. First tries updating tickers based on the
	 * company IDs, from the company DBF file. If there aren't enough matches, then
	 * that likely means that SI Pro has generated new company IDs (as happened in
	 * July 2011). In that case, this method does the reverse and updates the
	 * company IDs based on the tickers.
	 *
	 * @param ctx context whose vector is to be updated
	 * @param dbf
	 */
	private void remapCompanies(LoaderContext ctx, CompanyDbf dbf) {
		final List<SiProIdent> vec = ctx.getCompanies();

		/*
		 * index known companies by both company ID and ticker
		 *
		 * Note: later items override earlier items, as they were added more recently
		 */
		var comp2ident = new HashMap<String, SiProIdent>();
		var tkr2ident = new HashMap<String, SiProIdent>();
		for (var sip : vec) {
			comp2ident.put(sip.getCompId(), sip);
			tkr2ident.put(sip.getTicker(), sip);
		}

		var fcompid = dbf.getFcompid();
		var ftkr = dbf.getFticker();

		/*
		 * Try to map company ID to ticker
		 */

		var nmatches = 0;
		dbf.rewind();
		while (dbf.nextRecord()) {
			var compid = dbf.getField(fcompid);
			var ident = comp2ident.remove(compid);

			if (ident != null) {
				++nmatches;
				var newtkr = dbf.getField(ftkr);
				var oldtkr = ident.getTicker();

				ident.setTicker(newtkr);

				// remove both old and new ticker
				tkr2ident.remove(oldtkr);
				tkr2ident.remove(newtkr);
			}
		}

		if (nmatches > 2000 || vec.isEmpty()) {
			// found enough matches, thus company IDs are probably still valid
			return;
		}

		/*
		 * Not enough matches, so now map ticker to company ID
		 */

		System.out.print("company IDs don't match - use tickers instead? ");
		try (var scanner = new Scanner(System.in)) {
			String answer = scanner.next();
			if (!answer.toLowerCase().startsWith("y")) {
				System.exit(0);
			}
		}

		dbf.rewind();
		while (dbf.nextRecord()) {
			var newtkr = dbf.getField(ftkr);
			var ident = tkr2ident.remove(newtkr);

			if (ident != null) {
				var newcomp = dbf.getField(fcompid);
				ident.setCompId(newcomp);
			}
		}
	}

	/**
	 * Adds new companies, from the company DBF file, to the context.
	 *
	 * @param ctx context into which to add the companies
	 * @param dbf
	 */
	private void addNewCompanies(LoaderContext ctx, CompanyDbf dbf) {
		final List<SiProIdent> vec = ctx.getCompanies();

		var compids = vec.stream().map(SiProIdent::getCompId).collect(Collectors.toSet());

		var fcompid = dbf.getFcompid();
		var ftkr = dbf.getFticker();

		dbf.rewind();
		while (dbf.nextRecord()) {

			var compid = dbf.getField(fcompid);

			/*
			 * If it's a duplicate company ID, then nothing more to do, as already updated
			 * it via remapCompanies(). On the other hand, if it's a duplicate ticker, we
			 * add it anyway, as that implies that the ticker is now associated with a
			 * different company than it was before. In that case, the new entry in the
			 * vector will then supersede any previous entries.
			 */
			if (!compids.contains(compid)) {
				var tkr = dbf.getField(ftkr);
				vec.add(new SiProIdent(tkr, compid));
			}
		}
	}

	/**
	 * @param ctx
	 * @return a map from SI Pro company ID to radis company ID
	 */
	private Map<String, Integer> mapContextToRadis(LoaderContext ctx) {
		Map<String, Integer> sipro2radis = new HashMap<>();

		/*
		 * radis company ID equals the record number within the company dat file.
		 *
		 * Note: we don't just use sipro2radis.size() (just in case there's a bug and a
		 * company ID appears more than once).
		 */
		int recnum = 0;

		for (var entry : ctx.getCompanies()) {
			sipro2radis.put(entry.getCompId(), recnum++);
		}

		return sipro2radis;
	}

	/**
	 * Assigns a record number, relative to the start of the period, for each
	 * company in the company DBF file. Updates {@link #compid2recnum}.
	 *
	 * @param dbf company DBF file
	 */
	private void mapDbfToPeriodRecord(CompanyDbf dbf) {
		dbf.rewind();
		while (dbf.nextRecord()) {
			var compid = dbf.getCompanyId();
			compid2recnum.putIfAbsent(compid, compid2recnum.size());
		}
	}

	/**
	 * Populates {@link #compid2recs} from {@link #compid2recnum}.
	 */
	private void buildListMap() {
		for (var entry : compid2recnum.entrySet()) {
			compid2recs.computeIfAbsent(entry.getKey(), key -> new ArrayList<>()).add(entry.getValue());
		}
	}

	/**
	 * Adds a mapping from the keys, found within the company DBF, to the relative
	 * mmap record numbers of the records having that key
	 *
	 * @param dbf
	 * @param key2recs
	 * @param getKey   function to get the key field (e.g., industry code) from the
	 *                 company record
	 */
	private void buildMap(CompanyDbf dbf, Map<String, List<Integer>> key2recs, Function<CompanyDbf, String> getKey) {

		// build map from key to company id
		Map<String, List<String>> key2compid = buildKey2CompanyMap(dbf, getKey);

		// convert key2compid to key2recs
		for (var entry : key2compid.entrySet()) {
			var key = entry.getKey();
			var compidlst = entry.getValue();

			for (var compid : compidlst) {
				var recnum = compid2recnum.get(compid);
				if (recnum == null) {
					throw new InternalException("cannot find company id " + compid);
				}

				key2recs.computeIfAbsent(key, key2 -> new ArrayList<>()).add(recnum);
			}
		}
	}

	/**
	 * @param dbf
	 * @param getKey function to retrieve the key field from the company DBF
	 * @return a map from the keys, found within the company DBF, to the company IDs
	 *         having that key
	 */
	private Map<String, List<String>> buildKey2CompanyMap(CompanyDbf dbf, Function<CompanyDbf, String> getKey) {

		Map<String, List<String>> key2compid = new HashMap<>();

		dbf.rewind();

		while (dbf.nextRecord()) {
			var key = getKey.apply(dbf);
			var compid = dbf.getCompanyId();
			key2compid.computeIfAbsent(key, key2 -> new ArrayList<>()).add(compid);
		}

		return key2compid;
	}

	/**
	 * Stores the radis company IDs in the radis-id mapping table. See
	 * {@link RadisIdData}.
	 *
	 * @param ctx
	 * @param sipro2radis mapping from SI Pro company ID to radis company ID
	 * @throws IOException
	 */
	protected void saveRadisIds(LoaderContext ctx, Map<String, Integer> sipro2radis) throws IOException {

		var begrec = ctx.numRecords();
		var maxrecs = begrec + compid2recnum.size();
		var buf = ByteBuffer.allocate(compid2recnum.size() * RadisIdData.RECSZ);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		var ibuf = buf.asIntBuffer();

		// initialize the buffer with -1
		ibuf.rewind();
		for (int x = begrec; x < maxrecs; ++x) {
			ibuf.put(-1);
		}

		// store the radis company ID associated with each record within the new period
		for (var entry : compid2recnum.entrySet()) {
			var compid = entry.getKey();
			int recnum = entry.getValue();
			ibuf.put(recnum, sipro2radis.get(compid));
		}

		saveRadisIdBuf(ctx, buf);
	}

	/**
	 * Saves a buffer to the radis ID file.
	 *
	 * @param ctx
	 * @param buf buffer whose content is to be saved
	 * @throws IOException
	 */
	private void saveRadisIdBuf(LoaderContext ctx, ByteBuffer buf) throws IOException {
		buf.rewind();

		try (var file = FileChannel.open(Path.of(ctx.getDir() + RadisIdData.FILENM), StandardOpenOption.READ,
				StandardOpenOption.WRITE)) {

			// append the buffer to the end of the file
			file.position(ctx.numRecords() * RadisIdData.RECSZ);
			file.write(buf);
		}
	}

	/**
	 * Loads the data from all of the relevant SI Pro DBF files found in the given
	 * directory.
	 *
	 * @param ctx
	 * @param file2long maps an SI Pro DBF file name to a list of the long field
	 *                  names that it contains
	 * @param long2dd   maps a long field name to its data definition. Items are
	 *                  removed from here as they're processed
	 * @param dirName   directory whose DBF files are to be loaded
	 * @throws IOException
	 */
	private void loadData2(LoaderContext ctx, Map<String, List<String>> file2long, Map<String, DdField> long2dd,
			String dirName) throws IOException {

		try (var fileStream = Files.newDirectoryStream(Path.of(dirName), Files::isRegularFile)) {
			for (var fileName : fileStream) {
				String name = fileName.getFileName().toString();

				String lcname = name.toLowerCase();
				if (!lcname.endsWith(".dbf")) {
					continue;
				}

				// make file name without dbf
				String filenm = lcname.substring(0, lcname.length() - 4);

				if (!file2long.containsKey(filenm)) {
					continue;
				}

				List<String> longNames = file2long.get(filenm);

				String path = dirName + "/" + name;

				if (Util.printLoaderInfo) {
					System.out.println(path + ":");
				}

				Dbf dbf = new Dbf(path);

				AtomicReference<FieldDescriptor> refkey = new AtomicReference<>();

				Map<String, List<Integer>> key2recs = selectKey(dbf, filenm, refkey);
				FieldDescriptor fkey = refkey.get();

				// load each field this file contains
				for (var longnm : longNames) {
					var dd = long2dd.get(longnm);
					if (dd == null) {
						continue;
					}

					loadField(ctx, dbf, longnm, dd.getShortName(), fkey, key2recs);

					// indicate that it has been processed, as it may appear in more than one file
					long2dd.remove(longnm);
				}
			}
		}
	}

	/**
	 * Selects the key to use to map an SI Pro record to a relative mmap record.
	 *
	 * @param dbf    SI Pro DBF file
	 * @param filenm name of the DBF file
	 * @param fkey   populated with the field descriptor for extracting the key from
	 *               the SI Pro record
	 * @return a mapping from a key's value to the list of relevant mmap records
	 */
	private Map<String, List<Integer>> selectKey(Dbf dbf, String filenm, AtomicReference<FieldDescriptor> fkey) {

		if (DdLoader.IND_FILENM.equals(filenm)) {
			fkey.set(dbf.getField(INDUSTRY_CODE));
			return ind2recs;

		} else if (DdLoader.SEC_FILENM.equals(filenm)) {
			fkey.set(dbf.getField(SECTOR_CODE));
			return sec2recs;

		} else {
			// regular file (i.e., neither industry nor sector)

			if (!dbf.hasField(DdLoader.COMPANY_ID)) {
				throw new CorruptDbException("table is missing company id field: " + filenm);
			}

			fkey.set(dbf.getField(DdLoader.COMPANY_ID));
			return compid2recs;
		}
	}

	/**
	 * Loads the data, for a single field, from an SI Pro DBF file into a mmap file.
	 *
	 * @param ctx
	 * @param dbf      DBF file from which to read the field's values
	 * @param longnm   long field name
	 * @param shortnm  short field name
	 * @param fkey     descriptor used to extract the record's key (i.e., company
	 *                 ID) from the DBF file
	 * @param key2recs maps a record key, extracted from the DBF file, to the record
	 *                 number in the mmap file, relative to the start of the period
	 * @throws IOException
	 */
	private void loadField(LoaderContext ctx, Dbf dbf, String longnm, String shortnm, FieldDescriptor fkey,
			Map<String, List<Integer>> key2recs) throws IOException {

		FieldDescriptor field = dbf.getField(shortnm);
		ctx.loadFieldData(longnm, dbf, fkey, field, key2recs);
	}

	/**
	 * Finds a file of the given name within the SI Pro installation, in a
	 * case-insensitive manner.
	 *
	 * @param desiredName
	 * @return actual name of the file, as found in the directory
	 * @throws IOException
	 */
	public String findFileName(String desiredName) throws IOException {
		return findFileName(installDir, desiredName);
	}

	/**
	 * Finds a file, of the desired name, within the specified directory, in a
	 * case-insensitive manner.
	 *
	 * @param dirName     name of the directory containing the desired file
	 * @param desiredName name of the desired file
	 * @return the actual, full name of the file
	 * @throws IOException
	 */
	private String findFileName(String dirName, String desiredName) throws IOException {

		try (var fileStream = Files.newDirectoryStream(Path.of(dirName))) {
			for (var filenm : fileStream) {
				String name = filenm.getFileName().toString();
				String lcname = name.toLowerCase();
				if (lcname.equals(desiredName)) {
					return dirName + "/" + name;
				}
			}

			throw new FileNotFoundException(dirName + "/" + desiredName);
		}
	}
}
