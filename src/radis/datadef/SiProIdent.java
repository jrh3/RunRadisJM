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

import radis.types.Text;

/**
 * Maps an SI Pro company ID to the corresponding ticker.
 */
public class SiProIdent {
	public static final String FILE_NAME = "/company.dat";
	public static final int TKR_LEN = 8;
	public static final int RECSZ = TKR_LEN + 1 + 17;

	private String ticker;
	private String compid;

	/**
	 * Constructs the object from the given parameters.
	 *
	 * @param ticker
	 * @param compid SI Pro company ID
	 */
	public SiProIdent(String ticker, String compid) {
		this.ticker = ticker;
		this.compid = compid;
	}

	/**
	 * Constructs the object by reading the mapping from a buffer.
	 *
	 * @param buf buffer from which to read the mapping
	 */
	public SiProIdent(ByteBuffer buf) {
		ticker = Text.getString(buf, 8 + 1);
		compid = Text.getString(buf, 16 + 1);
	}

	/**
	 * Writes the mapping to a buffer.
	 *
	 * @param buf buffer to which to write the mapping
	 */
	public void write(ByteBuffer buf) {
		Text.putString(buf, 8 + 1, ticker);
		Text.putString(buf, 16 + 1, compid);
	}

	public String getTicker() {
		return ticker;
	}

	public String getCompId() {
		return compid;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setCompId(String compid) {
		this.compid = compid;
	}
}
