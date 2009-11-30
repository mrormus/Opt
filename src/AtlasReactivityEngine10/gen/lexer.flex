/* Scanner for reading from the ReactiveEngine command line */
/* Generator is JFlex */

package com.pervasa.reactivity;

import java_cup.runtime.*;

/**
 * This class scans input from the command line and returns tokens.
 */

%%
%class Lexer
%ignorecase
%unicode
%cup
%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

Date				= (0[1-9]|1[012]) "/" (0[1-9]|[12][0-9]|3[01]) "/" [:digit:][:digit:]
Time				= ([01][0-9]|2[0-3])\:([0-5][0-9])\:([0-5][0-9])
WhiteSpace	  		= [ \t\f]
Identifier     	  	= [:jletter:] ([:jletterdigit:]|[./])*
DecIntegerLiteral 	= 0 | [1-9][0-9]*

InputCharacter 		= [^\r\n]
Comment 			= "//" {InputCharacter}* ?


%state STRING

%%

<YYINITIAL> {
/* commands */
"DEFINE"           		{ return symbol(sym.DEFINE); }
"LIST"           		{ return symbol(sym.LIST); }
"BASIC"             	{ return symbol(sym.BASIC); }
"SET"               	{ return symbol(sym.SET); }
"RUN"               	{ return symbol(sym.RUN); }
"STOP"              	{ return symbol(sym.STOP); }
"LOAD"              	{ return symbol(sym.LOAD); }

/* subcommand */
"EVENT"             	{ return symbol(sym.EVENT); }
"CONDITION"         	{ return symbol(sym.CONDITION); }
"ACTION"            	{ return symbol(sym.ACT); }
"RULE"              	{ return symbol(sym.RULE); }

/* conditions */
"TRUE"					{ return symbol(sym.TRUE); }
"FALSE"					{ return symbol(sym.FALSE); }

/* TFM */
"NIL"					{ return symbol(sym.NIL); }
"INF"					{ return symbol(sym.INF); }
{Date}					{ return symbol(sym.DATE, new String(yytext())); }
{Time}					{ return symbol(sym.TIME, new String(yytext())); }
 
  /* identifiers */ 
  {Identifier}          { return symbol(sym.IDENTIFIER, new String(yytext())); }
 
  /* literals */
  {DecIntegerLiteral}   { return symbol(sym.NUMBER, new Integer(yytext())); }

  /* operators */
  "="                   { return symbol(sym.EQUALS); }
  "+"                   { return symbol(sym.PLUS); }
  "-"					{ return symbol(sym.MINUS); }
  "*"                 	{ return symbol(sym.STAR); }
  "%"                 	{ return symbol(sym.PERCENT); }
  
  /* delimiters */
  "("					{ return symbol(sym.LPAREN); }
  ")"					{ return symbol(sym.RPAREN); }
  "<"					{ return symbol(sym.LANGLE); }
  ">"					{ return symbol(sym.RANGLE); }
  ";"					{ return symbol(sym.SEMI); }
  ":"					{ return symbol(sym.COLON); }
  ","					{ return symbol(sym.COMMA); }
  "/"					{ return symbol(sym.SLASH); }
  
  /* comments */
  {Comment}				{ /* ignore */}
  
  /* whitespace */
  {WhiteSpace}          { /* ignore */ }
}

 /* error fallback */
.|\n                    { throw new Error("Illegal character <"+
                                                    yytext()+">"); }