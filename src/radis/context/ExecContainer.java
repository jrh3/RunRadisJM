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

/**
 * Container of data for a single screen execution. Provides easier access, by
 * subclasses, to various execution data.
 */
public class ExecContainer {

	/**
	 * Execution context.
	 */
	protected final ExecContext exec;

	/**
	 * Maps a record number to a true/false value, depending on whether that record
	 * has been retained by the screen rather than discarded.
	 */
	protected final boolean[] retain;

	/**
	 * Record numbers sorted based on "sort" statements.
	 */
	protected final int[] recOrder;

	/**
	 * First record number of interest.
	 */
	protected final int baseRec;

	/**
	 * Maximum record number of interest (plus one).
	 */
	protected final int maxRecNum;

	/**
	 * Constructs the object.
	 *
	 * @param exec
	 */
	public ExecContainer(ExecContext exec) {
		this.exec = exec;
		this.retain = exec.retain;
		this.recOrder = exec.recOrder;
		this.baseRec = exec.getBaseRec();
		this.maxRecNum = exec.getMaxRecNum();
	}
}
