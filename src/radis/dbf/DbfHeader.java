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

import java.nio.ByteBuffer;

/**
 * Header information from a DBF file.
 */
public class DbfHeader {
	private final int numRecords;
	private final int headerLen;
	private final int recordLen;

	public DbfHeader(ByteBuffer buf) {
		this.numRecords = buf.getInt();
		this.headerLen = buf.getShort();
		this.recordLen = buf.getShort();
	}

	public int getNumRecords() {
		return numRecords;
	}

	public int getHeaderLen() {
		return headerLen;
	}

	public int getRecordLen() {
		return recordLen;
	}
}
