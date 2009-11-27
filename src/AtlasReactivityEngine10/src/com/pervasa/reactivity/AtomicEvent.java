package com.pervasa.reactivity;

//ameya: defined class to store events
public class AtomicEvent {
	String name;
	long startTime;
	String expression;
	String expansion;
	AtomicEvent left;
	AtomicEvent right;
	boolean value;
	
	public AtomicEvent(String name, String expression, String expansion){
		this.name = name;
		this.expression = expression;
		this.expansion = expansion;
		this.startTime = 0;
		this.value = false;
	}
	
	public String toString(){
		return (name + ":" + expansion + " TV:" + value + " Time: " + startTime);
	}
	
	public boolean getTruthValue(){
		return value;
	}

}
