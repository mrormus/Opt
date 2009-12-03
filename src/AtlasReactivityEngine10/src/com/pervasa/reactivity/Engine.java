package com.pervasa.reactivity;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.osgi.framework.ServiceReference;
import org.sensorplatform.actuators.servo.hs322.HS322Servo;
import org.sensorplatform.sensors.digitalcontact.DigitalContactSensor;
import org.sensorplatform.sensors.humidity.HumiditySensor;
import org.sensorplatform.sensors.pressure.InterlinkPressureSensor;
import org.sensorplatform.sensors.temperature.TemperatureSensor;

import com.pervasa.atlas.dev.service.AtlasClient;
import com.pervasa.atlas.dev.service.AtlasService;

class Engine {

	private GUI gui;

	Logic logic = new Logic();
	State state = new State();
	Scheduler scheduler = new Scheduler();
	int counter = 0;

	void init(GUI gui) {
		this.gui = gui;
	}

	public void error(String s) {
		System.err.println(s);
	}

	private void updateConsole(String s) {
		gui.update(s);
	}

	/**
	 * Clean up the engine, wipe all the state, cancel all the timer threads.
	 */
	void close() {
		state.close();
		scheduler.close();
	}

	enum StateType {
		ACTUATOR, SENSOR, EVENT, CONDITION, ACTION, RULE
	};

	/**
	 * The Reactivity Engine component that takes care of maintaining the state
	 * of the system. Includes accessor and mutator methods for appropriate
	 * variables and other state.
	 * 
	 * @author Patrick
	 * 
	 */
	class State {

		// True if the engine is running, false otherwise
		private boolean run = false;

		// Maintain a list of all registered services so that if they go
		// offline, we can handle it
		Map<ServiceReference, Device> devices = new HashMap<ServiceReference, Device>();

		// Sensors and actuators available as services from OSGi
		Map<String, Sensor> sensors = new HashMap<String, Sensor>();
		Map<String, Actuator> actuators = new HashMap<String, Actuator>();

		// User defined events, conditions, and rules
		Map<String, Event> events = new ConcurrentHashMap<String, Event>();
		Map<String, Condition> conditions = new ConcurrentHashMap<String, Condition>();
		Map<String, Action> actions = new ConcurrentHashMap<String, Action>();
		Map<String, Rule> rules = new HashMap<String, Rule>();

		// Explicitly empty constructor
		State() {
		}

		/* Accessors */

		Collection<Event> getEvents() {
			return events.values();
		}

		Collection<Rule> getRules() {
			return rules.values();
		}

		Sensor getSensor(String nodeID) {
			if (sensors.containsKey(nodeID)) {
				return sensors.get(nodeID);
			} else {
				error("Sensor '" + nodeID + "' does not exist.");
				return null;
			}
		}

		Actuator getActuator(String nodeID) {
			if (actuators.containsKey(nodeID)) {
				return actuators.get(nodeID);
			} else {
				error("Actuator '" + nodeID + "' does not exist.");
				return null;
			}
		}

		Event getEvent(String name) {
			if (events.containsKey(name)) {
				return events.get(name);
			} else {
				error("Event '" + name + "' does not exist.");
				return null;
			}
		}

		Condition getCondition(String name) {
			if (conditions.containsKey(name)) {
				return conditions.get(name);
			} else {
				error("Condition '" + name + "' does not exist.");
				return null;
			}
		}

		Action getAction(String name) {
			if (actions.containsKey(name)) {
				return actions.get(name);
			} else {
				error("Action '" + name + "' does not exist.");
				return null;
			}
		}

		Rule getRule(String name) {
			if (rules.containsKey(name)) {
				return rules.get(name);
			} else {
				error("Rule '" + name + "' does not exist.");
				return null;
			}
		}

		/* Booleans */

		public boolean isRunning() {
			return run;
		}

		public boolean actuatorExists(String nodeID) {
			return actuators.containsKey(nodeID);
		}

		public boolean eventExists(String s) {
			return events.containsKey(s);
		}

		public boolean actionExists(String s) {
			return actions.containsKey(s);
		}

		public boolean conditionExists(String s) {
			return conditions.containsKey(s);
		}

		public boolean ruleExists(String s) {
			return rules.containsKey(s);
		}

		public boolean sensorExists(String nodeID) {
			return sensors.containsKey(nodeID);
		}

		/* Mutators */

		void setRunning(boolean b) {
			run = b;
		}

		public void add(String name, Event e) {
			events.put(name, e);
		}

		public void add(String name, Condition c) {
			conditions.put(name, c);
		}

		public void add(String name, Action a) {
			actions.put(name, a);
		}

		public void add(String name, Rule r) {
			rules.put(name, r);
		}

		/* Device Maintenance functionality */

		/**
		 * Called by the Activator (indirectly, through Engine's addDevice
		 * method) when any service in Knoplerfish is added.
		 * 
		 * @param sref
		 *            the Knopflerfish ServiceReference ID of the new/changed
		 *            service
		 * @param dev
		 *            a direct reference to the new/changed AtlasService
		 */
		public void addDevice(ServiceReference sref, AtlasService dev) {

			Device d = null;

			if (dev instanceof HS322Servo) {
				/* Servo */

				// Add servo to actuators map
				String nodeId = sref.getProperty("Node-Id").toString();
				Actuator actuator = new Actuator(nodeId, (HS322Servo) dev);
				actuators.put(nodeId, actuator);
				d = actuator;

			} else {
				/* Sensor */

				int sType = 0;
				if (dev instanceof InterlinkPressureSensor) {
					sType = DeviceType.PRESSURE;
				} else if (dev instanceof DigitalContactSensor) {
					sType = DeviceType.CONTACT;
				} else if (dev instanceof HumiditySensor) {
					sType = DeviceType.HUMIDITY;
				} else if (dev instanceof TemperatureSensor) {
					sType = DeviceType.TEMP;
				}

				// Store a reference to the sensor locally, giving it the
				// initial sensor reading of NULL
				String nodeId = sref.getProperty("Node-Id").toString();
				Sensor sensor = new Sensor(nodeId, sType, dev, Sensor.NULL);
				sensors.put(nodeId, sensor);
				d = sensor;
			}

			devices.put(sref, d);

		}

		/**
		 * Called by the Activator (indirectly, through Engine's addDevice
		 * method) when any service in Knoplerfish is removed.
		 * 
		 * @param sref
		 *            A reference to the service
		 */
		public void removeDevice(ServiceReference sref) {
			// FIXME: Removing a device simply halts the Engine. Not sure why,
			// since we're deregistering everything. Might have something to do
			// with threaded access?
			devices.get(sref).deregister();
			devices.remove(sref);
		}

		/**
		 * Subscribes to the appropriate sensor.
		 * 
		 * @param nodeID
		 */
		void subscribe(String nodeID) {
			sensors.get(nodeID).subscribe(logic);
		}

		/**
		 * Unsubscribes from all data feeds for all sensors. Does not affect
		 * TFMEvents or their timers.
		 */
		public void unsubscribe() {
			for (Sensor sensor : sensors.values()) {
				sensor.unsubscribe(logic);
			}
		}

		/* Methods */

		/**
		 * Called when the engine is shutting down; unsubscribes from all sensor
		 * feeds and deregisters (and dereferences) all devices
		 */
		public void close() {
			unsubscribe();
			for (Device d : devices.values()) {
				d.deregister();
			}
		}

		/**
		 * Called when processing the LIST directive to pretty print the state
		 * of the engine
		 * 
		 * @param type
		 * @return
		 */
		String getSummary(StateType type) {
			String ret = null;

			switch (type) {
			case SENSOR:
				ret = getSummary("Sensors", sensors);
				break;
			case ACTUATOR:
				ret = getSummary("Actuators", actuators);
				break;
			case EVENT:
				ret = getSummary("Events", events);
				break;
			case CONDITION:
				ret = getSummary("Conditions", conditions);
				break;
			case ACTION:
				ret = getSummary("Actions", actions);
				break;
			case RULE:
				ret = getSummary("Rules", rules);
				break;
			default:
				ret = "INVALID ENUM ";
				break;
			}

			return ret;
		}

		/**
		 * Helper method to construct a pretty printed summary for generic state
		 * objects
		 * 
		 * @param title
		 * @param map
		 * @return
		 */
		private String getSummary(String title, Map map) {
			// FIXME This output is quite ugly. We should go through the various
			// objects' toString() methods and fix them so that they display the
			// most relevant information. That is, it displays the identifier if
			// it's part of a composite event, and the raw device data if it's
			// an independent event. (Must take into consideration ad-hoc
			// defined objects, ie. some objects, even though they are
			// independent, have no identifier, eg. name field == null)
			StringBuilder buf = new StringBuilder();
			buf.append("---" + title + "---" + "\n");
			for (Entry<String, Object> e : (Set<Entry>) map.entrySet()) {
				buf.append(e.getKey().toString() + " = "
						+ e.getValue().toString() + "\n");
			}
			buf.append("\n");
			return buf.toString();
		}

	}

	/**
	 * The Reactivity Engine component that takes care of scheduling updates for
	 * TFMEvents. While the Logic component handles updates for Composite and
	 * Simple events, TFMEvents need to log each update, and their evaluation
	 * depends on exceeding a certain threshold, so they cannot simply be
	 * updated with the other events.
	 * <p>
	 * Hence, this class schedules updates for TFMEvents based on the various
	 * windows allowed by the system.
	 * 
	 * @author Patrick
	 * 
	 */
	private class Scheduler {

		// FIXME Relative timers do not close their windows gracefully. At the
		// close time, there is a period of frenetic activity during which
		// several timer threads are spawned and canceled. This is probably due
		// to the now.after(Date) checks giving wonky results at the edge case.

		// Keep a threadsafe collection of all spawned timers.
		// (Must be threadsafe, because spawned timers spawn
		// other timers)
		private ConcurrentLinkedQueue<Timer> spawnedTimers;

		/* Initialization */
		Scheduler() {
			spawnedTimers = new ConcurrentLinkedQueue<Timer>();
		}

		/*
		 * Creates a new Timer and adds its reference to the spawnedTimers table
		 */
		private Timer spawnTimer() {
			Timer t = new Timer(true);
			spawnedTimers.add(t);
			return t;
		}

		/**
		 * Called whenever a TFM event is successfully parsed. Even if the TFM
		 * was an ad-hoc definition as a part of a rule (eg.
		 * "DEFINE rule r = <nil,10,1>(e1),true,a1", but a1 is undefined, so the
		 * rule definition fails), the TFM and associated timers will be
		 * spawned.
		 * 
		 * This could be improved by not spawning the timers for a TFMEvent
		 * until the rule is completely parsed and validated.
		 */
		void add(TFMEvent e) {

			// FIXME: Don't spawn timers unless this event is definitely
			// necessary.

			try {
				int evalFreq = e.getEvalFreq().getDuration();
				Window w = e.getWindow();
				Window.Type wType = w.returnType();

				// Today
				Date now = new Date();

				// Get sensors from the modified event
				Set<Sensor> sensors = new HashSet<Sensor>();
				e.getModifiedEvent().addSensorsTo(sensors);

				switch (wType) {

				case ABSOLUTE:
					if (now.before(w.getRelEnd())) {
						// Then it will open sometime in future
						// or it is currently in the window

						// Create an AbsoluteStartTask
						Date start = w.getAbsStart();
						Date end = w.getAbsEnd();
						TFMAbsoluteStartTask startTask = new TFMAbsoluteStartTask(
								e, sensors, evalFreq, end);
						// When it fires, startTask will spawn two timers:
						// 1) a fixed rate Timer using TFMUpdateTask
						// 2) a one-shot Timer to cancel the above Timer

						// Schedule startTask to fire once at absStart.
						// (a Timer with an expired start time will start
						// immediately, so if we are within the window, we're
						// fine)

						spawnTimer().schedule(startTask, start);

					} else {
						// The window has passed. This event will never fire.
						// Do nothing.
					}

					break;

				case RELATIVE:
					/* TFMEvent with a relative window */

					// Create a new RelativeStartTask
					TFMRelativeStartTask startTask = new TFMRelativeStartTask(
							e, sensors, evalFreq);
					// startTask, when fired, will spawn two timers:
					// 1) a fixed-rate Timer using TFMUpdateTask
					// 2) a one-shot Timer that does two things:
					// __A) cancels the Timer from (1)
					// __B) schedules another RelativeStartTask

					// schedule startTask to fire once at getRelStart()
					// getRelStart() returns today's window, if it hasn't
					// already expired, or tomorrow's window, if it has.
					spawnTimer().schedule(startTask, w.getRelStart());

					break;

				case INFINITE:

					// Window never closes, no need for management timers.
					spawnTimer().scheduleAtFixedRate(
							new TFMUpdateTask(e, sensors), 0, evalFreq * 1000);
				}

			} catch (Exception exn) {
				// EvalFreq is infinite, so even though the window is open, we
				// won't schedule this event, because it will never evaluate to
				// true
			}

		}

		/**
		 * Cleans up this scheduler by calling cancel() on all spawned timers.
		 * 
		 * @see Timer
		 */
		void close() {
			for (Timer t : spawnedTimers) {
				t.cancel();
			}
		}

		/**
		 * A TimerTask for updating TFMEvents during their window. Should always
		 * be scheduled within a fixed-rate timer.
		 * 
		 * @author Patrick
		 * 
		 */
		private class TFMUpdateTask extends TimerTask {

			private Set<Sensor> sensors;
			private TFMEvent e;

			/**
			 * Constructor for this task
			 * 
			 * @param e
			 *            The associated TFMEvent
			 * @param sensors
			 *            The sensors that should be checked when this update
			 *            fires
			 */
			TFMUpdateTask(TFMEvent e, Set<Sensor> sensors) {
				this.e = e;
				this.sensors = sensors;
			}

			/**
			 * The code executed whenever a timer with this task fires.
			 */
			public void run() {
				error("update fires!");

				// Only update if the engine is running
				if (state.isRunning()) {

					// Request data from each sensor
					for (Sensor sensor : sensors) {
						sensor.pull(logic);
					}

					// Call the special TFMEvent.realUpdate() function, reserved
					// for these timers
					e.realUpdate();
				}
			}
		}

		/**
		 * A TimerTask for canceling update timers for TFMEvents with absolute
		 * windows. Such events' windows will never open again, thus this task
		 * does not schedule any more timers.
		 * 
		 * @author Patrick
		 * 
		 */
		private class TFMAbsoluteStopTask extends TimerTask {

			Timer timer;
			private TFMEvent e;

			TFMAbsoluteStopTask(TFMEvent e, Timer timer) {
				this.e = e;
				this.timer = timer;
			}

			/**
			 * When the timer fires, calls realUpdate() on the TFMEvent (which
			 * propagates an update down through its modifiedEveent) and then
			 * cancels the associated update timer.
			 */
			public void run() {
				
				// Call realUpdate() one last time. Since we are now outside the
				// window, this will cause the event's reportFreq to be reset,
				// and thus cause this event to evaluate to false.
				e.realUpdate();

				// Cancel the timer. If any update tasks are currently ongoing,
				// those tasks' realUpdate() calls will also reflect that the
				// window has expired, so there will be no conflicts.
				timer.cancel();
			}
		}

		/**
		 * A TimerTask for scheduling update timers for TFMEvents with absolute
		 * windows.
		 * 
		 * @author Patrick
		 * 
		 */
		private class TFMAbsoluteStartTask extends TimerTask {
			Set<Sensor> sensors;
			int evalFreq;
			Date end;
			TFMEvent e;

			TFMAbsoluteStartTask(TFMEvent e, Set<Sensor> sensors, int evalFreq,
					Date end) {
				this.e = e;
				this.sensors = sensors;
				this.evalFreq = evalFreq;
				this.end = end;
			}

			/**
			 * When the timer for this task fires, spawn two timers:
			 * 
			 * 1) a fixed-rate update timer using TFMUpdateTask
			 * 
			 * 2) a one-shot timer to cancel the first timer
			 */
			public void run() {

				// Schedule the fixed-rate update timer to start immediately
				Timer t = spawnTimer();
				t.scheduleAtFixedRate(new TFMUpdateTask(e, sensors), 0,
						evalFreq * 1000);

				// Schedule the one-shot cancel timer
				spawnTimer().schedule(new TFMAbsoluteStopTask(e, t), end);
			}
		}

		/**
		 * A TimerTask for scheduling update timers for TFMEvents with relative
		 * windows.
		 * 
		 * @author Patrick
		 * 
		 */
		private class TFMRelativeStartTask extends TimerTask {

			Set<Sensor> sensors;
			int evalFreq;
			TFMEvent e;

			TFMRelativeStartTask(TFMEvent e, Set<Sensor> sensors, int evalFreq) {
				this.sensors = sensors;
				this.evalFreq = evalFreq;
				this.e = e;
			}

			/**
			 * When the timer for this task fires, spawn two timers:
			 * 
			 * 1) a fixed-rate update timer using TFMUpdateTask
			 * 
			 * 2) a one-shot timer to cancel the first timer and spawn the next
			 * timer, using TFMRelativeStartTask
			 */
			public void run() {
				error("relstart fires!");

				// The closing time for this relative window. getRelEnd()
				// returns today's window close if the window hasn't expired and
				// tomorrow's window close if it has. This task will never be
				// scheduled today if the window has already expired, so this
				// call will always return today's closing window time.
				Date end = e.getWindow().getRelEnd();

				// Schedule the update timer to start immediately
				Timer t = spawnTimer();
				t.scheduleAtFixedRate(new TFMUpdateTask(e, sensors), 0,
						evalFreq * 1000);

				error("relend scheduled for " + end);
				// Schedule the cancel-and-reschedule RelativeStop timer
				spawnTimer().schedule(
						new TFMRelativeStopTask(t, e, sensors, evalFreq), end);

			}

		}

		/**
		 * A TimerTask for canceling update timers for TFMEvents with relative
		 * windows. Such events' windows will open again tomorrow, so this task
		 * also schedules a new timer to fire at the window's open tomorrow.
		 * 
		 * @author Patrick
		 * 
		 */
		private class TFMRelativeStopTask extends TimerTask {
			Timer t;
			Set<Sensor> sensors;
			int evalFreq;
			TFMEvent e;

			TFMRelativeStopTask(Timer t, TFMEvent e, Set<Sensor> sensors,
					int evalFreq) {

				this.t = t;
				this.e = e;
				this.sensors = sensors;
				this.evalFreq = evalFreq;

			}

			/**
			 * When the timer for this task fires, two timers will be spawned:
			 * 
			 * 1) a fixed-rate Timer using TFMUpdateTask
			 * 
			 * 2) a one-shot Timer that does two things:
			 * 
			 * A) cancels the Timer from (1) at the window's end
			 * 
			 * B) schedules another RelativeStartTask
			 */
			public void run() {
				error("relend fires!");

				// Call realUpdate() one last time. Since we are now outside the
				// window, this will cause the event's reportFreq to be reset,
				// and thus cause this event to evaluate to false.
				e.realUpdate();

				// Cancel the timer. If any update tasks are currently ongoing,
				// those tasks' realUpdate() calls will also reflect that the
				// window has expired, so there will be no conflicts.
				t.cancel();

				// Since this task fires only after today's window has expired,
				// this call to getRelStart() will always return tomorrow's
				// window's start time.
				Date start = e.getWindow().getRelStart();

				// Schedule a new timer for tomorrow
				spawnTimer().schedule(
						new TFMRelativeStartTask(e, sensors, evalFreq), start);
			}
		}

	}

	/**
	 * The Logic component of the Reactivity Engine handles the ReceivedData
	 * callbacks from the various sensors and the regular updating of Composite
	 * and Simple events. It also contains the logic for starting and stopping
	 * the engine.
	 * 
	 * @author Patrick
	 * 
	 */
	class Logic implements AtlasClient {

		Logic() {
		}

		public void ReceivedData(String data, Properties props) {
			counter++;
			System.out.println("Received data. " + counter);

			// Update sensor reading value
			String nodeId = props.getProperty("Node-Id");
			state.getSensor(nodeId).update(Integer.parseInt(data));

			// Change the truth values for the events
			for (Event e : state.getEvents()) {
				e.update();
			}
			// Check whether any rules need to be triggered
			for (Rule r : state.getRules()) {
				r.evaluate();
			}

		}

		/**
		 * Called when the RE enters the RUN mode or when the SET command is
		 * used AND the RE is in RUN mode
		 */
		void subscriptionManager() {

			// iterate through rules checking if the condition is true
			// and subscribing to the appropriate sensors if required.

			for (Rule r : state.getRules()) {

				if (r.getCondition().getValue()) {
					// Only subscribe if this Rule's Condition is true

					if (r.getEvent().isSimple()) {
						/* SimpleEvent */
						// Cast is safe isSimple() == true
						SimpleEvent simpleEvent = (SimpleEvent) r.getEvent();
						String nodeID = simpleEvent.getSensorNodeID();

						// Subscribe to this one sensor
						state.subscribe(nodeID);

					} else {
						if (r.getEvent().isTFM()) {
							/* TFM Event */
							// These subscriptions are handled by the Scheduler
							// So we can safely ignore them

						} else {
							/* Composite event */
							// Find all the sensors that the event uses
							Set<Sensor> s = new HashSet<Sensor>();
							r.getEvent().addSensorsTo(s);

							// Subscribe to each of them
							Iterator<Sensor> iter = s.iterator();
							while (iter.hasNext()) {
								iter.next().subscribe(logic);
							}
						}

					}

				}

			}

		}

		/**
		 * Starts the engine, allowing TFMEvent statuses to be updated by their
		 * update timers and subscribing to the sensors required by the
		 * Composite and Simple events
		 */
		public void start() {
			state.setRunning(true);
			subscriptionManager();
		}

		/**
		 * Stops the engine, causing TFMEvent update timers to stop pulling data
		 * and unsubscribing from any other sensors.
		 */
		void stop() {
			state.setRunning(false);

			// Unsubscribe from all the sensors once the engine stops
			// running
			state.unsubscribe();

		}

	}

	/* Command Methods */
	/*
	 * These methods are called by the parser as it parses commands from the
	 * GUI.
	 */

	enum ListType {
		ALL, BASIC, BASICEVENT, BASICACTION, EVENT, CONDITION, ACTION, RULE
	}

	void list(ListType type) {
		StringBuilder buf = new StringBuilder();
		if (type.equals(ListType.ALL) || type.equals(ListType.BASIC)
				|| type.equals(ListType.BASICEVENT)
				|| type.equals(ListType.EVENT)) {
			buf.append(state.getSummary(StateType.SENSOR));
		}
		if (type.equals(ListType.ALL) || type.equals(ListType.BASIC)
				|| type.equals(ListType.BASICACTION)
				|| type.equals(ListType.ACTION)) {
			buf.append(state.getSummary(StateType.ACTUATOR));
		}
		if (type.equals(ListType.ALL) || type.equals(ListType.EVENT)) {
			buf.append(state.getSummary(StateType.EVENT));
		}
		if (type.equals(ListType.ALL) || type.equals(ListType.ACTION)) {
			buf.append(state.getSummary(StateType.ACTION));
		}
		if (type.equals(ListType.ALL) || type.equals(ListType.CONDITION)) {
			buf.append(state.getSummary(StateType.CONDITION));
		}
		if (type.equals(ListType.ALL) || type.equals(ListType.RULE)) {
			buf.append(state.getSummary(StateType.RULE));
		}
		updateConsole(buf.toString());
	}

	/* DEFINE Directive */
	/*
	 * These methods have several things in common. Firstly, they all check that
	 * state.isRunning() is false. The system does not allow new definitions
	 * while running.
	 * 
	 * Secondly, they all check that an object with this name does not already
	 * exist, update the state with the name, and provide the object itself with
	 * a backreference to its new identifier. This is useful when pretty
	 * printing the system state with the LIST directive.
	 * 
	 * Finally, the constructors for all of these objects do more error checking
	 * to ensure that the system remains in a consistent, stable state. These
	 * constructors are called in the parser, where these objects come from.
	 */

	/**
	 * Corresponds to the <code>DEFINE event</code> directive.
	 * 
	 * @param name
	 *            The name of the event as it will be referred to by the user
	 * @param e
	 *            The Event object, a reference to the actual Event, used by the
	 *            system
	 */
	void defineEvent(String name, Event e) {

		if (!state.isRunning()) {
			if (!state.eventExists(name)) {
				e.setName(name);
				state.add(name, e);

			} else {
				error("Event '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

	/**
	 * Corresponds to the <code>DEFINE condition</code> directive.
	 * 
	 * @param name
	 *            The name of the condition as it will be referred to by the
	 *            user
	 * @param c
	 *            The Condition object, a reference to the actual Condition,
	 *            used by the system
	 */
	void defineCondition(String name, Condition c) {
		if (!state.isRunning()) {
			if (!state.conditionExists(name)) {
				c.setName(name);
				state.add(name, c);
			} else {
				error("Condition '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

	/**
	 * Corresponds to the <code>DEFINE action</code> directive.
	 * 
	 * @param name
	 *            The name of the action as it will be referred to by the user
	 * @param a
	 *            A List of actions, as generated by the parser. This list will
	 *            be resolved internally to a single action, either Composite
	 *            or, if the List contains only one action, that single element.
	 */
	void defineAction(String name, ArrayList<Action> a) {
		if (!state.run) {
			if (!state.actionExists(name)) {
				Action finalAct = null;
				if (a.size() == 1) {
					// If the List contains only one element
					for (Action act : a) {
						// Make this reference be equivalent to that element
						finalAct = act;
					}
				} else {
					// Otherwise, compose all the actions into a CompositeAction
					finalAct = new CompositeAction(a);

				}
				finalAct.setName(name);
				state.add(name, finalAct);

			} else {
				error("Action '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

	/**
	 * Corresponds to the <code>DEFINE rule</code> directive.
	 * 
	 * @param name
	 *            The name of the action as it will be referred to by the user
	 * @param r
	 *            The Condition object, a reference to the actual Condition,
	 *            used by the system
	 */
	void defineRule(String name, Rule r) {
		if (!state.run) {
			if (!state.ruleExists(name)) {
				r.setName(name);
				state.add(name, r);
			} else {
				error("Rule '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

	/* SET Directive */

	/**
	 * Corresponds to the <code>SET condition</code> directive.
	 * 
	 * @param name
	 *            The name of the condition to be set
	 * @param b
	 *            The boolean value to which the named condition will be set
	 */
	void setCondition(String name, boolean b) {
		if (state.conditionExists(name)) {
			state.getCondition(name).set(b);

			// This directive is allowed when the system is running. If it is
			// running, the subscription manager needs to be notified of the
			// condition change so that the proper sensors are subscribed to.
			if (state.isRunning()) {
				logic.subscriptionManager();
			}

		} else {
			error("Condition '" + name
					+ "' does not exist.  Please define it first.");
		}
	}

	/* RUN Directive */

	void run() {
		if (state.isRunning()) {
			error("RUN mode is already on");
			return;
		} else {
			logic.start();
		}

	}

	void stop() {
		if (!state.isRunning()) {
			error("RUN mode already off");
			return;
		} else {
			logic.stop();
		}
	}

	/* LOAD Directive */

	void loadFile(String path) {
		Scanner sc;
		try {
			sc = new Scanner(new File(path));
			while (sc.hasNextLine()) {
				parse(sc.nextLine());
			}
		} catch (FileNotFoundException e) {

			// The path is relative to the Knoplerfish's framework.jar
			// directory. This information is helpfully reported to the user if
			// they try to load anything that does not exist in that directory.
			error("File '" + System.getProperty("user.dir")
					+ System.getProperty("file.separator") + path
					+ "' not found.");
		}
	}

	void parse(String cmd) {
		System.out.println("Parsing: " + cmd);
		Lexer l = new Lexer(new StringReader(cmd));
		parser p = new parser(l, this);
		try {
			p.parse();
		} catch (Exception e) {
			System.err.println("Syntax error: " + cmd);
		}
	}

	/*
	 * The following accessor methods are used by the parser when constructing
	 * the objects to pass to the define methods above
	 */

	Action getAction(String nodeID) {
		return state.getAction(nodeID);
	}

	Event getEvent(String nodeID) {
		return state.getEvent(nodeID);
	}

	Rule getRule(String nodeID) {
		return state.getRule(nodeID);
	}

	Condition getCondition(String i) {
		return state.getCondition(i);
	}

	/**
	 * Called by the parser when provided with an event string of the form
	 * "NodeID(min, max)". Verifies that the sensor node exists.
	 * 
	 * @param nodeID
	 *            NodeID of the associated sensor
	 * @param min
	 *            Minimum value that would cause this event to trigger
	 * @param max
	 *            Maximum value that would cause this event to trigger
	 * @return A SimpleEvent object if the sensor node exists, null otherwise.
	 * 
	 */
	SimpleEvent createEvent(String nodeID, Integer min, Integer max) {
		if (state.sensorExists(nodeID)) {
			return new SimpleEvent(state.getSensor(nodeID), min, max);
		} else {
			error("Sensor '" + nodeID + "' does not exist.");
			return null;
		}
	}

	/**
	 * Called by the parser when provided with an event string of the form
	 * "NodeID(value)". Verifies that the sensor node exists.
	 * 
	 * @param nodeID
	 *            NodeID of the associated sensor
	 * @param value
	 *            Value that would cause this event to trigger
	 * 
	 * @return A SimpleEvent object if the sensor node exists, null otherwise.
	 * 
	 */
	Event createEvent(String nodeID, Integer value) {
		// Functionally, this is a SimpleEvent with a range, where the minimum
		// of the range is equal to the maximum.
		return createEvent(nodeID, value, value);
	}

	/**
	 * Called by the parser when provided with an event string of the form
	 * "<w,ef,rf>(e)". Even if the rest of the command string fails to complete
	 * (eg. if this is part of a <code>DEFINE event</code> directive, and the
	 * identifier has already been defined), this call will still result in a
	 * TFMEvent creation and subsequent timer scheduling.
	 * 
	 * @param modifiedEvent
	 *            The root of the event tree which this TFMEvent will be
	 *            managing
	 * @param w
	 *            The window, constructed by the parser
	 * @param ef
	 *            The evaluation frequency, constructed by the parser
	 * @param n
	 *            The report frequency integer, used as input for the TFMEvent
	 *            constructor
	 * @return A reference to the newly constructed TFMEvent
	 */
	TFMEvent createTFMEvent(Event modifiedEvent, Window w, EvalFreq ef,
			Integer n) {

		// FIXME Don't actually add this TFM to the scheduler until the rest of
		// the command is completed. As things stand, if there's a non-syntactic
		// error in the command string, this TFMEvent is created and scheduled
		// even if the command string flops.

		TFMEvent e = new TFMEvent(modifiedEvent, w, ef, n);
		scheduler.add(e);
		return e;
	}

	/**
	 * Called by the parser when provided with a condition string of the form
	 * "true" or "false".
	 * 
	 * @param b
	 *            The initial value of the new condition.
	 * @return A reference to the new condition
	 * 
	 */
	Condition createCondition(boolean b) {
		return new Condition(b);
	}

	/**
	 * Called by the parser when provided with an action string of the form
	 * "NodeID(value)". Verifies that the action node exists.
	 * 
	 * @param nodeID
	 *            NodeID of the associated actuator
	 * @param value
	 *            Value to which this actuator should be set upon executing this
	 *            action
	 * @return
	 */
	Action createAction(String nodeID, Integer value) {
		if (state.actuatorExists(nodeID)) {
			return new SimpleAction(state.getActuator(nodeID), value);
		} else {
			error("Actuator '" + nodeID + "' does not exist.");
			return null;
		}
	}

	/**
	 * Called by the parser when provided with a rule string of the form
	 * "event,condition,rule". Because the arguments are passed up from the
	 * above create methods, this method checks that they are not null before
	 * defining the new rule.
	 * 
	 * @param e
	 *            Reference to the root of the event tree, as determined by the
	 *            parser
	 * @param c
	 *            Reference to the condition, as determined by the parser
	 * @param a
	 *            Reference to the action or actions to execute if this rule
	 *            evaluates to true, as determined by the parser
	 * @return
	 */
	Rule createRule(Event e, Condition c, Action a) {
		if (e != null) {
			if (c != null) {
				if (a != null) {
					return new Rule(e, c, a);
				} else {
					error("Action '" + a + "' does not exist");
				}
			} else {
				error("Condition '" + c + "' does not exist");
			}
		} else {
			error("Event '" + e + "' does not exist");
		}
		return null;
	}

	/* OSGi Service Methods */
	/*
	 * These methods interface with OSGi and Atlas.
	 */

	// Pass the call through to the state
	void addDevice(ServiceReference sref, AtlasService dev) {
		state.addDevice(sref, dev);
	}

	// Pass the call through to the state
	void removeDevice(ServiceReference sref) {
		state.removeDevice(sref);
	}

	/**
	 * A callback method called by any Atlas service bundle to which the engine
	 * has subscribed (or from which it has requested data with a pull)
	 * 
	 * @param data
	 *            reading produced by the sensor
	 * @param props
	 *            information about the sensor that produced the data (name,
	 *            label, channel to which the device is connected, etc.)
	 */
	public void ReceivedData(String data, Properties props) {
		// Pass the call through to the logic
		logic.ReceivedData(data, props);
	}
}
