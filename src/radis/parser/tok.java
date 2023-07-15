//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package radis.parser;



//#line 2 "parser.y"

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
	byaccj\yacc -Jclass=tok -Jsemantic=OpItem -Jpackage=radis.parser parser.y
*/

import radis.op.*;
import radis.exception.InternalException;
//#line 46 "tok.java"




public class tok
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:OpItem
String   yytext;//user variable to return contextual strings
OpItem yyval; //used to return semantic vals from action routines
OpItem yylval;//the 'lval' (result) I got from yylex()
OpItem valstk[] = new OpItem[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new OpItem();
  yylval=new OpItem();
  valptr=-1;
}
final void val_push(OpItem val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    OpItem[] newstack = new OpItem[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final OpItem val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final OpItem val_peek(int relative)
{
  return valstk[valptr-relative];
}
final OpItem dup_yyval(OpItem val)
{
  return val;
}
//#### end semantic value section ####
public final static short NONE=1;
public final static short INTCON=257;
public final static short FLOATCON=258;
public final static short STRCON=259;
public final static short VAR=260;
public final static short SCRNM=261;
public final static short AGGVAR=262;
public final static short DEFINE=263;
public final static short END=264;
public final static short USES=265;
public final static short DEBLANK=266;
public final static short KEEP=267;
public final static short SORT=268;
public final static short TOP=269;
public final static short PLUSTIES=270;
public final static short CREATE=271;
public final static short REPLACE=272;
public final static short SET=273;
public final static short UNIQUE=274;
public final static short FIRST=275;
public final static short LAST=276;
public final static short PRINT=277;
public final static short BLANK=278;
public final static short ASCENDING=279;
public final static short DESCENDING=280;
public final static short WITH=281;
public final static short NOT=282;
public final static short COLON=283;
public final static short ADD=284;
public final static short SOS=285;
public final static short OVERLAP=286;
public final static short PAD=287;
public final static short ADDNOPAD=288;
public final static short TO=289;
public final static short SCORE=290;
public final static short AVERAGE=291;
public final static short MEDIAN=292;
public final static short SUM=293;
public final static short COUNT=294;
public final static short LPAREN=295;
public final static short RPAREN=296;
public final static short AGGAVERAGE=297;
public final static short AGGMEDIAN=298;
public final static short AGGMAX=299;
public final static short OR=300;
public final static short AND=301;
public final static short IF=302;
public final static short COMMA=303;
public final static short MIN=304;
public final static short MAX=305;
public final static short NOW=306;
public final static short NOW_VAR=307;
public final static short SIGN=308;
public final static short ABS=309;
public final static short LENGTH=310;
public final static short LEFT=311;
public final static short RIGHT=312;
public final static short MID=313;
public final static short MOD=314;
public final static short MATCH=315;
public final static short ADDSCREEN=316;
public final static short PADLIST=317;
public final static short ZTRUE=318;
public final static short ZFALSE=319;
public final static short CONCAT=320;
public final static short SUB=321;
public final static short MULT=322;
public final static short BOOL=323;
public final static short DATE=324;
public final static short STR=325;
public final static short NUM=326;
public final static short ANY=327;
public final static short EQ=328;
public final static short NEQ=329;
public final static short LT=330;
public final static short GT=331;
public final static short LE=332;
public final static short GE=333;
public final static short PLUS=334;
public final static short MINUS=335;
public final static short AMPER=336;
public final static short TIMES=337;
public final static short DIV=338;
public final static short POW=339;
public final static short NEG=340;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    3,    3,    4,    4,    4,    4,
    4,    4,    4,    4,    4,    4,    4,    4,    4,    4,
    4,    4,    4,    4,    9,    9,    7,    7,   14,   14,
   10,   10,   10,   10,   11,   11,   11,   11,   12,   15,
   15,    8,    8,    5,    5,    6,    6,    6,    6,    6,
    6,    6,    6,    6,    6,    6,    6,    6,    6,    6,
    6,    6,    6,    6,    6,    6,    6,    6,    6,    6,
    6,    6,    6,    6,    6,    6,    6,    6,    6,    6,
    6,    6,    6,    6,    6,    6,    6,   13,   13,
};
final static short yylen[] = {                            2,
    1,    2,    1,    4,    2,    1,    2,    2,    3,    2,
    4,    4,    4,    1,    4,    3,    3,    2,    2,    1,
    3,    1,    2,    2,    7,    6,    2,    1,    2,    2,
    3,    6,    6,    9,    3,    6,    6,    9,    3,    3,
    4,    1,    0,    2,    1,    3,    4,    4,    4,    8,
    4,    4,    4,    6,    6,    8,    8,    6,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    6,    2,    1,    1,    1,    1,    1,    3,    4,    4,
    4,    4,    4,    4,    4,    4,    4,    3,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    2,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   22,    0,
    0,    0,    6,   14,   45,    0,    0,    0,    0,    0,
    0,   28,    0,    0,    0,    0,    0,    0,   75,   76,
   74,   73,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   77,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   18,    0,    0,
   23,    0,   19,    4,    5,   44,    0,   29,   30,   27,
    0,    0,    0,    0,    0,    0,   16,   17,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   72,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   21,    0,    0,   42,   11,   12,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   46,    0,    0,    0,    0,    0,    0,   78,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   70,    0,    0,
    0,    0,    0,    0,    0,   47,   79,   80,   81,   82,
   83,   84,   48,   49,    0,   51,   52,   53,   85,   86,
   87,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   40,    0,    0,    0,    0,    0,   54,   55,
    0,   71,   58,    0,    0,   33,   41,    0,   37,    0,
    0,    0,    0,    0,   50,   56,   57,    0,    0,   34,
   38,
};
final static short yydgoto[] = {                          2,
    3,    4,   22,   23,   26,   65,   31,  128,   24,   68,
   73,   71,   66,   32,  172,
};
final static short yysindex[] = {                      -256,
 -220,    0, -256,    0, 1386,    0, -213, -213, -229, -251,
 -227, -192, -188, -179, -223,   69, -152, -173,    0, -172,
 -149,  962,    0,    0,    0, -141, -141,   69, -136, -135,
 -251,    0, -198, -156, -265, -155, -131, -129,    0,    0,
    0,    0, -163, -157, -154, -153, -148,   69, -147, -146,
 -145, -144, -142, -140,    0, -139, -137, -134, -133, -132,
 -127, -122, -120,   69, 1347, -164, -143,    0, -114, -113,
    0, -124,    0,    0,    0,    0, 1347,    0,    0,    0,
 -116, -116,   69, -121,   69,   69,    0,    0,   69,  -88,
  -29,  -74,  -73,  -94,   69,   69,   69,   69,   30, -105,
   69,   69,   69,   69,   69,   69,   69,   69,    0,   69,
   69,   69,   69,   69,   69,   69,   69,   69,   69,   69,
   69,   69,  -65,    0,  -66,  -61,    0,    0,    0, 1347,
  -86, -233, 1347,  -35,  -98, -284,  -97, -266,  -96,  -95,
    0, -258, -257,  671, -254,  -87, -253,    0,  100,  175,
  248,  683,  801,  813,  825,  862, -272, -272, -272, -272,
 -272, -272, -322, -322, -322, -128, -128,    0, 1347, -219,
  -75,  -51, -182,   69,  -68,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   69,    0,    0,    0,    0,    0,
    0,   69,   69,   69,   69,  -27,  -37,  -34,   -9,  -33,
  -32,  -31, 1347,   69,  874,  321,  394,  886,  464, -252,
   -3,   -2,    0,   -1,    1,    2, 1347,   69,    0,    0,
   69,    0,    0,    3,  -23,    0,    0,  -22,    0,  534,
  604,  -39,  -14,  -13,    0,    0,    0,   17,   34,    0,
    0,
};
final static short yyrindex[] = {                         0,
    0,    0,  305,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  987,    0,    0,
    0,    0,    0,    0,    0, 1012, 1037,    0,    0,    0,
 1062,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -263, 1087,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 1112,    0,    0,    0,
 1137, 1137,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, 1162,
    0,    0, 1187,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  415,  485,  555,  625,
  695,  765,  196,  269,  342, -151,  121,    0,  815, 1212,
    0, 1237, 1262,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0, 1287,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, 1312,    0,    0,    0,
    0,    0,    0,    0, 1337,    0,    0, 1362,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
    0,  304,    0,  286,  301,  -28,    0,  228,    0,    0,
    0,    0,  -64,  280,    0,
};
final static int YYTABLESIZE=1686;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         77,
   89,   89,   89,   89,   89,   89,    1,   89,   89,   89,
   89,  178,   84,   89,  119,  120,  121,   85,  122,   94,
   89,   89,   89,   89,   89,  136,  138,   29,   30,  180,
  142,  143,   89,  145,  147,  109,  122,  183,  184,   89,
    5,  186,  188,  223,  122,  122,   25,  175,  122,  122,
  224,   37,   38,   28,  130,   33,  132,  133,   81,   82,
  134,  116,  117,  118,  119,  120,  121,   34,  144,  197,
  198,   35,  149,  150,  151,  152,  153,  154,  155,  156,
   36,  157,  158,  159,  160,  161,  162,  163,  164,  165,
  166,  167,  168,  169,  110,  111,  112,  113,  114,  115,
  116,  117,  118,  119,  120,  121,  201,  202,   67,   69,
   70,   72,   68,   68,   68,   68,   68,   68,   76,   68,
   68,   68,   68,   78,   79,   68,   83,   86,   87,   68,
   88,   89,   68,   68,   68,   68,   68,   90,  122,  123,
   91,   92,  124,  125,   68,  203,   93,   95,   96,   97,
   98,   68,   99,  127,  100,  101,  205,  102,  126,  131,
  103,  104,  105,  206,  207,  208,  209,  106,   39,   40,
   41,   42,  107,  135,  108,  217,   68,   68,   68,   68,
   68,   68,   68,   68,   68,   68,   68,  139,  140,  230,
  148,  170,  231,   43,  171,  173,  174,  177,  179,  181,
  182,  141,   44,   45,   46,   47,   48,  199,  187,  200,
  121,   49,   50,   51,  204,   52,   53,   54,   55,   56,
   57,   58,   59,   60,   61,   62,   63,   39,   40,   41,
   42,  210,  137,  110,  111,  112,  113,  114,  115,  116,
  117,  118,  119,  120,  121,  211,   64,  213,  212,  214,
  215,  216,   43,  225,  226,  227,  237,  228,  229,  232,
  176,   44,   45,   46,   47,   48,  233,  234,  238,  239,
   49,   50,   51,  240,   52,   53,   54,   55,   56,   57,
   58,   59,   60,   61,   62,   63,   39,   40,   41,   42,
  241,  146,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  120,  121,    1,   64,    6,   75,   27,  129,
   80,   43,    0,    0,    0,    0,    0,    0,    0,    0,
   44,   45,   46,   47,   48,   39,   40,   41,   42,   49,
   50,   51,    0,   52,   53,   54,   55,   56,   57,   58,
   59,   60,   61,   62,   63,    0,    0,    0,    0,    0,
   43,    0,    0,    0,    0,    0,    0,    0,    0,   44,
   45,   46,   47,   48,   64,    0,    0,    0,   49,   50,
   51,    0,   52,   53,   54,   55,   56,   57,   58,   59,
   60,   61,   62,   63,   69,   69,   69,   69,   69,   69,
    0,   69,   69,   69,   69,  189,    0,   69,    0,    0,
    0,   69,    0,   64,   69,   69,   69,   69,   69,    0,
    0,    0,    0,    0,    0,    0,   69,    0,    0,    0,
    0,    0,    0,   69,    0,    0,    0,  110,  111,  112,
  113,  114,  115,  116,  117,  118,  119,  120,  121,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   69,   69,
   69,   69,   69,   69,   69,   69,   69,   69,   69,   66,
   66,   66,   66,   66,   66,    0,   66,   66,   66,   66,
  190,    0,   66,    0,    0,    0,   66,    0,    0,   66,
   66,   66,   66,   66,    0,    0,    0,    0,    0,    0,
    0,   66,    0,    0,    0,    0,    0,    0,   66,    0,
    0,    0,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  120,  121,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   66,   66,   66,   66,   66,   66,   66,
   66,   66,   67,   67,   67,   67,   67,   67,    0,   67,
   67,   67,   67,  191,    0,   67,    0,    0,    0,   67,
    0,    0,   67,   67,   67,   67,   67,    0,    0,    0,
    0,    0,    0,    0,   67,    0,    0,    0,    0,    0,
    0,   67,    0,    0,    0,  110,  111,  112,  113,  114,
  115,  116,  117,  118,  119,  120,  121,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   67,   67,   67,   67,
   67,   67,   67,   67,   67,   65,   65,   65,   65,   65,
   65,    0,   65,   65,   65,   65,  219,    0,   65,    0,
    0,    0,   65,    0,    0,   65,   65,   65,   65,   65,
    0,    0,    0,    0,    0,    0,    0,   65,    0,    0,
    0,    0,    0,    0,   65,    0,    0,    0,  110,  111,
  112,  113,  114,  115,  116,  117,  118,  119,  120,  121,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   65,
   65,   65,   65,   65,   65,   65,   65,   65,   59,   59,
   59,   59,   59,   59,    0,   59,   59,   59,   59,  220,
    0,   59,    0,    0,    0,   59,    0,    0,   59,   59,
   59,   59,   59,    0,    0,    0,    0,    0,    0,    0,
   59,    0,    0,    0,    0,    0,    0,   59,    0,    0,
    0,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   59,   59,   59,   59,   59,   59,   60,   60,
   60,   60,   60,   60,    0,   60,   60,   60,   60,  222,
    0,   60,    0,    0,    0,   60,    0,    0,   60,   60,
   60,   60,   60,    0,    0,    0,    0,    0,    0,    0,
   60,    0,    0,    0,    0,    0,    0,   60,    0,    0,
    0,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   60,   60,   60,   60,   60,   60,   61,   61,
   61,   61,   61,   61,    0,   61,   61,   61,   61,  235,
    0,   61,    0,    0,    0,   61,    0,    0,   61,   61,
   61,   61,   61,    0,    0,    0,    0,    0,    0,    0,
   61,    0,    0,    0,    0,    0,    0,   61,    0,    0,
    0,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   61,   61,   61,   61,   61,   61,   62,   62,
   62,   62,   62,   62,    0,   62,   62,   62,   62,  236,
    0,   62,    0,    0,    0,   62,    0,    0,   62,   62,
   62,   62,   62,    0,    0,    0,    0,    0,    0,    0,
   62,    0,    0,    0,    0,    0,    0,   62,    0,    0,
    0,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   62,   62,   62,   62,   62,   62,   63,   63,
   63,   63,   63,   63,    0,   63,   63,   63,   63,    0,
    0,   63,    0,  185,    0,   63,    0,    0,   63,   63,
   63,   63,   63,    0,    0,  192,    0,    0,    0,    0,
   63,    0,    0,    0,    0,    0,    0,   63,  110,  111,
  112,  113,  114,  115,  116,  117,  118,  119,  120,  121,
  110,  111,  112,  113,  114,  115,  116,  117,  118,  119,
  120,  121,   63,   63,   63,   63,   63,   63,   64,   64,
   64,   64,   64,   64,    0,   64,   64,   64,   64,    0,
    0,   64,    0,    0,    0,   64,    0,    0,   64,   64,
   64,   64,   64,    0,    0,    0,    0,    0,    0,    0,
   64,    0,    0,    0,    0,    0,    0,   64,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   88,   88,
   88,   88,   88,   88,    0,   88,   88,   88,   88,    0,
    0,   88,   64,   64,   64,   64,   64,   64,   88,   88,
   88,   88,   88,  193,    0,    0,    0,    0,    0,    0,
   88,    0,    0,    0,    0,  194,    0,   88,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  195,  110,  111,
  112,  113,  114,  115,  116,  117,  118,  119,  120,  121,
  110,  111,  112,  113,  114,  115,  116,  117,  118,  119,
  120,  121,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  120,  121,  196,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  218,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  221,  110,
  111,  112,  113,  114,  115,  116,  117,  118,  119,  120,
  121,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,  110,  111,  112,  113,  114,  115,  116,
  117,  118,  119,  120,  121,   74,    7,    8,    9,   10,
   11,    0,   12,   13,   14,   15,    0,    0,   16,    0,
    0,    0,    0,    0,    0,   17,   18,   19,   20,   21,
   20,   20,   20,   20,   20,   20,    0,   20,   20,   20,
   20,    0,    0,   20,    0,    0,    0,    0,    0,    0,
   20,   20,   20,   20,   20,    7,    7,    7,    7,    7,
    7,    0,    7,    7,    7,    7,    0,    0,    7,    0,
    0,    0,    0,    0,    0,    7,    7,    7,    7,    7,
    8,    8,    8,    8,    8,    8,    0,    8,    8,    8,
    8,    0,    0,    8,    0,    0,    0,    0,    0,    0,
    8,    8,    8,    8,    8,   10,   10,   10,   10,   10,
   10,    0,   10,   10,   10,   10,    0,    0,   10,    0,
    0,    0,    0,    0,    0,   10,   10,   10,   10,   10,
   24,   24,   24,   24,   24,   24,    0,   24,   24,   24,
   24,    0,    0,   24,    0,    0,    0,    0,    0,    0,
   24,   24,   24,   24,   24,    9,    9,    9,    9,    9,
    9,    0,    9,    9,    9,    9,    0,    0,    9,    0,
    0,    0,    0,    0,    0,    9,    9,    9,    9,    9,
   43,   43,   43,   43,   43,   43,    0,   43,   43,   43,
   43,    0,    0,   43,    0,    0,    0,    0,    0,    0,
   43,   43,   43,   43,   43,   13,   13,   13,   13,   13,
   13,    0,   13,   13,   13,   13,    0,    0,   13,    0,
    0,    0,    0,    0,    0,   13,   13,   13,   13,   13,
   15,   15,   15,   15,   15,   15,    0,   15,   15,   15,
   15,    0,    0,   15,    0,    0,    0,    0,    0,    0,
   15,   15,   15,   15,   15,   31,   31,   31,   31,   31,
   31,    0,   31,   31,   31,   31,    0,    0,   31,    0,
    0,    0,    0,    0,    0,   31,   31,   31,   31,   31,
   39,   39,   39,   39,   39,   39,    0,   39,   39,   39,
   39,    0,    0,   39,    0,    0,    0,    0,    0,    0,
   39,   39,   39,   39,   39,   35,   35,   35,   35,   35,
   35,    0,   35,   35,   35,   35,    0,    0,   35,    0,
    0,    0,    0,    0,    0,   35,   35,   35,   35,   35,
   26,   26,   26,   26,   26,   26,    0,   26,   26,   26,
   26,    0,    0,   26,    0,    0,    0,    0,    0,    0,
   26,   26,   26,   26,   26,   25,   25,   25,   25,   25,
   25,    0,   25,   25,   25,   25,    0,    0,   25,    0,
    0,    0,    0,    0,    0,   25,   25,   25,   25,   25,
   32,   32,   32,   32,   32,   32,    0,   32,   32,   32,
   32,    0,    0,   32,    0,    0,    0,    0,    0,    0,
   32,   32,   32,   32,   32,   36,   36,   36,   36,   36,
   36,    0,   36,   36,   36,   36,    0,    0,   36,    0,
    0,    0,    0,    0,    0,   36,   36,   36,   36,   36,
    7,    8,    9,   10,   11,    0,   12,   13,   14,   15,
    0,    0,   16,    0,    0,    0,    0,    0,    0,   17,
   18,   19,   20,   21,  110,  111,  112,  113,  114,  115,
  116,  117,  118,  119,  120,  121,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         28,
  264,  265,  266,  267,  268,  269,  263,  271,  272,  273,
  274,  296,  278,  277,  337,  338,  339,  283,  303,   48,
  284,  285,  286,  287,  288,   90,   91,  279,  280,  296,
   95,   96,  296,   98,   99,   64,  303,  296,  296,  303,
  261,  296,  296,  296,  303,  303,  260,  281,  303,  303,
  303,  275,  276,  283,   83,  283,   85,   86,  257,  258,
   89,  334,  335,  336,  337,  338,  339,  260,   97,  289,
  290,  260,  101,  102,  103,  104,  105,  106,  107,  108,
  260,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  121,  122,  328,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  338,  339,  289,  290,  261,  283,
  283,  261,  264,  265,  266,  267,  268,  269,  260,  271,
  272,  273,  274,  260,  260,  277,  283,  283,  260,  281,
  260,  295,  284,  285,  286,  287,  288,  295,  303,  283,
  295,  295,  257,  257,  296,  174,  295,  295,  295,  295,
  295,  303,  295,  270,  295,  295,  185,  295,  283,  281,
  295,  295,  295,  192,  193,  194,  195,  295,  257,  258,
  259,  260,  295,  262,  295,  204,  328,  329,  330,  331,
  332,  333,  334,  335,  336,  337,  338,  262,  262,  218,
  296,  257,  221,  282,  261,  257,  283,  296,  296,  296,
  296,  296,  291,  292,  293,  294,  295,  283,  296,  261,
  339,  300,  301,  302,  283,  304,  305,  306,  307,  308,
  309,  310,  311,  312,  313,  314,  315,  257,  258,  259,
  260,  259,  262,  328,  329,  330,  331,  332,  333,  334,
  335,  336,  337,  338,  339,  283,  335,  257,  283,  283,
  283,  283,  282,  257,  257,  257,  296,  257,  257,  257,
  296,  291,  292,  293,  294,  295,  290,  290,  283,  283,
  300,  301,  302,  257,  304,  305,  306,  307,  308,  309,
  310,  311,  312,  313,  314,  315,  257,  258,  259,  260,
  257,  262,  328,  329,  330,  331,  332,  333,  334,  335,
  336,  337,  338,  339,    0,  335,    3,   22,    8,   82,
   31,  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  291,  292,  293,  294,  295,  257,  258,  259,  260,  300,
  301,  302,   -1,  304,  305,  306,  307,  308,  309,  310,
  311,  312,  313,  314,  315,   -1,   -1,   -1,   -1,   -1,
  282,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  291,
  292,  293,  294,  295,  335,   -1,   -1,   -1,  300,  301,
  302,   -1,  304,  305,  306,  307,  308,  309,  310,  311,
  312,  313,  314,  315,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,  296,   -1,  277,   -1,   -1,
   -1,  281,   -1,  335,  284,  285,  286,  287,  288,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  296,   -1,   -1,   -1,
   -1,   -1,   -1,  303,   -1,   -1,   -1,  328,  329,  330,
  331,  332,  333,  334,  335,  336,  337,  338,  339,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  328,  329,
  330,  331,  332,  333,  334,  335,  336,  337,  338,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
  296,   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,
  285,  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,
   -1,   -1,  328,  329,  330,  331,  332,  333,  334,  335,
  336,  337,  338,  339,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  328,  329,  330,  331,  332,  333,  334,
  335,  336,  264,  265,  266,  267,  268,  269,   -1,  271,
  272,  273,  274,  296,   -1,  277,   -1,   -1,   -1,  281,
   -1,   -1,  284,  285,  286,  287,  288,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  296,   -1,   -1,   -1,   -1,   -1,
   -1,  303,   -1,   -1,   -1,  328,  329,  330,  331,  332,
  333,  334,  335,  336,  337,  338,  339,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  328,  329,  330,  331,
  332,  333,  334,  335,  336,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,  296,   -1,  277,   -1,
   -1,   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  296,   -1,   -1,
   -1,   -1,   -1,   -1,  303,   -1,   -1,   -1,  328,  329,
  330,  331,  332,  333,  334,  335,  336,  337,  338,  339,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  328,
  329,  330,  331,  332,  333,  334,  335,  336,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,  296,
   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,
   -1,  328,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  328,  329,  330,  331,  332,  333,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,  296,
   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,
   -1,  328,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  328,  329,  330,  331,  332,  333,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,  296,
   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,
   -1,  328,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  328,  329,  330,  331,  332,  333,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,  296,
   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,
   -1,  328,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  328,  329,  330,  331,  332,  333,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
   -1,  277,   -1,  303,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,  303,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,  328,  329,
  330,  331,  332,  333,  334,  335,  336,  337,  338,  339,
  328,  329,  330,  331,  332,  333,  334,  335,  336,  337,
  338,  339,  328,  329,  330,  331,  332,  333,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,  284,  285,
  286,  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  264,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
   -1,  277,  328,  329,  330,  331,  332,  333,  284,  285,
  286,  287,  288,  303,   -1,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,  303,   -1,  303,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  303,  328,  329,
  330,  331,  332,  333,  334,  335,  336,  337,  338,  339,
  328,  329,  330,  331,  332,  333,  334,  335,  336,  337,
  338,  339,  328,  329,  330,  331,  332,  333,  334,  335,
  336,  337,  338,  339,  303,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  303,  328,
  329,  330,  331,  332,  333,  334,  335,  336,  337,  338,
  339,  328,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,  328,  329,  330,  331,  332,  333,  334,
  335,  336,  337,  338,  339,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,
  284,  285,  286,  287,  288,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  328,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  338,  339,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=340;
final static String yyname[] = {
"end-of-file","NONE",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,"INTCON","FLOATCON","STRCON","VAR","SCRNM","AGGVAR",
"DEFINE","END","USES","DEBLANK","KEEP","SORT","TOP","PLUSTIES","CREATE",
"REPLACE","SET","UNIQUE","FIRST","LAST","PRINT","BLANK","ASCENDING",
"DESCENDING","WITH","NOT","COLON","ADD","SOS","OVERLAP","PAD","ADDNOPAD","TO",
"SCORE","AVERAGE","MEDIAN","SUM","COUNT","LPAREN","RPAREN","AGGAVERAGE",
"AGGMEDIAN","AGGMAX","OR","AND","IF","COMMA","MIN","MAX","NOW","NOW_VAR","SIGN",
"ABS","LENGTH","LEFT","RIGHT","MID","MOD","MATCH","ADDSCREEN","PADLIST","ZTRUE",
"ZFALSE","CONCAT","SUB","MULT","BOOL","DATE","STR","NUM","ANY","EQ","NEQ","LT",
"GT","LE","GE","PLUS","MINUS","AMPER","TIMES","DIV","POW","NEG",
};
final static String yyrule[] = {
"$accept : main",
"main : screens",
"screens : screens screen_def",
"screens : screen_def",
"screen_def : DEFINE SCRNM actions END",
"actions : actions action",
"actions : action",
"action : USES variables",
"action : DEBLANK variables",
"action : KEEP COLON expr",
"action : SORT sort_ops",
"action : TOP COLON INTCON tie_opt",
"action : TOP COLON FLOATCON tie_opt",
"action : CREATE VAR COLON expr",
"action : repl_stmt",
"action : SET VAR COLON expr",
"action : UNIQUE FIRST VAR",
"action : UNIQUE LAST VAR",
"action : ADD add_ops",
"action : ADDNOPAD add_nopad_ops",
"action : SOS",
"action : SOS COLON INTCON",
"action : OVERLAP",
"action : PAD pad_ops",
"action : PRINT args",
"repl_stmt : REPLACE VAR COLON expr WITH COLON expr",
"repl_stmt : REPLACE VAR BLANK WITH COLON expr",
"sort_ops : sort_ops sort_op",
"sort_ops : sort_op",
"sort_op : ASCENDING VAR",
"sort_op : DESCENDING VAR",
"add_ops : SCRNM COLON INTCON",
"add_ops : SCRNM COLON INTCON TO COLON INTCON",
"add_ops : SCRNM COLON INTCON SCORE COLON INTCON",
"add_ops : SCRNM COLON INTCON TO COLON INTCON SCORE COLON INTCON",
"add_nopad_ops : SCRNM COLON INTCON",
"add_nopad_ops : SCRNM COLON INTCON TO COLON INTCON",
"add_nopad_ops : SCRNM COLON INTCON SCORE COLON INTCON",
"add_nopad_ops : SCRNM COLON INTCON TO COLON INTCON SCORE COLON INTCON",
"pad_ops : COLON INTCON pad_list",
"pad_list : SCRNM COLON INTCON",
"pad_list : pad_list SCRNM COLON INTCON",
"tie_opt : PLUSTIES",
"tie_opt :",
"variables : variables VAR",
"variables : VAR",
"expr : LPAREN expr RPAREN",
"expr : NOT LPAREN expr RPAREN",
"expr : OR LPAREN args RPAREN",
"expr : AND LPAREN args RPAREN",
"expr : IF LPAREN expr COMMA expr COMMA expr RPAREN",
"expr : MIN LPAREN args RPAREN",
"expr : MAX LPAREN AGGVAR RPAREN",
"expr : MAX LPAREN args RPAREN",
"expr : LEFT LPAREN expr COMMA expr RPAREN",
"expr : RIGHT LPAREN expr COMMA expr RPAREN",
"expr : MID LPAREN expr COMMA expr COMMA expr RPAREN",
"expr : MATCH LPAREN expr COMMA STRCON COMMA INTCON RPAREN",
"expr : MATCH LPAREN expr COMMA STRCON RPAREN",
"expr : expr EQ expr",
"expr : expr NEQ expr",
"expr : expr LT expr",
"expr : expr GT expr",
"expr : expr LE expr",
"expr : expr GE expr",
"expr : expr AMPER expr",
"expr : expr PLUS expr",
"expr : expr MINUS expr",
"expr : expr TIMES expr",
"expr : expr DIV expr",
"expr : expr POW expr",
"expr : MOD LPAREN expr COMMA expr RPAREN",
"expr : MINUS expr",
"expr : VAR",
"expr : STRCON",
"expr : INTCON",
"expr : FLOATCON",
"expr : NOW_VAR",
"expr : NOW LPAREN RPAREN",
"expr : AVERAGE LPAREN AGGVAR RPAREN",
"expr : AVERAGE LPAREN args RPAREN",
"expr : MEDIAN LPAREN AGGVAR RPAREN",
"expr : MEDIAN LPAREN args RPAREN",
"expr : SUM LPAREN AGGVAR RPAREN",
"expr : COUNT LPAREN AGGVAR RPAREN",
"expr : SIGN LPAREN expr RPAREN",
"expr : ABS LPAREN expr RPAREN",
"expr : LENGTH LPAREN expr RPAREN",
"args : args COMMA expr",
"args : expr",
};

//#line 196 "parser.y"


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
//#line 869 "tok.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 2:
//#line 60 "parser.y"
{ yyval = null; }
break;
case 3:
//#line 61 "parser.y"
{ yyval = null; }
break;
case 4:
//#line 65 "parser.y"
{ yyval = null;  parserDefineScreen(val_peek(2).strval(), val_peek(1)); }
break;
case 5:
//#line 69 "parser.y"
{ yyval = new Op(COMMA, val_peek(1), val_peek(0)); }
break;
case 6:
//#line 70 "parser.y"
{ yyval = val_peek(0); }
break;
case 7:
//#line 74 "parser.y"
{ yyval = new Op(USES, val_peek(0)); }
break;
case 8:
//#line 75 "parser.y"
{ yyval = new Op(DEBLANK, val_peek(0)); }
break;
case 9:
//#line 76 "parser.y"
{ yyval = new Op(KEEP, val_peek(0)); }
break;
case 10:
//#line 77 "parser.y"
{ yyval = new Op(SORT, val_peek(0)); }
break;
case 11:
//#line 78 "parser.y"
{ yyval = new Op(TOP, val_peek(1), False, val_peek(0)); }
break;
case 12:
//#line 79 "parser.y"
{ yyval = new Op(TOP, val_peek(1), True, val_peek(0)); }
break;
case 13:
//#line 80 "parser.y"
{ yyval = new Op(CREATE, val_peek(2), val_peek(0)); }
break;
case 14:
//#line 81 "parser.y"
{ yyval = val_peek(0); }
break;
case 15:
//#line 82 "parser.y"
{ yyval = new Op(SET,val_peek(2),val_peek(0)); }
break;
case 16:
//#line 83 "parser.y"
{ yyval = new Op(UNIQUE,val_peek(0),True); }
break;
case 17:
//#line 84 "parser.y"
{ yyval = new Op(UNIQUE,val_peek(0),False); }
break;
case 18:
//#line 85 "parser.y"
{ yyval = val_peek(0); }
break;
case 19:
//#line 86 "parser.y"
{ yyval = val_peek(0); }
break;
case 20:
//#line 87 "parser.y"
{ yyval = new Op(SOS, IntOne); }
break;
case 21:
//#line 88 "parser.y"
{ yyval = new Op(SOS,val_peek(0)); }
break;
case 22:
//#line 89 "parser.y"
{ yyval = val_peek(0); }
break;
case 23:
//#line 90 "parser.y"
{ yyval = val_peek(0); }
break;
case 24:
//#line 91 "parser.y"
{ yyval = new Op(PRINT, val_peek(0)); }
break;
case 25:
//#line 95 "parser.y"
{ yyval = new Op(REPLACE,val_peek(5),val_peek(3),val_peek(0)); }
break;
case 26:
//#line 96 "parser.y"
{ yyval = new Op(REPLACE,val_peek(4),EmptyStr,val_peek(0)); }
break;
case 27:
//#line 100 "parser.y"
{ yyval = new Op(COMMA, val_peek(1), val_peek(0)); }
break;
case 28:
//#line 101 "parser.y"
{ yyval = val_peek(0); }
break;
case 29:
//#line 105 "parser.y"
{ yyval = new Op(ASCENDING,val_peek(0)); }
break;
case 30:
//#line 106 "parser.y"
{ yyval = new Op(DESCENDING,val_peek(0)); }
break;
case 31:
//#line 110 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(2), IntOne, val_peek(0), val_peek(0), True); }
break;
case 32:
//#line 111 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(5), val_peek(3), val_peek(0), val_peek(0), True); }
break;
case 33:
//#line 112 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(5), IntOne, val_peek(3), val_peek(0), True); }
break;
case 34:
//#line 113 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(8), val_peek(6), val_peek(3), val_peek(0), True); }
break;
case 35:
//#line 117 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(2), IntOne, val_peek(0), val_peek(0), False); }
break;
case 36:
//#line 118 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(5), val_peek(3), val_peek(0), val_peek(0), False); }
break;
case 37:
//#line 119 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(5), IntOne, val_peek(3), val_peek(0), False); }
break;
case 38:
//#line 120 "parser.y"
{ yyval = new Op(ADDSCREEN, val_peek(8), val_peek(6), val_peek(3), val_peek(0), False); }
break;
case 39:
//#line 124 "parser.y"
{ yyval = new Op(PAD, val_peek(1), val_peek(0)); }
break;
case 40:
//#line 128 "parser.y"
{ yyval = new Op(PADLIST, val_peek(2), val_peek(0)); }
break;
case 41:
//#line 129 "parser.y"
{ yyval = new Op(COMMA, val_peek(3), new Op(PADLIST, val_peek(2), val_peek(0))); }
break;
case 42:
//#line 133 "parser.y"
{ yyval = True; }
break;
case 43:
//#line 134 "parser.y"
{ yyval = False; }
break;
case 44:
//#line 138 "parser.y"
{ yyval = new Op(COMMA, val_peek(1), val_peek(0)); }
break;
case 45:
//#line 139 "parser.y"
{ yyval = val_peek(0); }
break;
case 46:
//#line 143 "parser.y"
{ yyval = val_peek(1); }
break;
case 47:
//#line 144 "parser.y"
{ yyval = new Op(NOT,val_peek(1)); }
break;
case 48:
//#line 145 "parser.y"
{ yyval = comma2binary(OR, val_peek(1)); }
break;
case 49:
//#line 146 "parser.y"
{ yyval = comma2binary(AND, val_peek(1)); }
break;
case 50:
//#line 148 "parser.y"
{ yyval = new Op(IF,val_peek(5),val_peek(3),val_peek(1)); }
break;
case 51:
//#line 150 "parser.y"
{ yyval = comma2binary(MIN, val_peek(1)); }
break;
case 52:
//#line 151 "parser.y"
{ yyval = new Op(AGGMAX,val_peek(1)); }
break;
case 53:
//#line 152 "parser.y"
{ yyval = comma2binary(MAX, val_peek(1)); }
break;
case 54:
//#line 153 "parser.y"
{ yyval = new Op(LEFT, val_peek(3), val_peek(1)); }
break;
case 55:
//#line 154 "parser.y"
{ yyval = new Op(RIGHT, val_peek(3), val_peek(1)); }
break;
case 56:
//#line 155 "parser.y"
{ yyval = new Op(MID, val_peek(5), val_peek(3), val_peek(1)); }
break;
case 57:
//#line 156 "parser.y"
{ yyval = new Op(MATCH, val_peek(5), val_peek(3), BoolCon(val_peek(1).intval() > 0)); }
break;
case 58:
//#line 157 "parser.y"
{ yyval = new Op(MATCH, val_peek(3), val_peek(1), True); }
break;
case 59:
//#line 159 "parser.y"
{ yyval = new Op(EQ,val_peek(2),val_peek(0)); }
break;
case 60:
//#line 160 "parser.y"
{ yyval = new Op(NEQ,val_peek(2),val_peek(0)); }
break;
case 61:
//#line 161 "parser.y"
{ yyval = new Op(LT,val_peek(2),val_peek(0)); }
break;
case 62:
//#line 162 "parser.y"
{ yyval = new Op(GT,val_peek(2),val_peek(0)); }
break;
case 63:
//#line 163 "parser.y"
{ yyval = new Op(LE,val_peek(2),val_peek(0)); }
break;
case 64:
//#line 164 "parser.y"
{ yyval = new Op(GE,val_peek(2),val_peek(0)); }
break;
case 65:
//#line 165 "parser.y"
{ yyval = new Op(CONCAT,val_peek(2),val_peek(0)); }
break;
case 66:
//#line 166 "parser.y"
{ yyval = new Op(ADD,val_peek(2),val_peek(0)); }
break;
case 67:
//#line 167 "parser.y"
{ yyval = new Op(SUB,val_peek(2),val_peek(0)); }
break;
case 68:
//#line 168 "parser.y"
{ yyval = new Op(MULT,val_peek(2),val_peek(0)); }
break;
case 69:
//#line 169 "parser.y"
{ yyval = new Op(DIV,val_peek(2),val_peek(0)); }
break;
case 70:
//#line 170 "parser.y"
{ yyval = new Op(POW,val_peek(2),val_peek(0)); }
break;
case 71:
//#line 171 "parser.y"
{ yyval = new Op(MOD,val_peek(3),val_peek(1)); }
break;
case 72:
//#line 172 "parser.y"
{ yyval = new Op(NEG,val_peek(0)); }
break;
case 73:
//#line 173 "parser.y"
{ yyval = val_peek(0); }
break;
case 74:
//#line 174 "parser.y"
{ yyval = val_peek(0); }
break;
case 75:
//#line 175 "parser.y"
{ yyval = FloatCon(val_peek(0).intval()); }
break;
case 76:
//#line 176 "parser.y"
{ yyval = val_peek(0); }
break;
case 77:
//#line 177 "parser.y"
{ yyval = CurTime; }
break;
case 78:
//#line 178 "parser.y"
{ yyval = CurTime; }
break;
case 79:
//#line 179 "parser.y"
{ yyval = new Op(AGGAVERAGE,val_peek(1)); }
break;
case 80:
//#line 180 "parser.y"
{ yyval = new Op(AVERAGE,val_peek(1)); }
break;
case 81:
//#line 181 "parser.y"
{ yyval = new Op(AGGMEDIAN,val_peek(1)); }
break;
case 82:
//#line 182 "parser.y"
{ yyval = new Op(MEDIAN,val_peek(1)); }
break;
case 83:
//#line 183 "parser.y"
{ yyval = new Op(SUM,val_peek(1)); }
break;
case 84:
//#line 184 "parser.y"
{ yyval = new Op(COUNT,val_peek(1)); }
break;
case 85:
//#line 185 "parser.y"
{ yyval = new Op(SIGN,val_peek(1)); }
break;
case 86:
//#line 186 "parser.y"
{ yyval = new Op(ABS,val_peek(1)); }
break;
case 87:
//#line 187 "parser.y"
{ yyval = new Op(LENGTH,val_peek(1)); }
break;
case 88:
//#line 191 "parser.y"
{ yyval = new Op(COMMA, val_peek(2), val_peek(0)); }
break;
case 89:
//#line 192 "parser.y"
{ yyval = val_peek(0); }
break;
//#line 1370 "tok.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public tok()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public tok(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
