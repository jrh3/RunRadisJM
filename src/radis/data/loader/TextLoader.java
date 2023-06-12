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
import radis.types.Text;

/**
 * Loader for an mmap file containing data from a TEXT field.
 */
public class TextLoader extends DataLoader<ByteBuffer> {

	/**
	 * Constructs the object.
	 *
	 * @param buf   buffer into which data will be loaded
	 * @param recsz record size
	 */
	public TextLoader(ByteBuffer buf, int recsz) {
		super(buf, recsz);
	}

	@Override
	protected void zap(int nrecords) {
		byte[] zeroes = new byte[recsz];

		for (int x = 0; x < nrecords; ++x) {
			buf.put(zeroes);
		}
	}

	@Override
	protected void put(int recnum, String data) {
		buf.position(recnum * recsz);
		Text.putString(buf, recsz, data);
	}
}
