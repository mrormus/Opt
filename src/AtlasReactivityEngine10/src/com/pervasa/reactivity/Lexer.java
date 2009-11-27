/* The following code was generated by JFlex 1.4.3 on 11/27/09 3:40 AM */

/* Scanner for reading from the ReactiveEngine command line */
/* Generator is JFlex */

package com.pervasa.reactivity;

import java_cup.runtime.*;

/**
 * This class scans input from the command line and returns tokens.
 */


class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int STRING = 2;
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\3\1\1\1\0\1\0\1\1\1\0\16\3\4\0\1\1\3\0"+
    "\1\2\3\0\1\31\1\32\1\30\1\27\1\36\3\0\1\4\11\5"+
    "\1\0\1\35\1\0\1\26\3\0\1\17\1\16\1\20\1\6\1\7"+
    "\1\10\2\2\1\11\2\2\1\13\1\2\1\12\1\23\1\24\1\2"+
    "\1\21\1\14\1\15\1\22\1\25\4\2\1\33\1\37\1\34\1\0"+
    "\1\2\1\0\1\17\1\16\1\20\1\6\1\7\1\10\2\2\1\11"+
    "\2\2\1\13\1\2\1\12\1\23\1\24\1\2\1\21\1\14\1\15"+
    "\1\22\1\25\4\2\4\0\41\3\2\0\4\2\4\0\1\2\2\0"+
    "\1\3\7\0\1\2\4\0\1\2\5\0\27\2\1\0\37\2\1\0"+
    "\u013f\2\31\0\162\2\4\0\14\2\16\0\5\2\11\0\1\2\21\0"+
    "\130\3\5\0\23\3\12\0\1\2\13\0\1\2\1\0\3\2\1\0"+
    "\1\2\1\0\24\2\1\0\54\2\1\0\46\2\1\0\5\2\4\0"+
    "\202\2\1\0\4\3\3\0\105\2\1\0\46\2\2\0\2\2\6\0"+
    "\20\2\41\0\46\2\2\0\1\2\7\0\47\2\11\0\21\3\1\0"+
    "\27\3\1\0\3\3\1\0\1\3\1\0\2\3\1\0\1\3\13\0"+
    "\33\2\5\0\3\2\15\0\4\3\14\0\6\3\13\0\32\2\5\0"+
    "\13\2\16\3\7\0\12\3\4\0\2\2\1\3\143\2\1\0\1\2"+
    "\10\3\1\0\6\3\2\2\2\3\1\0\4\3\2\2\12\3\3\2"+
    "\2\0\1\2\17\0\1\3\1\2\1\3\36\2\33\3\2\0\3\2"+
    "\60\0\46\2\13\3\1\2\u014f\0\3\3\66\2\2\0\1\3\1\2"+
    "\20\3\2\0\1\2\4\3\3\0\12\2\2\3\2\0\12\3\21\0"+
    "\3\3\1\0\10\2\2\0\2\2\2\0\26\2\1\0\7\2\1\0"+
    "\1\2\3\0\4\2\2\0\1\3\1\2\7\3\2\0\2\3\2\0"+
    "\3\3\11\0\1\3\4\0\2\2\1\0\3\2\2\3\2\0\12\3"+
    "\4\2\15\0\3\3\1\0\6\2\4\0\2\2\2\0\26\2\1\0"+
    "\7\2\1\0\2\2\1\0\2\2\1\0\2\2\2\0\1\3\1\0"+
    "\5\3\4\0\2\3\2\0\3\3\13\0\4\2\1\0\1\2\7\0"+
    "\14\3\3\2\14\0\3\3\1\0\11\2\1\0\3\2\1\0\26\2"+
    "\1\0\7\2\1\0\2\2\1\0\5\2\2\0\1\3\1\2\10\3"+
    "\1\0\3\3\1\0\3\3\2\0\1\2\17\0\2\2\2\3\2\0"+
    "\12\3\1\0\1\2\17\0\3\3\1\0\10\2\2\0\2\2\2\0"+
    "\26\2\1\0\7\2\1\0\2\2\1\0\5\2\2\0\1\3\1\2"+
    "\6\3\3\0\2\3\2\0\3\3\10\0\2\3\4\0\2\2\1\0"+
    "\3\2\4\0\12\3\1\0\1\2\20\0\1\3\1\2\1\0\6\2"+
    "\3\0\3\2\1\0\4\2\3\0\2\2\1\0\1\2\1\0\2\2"+
    "\3\0\2\2\3\0\3\2\3\0\10\2\1\0\3\2\4\0\5\3"+
    "\3\0\3\3\1\0\4\3\11\0\1\3\17\0\11\3\11\0\1\2"+
    "\7\0\3\3\1\0\10\2\1\0\3\2\1\0\27\2\1\0\12\2"+
    "\1\0\5\2\4\0\7\3\1\0\3\3\1\0\4\3\7\0\2\3"+
    "\11\0\2\2\4\0\12\3\22\0\2\3\1\0\10\2\1\0\3\2"+
    "\1\0\27\2\1\0\12\2\1\0\5\2\2\0\1\3\1\2\7\3"+
    "\1\0\3\3\1\0\4\3\7\0\2\3\7\0\1\2\1\0\2\2"+
    "\4\0\12\3\22\0\2\3\1\0\10\2\1\0\3\2\1\0\27\2"+
    "\1\0\20\2\4\0\6\3\2\0\3\3\1\0\4\3\11\0\1\3"+
    "\10\0\2\2\4\0\12\3\22\0\2\3\1\0\22\2\3\0\30\2"+
    "\1\0\11\2\1\0\1\2\2\0\7\2\3\0\1\3\4\0\6\3"+
    "\1\0\1\3\1\0\10\3\22\0\2\3\15\0\60\2\1\3\2\2"+
    "\7\3\4\0\10\2\10\3\1\0\12\3\47\0\2\2\1\0\1\2"+
    "\2\0\2\2\1\0\1\2\2\0\1\2\6\0\4\2\1\0\7\2"+
    "\1\0\3\2\1\0\1\2\1\0\1\2\2\0\2\2\1\0\4\2"+
    "\1\3\2\2\6\3\1\0\2\3\1\2\2\0\5\2\1\0\1\2"+
    "\1\0\6\3\2\0\12\3\2\0\2\2\42\0\1\2\27\0\2\3"+
    "\6\0\12\3\13\0\1\3\1\0\1\3\1\0\1\3\4\0\2\3"+
    "\10\2\1\0\42\2\6\0\24\3\1\0\2\3\4\2\4\0\10\3"+
    "\1\0\44\3\11\0\1\3\71\0\42\2\1\0\5\2\1\0\2\2"+
    "\1\0\7\3\3\0\4\3\6\0\12\3\6\0\6\2\4\3\106\0"+
    "\46\2\12\0\51\2\7\0\132\2\5\0\104\2\5\0\122\2\6\0"+
    "\7\2\1\0\77\2\1\0\1\2\1\0\4\2\2\0\7\2\1\0"+
    "\1\2\1\0\4\2\2\0\47\2\1\0\1\2\1\0\4\2\2\0"+
    "\37\2\1\0\1\2\1\0\4\2\2\0\7\2\1\0\1\2\1\0"+
    "\4\2\2\0\7\2\1\0\7\2\1\0\27\2\1\0\37\2\1\0"+
    "\1\2\1\0\4\2\2\0\7\2\1\0\47\2\1\0\23\2\16\0"+
    "\11\3\56\0\125\2\14\0\u026c\2\2\0\10\2\12\0\32\2\5\0"+
    "\113\2\3\0\3\2\17\0\15\2\1\0\4\2\3\3\13\0\22\2"+
    "\3\3\13\0\22\2\2\3\14\0\15\2\1\0\3\2\1\0\2\3"+
    "\14\0\64\2\40\3\3\0\1\2\3\0\2\2\1\3\2\0\12\3"+
    "\41\0\3\3\2\0\12\3\6\0\130\2\10\0\51\2\1\3\126\0"+
    "\35\2\3\0\14\3\4\0\14\3\12\0\12\3\36\2\2\0\5\2"+
    "\u038b\0\154\2\224\0\234\2\4\0\132\2\6\0\26\2\2\0\6\2"+
    "\2\0\46\2\2\0\6\2\2\0\10\2\1\0\1\2\1\0\1\2"+
    "\1\0\1\2\1\0\37\2\2\0\65\2\1\0\7\2\1\0\1\2"+
    "\3\0\3\2\1\0\7\2\3\0\4\2\2\0\6\2\4\0\15\2"+
    "\5\0\3\2\1\0\7\2\17\0\4\3\32\0\5\3\20\0\2\2"+
    "\23\0\1\2\13\0\4\3\6\0\6\3\1\0\1\2\15\0\1\2"+
    "\40\0\22\2\36\0\15\3\4\0\1\3\3\0\6\3\27\0\1\2"+
    "\4\0\1\2\2\0\12\2\1\0\1\2\3\0\5\2\6\0\1\2"+
    "\1\0\1\2\1\0\1\2\1\0\4\2\1\0\3\2\1\0\7\2"+
    "\3\0\3\2\5\0\5\2\26\0\44\2\u0e81\0\3\2\31\0\11\2"+
    "\6\3\1\0\5\2\2\0\5\2\4\0\126\2\2\0\2\3\2\0"+
    "\3\2\1\0\137\2\5\0\50\2\4\0\136\2\21\0\30\2\70\0"+
    "\20\2\u0200\0\u19b6\2\112\0\u51a6\2\132\0\u048d\2\u0773\0\u2ba4\2\u215c\0"+
    "\u012e\2\2\0\73\2\225\0\7\2\14\0\5\2\5\0\1\2\1\3"+
    "\12\2\1\0\15\2\1\0\5\2\1\0\1\2\1\0\2\2\1\0"+
    "\2\2\1\0\154\2\41\0\u016b\2\22\0\100\2\2\0\66\2\50\0"+
    "\15\2\3\0\20\3\20\0\4\3\17\0\2\2\30\0\3\2\31\0"+
    "\1\2\6\0\5\2\1\0\207\2\2\0\1\3\4\0\1\2\13\0"+
    "\12\3\7\0\32\2\4\0\1\2\1\0\32\2\12\0\132\2\3\0"+
    "\6\2\2\0\6\2\2\0\6\2\2\0\3\2\3\0\2\2\3\0"+
    "\2\2\22\0\3\3\4\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\3\2\4\12\3\1\5\1\6"+
    "\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
    "\21\3\1\17\5\3\1\20\4\3\1\21\1\22\1\23"+
    "\1\24\3\3\1\25\1\3\1\26\1\27\1\30\2\3"+
    "\1\31\1\32\3\3\1\33";

  private static int [] zzUnpackAction() {
    int [] result = new int[75];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\40\0\100\0\100\0\140\0\100\0\200\0\240"+
    "\0\300\0\340\0\u0100\0\u0120\0\u0140\0\u0160\0\u0180\0\u01a0"+
    "\0\u01c0\0\100\0\100\0\100\0\100\0\100\0\100\0\100"+
    "\0\100\0\100\0\100\0\u01e0\0\u0200\0\u0220\0\u0240\0\u0260"+
    "\0\u0280\0\u02a0\0\u02c0\0\u02e0\0\u0300\0\u0320\0\u0340\0\u0360"+
    "\0\u0380\0\u03a0\0\u03c0\0\u03e0\0\140\0\u0400\0\u0420\0\u0440"+
    "\0\u0460\0\u0480\0\140\0\u04a0\0\u04c0\0\u04e0\0\u0500\0\140"+
    "\0\140\0\140\0\140\0\u0520\0\u0540\0\u0560\0\140\0\u0580"+
    "\0\140\0\140\0\140\0\u05a0\0\u05c0\0\140\0\140\0\u05e0"+
    "\0\u0600\0\u0620\0\140";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[75];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\3\1\6\1\7\1\10\1\11"+
    "\1\12\2\5\1\13\1\14\1\15\1\16\1\17\1\20"+
    "\1\21\4\5\1\22\1\23\1\24\1\25\1\26\1\27"+
    "\1\30\1\31\1\32\1\33\40\3\42\0\24\5\16\0"+
    "\2\7\34\0\5\5\1\34\16\5\14\0\23\5\1\35"+
    "\14\0\15\5\1\36\6\5\14\0\7\5\1\37\11\5"+
    "\1\40\2\5\14\0\5\5\1\41\5\5\1\42\10\5"+
    "\14\0\17\5\1\43\4\5\14\0\15\5\1\44\6\5"+
    "\14\0\16\5\1\45\5\5\14\0\21\5\1\46\2\5"+
    "\14\0\20\5\1\47\3\5\14\0\6\5\1\50\15\5"+
    "\14\0\5\5\1\51\16\5\14\0\11\5\1\52\12\5"+
    "\14\0\12\5\1\53\11\5\14\0\15\5\1\54\6\5"+
    "\14\0\13\5\1\55\10\5\14\0\21\5\1\56\2\5"+
    "\14\0\20\5\1\57\3\5\14\0\12\5\1\60\11\5"+
    "\14\0\13\5\1\61\10\5\14\0\10\5\1\62\13\5"+
    "\14\0\10\5\1\63\1\64\12\5\14\0\7\5\1\65"+
    "\14\5\14\0\10\5\1\66\13\5\14\0\12\5\1\67"+
    "\11\5\14\0\13\5\1\70\10\5\14\0\4\5\1\71"+
    "\17\5\14\0\22\5\1\72\1\5\14\0\5\5\1\73"+
    "\16\5\14\0\7\5\1\74\14\5\14\0\7\5\1\75"+
    "\14\5\14\0\4\5\1\76\17\5\14\0\5\5\1\77"+
    "\16\5\14\0\10\5\1\100\13\5\14\0\13\5\1\101"+
    "\10\5\14\0\5\5\1\102\16\5\14\0\16\5\1\103"+
    "\5\5\14\0\21\5\1\104\2\5\14\0\7\5\1\105"+
    "\14\5\14\0\5\5\1\106\16\5\14\0\10\5\1\107"+
    "\13\5\14\0\13\5\1\110\10\5\14\0\7\5\1\111"+
    "\14\5\14\0\21\5\1\112\2\5\14\0\10\5\1\113"+
    "\13\5\12\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1600];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\2\11\1\1\1\11\13\1\12\11\60\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[75];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  Lexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1768) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzCurrentPos*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = zzReader.read(zzBuffer, zzEndRead,
                                            zzBuffer.length-zzEndRead);

    if (numRead > 0) {
      zzEndRead+= numRead;
      return false;
    }
    // unlikely but not impossible: read 0 characters, but not at end of stream    
    if (numRead == 0) {
      int c = zzReader.read();
      if (c == -1) {
        return true;
      } else {
        zzBuffer[zzEndRead++] = (char) c;
        return false;
      }     
    }

	// numRead < 0
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      for (zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL;
                                                             zzCurrentPosL++) {
        switch (zzBufferL[zzCurrentPosL]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn++;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL[zzCurrentPosL++];
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL[zzCurrentPosL++];
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 25: 
          { return symbol(sym.DEFINE);
          }
        case 28: break;
        case 11: 
          { return symbol(sym.RBRACKET);
          }
        case 29: break;
        case 16: 
          { return symbol(sym.RUN);
          }
        case 30: break;
        case 12: 
          { return symbol(sym.SEMI);
          }
        case 31: break;
        case 26: 
          { return symbol(sym.ACT);
          }
        case 32: break;
        case 20: 
          { return symbol(sym.TRUE);
          }
        case 33: break;
        case 8: 
          { return symbol(sym.LPAREN);
          }
        case 34: break;
        case 22: 
          { return symbol(sym.EVENT);
          }
        case 35: break;
        case 13: 
          { return symbol(sym.COMMA);
          }
        case 36: break;
        case 23: 
          { return symbol(sym.FALSE);
          }
        case 37: break;
        case 1: 
          { throw new Error("Illegal character <"+
                                                    yytext()+">");
          }
        case 38: break;
        case 21: 
          { return symbol(sym.RULE);
          }
        case 39: break;
        case 19: 
          { return symbol(sym.STOP);
          }
        case 40: break;
        case 10: 
          { return symbol(sym.LBRACKET);
          }
        case 41: break;
        case 7: 
          { return symbol(sym.STAR);
          }
        case 42: break;
        case 5: 
          { return symbol(sym.EQUALS);
          }
        case 43: break;
        case 15: 
          { return symbol(sym.SET);
          }
        case 44: break;
        case 14: 
          { return symbol(sym.WHACKWHACK);
          }
        case 45: break;
        case 17: 
          { return symbol(sym.LIST);
          }
        case 46: break;
        case 27: 
          { return symbol(sym.CONDITION);
          }
        case 47: break;
        case 9: 
          { return symbol(sym.RPAREN);
          }
        case 48: break;
        case 6: 
          { return symbol(sym.PLUS);
          }
        case 49: break;
        case 18: 
          { return symbol(sym.LOAD);
          }
        case 50: break;
        case 4: 
          { return symbol(sym.NUMBER, new Integer(yytext()));
          }
        case 51: break;
        case 3: 
          { return symbol(sym.IDENTIFIER, new String(yytext()));
          }
        case 52: break;
        case 2: 
          { /* ignore */
          }
        case 53: break;
        case 24: 
          { return symbol(sym.BASIC);
          }
        case 54: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
              { return new java_cup.runtime.Symbol(sym.EOF); }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
