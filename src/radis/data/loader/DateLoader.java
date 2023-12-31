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

package radis.data.loader;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import radis.data.DataLoader;
import radis.types.Date;

/**
 * Loader for an mmap file containing data from a float (i.e., DATE) field. Note:
 * DATE fields are stored as float values.
 */
public class DateLoader extends DataLoader<FloatBuffer> {

	public DateLoader(ByteBuffer buf) {
		super(buf.asFloatBuffer(), 4);
	}

	@Override
	public void zap(int nrecords) {
		for (int x = 0; x < nrecords; ++x) {
			buf.put(Date.INVALID_VALUE);
		}
	}

	@Override
	protected void put(int recnum, String data) {
		buf.put(recnum, Date.fromText(data));
	}
}
