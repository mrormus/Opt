package com.pervasa.reactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

interface Event {

	public void setName(String name);

	public String getName();

	public String toString();

	public boolean isSimple();

	public boolean isTFM();

	public boolean evaluate();

	public void update();

	public long getTimeLastChanged();

	public Set<Sensor> addSensorsTo(Set<Sensor> s);

}

enum EventType {
	SIMPLE, COMPOSITE, TFM
}

class EventBase {

	private String name;

	void setName(String name) {
		this.name = name;
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

	boolean isSimple() {
		return false;
	}

	boolean isTFM() {
		return false;
	}

}

class EventStatus {

	private boolean status = false;
	private long timeLastChanged = 0;

	boolean getStatus() {
		return status;
	}

	void setStatus(boolean newStatus) {
		if (status != newStatus) {
			timeLastChanged = Calendar.getInstance().getTimeInMillis();
		}
		status = newStatus;
	}

	long getTimeLastChanged() {
		return timeLastChanged;
	}
}

class SimpleEvent extends EventBase implements Event {

	// Fields for a basic event
	private EventStatus status;
	private Sensor sensor;
	private int min;
	private int max;

	/* Constructors */

	// Define a simple event from a sensor with one value
	SimpleEvent(Sensor sensor, Integer value) {
		this.status = new EventStatus();
		this.sensor = sensor;
		this.min = value;
		this.max = value;
	}

	// Define a simple event from a sensor with a range of values
	SimpleEvent(Sensor sensor, Integer min, Integer max) {
		this(sensor, min);
		this.max = max;
	}

	/* Methods */

	public void setName(String name) {
		super.setName(name);
	}

	public boolean isTFM() {
		return super.isTFM();
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	public String toString() {
		String ret = "";
		if (min == max) {
			ret = sensor.getNodeID() + "(" + min + ")";
		} else {
			ret = sensor.getNodeID() + "[" + min + "," + max + "]";
		}
		return ret;
	}

	public String getSensorNodeID() {
		return sensor.getNodeID();
	}

	public int getSensorType() {
		return sensor.getType();
	}

	// Using the current reading of the referenced sensor,
	// update the status of this simple event
	public void update() {
		// Using the referenced sensor reading, update the status if it is
		// within range
		status.setStatus(min <= sensor.reading() && max >= sensor.reading());

	}

	public boolean evaluate() {
		return status.getStatus();
	}

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		s.add(sensor);
		return s;
	}
}

class CompositeEvent extends EventBase implements Event {

	enum Operator {
		STAR, PLUS, SECS
	}

	EventStatus status = new EventStatus();
	Event left;
	private Operator op;
	private int secs;
	Event right;

	/* Constructors */

	CompositeEvent(Event left, Operator op, Event right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	CompositeEvent(Event left, int secs, Event right) {
		this(left, Operator.SECS, right);
		this.secs = secs;
	}

	/* Methods */

	public boolean isSimple() {
		return super.isSimple();
	}

	public boolean isTFM() {
		return super.isTFM();
	}

	public void setName(String name) {
		super.setName(name);
	}

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	public boolean evaluate() {
		return status.getStatus();
	}

	public String toString() {

		String ret = "";

		if (left != null && right != null) {
			// Composite event with children
			switch (op) {
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
		}

		return ret;
	}

	public void update() {

		// Update the children
		left.update();
		right.update();

		// Update the status
		switch (op) {
		case PLUS:
			status.setStatus(left.evaluate() || right.evaluate());
			break;
		case STAR:
			status.setStatus(left.evaluate() && right.evaluate());
			break;
		case SECS:
			status
					.setStatus(left.evaluate()
							&& right.evaluate()
							&& (left.getTimeLastChanged() - right
									.getTimeLastChanged()) <= (secs * 1000));
			break;
		}
	}

	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		left.addSensorsTo(s);
		right.addSensorsTo(s);
		return s;
	}

}

class TFMEvent extends EventBase implements Event {

	// Fields for TFM events
	private EventStatus status = new EventStatus();
	private Event modifiedEvent;
	private Window window;
	private EvalFreq evalFreq;
	private ReportFreq reportFreq;

	/* Constructors */

	// Define a Time-Frequency-Modulated event: <W,Fe,Fr>(e)
	TFMEvent(Event modifiedEvent, Window w, EvalFreq ef, Integer n) {

		this.modifiedEvent = modifiedEvent;
		this.window = w;
		this.evalFreq = ef;

		ReportFreq rf;
		if (n.equals(Integer.valueOf(0))) {
			rf = new ReportFreq();
		} else if (w.isNil()) {
			rf = new ReportFreq(n);
		} else {
			rf = new ReportFreq(n, ef, w);
		}
		this.reportFreq = rf;

	}

	/* Methods */

	public boolean isSimple() {
		return super.isSimple();
	}

	@Override
	public boolean isTFM() {
		return true;
	}

	public void setName(String name) {
		super.setName(name);
	}

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	public boolean evaluate() {
		return status.getStatus();
	}

	// toString method for pretty printing
	public String toString() {
		String ret = "";

		if (modifiedEvent != null) {
			String ef = "Inf";
			try {
				ef = Integer.toString(evalFreq.getDuration());
			} catch (Exception e) {
				// Ignore
			}
			ret = "<" + window.getInterval() + ", " + ef + ", " + reportFreq
					+ "> (" + modifiedEvent.getName() + ")";
		}

		return ret;
	}

	public EvalFreq getEvalFreq() {
		return evalFreq;
	}

	public Event getModifiedEvent() {
		return modifiedEvent;
	}

	public Window getWindow() {
		return window;
	}

	public void update() {
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
		status.setStatus(ret);

	}

	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		modifiedEvent.addSensorsTo(s);
		return s;
	}

}

class Window {

	private Date absStart;
	private Date absEnd;

	private Date relStart;
	private Date relEnd;

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

		SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yy");
		Date d1 = dateFmt.parse(date1);
		Date d2 = dateFmt.parse(date2);

		SimpleDateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");
		Date t1 = dateFmt2.parse(time1);
		Date t2 = dateFmt2.parse(time2);

		absStart = new Date(d1.getTime() + t1.getTime());
		absEnd = new Date(d2.getTime() + t2.getTime());

		this.type = ABSOLUTE;

	}

	// Constructor for a relative window
	public Window(String time1, String time2) {

		SimpleDateFormat dateFmt = new SimpleDateFormat("HH:mm:ss");
		try {
			relStart = dateFmt.parse(time1);
			relEnd = dateFmt.parse(time2);
		} catch (ParseException e) {
			System.out.println("Failed to parse times");
		}

		this.type = RELATIVE;
	}

	/* Functionality methods */

	public boolean withinWindow() {
		boolean ret = false;
		Date now = Calendar.getInstance().getTime();
		Calendar today = Calendar.getInstance();
		switch (type) {
		case INFINITE:
			// Always within window, return true
			ret = true;
			break;
		case RELATIVE:
			// Compare the current HH:mm:ss to the window
			today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
					today.get(Calendar.DATE), 0, 0, 0);
			Date thisMorning = today.getTime();
			Date start = new Date(thisMorning.getTime() + relStart.getTime());
			Date end = new Date(thisMorning.getTime() + relEnd.getTime());
			ret = start.before(now) && now.before(end);
			break;
		case ABSOLUTE:
			// Compare the current time to this window's date/times
			ret = absStart.before(now) && now.before(absEnd);
			break;
		}
		return ret;
	}

	public int returnType() {
		return type;
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
			ret = relEnd.getTime() - relStart.getTime();
			break;
		case ABSOLUTE:
			ret = absEnd.getTime() - absEnd.getTime();
			break;
		}

		return ret;
	}

	public boolean isNil() {
		return type == INFINITE;
	}

	/*
	 * Return the string representation of this window's interval
	 */
	public String getInterval() {
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

	public Date getAbsStart() {
		return absStart;
	}

	public Date getAbsEnd() {
		return absEnd;
	}

	public Date getRelStart() {
		return relStart;
	}

	public Date getRelEnd() {
		return relEnd;
	}

}

class EvalFreq {

	enum Type {
		INF, NUM
	}

	private int secs;
	private Type type;

	public EvalFreq() {
		this.type = Type.INF;
		System.err.println("inf def'd");
	}

	public EvalFreq(Integer n) {
		this.secs = n;
		this.type = Type.NUM;
		System.err.println("int def'd");
	}

	/*
	 * Return the number of seconds in between each evaluation
	 */
	int getDuration() throws Exception {
		if (type == Type.INF) {
			throw new Exception();
		}
		return secs;
	}

}

class ReportFreq {

	enum Type {
		ZERO, PERCENT, COUNT
	}

	private long threshold;
	private long current;
	private int percent;

	private Type type;

	/* Constructors */

	private ReportFreq(Type type) {
		this.current = 0;
		this.type = type;
	}

	ReportFreq() {
		this(Type.ZERO);
	}

	ReportFreq(Integer n) {
		this(Type.COUNT);
		this.threshold = n;
	}

	ReportFreq(Integer n, EvalFreq ef, Window w) {
		this(Type.PERCENT);
		this.percent = n;
		try {
			this.threshold = (n / 100)
					* (w.durationInMillis() / (ef.getDuration() * 1000));
		} catch (Exception e) {
			this.threshold = 0;
		}
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
