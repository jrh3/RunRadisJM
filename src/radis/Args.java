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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Parses command-line arguments.
 */
public class Args {

	/**
	 * Name of the screen to apply; defaults to the first screen parsed.
	 */
	public static String screen;

	/**
	 * Files to be parsed.
	 */
	public static final List<String> files = new LinkedList<String>();

	/**
	 * Starting date, in YYYYMMDD format, inclusive.
	 */
	static String dbegin = "00000000";

	/**
	 * Ending date, in YYYYMMDD format.
	 */
	static String dend = "99999999";

	/**
	 * Number of months per holding period.
	 */
	static int months = 1;

	/**
	 * Minimum number of equities to be held. If too few equities pass the screen,
	 * then the missing positions are held in cash. For instance, if "-min 10" is
	 * specified and only 7 stocks pass the screen, then 3 positions will be held in
	 * cash. Cash positions have a 0% return.
	 */
	public static int emin = 1;

	/**
	 * Maximum number of equities to be held. If too many equities pass the screen,
	 * the extras are discarded. Equities are picked in order, based on how they're
	 * sorted by the screen.
	 */
	public static int emax = 0;

	/**
	 * If true, then returns are weighted based on market cap. Otherwise, they're
	 * equal weighted.
	 */
	public static boolean weight;

	/**
	 * Non-survivor bias. This specifies the return to use if a new price cannot be
	 * found for a stock.
	 *
	 * Note: This is a 1-based return, thus: 1.10 = 10% gain 0.70 = 30% loss.
	 */
	static double bias = 1.0;

	/**
	 * If true, then returns are shown for each individual ticker. Otherwise,
	 * they're shown in aggregate.
	 */
	public static boolean indiv;

	/**
	 * Specification for parsing a command-line option.
	 */
	public abstract static class Spec {
		String name; // argument name
		String description; // help text for the argument

		/**
		 * Constructs an object.
		 *
		 * @param nm   name of the spec
		 * @param desc spec description/help message
		 */
		public Spec(String nm, String desc) {
			name = nm;
			description = desc;
		}

		String getArg(LinkedList<String> args) {
			if (args.isEmpty())
				throw new IllegalArgumentException(name + " requires a value");

			return args.remove();
		}

		abstract void setValue(LinkedList<String> args);
	}

	/**
	 * Specification for processing an argument as a string.
	 */
	public static class StrSpec extends Spec {
		private final Consumer<String> setval;

		public StrSpec(String name, String description, Consumer<String> setval) {
			super(name, description);
			this.setval = setval;
		}

		void setValue(LinkedList<String> args) {
			setval.accept(getArg(args));
		}
	}

	/**
	 * Specification for processing an argument as an integer.
	 */
	public static class IntSpec extends Spec {
		private final Consumer<Integer> setval;

		public IntSpec(String name, String description, Consumer<Integer> setval) {
			super(name, description);
			this.setval = setval;
		}

		void setValue(LinkedList<String> args) {
			setval.accept(Integer.valueOf(getArg(args)));
		}
	}

	/**
	 * Specification for processing an argument as a float.
	 */
	public static class FloatSpec extends Spec {
		private final Consumer<Float> setval;

		public FloatSpec(String name, String description, Consumer<Float> setval) {
			super(name, description);
			this.setval = setval;
		}

		void setValue(LinkedList<String> args) {
			setval.accept(Float.valueOf(getArg(args)));
		}
	}

	/**
	 * Specification for processing an argument as a boolean.
	 */
	public static class BoolSpec extends Spec {
		private final Runnable setval;

		public BoolSpec(String name, String description, Runnable setval) {
			super(name, description);
			this.setval = setval;
		}

		void setValue(LinkedList<String> args) {
			setval.run();
		}
	}

	/*
	 * Specifications for parsing command-line options.
	 */
	public static Spec beg_spec = new StrSpec("-begin", "  starting date, YYYYMMDD", v -> dbegin = v);
	public static Spec end_spec = new StrSpec("-end", "  ending date, YYYYMMDD", v -> dend = v);
	public static Spec months_spec = new IntSpec("-months", "  months per period (default=1)", v -> months = v);
	public static Spec emin_spec = new IntSpec("-min", "  min stocks to hold, rest in cash", v -> emin = v);
	public static Spec emax_spec = new IntSpec("-max", "  max stocks to hold", v -> emax = v);
	public static Spec weight_spec = new BoolSpec("-weight", "  weight by market cap", () -> weight = true);
	public static Spec bias_spec = new FloatSpec("-bias",
			"  return for non-surviving equities default=1.0 (i.e., 0% gain)", v -> bias = v);
	public static Spec screen_spec = new StrSpec("-screen", "  screen-name", v -> screen = v);
	public static Spec individ_ret_spec = new BoolSpec("-indiv", "  show returns for individual tickers",
			() -> indiv = true);

	/**
	 * Adds a file name to the list of files to be parsed.
	 */
	public static Spec add_file = new Spec("screen-def", "screen definition file") {
		void setValue(LinkedList<String> args) {
			files.add(getArg(args));
		}
	};

	/**
	 * Parses the command-line arguments.
	 *
	 * @param specs    array of argument specs
	 * @param dfltSpec default spec for arguments that have no key
	 * @param args     command-line arguments
	 */
	public static void parse(String[] args, Spec[] specs, Spec dfltSpec) {
		LinkedList<String> arglst = new LinkedList<>();
		Arrays.stream(args).forEach(arglst::add);

		while (!arglst.isEmpty()) {
			String nm = arglst.getFirst();

			boolean found = false;

			for (Spec sp : specs) {
				if (sp.name.equals(nm)) {
					arglst.remove();
					sp.setValue(arglst);
					found = true;
					break;
				}
			}

			if (found)
				continue; // ** CONTINUE **

			if (nm.startsWith("-")) {
				System.err.println("arg(s): [-<option> <value>]* [<screen-definition-file>]+");
				System.err.println("where the options are as follows:");

				for (Spec sp : specs) {
					System.err.println("\t" + sp.name + "\t" + sp.description);
				}

				System.exit(1);
			}

			dfltSpec.setValue(arglst);
		}
	}
}
