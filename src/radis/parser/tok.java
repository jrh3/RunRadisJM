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



import radis.exception.InternalException;
//#line 46 "tok.java"
import radis.op.*;




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
public final static short MATCH=314;
public final static short ADDSCREEN=315;
public final static short PADLIST=316;
public final static short ZTRUE=317;
public final static short ZFALSE=318;
public final static short ZNULL=319;
public final static short ZEXISTS=320;
public final static short CONCAT=321;
public final static short SUB=322;
public final static short MULT=323;
public final static short BOOL=324;
public final static short DATE=325;
public final static short STR=326;
public final static short NUM=327;
public final static short ANY=328;
public final static short EQ=329;
public final static short NEQ=330;
public final static short LT=331;
public final static short GT=332;
public final static short LE=333;
public final static short GE=334;
public final static short PLUS=335;
public final static short MINUS=336;
public final static short AMPER=337;
public final static short TIMES=338;
public final static short DIV=339;
public final static short POW=340;
public final static short NEG=341;
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
    6,    6,    6,    6,    6,    6,   13,   13,
};
final static short yylen[] = {                            2,
    1,    2,    1,    4,    2,    1,    2,    2,    3,    2,
    4,    4,    4,    1,    4,    3,    3,    2,    2,    1,
    3,    1,    2,    2,    7,    6,    2,    1,    2,    2,
    3,    6,    6,    9,    3,    6,    6,    9,    3,    3,
    4,    1,    0,    2,    1,    3,    4,    4,    4,    8,
    4,    4,    4,    6,    6,    8,    8,    6,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    2,    1,    1,    1,    1,    1,    3,    4,    4,    4,
    4,    4,    4,    4,    4,    4,    3,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    2,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   22,    0,
    0,    0,    6,   14,   45,    0,    0,    0,    0,    0,
    0,   28,    0,    0,    0,    0,    0,    0,   74,   75,
   73,   72,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   76,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   18,    0,    0,   23,
    0,   19,    4,    5,   44,    0,   29,   30,   27,    0,
    0,    0,    0,    0,    0,   16,   17,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   71,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   21,    0,    0,   42,   11,   12,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   46,    0,
    0,    0,    0,    0,    0,   77,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   70,    0,    0,    0,    0,    0,
    0,    0,   47,   78,   79,   80,   81,   82,   83,   48,
   49,    0,   51,   52,   53,   84,   85,   86,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   40,    0,    0,
    0,    0,    0,   54,   55,    0,   58,    0,    0,   33,
   41,    0,   37,    0,    0,    0,    0,    0,   50,   56,
   57,    0,    0,   34,   38,
};
final static short yydgoto[] = {                          2,
    3,    4,   22,   23,   26,   64,   31,  126,   24,   67,
   72,   70,   65,   32,  169,
};
final static short yysindex[] = {                      -253,
 -240,    0, -253,    0, 1315,    0, -199, -199, -212, -278,
 -204, -163, -147, -146, -257,   44, -137, -168,    0, -158,
 -135, -260,    0,    0,    0, -133, -133,   44, -132, -131,
 -278,    0, -205, -153, -213, -151, -127, -126,    0,    0,
    0,    0, -160, -159, -156, -155, -154,   44, -150, -145,
 -143, -142, -140, -139,    0, -138, -136, -125, -122, -121,
 -120, -119,   44, 1275, -166, -141,    0, -113,  -99,    0,
 -123,    0,    0,    0,    0, 1275,    0,    0,    0,  -93,
  -93,   44, -103,   44,   44,    0,    0,   44, -111,  -53,
  -83,  -76, -293,   44,   44,   44,   44,    5, -109,   44,
   44,   44,   44,   44,   44,   44,    0,   44,   44,   44,
   44,   44,   44,   44,   44,   44,   44,   44,   44,   44,
  -65,    0,  -51,  -49,    0,    0,    0, 1275,  -72,  -61,
 1275,   76,  -84, -281,  -82, -280,  -81,  -80,    0, -248,
 -234,  740, -198,  -79, -186,    0,  152,  228,  302,  752,
  764,  802,  814, -306, -306, -306, -306, -306, -306, -289,
 -289, -289, -118, -118,    0, 1275, -196,  -70,  -43, -194,
   44,  -64,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   44,    0,    0,    0,    0,    0,    0,   44,   44,
   44,  -38,  -60,  -59,  -30,  -57,  -55,  -52, 1275,   44,
  826,  376,  610,  864, -185,  -27,  -25,    0,  -24,  -23,
  -22, 1275,   44,    0,    0,   44,    0,  -21,  -47,    0,
    0,  -46,    0,  637,  655,  -50,  -33,  -17,    0,    0,
    0,  -20,  -12,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,  280,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  904,    0,    0,
    0,    0,    0,    0,    0,  941,  966,    0,    0,    0,
  991,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  732, 1016,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, 1041,    0,    0,    0, 1066,
 1066,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0, 1091,    0,    0,
 1116,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -165,  471,  511,  551,  591,  631,  249,
  323,  397,   97,  173,    0,  765, 1141,    0, 1166, 1191,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1216,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, 1241,    0,    0,    0,    0,    0,    0, 1266,    0,
    0, 1291,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,  278,    0,  260,  276,  -28,    0,  204,    0,    0,
    0,    0,  -31,  255,    0,
};
final static int YYTABLESIZE=1615;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         76,
   29,   30,  139,   73,    7,    8,    9,   10,   11,    1,
   12,   13,   14,   15,  175,  177,   16,   37,   38,   93,
    5,  120,  120,   17,   18,   19,   20,   21,  114,  115,
  116,  117,  118,  119,  107,  108,  109,  110,  111,  112,
  113,  114,  115,  116,  117,  118,  119,  180,  117,  118,
  119,   80,   81,  128,  120,  130,  131,  134,  136,  132,
   25,  181,  140,  141,   83,  143,  145,  142,  120,   84,
   28,  147,  148,  149,  150,  151,  152,  153,   33,  154,
  155,  156,  157,  158,  159,  160,  161,  162,  163,  164,
  165,  166,  193,  194,  197,  198,   34,  183,   59,   59,
   59,   59,   59,   59,  120,   59,   59,   59,   59,  185,
  217,   59,   35,   36,   68,   59,  120,  218,   59,   59,
   59,   59,   59,   66,   69,   71,   75,   77,   78,   82,
   59,   85,   86,   87,   88,   89,  120,   59,   90,   91,
   92,  121,  199,  122,   94,   39,   40,   41,   42,   95,
  133,   96,   97,  201,   98,   99,  100,  123,  101,  124,
  202,  203,  204,   59,   59,   59,   59,   59,   59,  102,
   43,  212,  103,  104,  105,  106,  125,  129,  137,   44,
   45,   46,   47,   48,  224,  138,  146,  225,   49,   50,
   51,  167,   52,   53,   54,   55,   56,   57,   58,   59,
   60,   61,   62,   39,   40,   41,   42,  170,  135,  168,
  171,  174,  195,  176,  178,  179,  184,  196,  200,  172,
  205,  119,  206,  207,   63,  209,  208,  210,   43,  219,
  211,  220,  221,  222,  223,  226,  234,   44,   45,   46,
   47,   48,  227,  228,  235,  231,   49,   50,   51,  232,
   52,   53,   54,   55,   56,   57,   58,   59,   60,   61,
   62,   39,   40,   41,   42,  233,  144,  108,  109,  110,
  111,  112,  113,  114,  115,  116,  117,  118,  119,    1,
    6,   74,   63,   27,  127,   79,   43,    0,    0,    0,
    0,    0,    0,    0,    0,   44,   45,   46,   47,   48,
   39,   40,   41,   42,   49,   50,   51,    0,   52,   53,
   54,   55,   56,   57,   58,   59,   60,   61,   62,    0,
    0,    0,    0,    0,    0,   43,    0,    0,    0,    0,
    0,    0,    0,    0,   44,   45,   46,   47,   48,    0,
   63,    0,    0,   49,   50,   51,    0,   52,   53,   54,
   55,   56,   57,   58,   59,   60,   61,   62,    0,    0,
   68,   68,   68,   68,   68,   68,    0,   68,   68,   68,
   68,  173,    0,   68,    0,    0,    0,   68,    0,   63,
   68,   68,   68,   68,   68,    0,    0,    0,    0,    0,
    0,    0,   68,    0,    0,    0,    0,    0,    0,   68,
    0,    0,    0,    0,  108,  109,  110,  111,  112,  113,
  114,  115,  116,  117,  118,  119,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   68,   68,   68,   68,   68,
   68,   68,   68,   68,   68,   68,   69,   69,   69,   69,
   69,   69,    0,   69,   69,   69,   69,  186,    0,   69,
    0,    0,    0,   69,    0,    0,   69,   69,   69,   69,
   69,    0,    0,    0,    0,    0,    0,    0,   69,    0,
    0,    0,    0,    0,    0,   69,    0,    0,    0,    0,
  108,  109,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   69,   69,   69,   69,   69,   69,   69,   69,   69,
   69,   69,   66,   66,   66,   66,   66,   66,    0,   66,
   66,   66,   66,  187,    0,   66,    0,    0,    0,   66,
    0,    0,   66,   66,   66,   66,   66,    0,    0,    0,
    0,    0,    0,    0,   66,    0,    0,    0,    0,    0,
    0,   66,    0,    0,    0,    0,  108,  109,  110,  111,
  112,  113,  114,  115,  116,  117,  118,  119,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   66,   66,   66,
   66,   66,   66,   66,   66,   66,   67,   67,   67,   67,
   67,   67,    0,   67,   67,   67,   67,  188,    0,   67,
    0,    0,    0,   67,    0,    0,   67,   67,   67,   67,
   67,    0,    0,    0,    0,    0,    0,    0,   67,    0,
    0,    0,    0,    0,    0,   67,    0,    0,    0,    0,
  108,  109,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   67,   67,   67,   67,   67,   67,   67,   67,   67,
   65,   65,   65,   65,   65,   65,    0,   65,   65,   65,
   65,  214,    0,   65,    0,    0,    0,   65,    0,    0,
   65,   65,   65,   65,   65,    0,    0,    0,    0,    0,
    0,    0,   65,    0,    0,    0,    0,    0,    0,   65,
    0,    0,    0,    0,  108,  109,  110,  111,  112,  113,
  114,  115,  116,  117,  118,  119,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   65,   65,   65,   65,   65,
   65,   65,   65,   65,   60,   60,   60,   60,   60,   60,
    0,   60,   60,   60,   60,    0,    0,   60,    0,    0,
    0,   60,    0,    0,   60,   60,   60,   60,   60,    0,
    0,    0,    0,    0,    0,    0,   60,    0,    0,    0,
    0,    0,    0,   60,   61,   61,   61,   61,   61,   61,
    0,   61,   61,   61,   61,    0,    0,   61,    0,    0,
    0,   61,    0,    0,   61,   61,   61,   61,   61,   60,
   60,   60,   60,   60,   60,    0,   61,    0,    0,    0,
    0,    0,    0,   61,   62,   62,   62,   62,   62,   62,
    0,   62,   62,   62,   62,    0,    0,   62,    0,    0,
    0,   62,    0,    0,   62,   62,   62,   62,   62,   61,
   61,   61,   61,   61,   61,    0,   62,    0,    0,    0,
    0,    0,    0,   62,   63,   63,   63,   63,   63,   63,
    0,   63,   63,   63,   63,    0,    0,   63,    0,    0,
    0,   63,    0,    0,   63,   63,   63,   63,   63,   62,
   62,   62,   62,   62,   62,    0,   63,    0,    0,    0,
    0,    0,    0,   63,   64,   64,   64,   64,   64,   64,
    0,   64,   64,   64,   64,  215,    0,   64,    0,    0,
    0,   64,    0,    0,   64,   64,   64,   64,   64,   63,
   63,   63,   63,   63,   63,    0,   64,    0,    0,    0,
    0,    0,  229,   64,    0,    0,    0,    0,  108,  109,
  110,  111,  112,  113,  114,  115,  116,  117,  118,  119,
  230,    0,    0,    0,    0,    0,    0,    0,    0,   64,
   64,   64,   64,   64,   64,  108,  109,  110,  111,  112,
  113,  114,  115,  116,  117,  118,  119,    0,    0,    0,
    0,    0,    0,  108,  109,  110,  111,  112,  113,  114,
  115,  116,  117,  118,  119,   88,   88,   88,   88,   88,
   88,    0,   88,   88,   88,   88,    0,    0,   88,    0,
    0,    0,    0,    0,    0,   88,   88,   88,   88,   88,
    0,    0,    0,    0,    0,    0,    0,   88,   87,   87,
   87,   87,   87,   87,   88,   87,   87,   87,   87,    0,
    0,   87,  182,    0,    0,    0,    0,    0,   87,   87,
   87,   87,   87,    0,  189,    0,    0,    0,    0,    0,
   87,    0,    0,    0,    0,    0,  190,   87,  108,  109,
  110,  111,  112,  113,  114,  115,  116,  117,  118,  119,
  108,  109,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  108,  109,  110,  111,  112,  113,  114,  115,
  116,  117,  118,  119,  191,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  192,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  213,    0,
  108,  109,  110,  111,  112,  113,  114,  115,  116,  117,
  118,  119,  108,  109,  110,  111,  112,  113,  114,  115,
  116,  117,  118,  119,  108,  109,  110,  111,  112,  113,
  114,  115,  116,  117,  118,  119,  216,   20,   20,   20,
   20,   20,   20,    0,   20,   20,   20,   20,    0,    0,
   20,    0,    0,    0,    0,    0,    0,   20,   20,   20,
   20,   20,  108,  109,  110,  111,  112,  113,  114,  115,
  116,  117,  118,  119,    7,    7,    7,    7,    7,    7,
    0,    7,    7,    7,    7,    0,    0,    7,    0,    0,
    0,    0,    0,    0,    7,    7,    7,    7,    7,    8,
    8,    8,    8,    8,    8,    0,    8,    8,    8,    8,
    0,    0,    8,    0,    0,    0,    0,    0,    0,    8,
    8,    8,    8,    8,   10,   10,   10,   10,   10,   10,
    0,   10,   10,   10,   10,    0,    0,   10,    0,    0,
    0,    0,    0,    0,   10,   10,   10,   10,   10,   24,
   24,   24,   24,   24,   24,    0,   24,   24,   24,   24,
    0,    0,   24,    0,    0,    0,    0,    0,    0,   24,
   24,   24,   24,   24,    9,    9,    9,    9,    9,    9,
    0,    9,    9,    9,    9,    0,    0,    9,    0,    0,
    0,    0,    0,    0,    9,    9,    9,    9,    9,   43,
   43,   43,   43,   43,   43,    0,   43,   43,   43,   43,
    0,    0,   43,    0,    0,    0,    0,    0,    0,   43,
   43,   43,   43,   43,   13,   13,   13,   13,   13,   13,
    0,   13,   13,   13,   13,    0,    0,   13,    0,    0,
    0,    0,    0,    0,   13,   13,   13,   13,   13,   15,
   15,   15,   15,   15,   15,    0,   15,   15,   15,   15,
    0,    0,   15,    0,    0,    0,    0,    0,    0,   15,
   15,   15,   15,   15,   31,   31,   31,   31,   31,   31,
    0,   31,   31,   31,   31,    0,    0,   31,    0,    0,
    0,    0,    0,    0,   31,   31,   31,   31,   31,   39,
   39,   39,   39,   39,   39,    0,   39,   39,   39,   39,
    0,    0,   39,    0,    0,    0,    0,    0,    0,   39,
   39,   39,   39,   39,   35,   35,   35,   35,   35,   35,
    0,   35,   35,   35,   35,    0,    0,   35,    0,    0,
    0,    0,    0,    0,   35,   35,   35,   35,   35,   26,
   26,   26,   26,   26,   26,    0,   26,   26,   26,   26,
    0,    0,   26,    0,    0,    0,    0,    0,    0,   26,
   26,   26,   26,   26,   25,   25,   25,   25,   25,   25,
    0,   25,   25,   25,   25,    0,    0,   25,    0,    0,
    0,    0,    0,    0,   25,   25,   25,   25,   25,   32,
   32,   32,   32,   32,   32,    0,   32,   32,   32,   32,
    0,    0,   32,    0,    0,    0,    0,    0,    0,   32,
   32,   32,   32,   32,   36,   36,   36,   36,   36,   36,
    0,   36,   36,   36,   36,    0,    0,   36,    0,    0,
    0,    0,    0,    0,   36,   36,   36,   36,   36,    7,
    8,    9,   10,   11,    0,   12,   13,   14,   15,    0,
    0,   16,    0,    0,    0,    0,    0,    0,   17,   18,
   19,   20,   21,  108,  109,  110,  111,  112,  113,  114,
  115,  116,  117,  118,  119,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         28,
  279,  280,  296,  264,  265,  266,  267,  268,  269,  263,
  271,  272,  273,  274,  296,  296,  277,  275,  276,   48,
  261,  303,  303,  284,  285,  286,  287,  288,  335,  336,
  337,  338,  339,  340,   63,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  338,  339,  340,  296,  338,  339,
  340,  257,  258,   82,  303,   84,   85,   89,   90,   88,
  260,  296,   94,   95,  278,   97,   98,   96,  303,  283,
  283,  100,  101,  102,  103,  104,  105,  106,  283,  108,
  109,  110,  111,  112,  113,  114,  115,  116,  117,  118,
  119,  120,  289,  290,  289,  290,  260,  296,  264,  265,
  266,  267,  268,  269,  303,  271,  272,  273,  274,  296,
  296,  277,  260,  260,  283,  281,  303,  303,  284,  285,
  286,  287,  288,  261,  283,  261,  260,  260,  260,  283,
  296,  283,  260,  260,  295,  295,  303,  303,  295,  295,
  295,  283,  171,  257,  295,  257,  258,  259,  260,  295,
  262,  295,  295,  182,  295,  295,  295,  257,  295,  283,
  189,  190,  191,  329,  330,  331,  332,  333,  334,  295,
  282,  200,  295,  295,  295,  295,  270,  281,  262,  291,
  292,  293,  294,  295,  213,  262,  296,  216,  300,  301,
  302,  257,  304,  305,  306,  307,  308,  309,  310,  311,
  312,  313,  314,  257,  258,  259,  260,  257,  262,  261,
  283,  296,  283,  296,  296,  296,  296,  261,  283,  281,
  259,  340,  283,  283,  336,  283,  257,  283,  282,  257,
  283,  257,  257,  257,  257,  257,  257,  291,  292,  293,
  294,  295,  290,  290,  257,  296,  300,  301,  302,  283,
  304,  305,  306,  307,  308,  309,  310,  311,  312,  313,
  314,  257,  258,  259,  260,  283,  262,  329,  330,  331,
  332,  333,  334,  335,  336,  337,  338,  339,  340,    0,
    3,   22,  336,    8,   81,   31,  282,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  291,  292,  293,  294,  295,
  257,  258,  259,  260,  300,  301,  302,   -1,  304,  305,
  306,  307,  308,  309,  310,  311,  312,  313,  314,   -1,
   -1,   -1,   -1,   -1,   -1,  282,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  291,  292,  293,  294,  295,   -1,
  336,   -1,   -1,  300,  301,  302,   -1,  304,  305,  306,
  307,  308,  309,  310,  311,  312,  313,  314,   -1,   -1,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,  296,   -1,  277,   -1,   -1,   -1,  281,   -1,  336,
  284,  285,  286,  287,  288,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,
   -1,   -1,   -1,   -1,  329,  330,  331,  332,  333,  334,
  335,  336,  337,  338,  339,  340,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  338,  339,  264,  265,  266,  267,
  268,  269,   -1,  271,  272,  273,  274,  296,   -1,  277,
   -1,   -1,   -1,  281,   -1,   -1,  284,  285,  286,  287,
  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  296,   -1,
   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,   -1,   -1,
  329,  330,  331,  332,  333,  334,  335,  336,  337,  338,
  339,  340,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  329,  330,  331,  332,  333,  334,  335,  336,  337,
  338,  339,  264,  265,  266,  267,  268,  269,   -1,  271,
  272,  273,  274,  296,   -1,  277,   -1,   -1,   -1,  281,
   -1,   -1,  284,  285,  286,  287,  288,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  296,   -1,   -1,   -1,   -1,   -1,
   -1,  303,   -1,   -1,   -1,   -1,  329,  330,  331,  332,
  333,  334,  335,  336,  337,  338,  339,  340,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  329,  330,  331,
  332,  333,  334,  335,  336,  337,  264,  265,  266,  267,
  268,  269,   -1,  271,  272,  273,  274,  296,   -1,  277,
   -1,   -1,   -1,  281,   -1,   -1,  284,  285,  286,  287,
  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  296,   -1,
   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,   -1,   -1,
  329,  330,  331,  332,  333,  334,  335,  336,  337,  338,
  339,  340,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  329,  330,  331,  332,  333,  334,  335,  336,  337,
  264,  265,  266,  267,  268,  269,   -1,  271,  272,  273,
  274,  296,   -1,  277,   -1,   -1,   -1,  281,   -1,   -1,
  284,  285,  286,  287,  288,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  296,   -1,   -1,   -1,   -1,   -1,   -1,  303,
   -1,   -1,   -1,   -1,  329,  330,  331,  332,  333,  334,
  335,  336,  337,  338,  339,  340,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  296,   -1,   -1,   -1,
   -1,   -1,   -1,  303,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,  329,
  330,  331,  332,  333,  334,   -1,  296,   -1,   -1,   -1,
   -1,   -1,   -1,  303,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,  329,
  330,  331,  332,  333,  334,   -1,  296,   -1,   -1,   -1,
   -1,   -1,   -1,  303,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,  329,
  330,  331,  332,  333,  334,   -1,  296,   -1,   -1,   -1,
   -1,   -1,   -1,  303,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,  296,   -1,  277,   -1,   -1,
   -1,  281,   -1,   -1,  284,  285,  286,  287,  288,  329,
  330,  331,  332,  333,  334,   -1,  296,   -1,   -1,   -1,
   -1,   -1,  296,  303,   -1,   -1,   -1,   -1,  329,  330,
  331,  332,  333,  334,  335,  336,  337,  338,  339,  340,
  296,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  329,
  330,  331,  332,  333,  334,  329,  330,  331,  332,  333,
  334,  335,  336,  337,  338,  339,  340,   -1,   -1,   -1,
   -1,   -1,   -1,  329,  330,  331,  332,  333,  334,  335,
  336,  337,  338,  339,  340,  264,  265,  266,  267,  268,
  269,   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,
   -1,   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  296,  264,  265,
  266,  267,  268,  269,  303,  271,  272,  273,  274,   -1,
   -1,  277,  303,   -1,   -1,   -1,   -1,   -1,  284,  285,
  286,  287,  288,   -1,  303,   -1,   -1,   -1,   -1,   -1,
  296,   -1,   -1,   -1,   -1,   -1,  303,  303,  329,  330,
  331,  332,  333,  334,  335,  336,  337,  338,  339,  340,
  329,  330,  331,  332,  333,  334,  335,  336,  337,  338,
  339,  340,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,  340,  303,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  303,   -1,
  329,  330,  331,  332,  333,  334,  335,  336,  337,  338,
  339,  340,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,  340,  329,  330,  331,  332,  333,  334,
  335,  336,  337,  338,  339,  340,  303,  264,  265,  266,
  267,  268,  269,   -1,  271,  272,  273,  274,   -1,   -1,
  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,  285,  286,
  287,  288,  329,  330,  331,  332,  333,  334,  335,  336,
  337,  338,  339,  340,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  264,
  265,  266,  267,  268,  269,   -1,  271,  272,  273,  274,
   -1,   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,
  285,  286,  287,  288,  264,  265,  266,  267,  268,  269,
   -1,  271,  272,  273,  274,   -1,   -1,  277,   -1,   -1,
   -1,   -1,   -1,   -1,  284,  285,  286,  287,  288,  265,
  266,  267,  268,  269,   -1,  271,  272,  273,  274,   -1,
   -1,  277,   -1,   -1,   -1,   -1,   -1,   -1,  284,  285,
  286,  287,  288,  329,  330,  331,  332,  333,  334,  335,
  336,  337,  338,  339,  340,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=341;
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
"ABS","LENGTH","LEFT","RIGHT","MID","MATCH","ADDSCREEN","PADLIST","ZTRUE",
"ZFALSE","ZNULL","ZEXISTS","CONCAT","SUB","MULT","BOOL","DATE","STR","NUM",
"ANY","EQ","NEQ","LT","GT","LE","GE","PLUS","MINUS","AMPER","TIMES","DIV","POW",
"NEG",
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

//#line 195 "parser.y"


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
//#line 853 "tok.java"
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
{ yyval = new Op(NEG,val_peek(0)); }
break;
case 72:
//#line 172 "parser.y"
{ yyval = val_peek(0); }
break;
case 73:
//#line 173 "parser.y"
{ yyval = val_peek(0); }
break;
case 74:
//#line 174 "parser.y"
{ yyval = FloatCon(val_peek(0).intval()); }
break;
case 75:
//#line 175 "parser.y"
{ yyval = val_peek(0); }
break;
case 76:
//#line 176 "parser.y"
{ yyval = CurTime; }
break;
case 77:
//#line 177 "parser.y"
{ yyval = CurTime; }
break;
case 78:
//#line 178 "parser.y"
{ yyval = new Op(AGGAVERAGE,val_peek(1)); }
break;
case 79:
//#line 179 "parser.y"
{ yyval = new Op(AVERAGE,val_peek(1)); }
break;
case 80:
//#line 180 "parser.y"
{ yyval = new Op(AGGMEDIAN,val_peek(1)); }
break;
case 81:
//#line 181 "parser.y"
{ yyval = new Op(MEDIAN,val_peek(1)); }
break;
case 82:
//#line 182 "parser.y"
{ yyval = new Op(SUM,val_peek(1)); }
break;
case 83:
//#line 183 "parser.y"
{ yyval = new Op(COUNT,val_peek(1)); }
break;
case 84:
//#line 184 "parser.y"
{ yyval = new Op(SIGN,val_peek(1)); }
break;
case 85:
//#line 185 "parser.y"
{ yyval = new Op(ABS,val_peek(1)); }
break;
case 86:
//#line 186 "parser.y"
{ yyval = new Op(LENGTH,val_peek(1)); }
break;
case 87:
//#line 190 "parser.y"
{ yyval = new Op(COMMA, val_peek(2), val_peek(0)); }
break;
case 88:
//#line 191 "parser.y"
{ yyval = val_peek(0); }
break;
//#line 1350 "tok.java"
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
