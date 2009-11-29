package com.pervasa.reactivity;

import java.io.Reader;

public class Interpreter implements Runnable {

	private Errorz r;
	private Reader in;
	private Engine e;

	public Interpreter(Errorz r, Engine e, Reader in) {
		this.r = r;
		this.in = in;
		this.e = e;
	}


	
	@Override
	public void run() {
		System.err.println("Interpreter init");
		Lexer l = new Lexer(in);
		parser p = new parser(r,l,e);
		try {
			p.parse();
			System.err.println("FATAL ERROR: Parser stopped.");
		} catch (Exception exn) {
			System.err.println("Interpreter thread error");
			exn.printStackTrace();
		}

	}

}
