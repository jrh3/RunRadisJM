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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import radis.Util;
import radis.data.Data;
import radis.data.buffer.BoolBufData;
import radis.data.buffer.DateBufData;
import radis.data.buffer.NumBufData;
import radis.data.buffer.RadisIdData;
import radis.data.buffer.TextByteBufData;
import radis.data.constant.TextConstData;
import radis.datadef.FieldDef;
import radis.datadef.Period;
import radis.exception.FieldNotFoundException;
import radis.exception.InternalException;

/**
 * Execution context used while executing a screen.
 */
public class ExecContext {
	protected final Context ctx;
	private final RadisIdData radisIds;

	/**
	 * Periods of interest, in order by date.
	 */
	private final List<Period> periods;

	/**
	 * Data associated with variables that have been set/created by the screen while
	 * it's executing.
	 */
	private final Map<String, Data> variables = new HashMap<>();

	/**
	 * Cached Data associated with fields that have been referenced by the screen
	 * while it's executing.
	 */
	private final Map<String, Data> fields = new HashMap<>();

	/**
	 * First record number of interest.
	 */
	private final int baseRec;

	/**
	 * Maximum record number of interest (plus one).
	 */
	private final int maxRecNum;

	/**
	 * Maps a record number to a true/false value, depending on whether that record
	 * has been retained by the screen rather than discarded.
	 */
	public final boolean[] retain;

	/**
	 * Record numbers sorted based on "sort" statements.
	 */
	public final int[] recOrder;

	/**
	 * @param ctx     the context
	 * @param periods only contains the periods of interest
	 * @throws IOException
	 */
	public ExecContext(Context ctx, List<Period> periods) throws IOException {
		this.ctx = ctx;
		this.periods = periods;
		this.baseRec = periods.get(0).beginRecord();
		this.maxRecNum = periods.get(periods.size() - 1).endRecord();
		this.retain = new boolean[maxRecNum];
		this.recOrder = new int[maxRecNum];

		// all records start out as being retained
		for (int x = 0; x < maxRecNum; ++x) {
			retain[x] = true;
		}

		// records start out in the order of their appearance
		for (int x = 0; x < maxRecNum; ++x) {
			recOrder[x] = x;
		}

		// add predefined values
		predefineVars();

		this.radisIds = loadRadisIds();
	}

	/**
	 * @return the mapping of internal company IDs
	 * @throws IOException
	 */
	protected RadisIdData loadRadisIds() throws IOException {
		String filenm = ctx.getDir() + RadisIdData.FILENM;
		int recsz = RadisIdData.RECSZ;

		try (var file = FileChannel.open(Path.of(filenm), StandardOpenOption.READ)) {
			var mmap = file.map(FileChannel.MapMode.READ_ONLY, 0, maxRecNum * recsz);
			mmap.order(ByteOrder.LITTLE_ENDIAN);

			return new RadisIdData(this, mmap.asIntBuffer());
		}
	}

	/**
	 * @param longnm long variable name, as specified in the screen definitions
	 * @return the data associated with the given field, which may be in the mmap DB
	 *         or may have been created by a create/set statement
	 */
	public Data getData(String longnm) {
		/*
		 * This field is not actually stored in the data files, as only those stocks
		 * containing a particular value are loaded into the DB, thus return constants
		 * instead.
		 */
		switch (longnm) {
		case "si adr/ads stock":
			return new TextConstData(this, "F");
		}

		var data = fields.get(longnm);
		if (data != null) {
			// already cached - get a copy
			return data.duplicate();
		}

		try {
			data = getFieldData(longnm);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (data != null) {
			// not cached yet - add it to the cache and return a copy
			fields.put(longnm, data);
			return data.duplicate();
		}

		data = variables.get(longnm);
		if (data != null) {
			// created via a create/set statement - return a copy
			return data.duplicate();
		}

		throw new FieldNotFoundException(longnm);
	}

	/**
	 * Gets data from a field in the mmap DB.
	 *
	 * @param longnm long field name, as specified in the screen definitions
	 * @return the associated mmap data
	 * @throws IOException
	 */
	private Data getFieldData(String longnm) throws IOException {
		FieldDef def = ctx.getFieldDef(longnm);
		if (def == null) {
			return null;
		}

		var mmap = getDataMap(def);

		switch (def.getType()) {
		case FLOAT:
			return new NumBufData(this, mmap.asFloatBuffer());

		case DATE:
			return new DateBufData(this, mmap.asFloatBuffer());

		case TEXT:
			return new TextByteBufData(this, mmap, def.recSize());

		case LOGICAL:
			return new BoolBufData(this, mmap);

		default:
			throw new InternalException("invalid type for " + longnm + ": " + def.getType());
		}
	}

	/**
	 * Gets a field's data from its memory mapped file.
	 *
	 * @param def field whose data is to be retrieved
	 * @return a buffer containing the memory mapped file data
	 * @throws IOException
	 */
	protected ByteBuffer getDataMap(FieldDef def) throws IOException {
		String filenm = ctx.getDir() + "/" + def.getFileName();
		int recsz = def.recSize();

		try (var file = FileChannel.open(Path.of(filenm), StandardOpenOption.READ)) {
			var mmap = file.map(FileChannel.MapMode.READ_ONLY, 0, maxRecNum * recsz);
			mmap.order(ByteOrder.LITTLE_ENDIAN);

			return mmap;
		}
	}

	/**
	 * Add pre-defined variables.
	 */
	private void predefineVars() {
		addVar(Util.RANK_VAR_NM, new NumBufData(this));
		addVar(Util.TIED_RANK_VAR_NM, new NumBufData(this));
		addVar(Util.PERCENT_TIED_RANK_VAR_NM, new NumBufData(this));

		// build NOW data
		DateBufData now = new DateBufData(this);
		addVar(Util.NOW_VAR_NM, now);

		for (var period : periods) {
			var date = period.getDate();
			int begrec = period.beginRecord();
			int endrec = period.endRecord();

			// set the date for every record within the period
			now.rewind(begrec);
			for (int x = begrec; x < endrec; ++x) {
				now.put(date);
			}
		}
	}

	/**
	 * Applies a function to each period.
	 *
	 * @param func
	 */
	public void applyPeriods(Consumer<Period> func) {
		for (var period : periods) {
			func.accept(period);
		}
	}

	/**
	 * Adds a variable to the cache
	 *
	 * @param varnm variable name, as specified in the screen definitions
	 * @param data  to associate with the variable
	 */
	public void addVar(String varnm, Data data) {
		variables.put(varnm, data);
	}

	/**
	 * @return the periods of interest
	 */
	public List<Period> getPeriods() {
		return periods;
	}

	/**
	 * @return the first record number of interest
	 */
	public int getBaseRec() {
		return baseRec;
	}

	/**
	 * @return the last record number of interest (plus one)
	 */
	public int getMaxRecNum() {
		return maxRecNum;
	}

	/**
	 * @param longnm long field name, as specified in the screen definitions
	 * @return {@code true} if the given field exists in the mmap DB
	 */
	public boolean isField(String longnm) {
		return (ctx.getFieldDef(longnm) != null);
	}

	/**
	 * @return the mapping of internal company IDs
	 */
	public RadisIdData getRadisIds() {
		return radisIds;
	}
}
