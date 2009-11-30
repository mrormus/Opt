package com.pervasa.reactivity;

public class Rule {
	private String name;
	private Event event;
	private Action action;
	private Condition condition;
	
	
	public Rule (String name, Event event, Condition condition, Action action){
		this.name = name;
		this.event = event;
		this.action = action;
		this.condition = condition;	
	}

	public String toString() {
		return name + ":" + event + ":" + condition + ":" + action;
	}
	
	void evaluate() {
		try {
			boolean eventVal = event.evaluate();
			System.out.println("Rule Eval:" + name + ":" + eventVal);
			if (eventVal == true) {
				// event has occurred
				// check conditions here
				if (condition.getValue()) {
					// condition is true, trigger action
					action.execute();
				}
			}
		} catch (NullPointerException ne) {
			System.out
					.println("Condition, event or action was not found in the maps!");
			ne.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Condition getCondition() {
		return condition;
	}
	
	Event getEvent() {
		return event;
	}
	
}
