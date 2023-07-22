
# RunRadisJM

[RunRadis]: https://github.com/adamfocht/runradis "RunRadis"
[Stock Investor Pro]: https://www.aaii.com/stock-investor-pro "Stock Investor Pro"

RunRadisJM is a Java implementation of [RunRadis][], which is a stock screen back-tester based
on data installed as part of [Stock Investor Pro][] from [AAII](https://www.aaii.com/). However,
whereas the [RunRadis][] database (DB) stores all fields within a single record, RunRadisJM
stores each field in a separate memory mapped file.  As such, it is an order of magnitude
faster than [RunRadis][].

This has been tested on a Linux platform, though it has not been tested on a Windows platform.
Nevertheless, it has been tested via Wine HQ, and since Java is very portable, it *should* run
on Windows, as well.

## Installation

The steps to install RunRadisJM are:

1. Download and compile the java code.  It was compiled using java 17, though it will
   probably compile with java 11, as well.  The code can be compiled via:   

			$ javac -d bin @sources.txt
   
2. Set the environment variables, MMAP and SI_PRO.

	+ **MMAP**: path to the RunRadisJM DB (i.e., the memory-map data files).  Note: this is not
	  needed by *pick*
	+ **SI_PRO**: path to the *Stock Investor Pro* installation.  This is the directory containing the
	  "Professional" subdirectory.  Note: this is only needed by *dbload* and *pick*.

3. Create the RunRadisJM DB using *dbinit*.

4. Install *Stock Investor Pro* for the starting period and then load it into the RunRadisJM DB
   using *dbload*. Repeat this process for each period to be included within the DB. (An
   archive of *Stock Investor Pro* downloads is available from [AAII](https://www.aaii.com/)
   as a premium service.)
   
   The back-tester assumes that each period in the DB represents one month of data,
   thus the option, "-months 3", will cause it to skip two periods of data in the DB.  In
   addition, loading more than one period of data per month could cause the program to run
   out of memory, as the entire memory-map file for each relevant field must be mapped into
   the memory space of the running program.  In other words,
   *only load one period of data per month*.
   
   That being said, data from different weeks of the month could be loaded into separate
   RunRadisJM DBs.  For instance, one MMAP directory could contain the DB for the first week
   of each month, while another MMAP directory could contain the DB for the third week of
   each month.
   
   The company IDs of the *Stock Investor Pro* data were completely changed in July 2011.
   As a result, when that period's data is loaded into the DB, *dbload* will prompt saying,
   
           company IDs don't match - use tickers instead?
	
   Answer "y" or "yes" to continue loading the data for that period.  It's possible that the
   company IDs may be renumbered again in the future.  If that happens, that prompt will likely
   appear again and, once again, just answer "y" or "yes" to continue.
   
   This is a time consuming process, taking 2-3 hours to load all of the monthly data
   for 2003-2023 on a system with SSD storage.
   
5. Create an *si_prices.txt* file in the $MMAP directory.
   
6. Create a screen definition using
   [RadisScript](http://www.datahelper.com/mi/search.phtml?nofool=youBet&mid=16923940)
   syntax and then back-test it using *bt*.


## Programs

The "programs" are all java classes, found in the *radis* package.  The following are the
"programs" that may be executed:

+ **bt**: back-tests a screen
+ **dbdel**: deletes one or more periods from the DB
+ **dbinit**: initializes/creates the DB
+ **dblist**: lists the periods contained within the DB
+ **dbload**: loads the DB with the current data stored within *Stock Investor Pro*   
+ **fields**: lists the names of all of the fields contained within the DB
+ **pick**: lists the tickers that pass the screen, when applied to the current
   *Stock Investor Pro* installation
+ **screens**: lists the screens contained within a screen definition file

Each of the above may be run via:

		java -cp ClassPath radis.XXX
	
where *ClassPath* is the path to the directory containing the compiled class files, or
the path to a jar file containing the compiled class files, and *XXX* is one of the
above "programs".

### bt

Back-tests a screen.

		$ java -cp bin radis.bt -?
		arg(s): [-<option> <value>]* [<screen-definition-file>]+
		where the options are as follows:
			-begin	  starting date, YYYYMMDD
			-end	  ending date, YYYYMMDD
			-months	  months per period (default=1)
			-min	  min stocks to hold, rest in cash
			-max	  max stocks to hold
			-screen	  screen-name
			-bias	  return for non-surviving equities default=1.0 (i.e., 0% gain)
			-indiv	  show returns for individual tickers
   
A screen definition file may contain one or more screens.  If no "-screen" option is
given, then it uses the first screen appearing within the screen definition file.  In
addition, it may contain a screen named, "basic".  The "basic" screen is used as a
pre-filter; only stocks passing the "basic" screen are candidates for consideration
by the other screens.

The back-tester will append ".txt" to the screen definition file name, if it doesn't
find the file otherwise.

Example:

		$ java -cp bin radis.bt t/basic t/screens -min 10 -max 10 -begin 2011 -end 201110
		PERIOD		   CUR	   YTD	  CAGR	PASSING
		20110128	   0.0	   0.0	   0.0	0
		20110225	   6.1	   6.1	 116.4	10
		20110325	   1.8	   8.0	  65.5	10
		20110429	   7.4	  16.0	  81.6	10
		20110527	  -3.4	  12.0	  41.8	10
		20110624	  -2.0	   9.8	  26.1	10
		20110729	   0.2	  10.0	  21.0	10
		20110826	  -3.5	   6.2	  10.9	6
		20110930	  -6.2	  -0.4	  -0.6	4
		
		MAX DD= -14.2

#### Considerations

+ Does *not* support "Add" or "SOS" statements.

+ Field and variable names are case-insensitive.  For example, "SI Ticker" and "si ticker"
  both refer to the same field.
  
+ Supports the *match()* function, which returns the index of the first occurrence of the
  matching text.  For example:
  
			Keep :MATCH([SI Ticker], "^[A-Z]{1,4}$") > 0
			
+ Supports a "print" statement.  Example:

			Print [SI Ticker], " ", [SI Price]

+ Supports the following fields: "rank", "tied rank", and "% tied rank".

+ Supports the following functions: *average*, *median*.  In addition, it supports: *min* and
  *max*, though they only accept two arguments.  Also supports: *sign* and *abs*.
  Example:

			Create [medpr]: median([SI Price M001], [SI Price M002], [SI Price M003])

+ Supports the following aggregate functions: *count*, *sum*, *average*, *median*, *max*.  Example:

			Set [avg]: average([[SI Gross Margin 12m]])
			
+ In general, it is not necessary to *Deblank* a non-text field, as any non-text field
  referenced by a screen will automatically be "deblanked".
  
+ The *Uses* statement has no effect and can be left out.
		
### dbdel

Deletes one or more periods from the DB.  The argument is the oldest period to be deleted;
all periods from then until the most recent will be deleted.

Example:

		$ java -cp bin radis.dbdel 20230127
		deleting starting with: 20230127
			20230526
			20230428
			20230331
			20230224
			20230127
		Are you sure? (y/n)

### dbinit

Initializes/creates the DB.

*Note:* This only truncates *some* of the files in the RunRadisJM DB.  Others are left intact,
but their content is subsequently overwritten by *dbload*.

Example:

		$ java -cp bin radis.dbinit
		
		*** re-initializing the entire DB: /tmp/radis ***
		
		Are you sure? (y/n) y
		done
		
		$ ls $MMAP
		company.dat  fieldef.dat  period.dat
		$

### dblist

Lists the periods contained within the DB.

Example:

		$ java -cp bin radis.dblist
		...
		20110128
		20110225
		20110325
		20110429
		20110527
		20110624
		20110729
		20110826
		20110930
		...
		Ok
		$

### dbload

Loads the DB with the current data stored within *Stock Investor Pro*.  For the most
part, the loader only loads fields that are numeric or boolean/logical.  The only text
fields that it loads are:

+ si ticker
+ si exchange
+ si company name
+ si country
+ si standard and poor stock

It includes industry and sector data, though it does not currently load data from the
"universal" data file (e.g., standard deviations, etc.).

Example:

		$ export SI_PRO=/sipro/201128
		$ java -cp bin radis.dbload
		...
		add field si depreciation and amortization - q7: si_cfq/dep_cf/q/7.map
		add field si depreciation and amortization - q8: si_cfq/dep_cf/q/8.map
		add field si net current assets per shr q1: si_bsq/ncaps/q/1.map
		...
		/sipro/201128/Professional/Static/si_cfa.dbf:
		numrecs=9883
		numfields=82
		/sipro/201128/Professional/Static/si_date.dbf:
		numrecs=9883
		numfields=64
		Ok
		$

### fields

Lists the names of all of the fields contained within the DB.  All field names are listed
in lower case.

Example:

		$ java -cp bin radis.fields
		...
		si volume--dollar daily avg 3m
		si yield
		si yield high-avg 7 year
		si yield-1 year ago
		si yield-average 3 years
		si yield-average 5 years
		si yield-average 7 years
		si yield-average y1
		si yield-average y2
		si yield-average y3
		...
		$

### pick

Lists the stock picks, i.e., those passing the screen when applied to the current
*Stock Investor Pro* installation.

		$ java -cp bin radis.pick -?
		arg(s): [-<option> <value>]* [<screen-definition-file>]+
		where the options are as follows:
			-min	  min stocks to hold, rest in cash
			-max	  max stocks to hold
			-screen	  screen-name

Example:

		$ java -cp bin radis.pick -min 10 -max 10 t/basic t/screens
		date=20230526
		3pt_relative_value
		DXPE
		SCPL
		MDU
		JACK
		AMWD
		GMS
		MHO
		MLI
		CCS
		BZH

#### Considerations

This program uses the current *Stock Investor Pro* installation, directly;
it does not use the RunRadisJM DB.  Consequently, it is not necessary to
load the RunRadisJM DB prior to running it.
		
### screens

Lists the screens contained within a screen definition file.

Example:

		$ java -cp bin radis.screens t/screens
		optiman
		blue_skies
		advanced
		rabbit
		quality_earnings
		...
		$


## RunRadisJM Database Layout

All numeric data is stored in binary, using the "little-endian" byte order.  In addition,
*date* fields are stored as *floats*, representing the number of days since the start of
the epoch (i.e., 1/1/1970). Integers do not appear within the memory map files; they only
appear in *radisid.map* and some of the \*.dat files.

| Type         | Record size (in bytes) | Invalid/Unknown | Notes |
|--------------|-----------|-------|----|
| LOGICAL (i.e., boolean) | 1 | 0x02 | True=1, False=0 |
| FLOAT   | 4 | IEEE NaN | |
| DATE | 4 | IEEE NaN | |
| INTEGER | 4 | N/A |
| TEXT | fixed length | "#VALUE!" | ASCII text, terminated by a zero byte, if it fits

### company.dat

Contains the unique *Stock Investor Pro* company ID and ticker associated with each
company within the RunRadisJM DB.  The file contains a vector of fixed length data
structures (see [SiProIdent.java](src/radis/datadef/SiProIdent.java)).  The position
of an item within the vector corresponds to the radis company ID, thus the item in
record 25 corresponds to the radis company ID, 25.  Items are never removed from the
vector, though they may be superseded by newer entries, for example, if
a *Stock Investor Pro* company ID goes inactive and the ticker is subsequently
assigned to another company.

### radisid.map

Contains a vector of numbers, the radis company ID associated with each record in the memory
map files.  The entries for each period appear back to back within the file.  If entry 20 of
period 30 contains the number, 77, then that means that record 20 of period 30 across all of
the memory map files is associated with radis company ID 77.  A given radis company ID may
appear in more than one record within *radisid.map*, but it will not appear in more than one
record within a given period.

### period.dat

Contains information about each period contained within the DB.  The file
is a vector of fixed length data structures (see [Period.java](src/radis/datadef/Period.java)).
Each structure contains the date associated with the period (as read from the *Stock Investor Pro*
installation), the number of records, and the absolute record number at which the period's data
begins within each of the memory map files.

### fieldef.dat

Contains information about each field stored within the DB.  The file contains a vector of
fixed length data structures (see [FieldDef.java](src/radis/datadef/FieldDef.java)).

### si_prices.txt

A "csv" file containing price corrections, sorted in ascending order by date.  Prices from
this file override prices extracted from *Stock Investor Pro*, and each line has the following
format:

		YYYYMMDD,Ticker,Price

Example:

		20040430,RDI,7.21
		20040430,VSNT,2.08
		20040903,BBBB,18.93
		20040903,FCH,11.96
		20040903,LECO,31.25
		20040903,QLTY,6.779

### *.map (memory map file)

Each memory map file contains the data for a single field.  Records are arranged in one-to-one
correspondence with the records in *radisid.map*, with the entries for each period appearing
back to back.  The path and file name of each file is derived from the file name and the short
field name, as they appear within the *Stock Investor Pro* installation's data dictionary.

## Code Considerations

### Text Fields

The list of text fields to be loaded is specified via the *textField* variable, found in
[DdLoader.java](src/radis/dbf/DdLoader.java).

### Filtering

The loader, *dbload*, performs filtering.  The evaluation code takes advantage of the
pre-filtering done by the loader and evaluates them to constant expressions, if they appear
within the screen definition.  As such, if the filter in the loader is changed, then the
corresponding evaluation code must also be changed.  The loader filters out companies
as specified in the table below:

| Description | Filter Class | Evaluation Class |
|--------------|-----------|-------|
| OTC stocks | CompanyDbf | Expression.eval() case tok.NEQ |
| ADR/ADS stocks  | CompanyDbf | ExecContext.getData() case "si adr/ads stock" |
| tickers that are more than four characters long or contain non-alphabetic characters | CompanyDbf | Expression.match() switch (regex) |
| company names that contain any of the following text items: LLC, Partners, Trust, Holding, L.P., LP | CompanyDbf | Expression.match() switch (regex) |


## Contributing

Still under construction...
