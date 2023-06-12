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

package radis.data.constant;

import radis.context.ExecContext;
import radis.data.DataImpl;

/**
 * Implementation of a wrapper for a constant, optimized so that only one value
 * is remembered rather than one value per record.
 */
public abstract class ConstDataImpl extends DataImpl implements ConstData {

	public ConstDataImpl(ExecContext exec, int type) {
		super(exec, type, true);
	}

	@Override
	public ConstData duplicate() {
		return this;
	}

	@Override
	public ConstData toConst() {
		return this;
	}
}
