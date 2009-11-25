package com.pervasa.reactivity;

public class Rule {
	String name;
	String event;
	String action;
	String condition;
	
	
	public Rule (String name, String event, String condition, String action){
		this.name = name;
		this.event = event;
		this.action = action;
		this.condition = condition;	
	}

	
	public String toString() {
		return name + ":" + event + ":" + condition + ":" + action;
	}
}
