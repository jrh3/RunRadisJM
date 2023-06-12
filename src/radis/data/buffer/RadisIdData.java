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

package radis.data.buffer;

import java.nio.IntBuffer;

import radis.context.ExecContext;

/**
 * Radis company ID data. The file is structured as follows:
 * <ul>
 * <li>record "i" contains the radis company ID of the data associated with
 * record "i" across all of the mmap files.</li>
 * </ul>
 */
public class RadisIdData {
	public static final String FILENM = "/radisid.map";
	public static final int RECSZ = 4;

	private final ExecContext exec;
	private final IntBuffer buf;

	public RadisIdData(ExecContext exec, IntBuffer buf) {
		this.exec = exec;
		this.buf = buf;
	}

	public void rewind() {
		buf.position(exec.getBaseRec());
	}

	public void rewind(int begrec) {
		buf.position(begrec);
	}

	public boolean hasRemaining() {
		return buf.hasRemaining();
	}

	public int get() {
		return buf.get();
	}

	public int get(int recnum) {
		return buf.get(recnum);
	}
}
