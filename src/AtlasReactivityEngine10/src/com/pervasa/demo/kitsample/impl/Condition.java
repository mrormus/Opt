package com.pervasa.demo.kitsample.impl;

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
	public boolean getValue(){
		return value;
	}
	public String toString(){
		return (name + ":" + value);
	}
}
