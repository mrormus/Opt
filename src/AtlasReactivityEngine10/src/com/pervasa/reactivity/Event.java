package com.pervasa.reactivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;

/**
 * Common methods that events of all types share
 * 
 * @author Patrick
 * 
 */
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

/**
 * Super class off of which other Event subclasses are based. Contains common
 * methods like getName.
 * 
 * @author Patrick
 * 
 */
class EventBase {

	private String name;

	void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (name != null) {
			return name;
		} else {
			return toString();
		}
	}

	boolean isSimple() {
		return false;
	}

	boolean isTFM() {
		return false;
	}

}

/**
 * Represents the status of an event. Maintains the truth value and the last
 * time modified
 * 
 * @author Patrick
 * 
 */
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

/**
 * The leaf nodes of the event tree. Basically a wrapper for a sensor, with a
 * filter for a specific range of values. Is associated with exactly one sensor
 * 
 * @author Patrick
 * 
 */
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

	/* Accessors */

	public String toString() {
		if (min == max) {
			return sensor.getNodeID() + "(" + min + ")";
		} else {
			return sensor.getNodeID() + "[" + min + ", " + max + "]";
		}
	}

	public String getSensorNodeID() {
		return sensor.getNodeID();
	}

	public int getSensorType() {
		return sensor.getType();
	}

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	/**
	 * Given a pre-constructed set, adds the sensors of this event subtree to
	 * the set
	 */
	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		s.add(sensor);
		return s;
	}

	public boolean isTFM() {
		return super.isTFM();
	}

	// Overrides EventBase's isSimple() method
	@Override
	public boolean isSimple() {
		return true;
	}

	// Called by rules when evaluating the rule and by update() when propagating
	// an update throughout the event tree
	public boolean evaluate() {
		return status.getStatus();
	}

	/* Mutators */

	public void setName(String name) {
		super.setName(name);
	}

	/**
	 * The terminal update() operation for an event tree update() call. Using
	 * the current reading of the referenced sensor, update the status of this
	 * simple event.
	 */
	public void update() {
		// Using the referenced sensor reading, update the status if it is
		// within range
		status.setStatus(min <= sensor.reading() && max >= sensor.reading());

	}

}

/**
 * Inner nodes of an event tree. References exactly two events. Evaluates to
 * true or false depending on this event's operator (can be PLUS for OR, or STAR
 * for AND, or SECS for time difference) and the evaluation of its subtrees.
 * 
 * @author Patrick
 * 
 */
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

	// Should never be called with Operator.PLUS
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

	/* Accessors */

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

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	/**
	 * Given a pre-constructed set, adds the sensors of this event subtree to
	 * the set
	 */
	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		left.addSensorsTo(s);
		right.addSensorsTo(s);
		return s;
	}

	public boolean isSimple() {
		return super.isSimple();
	}

	public boolean isTFM() {
		return super.isTFM();
	}

	public boolean evaluate() {
		return status.getStatus();
	}

	/* Mutators */

	public void setName(String name) {
		super.setName(name);
	}

	/**
	 * Propagates an update() throughout the event tree
	 */
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

}

class TFMEvent extends EventBase implements Event {

	private EventStatus status = new EventStatus();

	// Abstract representations of the components of a TFMEvent
	private Window window;
	private EvalFreq evalFreq;
	private ReportFreq reportFreq;

	// The update-timer responsible for managing this TFMEvent
	private Timer timer;

	// The base event which this TFMEvent is modfying...
	private Event modifiedEvent;

	// ... and the sensors associated with that event
	private HashSet<Sensor> sensors;

	/* Constructors */

	/**
	 * Define a Time-Frequency-Modified event: <W,Fe,Fr>(e)
	 * 
	 * Called by the parser, which constructs the window and evalFreq objects.
	 */
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

		this.sensors = new HashSet<Sensor>();
		this.modifiedEvent.addSensorsTo(this.sensors);

	}

	/* Methods */

	/* Accessors */

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

	public long getTimeLastChanged() {
		return status.getTimeLastChanged();
	}

	/**
	 * Given a pre-constructed set, adds the sensors of this event subtree to
	 * the set
	 * 
	 * Special note for TFMEvents: TFMEvents do not descend into their
	 * modifiedEvent's sensors, because the scheduler's timers handle the
	 * updating of a TFMEvent's sensors.
	 */
	public Set<Sensor> addSensorsTo(Set<Sensor> s) {
		return s;
	}

	public HashSet<Sensor> getSensors() {
		return this.sensors;
	}

	public boolean isSimple() {
		return super.isSimple();
	}

	// Overrides EventBase's isTFM() method
	@Override
	public boolean isTFM() {
		return true;
	}

	public boolean evaluate() {
		return status.getStatus();
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

	/* Mutators */

	public void setName(String name) {
		super.setName(name);
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	/**
	 * Called when an update is being propagated through the event tree by
	 * ReceivedData(). We only want TFMEvents's to be updated by the timers that
	 * are assigned to them. Those timers call the realUpdate() method.
	 */
	public void update() {
		// do nothing

	}

	/**
	 * Called by timers dedicated to updating this TFMEvent. Since the TFMEvent
	 * is skipped during the updateAll() traversal, the timers are fully
	 * responsible for keeping this event up-to-date.
	 */
	public void realUpdate() {

		// Initialize the return value
		boolean ret = false;

		if (window.withinWindow()) {

			// Propagate an update() throughout the modifiedEvent subTree
			modifiedEvent.update();

			// Get the reported status of the modifiedEvent
			ret = modifiedEvent.evaluate();

			if (ret) {
				// If the modifiedEvent is reporting true, this TFMEvent must
				// record the occurrence in its ReportFreq log
				reportFreq.logOccurrence();
			}
		} else {
			// If we're outside the window, reset the log occurrences
			// so that next time we're in, we start from zero
			reportFreq.reset();
		}

		// If this event is now reporting, there is no need for its timer to
		// keep pinging sensors, so cancel it.
		if (timer != null && reportFreq.isReporting()) {
			timer.cancel();
		}

		// Finish this realUpdate() call by actually updating the status of the
		// TFMEvent, exactly like an update() would have done
		status.setStatus(reportFreq.isReporting());

	}

}

class Window {

	private Date absStart;
	private Date absEnd;

	private String relStart;
	private String relEnd;

	enum Type {
		INFINITE, ABSOLUTE, RELATIVE
	}

	private Type type;

	/* Constructors */

	// Constructor for an infinite window (always true)
	public Window() {
		this.type = Type.INFINITE;
	}

	// Constructor for an absolute window
	public Window(String date1, String time1, String date2, String time2)
			throws Exception {

		// Date math is incredibly annoying

		SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yy");
		Date d1 = dateFmt.parse(date1);
		Date d2 = dateFmt.parse(date2);

		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		addTime(c1, time1);
		addTime(c2, time2);

		absStart = c1.getTime();
		absEnd = c2.getTime();

		this.type = Type.ABSOLUTE;

	}

	// Parses a string of the format "HH:mm:ss" and adjusts a calendar
	// accordingly
	private void addTime(Calendar c, String hhmmss) {
		StringTokenizer st = new StringTokenizer(hhmmss, ":");
		c.add(Calendar.HOUR_OF_DAY, Integer.parseInt(st.nextToken()));
		c.add(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
		c.add(Calendar.SECOND, Integer.parseInt(st.nextToken()));
	}

	// Constructor for a relative window
	public Window(String time1, String time2) {

		relStart = time1;
		relEnd = time2;
		this.type = Type.RELATIVE;
	}

	/* Accessors */

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

			Calendar c1 = Calendar.getInstance();
			c1.setTime(thisMorning);
			addTime(c1, relStart);
			Calendar c2 = Calendar.getInstance();
			c2.setTime(thisMorning);
			addTime(c2, relEnd);

			Date start = c1.getTime();
			Date end = c2.getTime();

			ret = start.before(now) && now.before(end);

			break;
		case ABSOLUTE:
			// Compare the current time to this window's date/times
			ret = absStart.before(now) && now.before(absEnd);
			break;
		}
		return ret;
	}

	public Date getRelStart() {
		Date d = calculateTime(relStart);
		return todayOrTomorrow(d);
	}

	public Date getRelEnd() {
		Date d = calculateTime(relEnd);
		return todayOrTomorrow(d);
	}

	// Returns a Date object, with the time set to today, plus the amount of
	// time indicated by the hhmmss string
	private Date calculateTime(String hhmmss) {

		Calendar today = Calendar.getInstance();
		today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today
				.get(Calendar.DATE), 0, 0, 0);
		addTime(today, hhmmss);
		return today.getTime();
	}

	// If we're outside of this window, add a day to the date argument.
	// Otherwise, leave it alone. This method is called when returning a
	// relative window's start/end times
	private Date todayOrTomorrow(Date d) {
		Date now = new Date();
		if (!now.before(calculateTime(relEnd))) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DATE, 1);
			d = c.getTime();
		}
		return d;
	}

	Type returnType() {
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
			try {
				SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
				Date start = fmt.parse(relStart);
				Date end = fmt.parse(relEnd);
				ret = end.getTime() - start.getTime();
			} catch (ParseException e) {
				System.err.println("Invalid time format");
			}
			break;
		case ABSOLUTE:
			ret = absEnd.getTime() - absEnd.getTime();
			break;
		}

		return ret;
	}

	public boolean isNil() {
		return type == Type.INFINITE;
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

}

class EvalFreq {

	enum Type {
		INF, NUM
	}

	private int secs;
	private Type type;

	public EvalFreq() {
		this.type = Type.INF;
	}

	public EvalFreq(Integer n) {
		this.secs = n;
		this.type = Type.NUM;
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
			float q1 = (float) n / (float) 100;
			long q2 = w.durationInMillis() / (ef.getDuration() * 1000);
			float fin = q1 * q2;
			this.threshold = (long) fin;
		} catch (Exception e) {
			this.threshold = 0;
		}
	}

	/* Methods */
	
	/* Accessors */
	
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
			ret = Integer.toString(percent) + "%";
			break;
		}
		return ret;
	}
	
	/*
	 * Returns true if this TFM event has logged occurrences greater than or
	 * equal to its given threshold
	 */
	boolean isReporting() {
		return current >= threshold;
	}

	public String current() {
		return current + "/" + threshold;
	}
	
	/* Mutators */

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

}
