package com.pervasa.reactivity;

import java.util.ArrayList;
import java.util.Iterator;

interface Action {
	public void setName(String name);
	public String toString();
	public void execute();
}

class SimpleAction implements Action {
	
	String name; 
	
	// SimpleAction fields
	Actuator actuator;
	int value;


	public SimpleAction(Actuator actuator, int value) {
		this.actuator = actuator;
		this.value = value;
	}
	
	public void setName(String s) {
		this.name = s;
	}
	
	public String getName() {
		String ret = "";
		if (name == null) {
			ret = toString();
		} else {
			ret = name;
		}
		return ret;
	}
			
	
	public String toString() {
		return name + ": " + actuator;
	}
	
	public void execute() {
		actuator.actuate(value);
	}	
}

class CompositeAction implements Action {
	
	String name;
	
	// CompositeAction fields
	ArrayList<Action> actions;
	
	public CompositeAction(ArrayList<Action> actions){
		this.actions = actions;
	}	
	
	public void setName(String s) {
		this.name = s;
	}
	
	public String toString() {
		return name + ": " + actions.toString();
	}
	
	public void execute() {
		Iterator<Action> iter = actions.iterator();
		while (iter.hasNext()) {
			iter.next().execute();
		}
	}
}
