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
