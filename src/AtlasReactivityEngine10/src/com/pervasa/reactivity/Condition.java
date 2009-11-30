package com.pervasa.reactivity;

public class Condition {
	boolean value;
	String name;
	
	public Condition (String name, String value){
		this.name = name;
		this.value = Boolean.parseBoolean(value);
	}
	public Condition (String name, boolean value){
		this.name = name;
		this.value = value;
	}
	public Condition(boolean b) {
		this.value = b;
	}
	public boolean getValue(){
		return value;
	}
	public void setName(String s) {
		this.name = s;
	}
	void set(boolean b) {
		this.value = b;
	}
	public String toString(){
		if (name != null) {
		return (name + ":" + value);
		} else {
			return "" + value;
		}
	}
}
