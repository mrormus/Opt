package com.pervasa.demo.kitsample.impl;

import java.util.StringTokenizer;

public class Action {
	String name;
	String actionList;
	String actionDisplay;

	public Action(String name, String actionlist, String actionDisplay){
		this.name = name;
		this.actionList = actionlist;
		this.actionDisplay = actionDisplay;
	}
	
	private void takeAction(String command) {
		System.out.println("Taking Action:" + command);
		StringTokenizer strTok = new StringTokenizer(command,"_()");
		String nodeId;
		String movement;
		String nodeType;
		double move;
		try {
			nodeType = strTok.nextToken();
			nodeId = strTok.nextToken();
			movement = strTok.nextToken();
			move = Integer.parseInt(movement);
			//servo is the only actuatoe, hence this call. Else need a class hierarchy
			//of actuators and fire the appropriate methods
			//move = (move*100)/180;
			moveServo(nodeId,(int)move);
		}
		catch(Exception e) {
			System.out.println("Invalid action: " + command);
		}
		
	}
	public void performAction(){
		try {
			StringTokenizer strTok = new StringTokenizer(actionList,";");
			while (strTok.hasMoreTokens()) {
				takeAction(strTok.nextToken());
			}
		}
		catch (NullPointerException ne){
			System.out.println("Problem with the action list " + actionList);
		}
		
	}
	public String toString() {
		return name + ":" + actionList;
	}
}
