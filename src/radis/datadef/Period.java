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

package radis.datadef;

import java.nio.ByteBuffer;

/**
 * Information about a single period stored in the radis DB.
 */
public class Period {
	public static final String FILE_NAME = "/period.dat";
	public static final int RECSZ = 3 * 4;

	/**
	 * Date associated with the period, set using Date.fromText().
	 */
	private final float date;

	/**
	 * Number of records contained within the period.
	 */
	private final int nrecs;

	/**
	 * Record number, within the mmap files, of the first record associated with the
	 * period.
	 */
	private final int begrec;

	/**
	 * Record number (plus one), within the mmap files, of the last record
	 * associated with the period.
	 */
	private final int endrec;

	/**
	 * Constructs the object from the given parameters.
	 *
	 * @param date      date associated with the period
	 * @param nrecs     number of records contained within the period
	 * @param nprevrecs number of records, appearing before the period, within the
	 *                  mmap files
	 */
	public Period(float date, int nrecs, int nprevrecs) {
		this.date = date;
		this.nrecs = nrecs;
		this.begrec = nprevrecs;
		this.endrec = begrec + nrecs;
	}

	/**
	 * Constructs the object by reading the period information from a buffer.
	 *
	 * @param buf buffer from which to read the information
	 */
	public Period(ByteBuffer buf) {
		date = buf.getFloat();
		nrecs = buf.getInt();
		begrec = buf.getInt();
		endrec = begrec + nrecs;
	}

	/**
	 * Writes the period information to a buffer.
	 *
	 * @param buf buffer to which to write the information
	 */
	public void write(ByteBuffer buf) {
		buf.putFloat(date);
		buf.putInt(nrecs);
		buf.putInt(begrec);
	}

	public float getDate() {
		return date;
	}

	public int numRecords() {
		return nrecs;
	}

	public int beginRecord() {
		return begrec;
	}

	public int endRecord() {
		return endrec;
	}

	/**
	 * @param retain {@code true} if a record/company has been retained,
	 *               {@code false} if it has been discarded
	 * @return the number of companies, contained within the period, that haven't
	 *         been discarded by the current stock screen
	 */
	public int countRecords(boolean[] retain) {
		int count = 0;

		for (int x = begrec; x < endrec; ++x) {
			if (retain[x]) {
				++count;
			}
		}

		return count;
	}

}
