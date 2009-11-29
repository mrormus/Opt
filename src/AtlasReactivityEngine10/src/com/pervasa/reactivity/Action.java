package com.pervasa.reactivity;

import java.util.Iterator;
import java.util.List;

public class Action {
	
	String name; 
	
	// Composite action
	List<Action> actions;
	
	// Simple action
	Actuator actuator;
	int value;

	public Action(List<Action> actions){
		this.actions = actions;
	}
	
	public Action(Actuator actuator, int value) {
		this.actuator = actuator;
		this.value = value;
	}
	
	void setName(String s) {
		this.name = s;
	}
	
	public String toString() {
		return name + ":" + actions;
	}
	
	void execute() {
		if (actions != null) {
			Iterator<Action> iter = actions.iterator();
			while (iter.hasNext()) {
				iter.next().execute();
			}
		} else {
			actuator.actuate(value);
		}
	}	
}
