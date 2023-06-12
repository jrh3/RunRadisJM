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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import radis.exception.InternalException;
import radis.op.OpItem;

/**
 * Parser for RadisScript files.
 */
public class Parser {

	/**
	 * Name of the "basic" screen.
	 */
	private static final String BASIC_SCREEN_NAME = "basic";

	/**
	 * Maps screen-name to its parsed actions. Populated during parse. Screen names
	 * are in lower case
	 */
	private Map<String, OpItem> screen2act = new HashMap<>();

	/**
	 * Name of the first screen parsed.
	 */
	public String firstScreen;

	public Parser() {

	}

	/**
	 * @return the definition of the "basic" screen, which is used as a pre-filter
	 *         to all other screens
	 */
	public OpItem getBasicScreenAction() {
		return screen2act.get(BASIC_SCREEN_NAME);
	}

	/**
	 * @return the names of all the screens defined by the input files, excluding
	 *         any "basic" screen
	 */
	public List<String> getScreens() {
		return screen2act.keySet().stream().filter(name -> !BASIC_SCREEN_NAME.equals(name)).toList();
	}

	/**
	 * @param screenm name of the screen of interest
	 * @return the actions associated with the given screen
	 */
	public OpItem getAction(String screenm) {
		var op = screen2act.get(screenm);
		if (op == null) {
			throw new RuntimeException("undefined screen: " + screenm);
		}

		return op;
	}

	/**
	 * Associates a set of actions with the given screen.
	 *
	 * @param screenm   name of the screen of interest
	 * @param screenDef the actions/definition to associate with the screen
	 */
	public void defineScreen(String screenm, OpItem screenDef) {
		String snm = screenm.toLowerCase();

		if (snm.isEmpty()) {
			throw new InternalException("missing screen name");
		}

		if (!BASIC_SCREEN_NAME.equals(snm) && firstScreen == null) {
			firstScreen = snm;
		}

		if (screen2act.containsKey(snm)) {
			throw new RuntimeException("screen redefined: " + snm);
		}

		screen2act.put(snm, screenDef);
	}

	/**
	 * Parses a file containing screen definitions.
	 *
	 * @param filenm name of the screen definition file
	 * @throws FileNotFoundException if the file doesn't exist
	 * @throws IOException           if the file cannot be read
	 */
	public void parse(String filenm) throws FileNotFoundException, IOException {
		try (var frdr = new BufferedReader(openFile(filenm))) {
			StringReader rdr = new StringReader(NormalizeContent.normalize(frdr));

			RadisParser parser = new RadisParser(rdr) {
				public void defineScreen(String screenm, OpItem screenDef) {
					Parser.this.defineScreen(screenm, screenDef);
				}
			};
			parser.parse();
		}
	}

	/**
	 * Opens a file for reading, first adding a ".txt" extension to the file name,
	 * and, if that fails, using the file name as is.
	 *
	 * @param filenm
	 * @return a new file reader
	 * @throws FileNotFoundException
	 */
	private FileReader openFile(String filenm) throws FileNotFoundException {
		try {
			return new FileReader(filenm + ".txt");
		} catch (FileNotFoundException ex) {
			return new FileReader(filenm);
		}
	}

	/**
	 * Parses screen definitions from the specified files.
	 *
	 * @param files
	 */
	public void parseAll(List<String> files) {
		for (var filenm : files) {
			try {
				parse(filenm);
			} catch (IOException e) {
				throw new RuntimeException(filenm, e);
			}
		}
	}
}
