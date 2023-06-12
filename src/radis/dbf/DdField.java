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

/**
 * Definition of a DBF field, as read from the data dictionary.
 */
public class DdField {

	/**
	 * Name of the DBF file containing this field. There may be more than one, but
	 * only one is recorded.
	 */
	private String filenm;

	/**
	 * Short field name.
	 */
	private String name;

	private String description;

	/*
	 * The SI Pro data dictionary includes two field types. Not sure what the
	 * difference is.
	 */
	// DBF field type
	private String dbftype;

	// SI Pro field type
	private String sitype;

	/**
	 * Constructs the object.
	 */
	public DdField() {
	}

	public String getFileName() {
		return filenm;
	}

	public String getShortName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDbfType() {
		return dbftype;
	}

	public String getSiType() {
		return sitype;
	}

	public void setFileName(String filenm) {
		this.filenm = filenm;
	}

	public void setShortName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDbfType(String vtype) {
		this.dbftype = vtype;
	}

	public void setSiType(String vsitype) {
		this.sitype = vsitype;
	}
}
