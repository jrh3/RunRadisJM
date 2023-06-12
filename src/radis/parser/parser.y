%{

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
	byaccj\yacc -Jclass=tok -Jsemantic=OpItem -Jpackage=radis.compiler parser.y
*/

import radis.op.*;
import radis.InternalException;
%}

%token NONE 1
%token INTCON FLOATCON STRCON VAR SCRNM AGGVAR DEFINE END USES DEBLANK
%token KEEP SORT TOP PLUSTIES CREATE REPLACE SET UNIQUE FIRST LAST
%token PRINT BLANK ASCENDING DESCENDING WITH NOT COLON ADD SOS OVERLAP
%token PAD ADDNOPAD TO SCORE AVERAGE MEDIAN SUM COUNT LPAREN RPAREN
%token AGGAVERAGE AGGMEDIAN AGGMAX
%token OR AND IF COMMA
%token MIN MAX NOW NOW_VAR SIGN ABS LENGTH LEFT RIGHT MID
%token MATCH ADDSCREEN PADLIST ZTRUE ZFALSE CONCAT
%token SUB MULT
%token BOOL DATE STR NUM ANY

%right COMMA
%left EQ NEQ LT GT LE GE
%left PLUS MINUS AMPER
%left TIMES DIV
%left POW
%nonassoc NEG

%start main

%%

main:
		screens
;

screens:
		screens screen_def		{ $$ = null; }
	|	screen_def				{ $$ = null; }
;

screen_def:
		DEFINE SCRNM actions END	{ $$ = null;  parserDefineScreen($2.strval(), $3); }
;

actions:
		actions action			{ $$ = new Op(COMMA, $1, $2); }
	|	action					{ $$ = $1; }
;

action:
		USES variables			{ $$ = new Op(USES, $2); }
	|	DEBLANK variables		{ $$ = new Op(DEBLANK, $2); }
	|	KEEP COLON expr			{ $$ = new Op(KEEP, $3); }
	|	SORT sort_ops			{ $$ = new Op(SORT, $2); }
	|	TOP COLON INTCON tie_opt	{ $$ = new Op(TOP, $3, False, $4); }
	|	TOP COLON FLOATCON tie_opt	{ $$ = new Op(TOP, $3, True, $4); }
	|	CREATE VAR COLON expr	{ $$ = new Op(CREATE, $2, $4); }
	|	repl_stmt				{ $$ = $1; }
	|	SET VAR COLON expr		{ $$ = new Op(SET,$2,$4); }
	|	UNIQUE FIRST VAR		{ $$ = new Op(UNIQUE,$3,True); }
	|	UNIQUE LAST VAR			{ $$ = new Op(UNIQUE,$3,False); }
	|	ADD add_ops				{ $$ = $2; }
	|	ADDNOPAD add_nopad_ops	{ $$ = $2; }
	|	SOS						{ $$ = new Op(SOS, IntOne); }
	|	SOS COLON INTCON		{ $$ = new Op(SOS,$3); }
	|	OVERLAP					{ $$ = $1; }
	|	PAD pad_ops				{ $$ = $2; }
	|	PRINT args				{ $$ = new Op(PRINT, $2); }
;

repl_stmt:
		REPLACE VAR COLON expr WITH COLON expr	{ $$ = new Op(REPLACE,$2,$4,$7); }
	|	REPLACE VAR BLANK WITH COLON expr		{ $$ = new Op(REPLACE,$2,EmptyStr,$6); }
;

sort_ops:
		sort_ops sort_op		{ $$ = new Op(COMMA, $1, $2); }
	|	sort_op					{ $$ = $1; }
;

sort_op:
		ASCENDING VAR			{ $$ = new Op(ASCENDING,$2); }
	|	DESCENDING VAR			{ $$ = new Op(DESCENDING,$2); }
;

add_ops:
		SCRNM COLON INTCON			{ $$ = new Op(ADDSCREEN, $1, IntOne, $3, $3, True); }
	|	SCRNM COLON INTCON TO COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, $3, $6, $6, True); }
	|	SCRNM COLON INTCON SCORE COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, IntOne, $3, $6, True); }
	|	SCRNM COLON INTCON TO COLON INTCON SCORE COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, $3, $6, $9, True); }
;

add_nopad_ops:
		SCRNM COLON INTCON			{ $$ = new Op(ADDSCREEN, $1, IntOne, $3, $3, False); }
	|	SCRNM COLON INTCON TO COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, $3, $6, $6, False); }
	|	SCRNM COLON INTCON SCORE COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, IntOne, $3, $6, False); }
	|	SCRNM COLON INTCON TO COLON INTCON SCORE COLON INTCON	{ $$ = new Op(ADDSCREEN, $1, $3, $6, $9, False); }
;

pad_ops:
		COLON INTCON pad_list	{ $$ = new Op(PAD, $2, $3); }
;

pad_list:
		SCRNM COLON INTCON		{ $$ = new Op(PADLIST, $1, $3); }
	|	pad_list SCRNM COLON INTCON		{ $$ = new Op(COMMA, $1, new Op(PADLIST, $2, $4)); }
;

tie_opt:
		PLUSTIES				{ $$ = True; }
	|							{ $$ = False; }
;

variables:
		variables VAR			{ $$ = new Op(COMMA, $1, $2); }
	|	VAR						{ $$ = $1; }
;

expr:
		LPAREN expr RPAREN					{ $$ = $2; }
	|	NOT LPAREN expr RPAREN				{ $$ = new Op(NOT,$3); }
	|	OR LPAREN args RPAREN				{ $$ = comma2binary(OR, $3); }
	|	AND LPAREN args RPAREN				{ $$ = comma2binary(AND, $3); }
	|	IF LPAREN expr COMMA expr COMMA expr RPAREN
											{ $$ = new Op(IF,$3,$5,$7); }

	|	MIN LPAREN args RPAREN				{ $$ = comma2binary(MIN, $3); }
	|	MAX LPAREN AGGVAR RPAREN			{ $$ = new Op(AGGMAX,$3); }
	|	MAX LPAREN args RPAREN				{ $$ = comma2binary(MAX, $3); }
	|	LEFT LPAREN expr COMMA expr RPAREN	{ $$ = new Op(LEFT, $3, $5); }
	|	RIGHT LPAREN expr COMMA expr RPAREN	{ $$ = new Op(RIGHT, $3, $5); }
	|	MID LPAREN expr COMMA expr COMMA expr RPAREN		{ $$ = new Op(MID, $3, $5, $7); }
	|	MATCH LPAREN expr COMMA STRCON COMMA INTCON RPAREN	{ $$ = new Op(MATCH, $3, $5, BoolCon($7.intval() > 0)); }
	|	MATCH LPAREN expr COMMA STRCON RPAREN	{ $$ = new Op(MATCH, $3, $5, True); }

	|	expr EQ expr						{ $$ = new Op(EQ,$1,$3); }
	|	expr NEQ expr						{ $$ = new Op(NEQ,$1,$3); }
	|	expr LT expr						{ $$ = new Op(LT,$1,$3); }
	|	expr GT expr						{ $$ = new Op(GT,$1,$3); }
	|	expr LE expr						{ $$ = new Op(LE,$1,$3); }
	|	expr GE expr						{ $$ = new Op(GE,$1,$3); }
	|	expr AMPER expr						{ $$ = new Op(CONCAT,$1,$3); }
	|	expr PLUS expr						{ $$ = new Op(ADD,$1,$3); }
	|	expr MINUS expr						{ $$ = new Op(SUB,$1,$3); }
	|	expr TIMES expr						{ $$ = new Op(MULT,$1,$3); }
	|	expr DIV expr						{ $$ = new Op(DIV,$1,$3); }
	|	expr POW expr						{ $$ = new Op(POW,$1,$3); }
	|	MINUS expr %prec NEG				{ $$ = new Op(NEG,$2); }
	|	VAR									{ $$ = $1; }
	|	STRCON								{ $$ = $1; }
	|	INTCON								{ $$ = FloatCon($1.intval()); }
	|	FLOATCON							{ $$ = $1; }
	|	NOW_VAR								{ $$ = CurTime; }
	|	NOW LPAREN RPAREN					{ $$ = CurTime; }
	|	AVERAGE LPAREN AGGVAR RPAREN		{ $$ = new Op(AGGAVERAGE,$3); }
	|	AVERAGE LPAREN args RPAREN			{ $$ = new Op(AVERAGE,$3); }
	|	MEDIAN LPAREN AGGVAR RPAREN			{ $$ = new Op(AGGMEDIAN,$3); }
	|	MEDIAN LPAREN args RPAREN			{ $$ = new Op(MEDIAN,$3); }
	|	SUM LPAREN AGGVAR RPAREN			{ $$ = new Op(SUM,$3); }
	|	COUNT LPAREN AGGVAR RPAREN			{ $$ = new Op(COUNT,$3); }
	|	SIGN LPAREN expr RPAREN				{ $$ = new Op(SIGN,$3); }
	|	ABS LPAREN expr RPAREN				{ $$ = new Op(ABS,$3); }
	|	LENGTH LPAREN expr RPAREN			{ $$ = new Op(LENGTH,$3); }
;

args:
		args COMMA expr				{ $$ = new Op(COMMA, $1, $3); }
	|	expr						{ $$ = $1; }
;

%%


/**
 * This must be overridden in subclasses.
 */
int yylex()
{
	throw new InternalException("yylex is not defined");
}

/**
 * This must be overridden in subclasses.
 */
void parserDefineScreen(String screenm, OpItem screenDef)
{
	throw new InternalException("defineScreen is not defined");
}

void yyerror(String msg)
{
	System.err.println(msg);
	System.exit(1);
}

/**
 * Walk a comma-delimited tree and replace the commas with a binary operator
 * of the specified type.
 */
OpItem comma2binary(int type, OpItem p)
{
	if(p.type() != COMMA) {
		return p;
	}

	return new Op(type,
					comma2binary(type, p.left()),
					comma2binary(type, p.right()));
}

static public IntToken IntCon(int v)
{
	if(v == 1) {
		return IntOne;

	} else {
		return new IntToken(INTCON, v);
	}
}

static public FloatToken FloatCon(double v)
{
	return new FloatToken(FLOATCON, v);
}

static public FloatToken FloatCon(int v)
{
	return new FloatToken(FLOATCON, v);
}

static public StrToken StrCon(String v)
{
	if(v.length() == 0) {
		return EmptyStr;
	}

	return new StrToken(STRCON, v);
}

static public BoolToken BoolCon(boolean v)
{
	return(v ? True : False);
}

static public Op OpList(int optype)
{
	return new Op(optype);
}

static final IntToken IntOne = new IntToken(INTCON, 1);
static final StrToken EmptyStr = new StrToken(STRCON, "");
static final BoolToken True = new BoolToken(ZTRUE, true);
static final BoolToken False = new BoolToken(ZFALSE, false);
static final Action CurTime = new Action(NOW);
