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

import radis.data.DataLoader;
import radis.types.Bool;

/**
 * Loader for an mmap file containing data from a boolean (i.e., LOGICAL) field.
 */
public class BoolLoader extends DataLoader<ByteBuffer> {

	public BoolLoader(ByteBuffer buf) {
		super(buf, 1);
	}

	@Override
	protected void zap(int nrecords) {
		for (int x = 0; x < nrecords; ++x) {
			buf.put(Bool.INVALID_VALUE);
		}
	}

	@Override
	protected void put(int recnum, String data) {
		buf.put(recnum, Bool.fromText(data));
	}
}
