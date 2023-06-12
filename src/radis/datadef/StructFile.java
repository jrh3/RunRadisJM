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

package radis.datadef;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import radis.exception.InternalException;

/**
 * A "structured" file, that is, a file containing records of a particular data
 * type.
 *
 * @param <T> type of data contained within the file
 */
public class StructFile<T> {
	private final String path;

	/**
	 * Constructs the object, but does not create or read the file.
	 *
	 * @param path path to the file that will contain the data
	 */
	public StructFile(String path) {
		this.path = path;
	}

	/**
	 * Reads the records from the file.
	 *
	 * @param recsz record size
	 * @param maker function that reads a byte buffer and returns a new data element
	 * @return a list of the data elements that were read from the file
	 * @throws IOException
	 */
	public List<T> read(int recsz, Function<ByteBuffer, T> maker) throws IOException {
		try (var fd = FileChannel.open(Path.of(path), StandardOpenOption.READ)) {
			int size = (int) fd.size();
			int nrecs = size / recsz;
			var vec = new ArrayList<T>(nrecs);
			var buf = ByteBuffer.allocateDirect(size);

			if (fd.read(buf) != size) {
				throw new InternalException("couldn't read entire data file");
			}

			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.rewind();
			for (int x = 0; x < nrecs; ++x) {
				vec.add(maker.apply(buf));
			}

			return vec;
		}
	}

	/**
	 * Writes the records to the file. First writes them to a temp file and then
	 * atomically renamed the temp file to the final file name.
	 *
	 * @param recsz  record size
	 * @param list   list of data elements to be written to the file
	 * @param writer function that writes a data element to a buffer
	 * @throws IOException
	 */
	public void write(int recsz, Collection<T> list, BiConsumer<T, ByteBuffer> writer) throws IOException {
		int size = list.size() * recsz;
		var buf = ByteBuffer.allocateDirect(size);

		buf.order(ByteOrder.LITTLE_ENDIAN);
		for (var t : list) {
			writer.accept(t, buf);
		}

		buf.rewind();

		var tmp = Path.of(path + ".tmp");
		try (var fd = FileChannel.open(tmp, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {

			if (fd.write(buf) != size) {
				throw new InternalException("couldn't write entire data file");
			}
		}

		Files.move(tmp, Path.of(path), StandardCopyOption.ATOMIC_MOVE);
	}
}
