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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import radis.Util;
import radis.exception.CorruptDbException;
import radis.exception.FieldNotFoundException;

/**
 * A single DBF file.
 */
public class Dbf {

	private final DbfHeader header;
	private final List<FieldDescriptor> fields = new ArrayList<>();

	/**
	 * Maps a field's name to its descriptor.
	 */
	private final Map<String, FieldDescriptor> name2field = new HashMap<>();

	/**
	 * Position, within the buffer, of the start of the current record.
	 */
	private int currec;

	/**
	 * Position, within the buffer, of the last record, inclusive.
	 */
	private final int lastrec;

	/**
	 * Entire content of the file.
	 */
	private final ByteBuffer buf;

	/**
	 * Buffer used to process text strings.
	 */
	private final byte[] buf2 = new byte[256 + 1];

	/**
	 * Constructs the object. Reads the entire content of the DBF file into an
	 * internal buffer.
	 *
	 * @param fileName path to the DBF file
	 * @throws IOException
	 */
	public Dbf(String fileName) throws IOException {
		this.buf = ByteBuffer.wrap(Files.readAllBytes(Path.of(fileName)));

		buf.order(ByteOrder.LITTLE_ENDIAN);

		buf.get(); // dbtype
		buf.get();
		buf.get();
		buf.get();
		this.header = new DbfHeader(buf);

		if (Util.printLoaderInfo) {
			System.out.println("numrecs=" + header.getNumRecords());
		}

		readFields(header.getHeaderLen());
		indexFields();

		if (Util.printLoaderInfo) {
			System.out.println("numfields=" + name2field.size());
		}

		if (!fields.isEmpty()) {
			var fd = fields.get(fields.size() - 1);
			if (fd.getOffset() + fd.getLength() > header.getRecordLen()) {
				throw new CorruptDbException("field length exceeds record length in " + fileName);
			}
		}

		long lbufsz = header.getNumRecords() * (long) header.getRecordLen();
		if (lbufsz > 100 * 1000 * 1000L) {
			throw new CorruptDbException("file size " + lbufsz + " is too large for " + fileName);
		}

		this.currec = header.getHeaderLen() - header.getRecordLen();
		this.lastrec = currec + (int) lbufsz;
	}

	/**
	 * Reads a string from the DBF file, using the ASCII encoding. Removes leading
	 * and trailing white-space.
	 *
	 * @param nchars number of characters to read
	 * @return the string read from the file
	 */
	private String readChars(int nchars) {
		buf.get(buf2, 0, nchars);
		var str = new String(buf2, 0, nchars, StandardCharsets.US_ASCII);
		return str.trim();
	}

	/**
	 * Populates {@link #fields} by reading all of the fields from the buffer.
	 *
	 * @param headerLen length of the header, which includes the fields
	 */
	private void readFields(int headerLen) {

		int nfields = (headerLen - 33) / 32;

		// skip the "deleted" mark
		int offset = 1;

		for (int n = 0; n < nfields; ++n) {
			buf.position((n + 1) * 32);

			String name = readChars(10);
			if (name.isEmpty()) {
				break;
			}

			name = name.toLowerCase();
			buf.get();
			char type = (readChars(1) + " ").charAt(0);
			buf.get();
			buf.get();
			buf.get();
			buf.get();
			byte length = buf.get();
			byte numDecimals = buf.get();

			fields.add(new FieldDescriptor(name, type, length, numDecimals, offset));

			offset += length;
			// System.out.println(name + " " + type + " " + length + " " + offset);
		}
	}

	/**
	 * Builds an index of {@link #fields}, populating {@link #name2field} from it.
	 */
	private void indexFields() {
		for (var fd : fields) {
			name2field.put(fd.getName(), fd);
		}
	}

	/**
	 * @param fieldName
	 * @return {@code true} if the DBF file contains the specified field
	 */
	public boolean hasField(String fieldName) {
		return name2field.containsKey(fieldName.toLowerCase());
	}

	/**
	 * @param fieldName
	 * @throws FieldNotFoundException if the field does not exist
	 * @return a descriptor for the specified field
	 */
	public FieldDescriptor getField(String fieldName) {
		var fd = name2field.get(fieldName.toLowerCase());
		if (fd == null) {
			throw new FieldNotFoundException(fieldName);
		}

		return fd;
	}

	/**
	 * Gets the specified field, from the buffer, of the current record.
	 *
	 * @param fd
	 * @return the content of the specified field within the current record
	 */
	public String getField(FieldDescriptor fd) {
		buf.position(currec + fd.getOffset());
		return readChars(fd.getLength());
	}

	/**
	 * Rewinds the current record position so that the next call to
	 * {@link #nextRecord()} will return the first record in the buffer.
	 */
	public void rewind() {
		currec = header.getHeaderLen() - header.getRecordLen();
	}

	/**
	 * Moves the current record position to the next active (i.e., undeleted)
	 * record.
	 *
	 * @return {@code true} if the current record position now points at an active
	 *         record, {@code false} if there are no more records in the buffer
	 */
	public boolean nextRecord() {
		while (currec != lastrec) {
			currec += header.getRecordLen();

			buf.position(currec);

			// "_" indicates that the record has been deleted
			if (buf.get() != '_') {
				buf.position(currec);
				return true;
			}
		}

		return false;
	}
}
