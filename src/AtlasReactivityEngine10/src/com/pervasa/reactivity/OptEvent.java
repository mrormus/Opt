package com.pervasa.reactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class OptEvent {

	// Type indicators
	public static final int STAR = 0;
	public static final int PLUS = 1;
	public static final int SECS = 2;
	public static final int TFM  = 3;

	// Fields common to all events
	private String name;
	boolean status = false;
	long timeLastChanged = 0;

	// Fields for a basic event
	Device sensor;
	int min;
	int max;

	// Fields for composite events
	OptEvent left;
	int operator;
	int secs;
	OptEvent right;
	
	// Fields for TFM events
	OptEvent modifiedEvent;
	Window window;
	EvalFreq evalFreq;
	ReportFreq reportFreq;

	/* Private Constructors */
	
	// For constructing composite events
	private OptEvent(OptEvent left, OptEvent right) {
		this.left = left;
		this.right = right;
	}
	
	// For constructing TFM events
	private OptEvent(OptEvent modifiedEvent) {
		this.modifiedEvent = modifiedEvent;
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

	// Define a composite, time difference event: e1*secs*e2
	static OptEvent SECS(OptEvent left, int secs, OptEvent right) {
		OptEvent e = new OptEvent(left, right);
		e.operator = SECS;
		e.secs = secs;
		return e;
	}
	
	// Define a Time-Frequency-Modulated event: <W,Fe,Fr>(e)
	static OptEvent TFM (OptEvent modifiedEvent, Window w, EvalFreq ef, ReportFreq rf) {
		OptEvent e = new OptEvent(modifiedEvent);
		e.window = w;
		e.evalFreq = ef;
		e.reportFreq = rf;
		return e;
	}

	/* Functionality Methods */

	public void setName(String name) {
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
				updateStatus(left.evaluate()
						&& right.evaluate()
						&& (left.timeLastChanged - right.timeLastChanged) <= (secs * 1000));
				break;
			case TFM:
				updateStatus(tfmStatus());
				break;
			}
		}
	}
	
	private boolean tfmStatus() {
		boolean ret = false;
		if (window.withinWindow()) {
			ret = modifiedEvent.evaluate();
		}
		return ret;

	}

}

class Window {

	private Date date1;
	private Date time1;
	private Date date2;
	private Date time2;

	static final int INFINITE = 0;
	static final int ABSOLUTE = 1;
	static final int RELATIVE = 2;

	private int type;

	public Window() {
		this.type = INFINITE;
	}

	public Window(String date1, String time1, String date2, String time2) throws Exception {

		try {
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
			this.date1 = df.parse(date1);
			this.date2 = df.parse(date2);

			df.applyPattern("HH:mm:ss");
			this.time1 = df.parse(time1);
			this.time2 = df.parse(time2);
		} catch (ParseException e) {
			throw new Exception("Parsing error while creating time window for TFM event");
		}

		this.type = ABSOLUTE;
	}

	public Window(String time1, String time2) {

		try {
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			this.time1 = df.parse(time1);
			this.time2 = df.parse(time2);
		} catch (ParseException e) {
			// FIXME: I have no idea what to do in this case
			System.err.println("Couldn't parse strings, please restart RE.");
		}

		this.type = RELATIVE;
	}
	
	public boolean withinWindow() {
		boolean ret = false;
		switch (type) {
		case INFINITE:
			// Always within window, return true
			ret = true;
			break;
		case RELATIVE:
			// Compare the current HH:mm:ss to the window
			// TODO
			break;
		case ABSOLUTE:
			// Compare the current time to this window's date/times
			// TODO
			break;
		}
		return ret;
	}
	
}

class EvalFreq {
	
	private int secs;
	
	public EvalFreq (Integer n) {
		this.secs = n;
	}

}

class ReportFreq {
	
	static final int ZERO = 0;
	static final int PERCENT = 1;
	static final int COUNT = 2;
	
	private int n;
	private int type;
	
	private ReportFreq(int type) {
		this.type = type;
	}
	
	static ReportFreq ZERO () {
		return new ReportFreq(ZERO);
	}
	
	static ReportFreq COUNT (Integer n) {
		ReportFreq rf = new ReportFreq(COUNT);
		rf.n = n;
		return rf;
	}
	
	static ReportFreq PERCENT (Integer n) {
		ReportFreq rf = new ReportFreq(PERCENT);
		rf.n = n;
		return rf;
	}

}