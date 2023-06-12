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

import radis.dbf.DdInfo;
import radis.exception.CorruptDbException;
import radis.types.Text;

/**
 * Definition of a field stored in the radis DB.
 */
public class FieldDef {
	public static final String FILE_NAME = "/fieldef.dat";
	public static final int RECSZ = 2 + 49 + 17 + 33;

	public enum Type {
		TEXT, FLOAT, DATE, LOGICAL
	};

	private Type type; // data type
	private int recsz; // record size
	private String longName; // long field name
	private String shortName; // short field name
	private String fileName; // name of the mmap file containing the field

	/**
	 * Constructs the object from an SI Pro field/data definition.
	 *
	 * @param info     SI Pro field/data definition
	 * @param fileName name of the mmap file containing the field
	 */
	public FieldDef(DdInfo info, String fileName) {
		this.type = info.getType();
		this.recsz = info.getRecSize();
		this.longName = info.getLongName();
		this.shortName = info.getShortName();
		this.fileName = fileName;
	}

	/**
	 * Constructs the object by reading the field definition from a buffer.
	 *
	 * @param buf buffer from which to read the definition
	 */
	public FieldDef(ByteBuffer buf) {
		int t = buf.get();
		recsz = buf.get();
		longName = Text.getString(buf, 48 + 1);
		shortName = Text.getString(buf, 16 + 1);
		fileName = Text.getString(buf, 32 + 1);

		switch (t) {
		case 0:
			type = Type.TEXT;
			break;
		case 1:
			type = Type.FLOAT;
			break;
		case 2:
			type = Type.DATE;
			break;
		case 3:
			type = Type.LOGICAL;
			break;
		default:
			throw new CorruptDbException("invalid type for " + longName + ": " + t);
		}
	}

	/**
	 * Writes the field definition to a buffer.
	 *
	 * @param buf buffer to which to write the definition
	 */
	public void write(ByteBuffer buf) {
		byte t;
		switch (type) {
		case TEXT:
			t = 0;
			break;
		case FLOAT:
			t = 1;
			break;
		case DATE:
			t = 2;
			break;
		case LOGICAL:
			t = 3;
			break;
		default:
			throw new CorruptDbException("invalid type for " + longName + ": " + type);
		}

		buf.put(t);
		buf.put((byte) recsz);
		Text.putString(buf, 48 + 1, longName);
		Text.putString(buf, 16 + 1, shortName);
		Text.putString(buf, 32 + 1, fileName);
	}

	public Type getType() {
		return type;
	}

	public int recSize() {
		return recsz;
	}

	public String getLongName() {
		return longName;
	}

	public String getShortName() {
		return shortName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setRecSize(int recsz) {
		this.recsz = recsz;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
