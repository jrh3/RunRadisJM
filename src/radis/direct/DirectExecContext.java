/*
	RunRadisJM: Run RadiScript screens
	Copyright (C) 2023  James Hahn

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

package radis.direct;

import java.io.IOException;
import java.nio.ByteBuffer;

import radis.context.ExecContext;
import radis.data.buffer.RadisIdData;
import radis.datadef.FieldDef;

/**
 * Execution context that gets its data directly from the DB.
 */
public class DirectExecContext extends ExecContext {

	public DirectExecContext(DirectLoaderContext ctx) throws IOException {
		super(ctx, ctx.getPeriods());
	}

	@Override
	protected RadisIdData loadRadisIds() throws IOException {
		return null;
	}

	/**
	 * Gets the field's data from the context.
	 */
	@Override
	protected ByteBuffer getDataMap(FieldDef def) throws IOException {
		return ((DirectLoaderContext) ctx).getFieldData(def.getLongName());
	}
}
