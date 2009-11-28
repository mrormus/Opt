package com.pervasa.reactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class OptEvent {

	// Type indicators
	public static final int STAR = 0;
	public static final int PLUS = 1;
	public static final int SECS = 2;

	// Fields common to all events
	private String name;
	private boolean status = false;
	private long timeLastChanged = 0;

	// Fields for a basic event
	private Device sensor;
	private int min;
	private int max;

	// Fields for composite events
	OptEvent left;
	private int operator;
	private int secs;
	OptEvent right;

	// Fields for TFM events
	private OptEvent modifiedEvent;
	private Window window;
	private EvalFreq evalFreq;
	private ReportFreq reportFreq;

	/* Private Constructors */

	// For constructing composite events
	private OptEvent(OptEvent left, OptEvent right) {
		this.left = left;
		this.right = right;
	}

	// Simple instantiation constructor
	private OptEvent() {

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
	static OptEvent TFM(OptEvent modifiedEvent, Window w, EvalFreq ef, Integer n) {
		OptEvent e = new OptEvent();
		e.modifiedEvent = modifiedEvent;
		e.window = w;
		e.evalFreq = ef;

		ReportFreq rf;
		if (n.equals(Integer.valueOf(0))) {
			rf = ReportFreq.ZERO();
		} else if (w.isNil()) {
			rf = ReportFreq.COUNT(n);
		} else {
			rf = ReportFreq.PERCENT(n, ef, w);
		}
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
			}
		} else if (modifiedEvent != null) {
			// TFM event
			ret = "<" + window.getInterval() + ", "
			+ Integer.toString(evalFreq.getDuration()) + ", "
			+ reportFreq + "> (" + modifiedEvent.getName() + ")";
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
		} else if (left != null && right != null) {

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
			}
		} else if (modifiedEvent != null) {
			updateStatus(updateTFM());
		} else {
			System.err.println("Severe error: Event improperly defined");
		}
	}

	private boolean updateTFM() {
		boolean ret = false;
		if (window.withinWindow()) {
			ret = modifiedEvent.evaluate();
			if (ret) {
				reportFreq.logOccurrence();
			}
		} else {
			// If we're outside the window, we should reset the log occurrences
			// so that next time we're in, we start from zero
			reportFreq.reset();
		}
		return ret;

	}

}

class Window {

	private DateTime absStart;
	private DateTime absEnd;

	private LocalTime relStart;
	private LocalTime relEnd;

	static final int INFINITE = 0;
	static final int ABSOLUTE = 1;
	static final int RELATIVE = 2;

	private int type;

	/* Constructors */

	// Constructor for an infinite window (always true)
	public Window() {
		this.type = INFINITE;
	}

	// Constructor for an absolute window
	public Window(String date1, String time1, String date2, String time2)
			throws Exception {

		DateTimeFormatter dateFmt = DateTimeFormat.forPattern("MM/dd/yy");
		DateTime d1 = dateFmt.parseDateTime(date1);
		DateTime d2 = dateFmt.parseDateTime(date2);

		DateTimeFormatter timeFmt = DateTimeFormat.forPattern("HH:mm:ss");
		LocalTime t1 = new LocalTime(timeFmt.parseDateTime(time1));
		LocalTime t2 = new LocalTime(timeFmt.parseDateTime(time2));

		absStart = d1.plus(t1.getMillisOfDay());
		absEnd = d2.plus(t2.getMillisOfDay());

	}

	// Constructor for a relative window
	public Window(String time1, String time2) {

		DateTimeFormatter timeFmt = DateTimeFormat.forPattern("HH:mm:ss");
		relStart = new LocalTime(timeFmt.parseDateTime(time1));
		relEnd = new LocalTime(timeFmt.parseDateTime(time2));

		this.type = RELATIVE;
	}

	/* Functionality methods */

	public boolean withinWindow() {
		boolean ret = false;
		switch (type) {
		case INFINITE:
			// Always within window, return true
			ret = true;
			break;
		case RELATIVE:
			// Compare the current HH:mm:ss to the window
			// (null is defined as the current instant in time in Joda-time)
			ret = relStart.isBefore(null) && relEnd.isAfter(null);
			break;
		case ABSOLUTE:
			// Compare the current time to this window's date/times
			// (null is defined as the current instant in time in Joda-time)
			ret = absStart.isBefore(null) && absEnd.isAfter(null);
			break;
		}
		return ret;
	}

	/*
	 * Returns the duration of this window in milliseconds
	 */
	public long durationInMillis() {

		long ret = 0L;

		switch (type) {
		case INFINITE:
			// This should probably never happen
			ret = Long.MAX_VALUE;
			break;
		case RELATIVE:
			ret = relEnd.millisOfDay().get() - relStart.millisOfDay().get();
			break;
		case ABSOLUTE:
			ret = absEnd.getMillis() - absEnd.getMillis();
			break;
		}

		return ret;
	}

	boolean isNil() {
		return type == INFINITE;
	}

	/*
	 * Return the string representation of this window's interval
	 */
	String getInterval() {
		String ret = "";
		switch (type) {
		case INFINITE:
			ret = "nil";
			break;
		case RELATIVE:
			ret = relStart + "-" + relEnd;
			break;
		case ABSOLUTE:
			ret = absStart + "-" + absEnd;
			break;
		}
		return ret;
	}

}

class EvalFreq {

	private int secs;

	public EvalFreq(Integer n) {
		this.secs = n;
	}

	/*
	 * Return the number of seconds in between each evaluation
	 */
	int getDuration() {
		return secs;
	}

}

class ReportFreq {

	static final int ZERO = 0;
	static final int PERCENT = 1;
	static final int COUNT = 2;

	private long threshold;
	private long current;
	private int percent;

	private int type;

	/* Constructors */

	private ReportFreq(int type) {
		this.current = 0;
		this.type = type;
	}

	static ReportFreq ZERO() {
		return new ReportFreq(ZERO);
	}

	static ReportFreq COUNT(Integer n) {
		ReportFreq rf = new ReportFreq(COUNT);
		rf.threshold = n;
		return rf;
	}

	static ReportFreq PERCENT(Integer n, EvalFreq ef, Window w) {
		ReportFreq rf = new ReportFreq(PERCENT);
		rf.percent = n;
		rf.threshold = (n / 100)
				* (w.durationInMillis() / (ef.getDuration() * 1000));
		return rf;
	}

	/* Methods */

	/*
	 * Called when the window closes. Resets the number of occurrences reported
	 * for this TFM event.
	 */
	void reset() {
		current = 0;
	}

	/*
	 * Called whenever the modifiedEvent evaluates to true. Increments the
	 * counter of reported occurrences.
	 */
	void logOccurrence() {
		current++;
	}

	/*
	 * Returns true if this TFM event has logged occurrences greater than or
	 * equal to its given threshold
	 */
	boolean isReporting() {
		return current >= threshold;
	}

	public String toString() {
		String ret = "";
		switch (type) {
		case ZERO:
			ret = "0";
			break;
		case COUNT:
			ret = Long.toString(threshold);
			break;
		case PERCENT:
			ret = Integer.toBinaryString(percent) + "%";
			break;
		}
		return ret;
	}

}