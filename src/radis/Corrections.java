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

package radis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import radis.types.Num;

/**
 * Applies corrections to the equity data based on the contents in other flat
 * files
 */
public class Corrections {
	private Map<String, Float> tkr2prevPrice = new HashMap<>();
	private Map<String, Float> tkr2curPrice = new HashMap<>();

	private DateReader pr_rdr;

	public Corrections() throws FileNotFoundException {
		this.pr_rdr = new DateReader("si_prices.txt");
	}

	/**
	 * Reads lines from a flat file that are sorted by date. Fields within a line
	 * must be comma-separated and the first field must be of the form YYYYMMDD
	 */
	static class DateReader {
		BufferedReader rdr;
		String[] fdata; // fields in last line that was read

		static final String[] endData = { "99999999", "", "", "", "", "" };

		DateReader(String filenm) throws FileNotFoundException {
			String path = System.getenv("MMAP");
			if (path == null) {
				throw new RuntimeException("missing MMAP environment variable");
			}

			rdr = new BufferedReader(new FileReader(path + "/" + filenm));

			fdata = new String[1];
			fdata[0] = "";
		}

		/**
		 * @return the data from the current line
		 */
		String[] curLine() {
			return fdata;
		}

		/**
		 * @return data from the next line, split on ",", or {@code null} if all lines
		 *         have been read
		 */
		String[] nextLine() {
			if (rdr != null) {
				try {
					String ln = rdr.readLine();
					if (ln != null) {
						fdata = ln.trim().split(",");

						if (fdata.length >= 1) {
							return fdata;
						}
					}
				} catch (IOException ex) {
				}

				try {
					rdr.close();
				} catch (IOException ex) {
				}

				rdr = null;
			}

			fdata = endData;
			return fdata;
		}

		/**
		 * Skips to the first line containing data for the given date.
		 *
		 * @param date desired date, in YYYYMMDD form
		 */
		void skipTo(String date) {
			while (fdata[0].compareTo(date) < 0) {
				nextLine();
			}
		}
	}

	/**
	 * Loads prices from the price file
	 *
	 * @param date desired date, in YYYYMMDD form
	 */
	public void correctPrices(String date) {
		tkr2prevPrice = tkr2curPrice;
		tkr2curPrice = new HashMap<>();

		pr_rdr.skipTo(date);

		String[] data;

		while ((data = pr_rdr.curLine())[0].equals(date)) {
			if (data.length == 3) {
				tkr2curPrice.put(data[1], Float.valueOf(data[2]));
			}

			pr_rdr.nextLine();
		}
	}

	/**
	 * @param tkr
	 * @return the previous price associated with the ticker, or INVALID_VALUE, if
	 *         there is none
	 */
	public float getPrevPrice(String tkr) {
		return tkr2prevPrice.getOrDefault(tkr, Num.INVALID_VALUE);
	}

	/**
	 * @param tkr
	 * @return the current price associated with the ticker, or INVALID_VALUE, if
	 *         there is none
	 */
	public float getCurPrice(String tkr) {
		return tkr2curPrice.getOrDefault(tkr, Num.INVALID_VALUE);
	}
}
