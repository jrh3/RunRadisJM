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

import radis.parser.Parser;

/**
 * Prints the screens found in a screen definition file(s).
 */
public class screens {
	private final Parser parser;

	/**
	 * Parses the screens.
	 */
	public screens() {
		this.parser = new Parser();

		parser.parseAll(Args.files);
	}

	/**
	 * Prints the names of the screens found in the screen definition file(s).
	 */
	public void run() {
		parser.getScreens().forEach(System.out::println);
	}

	/**
	 * @param args
	 */
	static public void main(String[] args) {
		Args.Spec[] specs = {};

		Args.parse(args, specs, Args.add_file);

		if (Args.files.isEmpty()) {
			System.err.println("missing source file name");
			System.exit(1);
		}

		try {
			new screens().run();

		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			e.printStackTrace();
		}
	}
}
