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

package radis.data;

import radis.context.ExecContext;

/**
 * Data Implementation.
 */
public abstract class DataImpl implements Data {
	protected final ExecContext exec;
	private final int type;
	private final boolean dconst;

	public DataImpl(ExecContext exec, int type, boolean isConst) {
		this.exec = exec;
		this.type = type;
		this.dconst = isConst;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public boolean isConst() {
		return dconst;
	}
}
