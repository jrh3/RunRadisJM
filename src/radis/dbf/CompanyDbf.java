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

package radis.dbf;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * DBF file containing company data.
 */
public class CompanyDbf extends Dbf {
	public static final String TKR_RE = "^[A-Z]{1,4}$";
	public static final String NAME_RE = "L[.]?P[.]?|LLC|Partners|Trust|Holding";

	private static final Pattern TKR_PAT = Pattern.compile(TKR_RE);
	private static final Pattern NAME_PAT = Pattern.compile(NAME_RE);

	private final FieldDescriptor fticker;
	private final FieldDescriptor fcompid; // company id
	private final FieldDescriptor fexchg; // exchange
	private final FieldDescriptor fname; // company name
	private final FieldDescriptor findustry;
	private final FieldDescriptor fsector;
	private final FieldDescriptor fadr; // ADR/ADS flag

	/**
	 * Constructs the object.
	 *
	 * @param fileName name of the DBF file
	 * @throws IOException
	 */
	public CompanyDbf(String fileName) throws IOException {
		super(fileName);

		this.fticker = getField(DdLoader.TICKER);
		this.fcompid = getField(DdLoader.COMPANY_ID);
		this.fexchg = getField("exchange");
		this.fname = getField("company");
		this.findustry = getField("ind_3_dig");
		this.fsector = getField("ind_2_dig");
		this.fadr = getField("adr");
	}

	/**
	 * Gets the next company record, filtering out those that should be discarded.
	 */
	@Override
	public boolean nextRecord() {

		while (super.nextRecord()) {
			if ("O".equals(getExchange())) {
				// OTC
				continue;
			}

			if (isAdrAds()) {
				continue;
			}

			if (!TKR_PAT.matcher(getTicker()).matches()) {
				// invalid ticker
				continue;
			}

			if (NAME_PAT.matcher(getName()).find()) {
				// LLC or some other undesired company name
				continue;
			}

			if (getCompanyId().isEmpty()) {
				continue;
			}

			// this company passed all of the filters - keep it
			return true;
		}

		// no more records
		return false;
	}

	public FieldDescriptor getFticker() {
		return fticker;
	}

	public FieldDescriptor getFcompid() {
		return fcompid;
	}

	public String getTicker() {
		return getField(fticker);
	}

	public String getCompanyId() {
		return getField(fcompid);
	}

	public String getExchange() {
		return getField(fexchg);
	}

	public String getName() {
		return getField(fname);
	}

	public String getIndustry() {
		return getField(findustry);
	}

	public String getSector() {
		return getField(fsector);
	}

	/**
	 * @return {@code true} if the current record refers to an ADR/ADS company
	 */
	public boolean isAdrAds() {
		return !"F".equals(getField(fadr));
	}
}
