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
 
/*
Generate via:
	jflex lexer.l
*/

package radis.parser;


import radis.Util;
import radis.op.*;

import java.io.IOException;
import java.util.HashMap;

%%
 
%byaccj
%unicode
%class RadisParser
%public
%abstract
%function scan
%line
%column
%ignorecase

%{
	static final HashMap<String,OpItem> specialVarTbl
					= new HashMap<String,OpItem>(53);

	/**
	Adds an entry to the specialVarTbl.
	@param name		name of the variable to be added
	@param val		token type of the variable
	*/
	static void makeSpec(String name, int val)
	{
		specialVarTbl.put(Util.normalizeName(name), new Op(val));
	}


	// initialize table
	static
	{
		makeSpec("SI Weekly data date", tok.NOW_VAR);
	}

	abstract public void defineScreen(String screenm, OpItem screenDef);

	
	tok parser = new tok()
		{
			@Override
			int yylex()
			{
				try {
					return scan();

				} catch(IOException ex) {
					System.err.println("problem with input file: " + ex);
					return -1;
				}
			}
			
			@Override
			void parserDefineScreen(String screenm, OpItem screenDef)
			{
				defineScreen(screenm, screenDef);
			}
			
			@Override
			void yyerror(String msg)
			{
				System.err.println("" + (yyline+1) + ":" + (yycolumn+1) + ": ");
				System.err.println(msg);
				System.exit(1);
			}
		};

	int returnToken(int type)
	{
		parser.yylval = new Action(type);
		return type;
	}

	public void parse()
	{
		parser.yyparse();
	}

	static String stripWrapper(String s)
	{
		return s.substring(1, s.length()-1);
	}

%}


%xstate SKIP2NL DEFINITION

ws			=	[ \t\f\r\n]
dquote		=	\"
lbrkt		=	"["
rbrkt		=	"]"
lcurl		=	"{"
rcurl		=	"}"
intnum		=	[0-9]+
expsfx		=	e[\-]?[0-9]+
decsfx		=	[.][0-9]*{expsfx}?
floatsfx	=	{decsfx}|{expsfx}
floatnum	=	({intnum}{floatsfx})|{decsfx}
symbol		=	[a-zA-Z][a-zA-Z0-9_]*

%%

<YYINITIAL>{
{ws}+		{ }

Define		{
				yybegin(DEFINITION);
				return returnToken(tok.DEFINE);
			}

.			{ yybegin(SKIP2NL); }

}		/* end YYINITIAL */


<SKIP2NL>{
[^\n]*\n	{ yybegin(YYINITIAL); }
}


<DEFINITION>{

;[^\n]*\n	{	/* skip comments */ }

{ws}+		{	/* skip blanks */ }

({intnum}|{floatnum})[%]	{
			parser.yylval = tok.FloatCon(Double.valueOf(yytext().substring(0, yytext().length()-1)) / 100.0);
			return parser.yylval.type();
		}

{floatnum}	{
			parser.yylval = tok.FloatCon(Double.valueOf(yytext()));
			return parser.yylval.type();
		}

{intnum}	{
			parser.yylval = tok.IntCon(Integer.valueOf(yytext()));
			return parser.yylval.type();
		}

{lbrkt}{lbrkt}[^\]\t\n]+{rbrkt}{rbrkt}	{
			parser.yylval = new StrToken(tok.AGGVAR, Util.normalizeName(stripWrapper(stripWrapper(yytext()))));
			return parser.yylval.type();
		}

{lbrkt}[^\]\t\n]+{rbrkt}	{
			String normvar = Util.normalizeName(stripWrapper(yytext()));
			parser.yylval = specialVarTbl.get(normvar);

			if(parser.yylval == null) {
				parser.yylval = new StrToken(tok.VAR, normvar);
			}

			return parser.yylval.type();
		}

{lcurl}[^\]\t\n]+{rcurl}	{
			parser.yylval = new StrToken(tok.SCRNM, stripWrapper(yytext()));
			return parser.yylval.type();
		}

{dquote}{intnum}[\-][^\"]*{dquote}	{
			parser.yylval = tok.IntCon(Integer.valueOf(yytext().substring(1,yytext().indexOf('-'))));
			return parser.yylval.type();
		}

{dquote}[^\"]*{dquote}	{
			parser.yylval = tok.StrCon(stripWrapper(yytext()));
			return parser.yylval.type();
		}
		
Define    { return returnToken(tok.DEFINE); }
Uses    { return returnToken(tok.USES); }
Deblank    { return returnToken(tok.DEBLANK); }
Keep    { return returnToken(tok.KEEP); }
Sort    { return returnToken(tok.SORT); }
Top    { return returnToken(tok.TOP); }
PlusTies    { return returnToken(tok.PLUSTIES); }
Create    { return returnToken(tok.CREATE); }
Replace    { return returnToken(tok.REPLACE); }
Set    { return returnToken(tok.SET); }
Add    { return returnToken(tok.ADD); }
AddNoPad    { return returnToken(tok.ADDNOPAD); }
Sos    { return returnToken(tok.SOS); }
Overlap    { return returnToken(tok.OVERLAP); }
Pad    { return returnToken(tok.PAD); }
Unique    { return returnToken(tok.UNIQUE); }
To    { return returnToken(tok.TO); }
Score    { return returnToken(tok.SCORE); }
First    { return returnToken(tok.FIRST); }
Last    { return returnToken(tok.LAST); }
Print    { return returnToken(tok.PRINT); }
Blank    { return returnToken(tok.BLANK); }
Ascending    { return returnToken(tok.ASCENDING); }
Descending    { return returnToken(tok.DESCENDING); }
With    { return returnToken(tok.WITH); }
NOW    { return returnToken(tok.NOW); }
OR    { return returnToken(tok.OR); }
AND    { return returnToken(tok.AND); }
NOT    { return returnToken(tok.NOT); }
IF    { return returnToken(tok.IF); }
AVERAGE    { return returnToken(tok.AVERAGE); }
MEDIAN    { return returnToken(tok.MEDIAN); }
SUM    { return returnToken(tok.SUM); }
COUNT    { return returnToken(tok.COUNT); }
MIN    { return returnToken(tok.MIN); }
MAX    { return returnToken(tok.MAX); }
SIGN    { return returnToken(tok.SIGN); }
ABS    { return returnToken(tok.ABS); }
LEN    { return returnToken(tok.LENGTH); }
LEFT    { return returnToken(tok.LEFT); }
RIGHT    { return returnToken(tok.RIGHT); }
MID    { return returnToken(tok.MID); }
MATCH    { return returnToken(tok.MATCH); }

End		{
			yybegin(SKIP2NL);
			return returnToken(tok.END);
		}

{symbol}	{ parser.yyerror(": '" + yytext() + "' is not a keyword"); }

"("		{ return returnToken(tok.LPAREN); }
")"		{ return returnToken(tok.RPAREN); }
"="		{ return returnToken(tok.EQ); }
"<>"	{ return returnToken(tok.NEQ); }
"<"		{ return returnToken(tok.LT); }
">"		{ return returnToken(tok.GT); }
"<="	{ return returnToken(tok.LE); }
">="	{ return returnToken(tok.GE); }
"+"		{ return returnToken(tok.PLUS); }
"-"		{ return returnToken(tok.MINUS); }
"*"		{ return returnToken(tok.TIMES); }
"/"		{ return returnToken(tok.DIV); }
"^"		{ return returnToken(tok.POW); }
"&"		{ return returnToken(tok.AMPER); }
","		{ return returnToken(tok.COMMA); }
":"		{ return returnToken(tok.COLON); }

.		{
			parser.yyerror(": invalid character '" + yytext() + "'");
		}

}	/* end of DEFINITION */
