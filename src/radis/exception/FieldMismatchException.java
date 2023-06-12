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

package radis.exception;

/**
 * Exception indicating that a field in the new SI Pro DB is incompatible with
 * the corresponding field in the mmap DB.
 */
public class FieldMismatchException extends LoaderException {
	private static final long serialVersionUID = 1L;

	public FieldMismatchException() {
	}

	public FieldMismatchException(String message) {
		super(message);
	}

	public FieldMismatchException(Throwable cause) {
		super(cause);
	}

	public FieldMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

}
