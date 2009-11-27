package com.pervasa.reactivity;

import java.util.Calendar;

public class OptEvent {

	public static final int STAR = 0;
	public static final int PLUS = 1;
	public static final int SECS = 2;
	public static final int TFM = 3;

	String name;
	boolean status = false;
	long timeLastChanged = 0;

	Device sensor;
	int min;
	int max;

	OptEvent left;
	int operator;
	int secs;
	OptEvent right;

	// Private constructor
	private OptEvent(OptEvent left, OptEvent right) {
		this.left = left;
		this.right = right;
	}

	/* constructors */
	/*
	 * Events constructed with these methods cannot be referenced directly
	 * through the ReactiveEngine's eventList map. Instead, they are
	 * subcomponents, or children, of such a named event.
	 */

	/* Simple Event Constructors */

	// Define a simple event from a sensor with one value
	OptEvent(Device sensor, Integer value) {
		this.sensor = sensor;
		this.min = value;
		this.max = value;
	}

	// Define a simple event from a sensor with a range of values
	OptEvent(Device sensor, Integer min, Integer max) {
		this(sensor, min);
		this.max = max;
	}

	/* Composite Event Constructors */

	// Define an OR composite event
	static OptEvent OR(OptEvent left, OptEvent right) {
		OptEvent e = new OptEvent(left, right);
		e.operator = PLUS;
		return e;
	}

	// Define an AND composite event
	static OptEvent AND(OptEvent left, OptEvent right) {
		OptEvent e = new OptEvent(left, right);
		e.operator = STAR;
		return e;
	}

	// Define a composite, time difference event (e1*secs*e2)
	static OptEvent SECS(OptEvent left, int secs, OptEvent right) {
		OptEvent e = new OptEvent(left, right);
		e.operator = SECS;
		e.secs = secs;
		return e;
	}

	/* Functionality Methods */
	
	public void assignName(String name) {
		this.name = name;
	}

	// toString method for pretty printing
	public String toString() {
		String ret = "";

		if (left != null && right != null) {
			// Composite event with children
			switch (operator) {
			case STAR:
				ret = left.getName() + "*" + right.getName();
				break;
			case PLUS:
				ret = left.getName() + "+" + right.getName();
				break;
			case SECS:
				ret = left.getName() + "*" + secs + "*" + right.getName();
				break;
			case TFM:
				// TODO
				ret = "TFM";
				break;
			}
		} else {
			// Simple event, based on a sensor
			if (min == max) {
				ret = sensor.getNodeID() + "(" + min + ")";
			} else {
				ret = sensor.getNodeID() + "[" + min + "," + max + "]";
			}
		}

		return ret;
	}
	
	public String getName() {
		String ret = "";
		if (name != null) {
			ret = name;
		} else {
			ret = toString();
		}
		return ret;
	}
	
	public String getSensorNodeID() {
		return sensor.getNodeID();
	}
	
	public int getSensorType() {
		return sensor.getType();
	}
	
	public boolean isSimple() {
		return (left == null && right == null);
	}

	// Return the truth value of this event
	boolean evaluate() {
		return this.status;
	}

	// Update the status of this event, modifying timeLastChanged if appropriate
	private void updateStatus(boolean newStatus) {

		// Update the status of this event
		status = newStatus;

		// If the status changed, record the time that it changed
		if (status != newStatus) {
			timeLastChanged = Calendar.getInstance().getTimeInMillis();
		}
	}

	// Using the current reading of the referenced sensor,
	// update the status of this simple event
	void updateSimpleEvent() {

		// First, ensure that this event is indeed a simple event
		// and that checking the assigned sensor value won't throw
		// a null pointer exception
		if (left == null && right == null) {
			// Using the referenced sensor reading, update the status if it is
			// within range
			updateStatus(min <= sensor.reading() && max >= sensor.reading());
		}
	}

	// Propagate truth values down through this event tree
	void update() {
		System.out.println("Updating event '" + getName() + "'");
		if (left == null && right == null) {
			// Simple event: Status is directly based on a sensor reading
			updateSimpleEvent();
		} else {

			// Composite event: has children

			// Update the children
			left.update();
			right.update();

			// Update the status
			switch (operator) {
			case PLUS:
				updateStatus(left.evaluate() || right.evaluate());
				break;
			case STAR:
				updateStatus(left.evaluate() && right.evaluate());
				break;
			case SECS:
				// TODO
				updateStatus(left.evaluate()
						&& right.evaluate()
						&& (left.timeLastChanged - right.timeLastChanged) <= (secs * 1000));
				break;
			case TFM:
				// TODO
				break;
			}

		}
	}

}
