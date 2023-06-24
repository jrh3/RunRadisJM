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

package radis.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import radis.exception.InternalException;

/**
 * An OpItem that contains children.
 */
public class Op extends OpItem {

	/**
	 * Children.
	 */
	protected final List<OpItem> args;

	/**
	 * Constructs an OpItem.
	 *
	 * @param optype
	 * @param args   the operation's arguments
	 */
	public Op(int optype, OpItem... args) {
		super(optype);

		var lst = Arrays.stream(args).map(this::check).toList();

		// wrap in a new ArrayList to make it modifiable
		this.args = new ArrayList<>(lst);
	}

	/**
	 * Verifies that an OpItem is not "None".
	 *
	 * @param p the item that's being manipulated
	 * @throws InternalException if the item is "None"
	 * @return p, if it's valid
	 */
	private OpItem check(OpItem p) throws InternalException {
		if (p == None) {
			throw new InternalException("adding None to " + type());
		}

		return p;
	}

	/**
	 * Gets a particular child
	 *
	 * @return the desired child, if available, or None otherwise
	 * @param idx child index, where the first child is idx=0
	 */
	@Override
	public OpItem arg(int idx) {
		if (idx >= args.size()) {
			return None;
		}

		return args.get(idx);
	}

	/**
	 * Replaces the right (second) child with a new value
	 *
	 * @param p new child
	 */
	@Override
	public void replaceRight(OpItem p) {
		if (args.size() >= 3) {
			args.set(2, p);

		} else {
			throw new IllegalArgumentException("OpItem has no right child");
		}
	}

	/**
	 * Compares this object with another.
	 *
	 * @param p object with which to compare this
	 * @return true if they have the same token type and all of the children are
	 *         equal, false otherwise
	 */
	@Override
	public boolean equals(Object p) {
		if (this == p) {
			return true;

		} else if (!(p instanceof Op)) {
			return false;
		}

		Op t = (Op) p;
		if (t.args.size() != args.size()) {
			return false;
		}

		Iterator<OpItem> ita = args.iterator();
		Iterator<OpItem> itb = t.args.iterator();

		while (ita.hasNext()) {
			if (!ita.next().equals(itb.next())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Walk the item, and any children, displaying them as we go.
	 */
	public void walk() {
		System.out.println("(" + type() + "> ");

		for (OpItem p : args) {
			p.walk();
		}

		System.out.println(" <" + type() + ")");
	}

	public List<OpItem> children() {
		return args;
	}

	/**
	 * Adds an item to the list of children.
	 *
	 * @return "this"
	 */
	public Op append(OpItem a) {
		args.add(check(a));
		return this;
	}

	/**
	 * Adds all of the children of "p" to the list of children.
	 *
	 * @return "this"
	 */
	public Op appendChildren(Op p) {
		args.addAll(p.args);
		return this;
	}

	/**
	 * Adds all of the children to the list of children.
	 *
	 * @return "this"
	 */
	public Op appendAll(List<OpItem> children) {
		args.addAll(children);
		return this;
	}
}
