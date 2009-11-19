package com.pervasa.demo.kitsample.impl;

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

	public void evaluate(){
		try {
			boolean eventVal = eventList.get(this.event).getTruthValue();
			System.out.println("Rule Eval:" + name + ":" + eventVal);
			if (eventVal == true){
				//event has occured
				//check conditions here
				boolean condVal = runtimeConditions.get(this.condition).getValue();
				if (condVal) {
					//condition is true, trigger action
					runtimeActions.get(this.action).performAction();
				}
			}
		}
		catch(NullPointerException ne){
			System.out.println("Condition, event or action was not found in the maps!");
			ne.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return name + ":" + event + ":" + condition + ":" + action;
	}
}
