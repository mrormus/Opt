package com.pervasa.reactivity;

import java.io.Reader;

public class Interpreter implements Runnable {

	private ErrorReporter r;
	private Reader in;
	private Engine e;

	public Interpreter(ErrorReporter r, Engine e, Reader in) {
		this.in = in;
		this.e = e;
	}

	@Override
	public void run() {
		System.err.println("Interpreter init");
		Lexer l = new Lexer(in);
		parser p = new parser(r, l, e);
		try {
			p.parse();
			System.err.println("FATAL ERROR: Parser stopped.");
		} catch (Exception exn) {
			System.err.println("Interpreter thread error");
			exn.printStackTrace();
		}

	}

}
