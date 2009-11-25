package com.pervasa.reactivity;

public class Action {
	String name;
	String actionList;
	String actionDisplay;

	public Action(String name, String actionlist, String actionDisplay){
		this.name = name;
		this.actionList = actionlist;
		this.actionDisplay = actionDisplay;
	}
	
	public String toString() {
		return name + ":" + actionList;
	}
}
