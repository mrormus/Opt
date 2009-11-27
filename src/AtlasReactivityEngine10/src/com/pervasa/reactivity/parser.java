
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Wed Nov 25 20:58:44 EST 2009

package com.pervasa.reactivity;


/** CUP v0.11a beta 20060608 generated parser.
  * @version Wed Nov 25 20:58:44 EST 2009
  */
public class parser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public parser() {super();}

  /** Constructor which sets the default scanner. */
  public parser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public parser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\042\000\002\002\003\000\002\002\004\000\002\002" +
    "\003\000\002\002\003\000\002\002\006\000\002\002\003" +
    "\000\002\002\003\000\002\002\004\000\002\003\003\000" +
    "\002\003\004\000\002\003\004\000\002\004\003\000\002" +
    "\004\004\000\002\004\004\000\002\004\004\000\002\004" +
    "\004\000\002\005\007\000\002\005\007\000\002\005\007" +
    "\000\002\005\013\000\002\012\005\000\002\012\003\000" +
    "\002\013\003\000\002\013\003\000\002\011\007\000\002" +
    "\011\006\000\002\011\010\000\002\011\003\000\002\006" +
    "\003\000\002\006\003\000\002\010\005\000\002\010\003" +
    "\000\002\007\003\000\002\007\006" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\106\000\020\006\005\007\012\010\004\011\010\012" +
    "\016\013\014\014\015\001\002\000\010\002\ufff9\015\107" +
    "\017\110\001\002\000\012\015\034\016\033\017\035\020" +
    "\032\001\002\000\004\002\031\001\002\000\004\002\001" +
    "\001\002\000\004\005\024\001\002\000\004\002\ufffe\001" +
    "\002\000\014\002\ufff6\015\022\016\021\017\023\020\020" +
    "\001\002\000\004\002\uffff\001\002\000\004\002\ufffb\001" +
    "\002\000\004\005\017\001\002\000\004\002\ufffc\001\002" +
    "\000\004\002\ufffa\001\002\000\004\002\ufff2\001\002\000" +
    "\004\002\ufff3\001\002\000\004\002\ufff5\001\002\000\004" +
    "\002\ufff4\001\002\000\004\021\025\001\002\000\006\022" +
    "\027\023\030\001\002\000\004\002\ufffd\001\002\000\004" +
    "\002\uffe5\001\002\000\004\002\uffe4\001\002\000\004\002" +
    "\000\001\002\000\004\005\100\001\002\000\004\005\075" +
    "\001\002\000\004\005\050\001\002\000\004\005\036\001" +
    "\002\000\004\021\037\001\002\000\004\005\042\001\002" +
    "\000\006\002\uffe2\032\uffe2\001\002\000\006\002\ufff0\032" +
    "\046\001\002\000\010\002\uffe1\026\043\032\uffe1\001\002" +
    "\000\004\004\044\001\002\000\004\027\045\001\002\000" +
    "\006\002\uffe0\032\uffe0\001\002\000\004\005\042\001\002" +
    "\000\006\002\uffe3\032\uffe3\001\002\000\004\021\051\001" +
    "\002\000\004\005\054\001\002\000\010\002\ufff1\024\072" +
    "\025\071\001\002\000\010\002\uffec\024\uffec\025\065\001" +
    "\002\000\014\002\uffe6\024\uffe6\025\uffe6\026\055\030\056" +
    "\001\002\000\004\004\063\001\002\000\004\004\057\001" +
    "\002\000\004\033\060\001\002\000\004\004\061\001\002" +
    "\000\004\031\062\001\002\000\010\002\uffe7\024\uffe7\025" +
    "\uffe7\001\002\000\004\027\064\001\002\000\010\002\uffe8" +
    "\024\uffe8\025\uffe8\001\002\000\004\004\066\001\002\000" +
    "\004\025\067\001\002\000\004\005\054\001\002\000\010" +
    "\002\uffe9\024\uffe9\025\uffe9\001\002\000\004\005\uffeb\001" +
    "\002\000\004\005\uffea\001\002\000\004\005\054\001\002" +
    "\000\010\002\uffed\024\uffed\025\065\001\002\000\004\021" +
    "\076\001\002\000\006\022\027\023\030\001\002\000\004" +
    "\002\uffef\001\002\000\004\021\101\001\002\000\004\005" +
    "\102\001\002\000\004\033\103\001\002\000\004\005\104" +
    "\001\002\000\004\033\105\001\002\000\004\005\106\001" +
    "\002\000\004\002\uffee\001\002\000\004\002\ufff8\001\002" +
    "\000\004\002\ufff7\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\106\000\012\002\005\003\006\004\012\005\010\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\004\006\025\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\006\007\037\010\040" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\004\007\046\001\001\000\002\001\001\000\002\001" +
    "\001\000\006\011\052\012\051\001\001\000\004\013\072" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\011\067\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\004\011\073\001\001\000\002\001\001" +
    "\000\002\001\001\000\004\006\076\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}




  ReactiveEngine re;
  
  public parser(Lexer l, ReactiveEngine re) {
   	this(l);
  	this.re = re;
  }
  
  public void syntax_error(java_cup.runtime.Symbol current) {
    report_error("Syntax error (" + current.sym + ")", current);
  }
  public void report_error(String message, java_cup.runtime.Symbol info) {
    re.error(message);
  }
  

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$parser$actions {
  private final parser parser;

  /** Constructor */
  CUP$parser$actions(parser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$parser$do_action(
    int                        CUP$parser$act_num,
    java_cup.runtime.lr_parser CUP$parser$parser,
    java.util.Stack            CUP$parser$stack,
    int                        CUP$parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$parser$result;

      /* select the action based on the action number */
      switch (CUP$parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 33: // act ::= IDENTIFIER LPAREN NUMBER RPAREN 
            {
              String RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int nleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Integer n = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		
	 	RESULT = "";
	  	String basicAction = i + "(" + n + ")";
	  	if (parser.re.basicActionExists(i)) {
	  		RESULT = basicAction;
	  	} else {}
	 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("act",5, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 32: // act ::= IDENTIFIER 
            {
              String RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = ""; 
		String expansion = parser.re.evaluateAction(i);
		if (expansion.matches("invalid")) {
		} else {
			RESULT = expansion;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("act",5, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 31: // acts ::= act 
            {
              String RESULT =null;
		int sleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int sright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String s = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		if (s.matches("")) {
		} else {
			RESULT = s;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("acts",6, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 30: // acts ::= acts SEMI act 
            {
              String RESULT =null;
		int s1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int s1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String s1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int s2left = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int s2right = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String s2 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		if (s2.matches("")) {
		} else {
			RESULT = s1 + ";" + s2;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("acts",6, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 29: // boolean ::= FALSE 
            {
              Boolean RESULT =null;
		 RESULT = false; 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("boolean",4, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 28: // boolean ::= TRUE 
            {
              Boolean RESULT =null;
		 RESULT = true; 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("boolean",4, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 27: // event ::= IDENTIFIER 
            {
              String RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		String expansion = parser.re.evaluateAtomicEvent(i);
		if (expansion.matches("invalid")) {
			parser.re.error("Expansion of '" + i + " failed.");
		} else {
			RESULT = expansion;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("event",7, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 26: // event ::= IDENTIFIER LBRACKET NUMBER COMMA NUMBER RBRACKET 
            {
              String RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-5)).value;
		int n1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int n1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		Integer n1 = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int n2left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int n2right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Integer n2 = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		
		RESULT = "";
	  	String basicEvent = i + "[" + n1 + "," + n2 + "]";
	  	if (parser.re.basicEventExists(i)) {
	  		RESULT = basicEvent;
	  	} else {
	  		parser.re.error("Event '" + basicEvent + " does not exist.");
	  	}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("event",7, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-5)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 25: // event ::= IDENTIFIER LPAREN NUMBER RPAREN 
            {
              String RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-3)).value;
		int nleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Integer n = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		
		RESULT = "";
		String basicEvent = i + "(" + n + ")";
		if (parser.re.basicEventExists(i)) {
			RESULT = basicEvent;
		} else {
			parser.re.error("Event '" + basicEvent + " does not exist.");
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("event",7, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 24: // event ::= event STAR NUMBER STAR event 
            {
              String RESULT =null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)).right;
		String e1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-4)).value;
		int nleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		Integer n = (Integer)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String e2 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		if (e1.matches("") || e2.matches("")) {
		} else { 
			RESULT = e1 + "*" + n + "*" + e2;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("event",7, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 23: // eventoperator ::= PLUS 
            {
              String RESULT =null;
		 RESULT = "+"; 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("eventoperator",9, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 22: // eventoperator ::= STAR 
            {
              String RESULT =null;
		 RESULT = "*"; 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("eventoperator",9, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 21: // events ::= event 
            {
              String RESULT =null;
		int eleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String e = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		if (e.matches("")) {
			// Empty string indicates that e is an invalid event
			// Therefore: do nothing
		} else {
			RESULT = e;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("events",8, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // events ::= events eventoperator event 
            {
              String RESULT =null;
		int e1left = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int e1right = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String e1 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int oleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int oright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		String o = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		int e2left = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int e2right = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String e2 = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		RESULT = "";
		if (e2.matches("")) {
			// Empty string indicates that e2 is an invalid event
			// Therefore: do nothing
		} else {
			RESULT = e1 + o + e2;
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("events",8, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // define ::= DEFINE RULE IDENTIFIER EQUALS IDENTIFIER COMMA IDENTIFIER COMMA IDENTIFIER 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-6)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-6)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-6)).value;
		int eleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)).right;
		String e = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-4)).value;
		int cleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int cright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String c = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int aleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int aright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String a = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		 parser.re.defineRule(i, e, c, a); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("define",3, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-8)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // define ::= DEFINE CONDITION IDENTIFIER EQUALS boolean 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int bleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int bright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		Boolean b = (Boolean)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		 parser.re.defineCondition(i, b); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("define",3, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // define ::= DEFINE ACT IDENTIFIER EQUALS acts 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int aleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int aright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String a = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		
		if (a.matches("")) {
			// Empty string indicates that an invalid action was specified
			// Therefore: do nothing.
		} else {
			parser.re.defineAction(i, a, a);
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("define",3, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // define ::= DEFINE EVENT IDENTIFIER EQUALS events 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int eleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int eright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		String e = (String)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		 
		if (e.matches("")) {
			// Empty string indicates that an invalid event was specified
			// Therefore: do nothing
		} else {
			parser.re.defineEvent(i, e, e);
		}
	
              CUP$parser$result = parser.getSymbolFactory().newSymbol("define",3, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-4)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // list ::= LIST RULE 
            {
              Object RESULT =null;
		 parser.re.listRules(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("list",2, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // list ::= LIST CONDITION 
            {
              Object RESULT =null;
		 parser.re.listConditions(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("list",2, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // list ::= LIST ACT 
            {
              Object RESULT =null;
		 parser.re.listActions(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("list",2, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // list ::= LIST EVENT 
            {
              Object RESULT =null;
		 parser.re.listEvents(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("list",2, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // list ::= LIST 
            {
              Object RESULT =null;
		 parser.re.listAll(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("list",2, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // basic ::= BASIC ACT 
            {
              Object RESULT =null;
		 parser.re.listBasicActions(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("basic",1, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // basic ::= BASIC EVENT 
            {
              Object RESULT =null;
		 parser.re.listBasicEvents(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("basic",1, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // basic ::= BASIC 
            {
              Object RESULT =null;
		 parser.re.listBasic(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("basic",1, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // cmd ::= LOAD IDENTIFIER 
            {
              Object RESULT =null;

              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // cmd ::= STOP 
            {
              Object RESULT =null;
		 parser.re.stopCommand(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // cmd ::= RUN 
            {
              Object RESULT =null;
		 parser.re.runCommand(); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // cmd ::= SET IDENTIFIER EQUALS boolean 
            {
              Object RESULT =null;
		int ileft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-2)).right;
		String i = (String)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-2)).value;
		int bleft = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).left;
		int bright = ((java_cup.runtime.Symbol)CUP$parser$stack.peek()).right;
		Boolean b = (Boolean)((java_cup.runtime.Symbol) CUP$parser$stack.peek()).value;
		 parser.re.setCondition(i, b); 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-3)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // cmd ::= define 
            {
              Object RESULT =null;

              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // cmd ::= list 
            {
              Object RESULT =null;

              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= cmd EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$parser$stack.elementAt(CUP$parser$top-1)).value;
		RESULT = start_val;
              CUP$parser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$parser$stack.elementAt(CUP$parser$top-1)), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$parser$parser.done_parsing();
          return CUP$parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // cmd ::= basic 
            {
              Object RESULT =null;

              CUP$parser$result = parser.getSymbolFactory().newSymbol("cmd",0, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }
          return CUP$parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

