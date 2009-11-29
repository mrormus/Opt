package com.pervasa.reactivity;

public class Syntax {
	
	public static String error (java_cup.runtime.Symbol symbol) {
		StringBuilder ret = new StringBuilder();
		ret.append("Syntax error near '");
		switch (symbol.sym) {
		
		case sym.DEFINE: ret.append("DEFINE"); break;
		case sym.LIST: ret.append("LIST"); break;
		case sym.BASIC: ret.append("BASIC"); break;
		case sym.STOP: ret.append("STOP"); break;
		case sym.RUN: ret.append("RUN"); break;
		case sym.SET: ret.append("SET"); break;		
		case sym.LOAD: ret.append("LOAD"); break;
		
		case sym.EVENT: ret.append("EVENT"); break;
		case sym.CONDITION: ret.append("CONDITION"); break;
		case sym.ACT: ret.append("ACTION"); break;
		case sym.RULE: ret.append("RULE"); break;
		
		case sym.PLUS: ret.append("+"); break;
		case sym.STAR: ret.append("*"); break;
		
		case sym.LPAREN: ret.append("("); break;
		case sym.RPAREN: ret.append(")"); break;
		case sym.LBRACKET: ret.append("["); break;
		case sym.RBRACKET: ret.append("]"); break;
		case sym.EQUALS: ret.append("="); break;
		case sym.SEMI: ret.append(";"); break;
		case sym.COMMA: ret.append(","); break;

		case sym.WHACKWHACK: ret.append("\\\\"); break;
		
		case sym.TRUE: ret.append("TRUE"); break;
		case sym.FALSE: ret.append("FALSE"); break;
		
		case sym.error: ret.append("error"); break;
		case sym.EOF: ret.append("end of input"); break;
		
		case sym.IDENTIFIER:
			ret.append((String) symbol.value);
			break;
		case sym.NUMBER:
			Integer i = (Integer) symbol.value;
			ret.append(i.toString());
			break;
		default:
			ret.append("UNKNOWN");
		}
		
		ret.append("'.");
		
		return ret.toString();
	}
	
	 

}
