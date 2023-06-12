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

import radis.datadef.FieldDef;

/**
 * Extended data definition.
 */
public class DdInfo extends DdField {

	/**
	 * The field's radis type.
	 */
	private FieldDef.Type type = FieldDef.Type.FLOAT;

	/**
	 * Long field name.
	 */
	private String longnm;

	/**
	 * Record size within the mmap file.
	 */
	private int recsz = 0;

	/**
	 * Constructs the object.
	 */
	public DdInfo() {
	}

	public FieldDef.Type getType() {
		return type;
	}

	public String getLongName() {
		return longnm;
	}

	public int getRecSize() {
		return recsz;
	}

	public void setType(FieldDef.Type type) {
		this.type = type;
	}

	public void setLongName(String longnm) {
		this.longnm = longnm;
	}

	public void setRecSize(int recsz) {
		this.recsz = recsz;
	}
}
