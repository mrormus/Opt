package com.pervasa.reactivity;

public class Rule {
	String name;
	OptEvent event;
	String action;
	String condition;
	
	
	public Rule (String name, OptEvent event, String condition, String action){
		this.name = name;
		this.event = event;
		this.action = action;
		this.condition = condition;	
	}

	
	public String toString() {
		return name + ":" + event + ":" + condition + ":" + action;
	}
}
