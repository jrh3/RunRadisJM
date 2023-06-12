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

package radis.parser;

import java.io.IOException;
import java.io.Reader;

/**
 * Normalizes the content from a Reader as follows:
 * <dl>
 * <li>Everything outside of double quotes is mapped to lower case.
 * <li>Extra spaces are stripped from variable names (i.e., between square
 * brackets).
 * <li>Periods are stripped from variable names
 * </dl>
 */
public class NormalizeContent {

	/**
	 * Read and normalize all data.
	 *
	 * @param rdr data source
	 * @return entire content from the reader, normalized
	 * @throw IOException If an exception occurs reading from the reader
	 */
	static public String normalize(Reader rdr) throws IOException {
		StringBuilder b = new StringBuilder(16 * 1024);

		final int NORMAL = 0;
		final int INSTR = NORMAL + 1;
		final int INVAR = INSTR + 1;
		final int INVARSP = INVAR + 1;

		int state = NORMAL;

		int cv;
		while ((cv = rdr.read()) != -1) {
			char c = (char) cv;

			switch (state) {
			case NORMAL:
				b.append(Character.toLowerCase(c));

				switch (c) {
				case '[':
					state = INVAR;
					break;
				case '"':
					state = INSTR;
					break;
				}
				break;

			case INSTR:
				// don't map strings to lower case
				b.append(c);

				switch (c) {
				case '"':
				case '\n':
					state = NORMAL;
					break;
				}
				break;

			case INVAR:
				switch (c) {
				case ']':
				case '\n':
					state = NORMAL;
					b.append(c);
					break;
				case ' ':
					state = INVARSP;
					b.append(c);
					break;
				case '.':
					// discard periods
					break;
				default:
					b.append(Character.toLowerCase(c));
					break;
				}
				break;

			case INVARSP:
				switch (c) {
				case ']':
				case '\n':
					b.append(c);
					state = NORMAL;
					break;
				case ' ':
					// discard spaces
					break;
				case '.':
					// discard periods
					break;
				default:
					b.append(Character.toLowerCase(c));
					state = INVAR;
					break;
				}
				break;
			}
		}

		return b.toString();
	}
}
