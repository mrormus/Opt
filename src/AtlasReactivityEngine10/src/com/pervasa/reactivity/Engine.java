package com.pervasa.reactivity;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
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
	Scheduler scheduler = new Scheduler();
	State state = new State();

	void init(GUI gui) {
		this.gui = gui;
	}

	public void error(String s) {
		System.err.println(s);
	}

	private void updateConsole(String s) {
		gui.update(s);
	}

	// this is basically a callback method called by any Atlas service
	// bundle to which the application has subscribed (in this case,
	// the pressure sensor service and the digital contact sensor
	// service).
	// data contains the reading produced by the sensor
	// props contains information about the sensor that produced the data
	// (name, label, channel to which the device is connected, etc.
	public void ReceivedData(String data, Properties props) {
		logic.ReceivedData(data, props);
	}

	void close() {
		// FIXME: Should the engine unsubscribe from all sensor data before
		// closing?
		// unsubscribeAll();
	}

	enum StateType {
		ACTUATOR, SENSOR, EVENT, CONDITION, ACTION, RULE
	};

	private class State {

		private boolean run = false;

		// Local references to the sensor services
		private InterlinkPressureSensor sensorPressure = null;
		private HS322Servo actuatorServo = null;
		private DigitalContactSensor sensorContact = null;
		private TemperatureSensor sensorTemp = null;
		private HumiditySensor sensorHumid = null;

		// Knopflerfish's serviceReference IDs for the sensor services
		private ServiceReference refPressure = null;
		private ServiceReference refServo = null;
		private ServiceReference refContact = null;
		private ServiceReference refHumid = null;
		private ServiceReference refTemp = null;

		// Maps
		Map<String, HS322Servo> servoMap = new HashMap<String, HS322Servo>();
		Map<String, Sensor> sensorMap = new HashMap<String, Sensor>();
		Map<String, Actuator> actuators = new HashMap<String, Actuator>();
		Map<String, Event> eventList = new ConcurrentHashMap<String, Event>();
		Map<String, Condition> runtimeConditions = new ConcurrentHashMap<String, Condition>();
		Map<String, Action> runtimeActions = new ConcurrentHashMap<String, Action>();
		Map<String, Rule> rules = new HashMap<String, Rule>();

		Collection<Event> getEvents() {
			return eventList.values();
		}

		String getSummary(StateType type) {
			String ret = null;

			switch (type) {
			case SENSOR:
				ret = getSummary("Sensors", sensorMap);
				break;
			case ACTUATOR:
				ret = getSummary("Actuators", actuators);
				break;
			case EVENT:
				ret = getSummary("Events", eventList);
				break;
			case CONDITION:
				ret = getSummary("Conditions", runtimeConditions);
				break;
			case ACTION:
				ret = getSummary("Actions", runtimeActions);
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

		private String getSummary(String title, Map map) {
			StringBuilder buf = new StringBuilder();
			buf.append("---" + title + "---" + "\n");
			for (Entry<String, Object> e : (Set<Entry>) map.entrySet()) {
				buf.append(e.getKey().toString() + " = "
						+ e.getValue().toString() + "\n");
			}
			buf.append("\n");
			return buf.toString();
		}

		Collection<Rule> getRules() {
			return rules.values();
		}

		Action getAction(String name) {
			return runtimeActions.get(name);
		}

		Condition getCondition(String name) {
			return runtimeConditions.get(name);
		}

		Rule getRule(String name) {
			return rules.get(name);
		}

		public boolean actuatorExists(String nodeID) {
			return actuators.containsKey(nodeID);
		}

		public boolean eventExists(String s) {
			return eventList.containsKey(s);
		}

		public boolean actionExists(String s) {
			return runtimeActions.containsKey(s);
		}

		public boolean conditionExists(String s) {
			return runtimeConditions.containsKey(s);
		}

		public boolean ruleExists(String s) {
			return rules.containsKey(s);
		}

		public boolean sensorExists(String nodeID) {
			return sensorMap.containsKey(nodeID);
		}

		// this method is called by the Reactive Engine bundle's Activator when
		// any
		// service running
		// in Knopflerfish starts or changes
		// sref is the Knopflerfish ServiceReference ID of the new/changed
		// service
		// dev is a direct reference to the new/changed service
		// AtlasService is an interface implemented by all Atlas sensor and
		// actuator
		// services, and is what allows an AtlasClient implementor (like
		// the Reactive Engine)
		// to bind with attached Atlas devices.
		public void addDevice(ServiceReference sref, AtlasService dev) {

			if (dev instanceof InterlinkPressureSensor) {

				// Set references to the Pressure sensor emulator
				refPressure = sref;
				sensorPressure = (InterlinkPressureSensor) dev;

				// Put sensor in a HashMap of BASIC EVENTS
				String nodeId = sref.getProperty("Node-Id").toString();
				sensorMap.put(nodeId, new Sensor(nodeId, DeviceType.PRESSURE,
						Sensor.NULL));

			}

			else if (dev instanceof HS322Servo) {

				// Set references to the Servo sensor emulator
				refServo = sref;
				actuatorServo = (HS322Servo) dev;

				// Put actuator in a HashMap of BASIC ACTIONS
				String nodeId = sref.getProperty("Node-Id").toString();
				actuators.put(nodeId, new Actuator(nodeId, actuatorServo));

				// Put actuator in a ServoMap
				servoMap.put(nodeId, actuatorServo);

			}

			else if (dev instanceof DigitalContactSensor) {

				// Set references to the Digital Contact sensor emulator
				refContact = sref;
				sensorContact = (DigitalContactSensor) dev;

				// Put sensor in HashMap of BASIC EVENTS
				String nodeId = sref.getProperty("Node-Id").toString();
				sensorMap.put(nodeId, new Sensor(nodeId, DeviceType.CONTACT,
						Sensor.NULL));

			}

			// 
			else if (dev instanceof TemperatureSensor) {

				// Set references to Temperature sensor emulator
				refTemp = sref;
				sensorTemp = (TemperatureSensor) dev;

				// Put sensor in HashMap of BASIC EVENTS
				String nodeId = sref.getProperty("Node-Id").toString();
				sensorMap.put(nodeId, new Sensor(nodeId, DeviceType.TEMP,
						Sensor.NULL));

			}

			else if (dev instanceof HumiditySensor) {

				// Set references to Humidity sensor emulator
				refHumid = sref;
				sensorHumid = (HumiditySensor) dev;

				// Put sensor in HashMap of BASIC EVENTS
				String nodeId = sref.getProperty("Node-Id").toString();
				sensorMap.put(nodeId, new Sensor(nodeId, DeviceType.HUMIDITY,
						Sensor.NULL));

			}

		}

		// this method is called by the Reactive Engine bundle's Activator when
		// any
		// service
		// running in Knopflerfish goes offline
		// sref is the Knopflerfish ServiceReference ID of the departing service
		// because the service has already been unbound in OSGi, we cannot do
		// the
		// "instanceof" check like in addDevice (the "dev" parameter in that
		// method would be null here). This is why addDevice must record the
		// ServiceReference.
		public void removeDevice(ServiceReference sref) {

			if (sref == refPressure) {

				// set the reading of sensor to NULL (we could always remove it
				// from
				// the map but since events may have been defined so to ensure
				// those
				// events evaluate to false, we set the reading to null)
				String nodeId = refPressure.getProperty("Node-Id").toString();
				sensorMap.remove(nodeId);

				refPressure = null;
				sensorPressure = null;

			}

			else if (sref == refServo) {
				refServo = null;
				actuatorServo = null;

			}

			else if (sref == refContact) {

				String nodeId = refContact.getProperty("Node-Id").toString();
				sensorMap.remove(nodeId);

				refContact = null;
				sensorContact = null;
			}

			else if (sref == refTemp) {

				String nodeId = refTemp.getProperty("Node-Id").toString();
				sensorMap.remove(nodeId);

				refTemp = null;
				sensorTemp = null;

			}

			else if (sref == refHumid) {

				String nodeId = refHumid.getProperty("Node-Id").toString();
				sensorMap.remove(nodeId);

				refHumid = null;
				sensorTemp = null;

			}
		}

		Sensor getSensor(String nodeID) {
			if (sensorMap.containsKey(nodeID)) {
				return sensorMap.get(nodeID);
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
			if (eventList.containsKey(name)) {
				return eventList.get(name);
			} else {
				error("Event '" + name + "' does not exist.");
				return null;
			}
		}

		public boolean isRunning() {
			return run;
		}

		void setRunning(boolean b) {
			run = b;
		}

		public void add(String name, Event e) {
			eventList.put(name, e);
		}

		public void add(String name, Condition c) {
			runtimeConditions.put(name, c);
		}

		public void add(String name, Action a) {
			runtimeActions.put(name, a);
		}

		public void add(String name, Rule r) {
			rules.put(name, r);
		}

		// Subscribes to the appropriate sensor.
		void subscribe(int sType) {

			switch (sType) {

			case DeviceType.CONTACT:
				sensorContact.subscribeToContactData(logic);
				System.out.println("Subscribed to Contact Data");
				break;
			case DeviceType.PRESSURE:
				sensorPressure.subscribeToPressureData(logic);
				System.out.println("Subscribed to Pressure Data");
				break;
			case DeviceType.HUMIDITY:
				sensorHumid.subscribeToSensorData(logic);
				System.out.println("Subscribed to Humidity Data");
				break;
			case DeviceType.TEMP:
				sensorTemp.subscribeToSensorData(logic);
				System.out.println("Subscribed to Temperature Data");
				break;
			}

		}

		void pull(int sType) {

			switch (sType) {

			case DeviceType.CONTACT:
				sensorContact.getContactReading(new Logic());
				System.out.println("Subscribed to Contact Data");
				break;
			case DeviceType.PRESSURE:
				sensorPressure.getPressureReading(new Logic());
				System.out.println("Subscribed to Pressure Data");
				break;
			case DeviceType.HUMIDITY:
				sensorHumid.getSensorReading(new Logic());
				System.out.println("Subscribed to Humidity Data");
				break;
			case DeviceType.TEMP:
				sensorTemp.getSensorReading(new Logic());
				System.out.println("Subscribed to Temperature Data");
				break;
			}

		}

		public void unsubscribe() {
			if (sensorContact != null) {
				sensorContact.unsubscribeFromContactData(logic);
			}
			if (sensorPressure != null) {
				sensorPressure.unsubscribeFromPressureData(logic);
			}
			if (sensorHumid != null) {
				sensorHumid.unsubscribeFromSensorData(logic);
			}
			if (sensorTemp != null) {
				sensorTemp.unsubscribeFromSensorData(logic);
			}
		}

	}

	private class Scheduler {

		private Map<Timer, TFMEvent> executionTimers;

		// private Map<TFMEvent, Timer> managementTimers;

		/* Initialization */
		Scheduler() {
			executionTimers = new HashMap<Timer, TFMEvent>();
			// managementTimers = new HashMap<TFMEvent, Timer>();
		}

		/*
		 * Called when the time window of a TFM event opens.
		 */
		void add(TFMEvent e) {
			// TODO: Create a timer for the TFM event, store it, and start it
			// with the PullData class.

			try {
				int evalFreq = e.getEvalFreq().getDuration();
				int wType = e.getWindow().returnType();

				Timer t1 = new Timer(); // Timer for executing event when window
				// is open
				Timer t2 = new Timer(); // Timer to initiate t1 when window is
				// open, close
				// when window is closed

				/* Dummy - need to be actually implemented */
				Date absEnd = new Date();
				Date relEnd = new Date();
				Date relStart = new Date();

				switch (wType) {

				case Window.ABSOLUTE:
					if (e.getWindow().withinWindow()) {
						// Pull data every evalFreq seconds
						t1.scheduleAtFixedRate(new PullData(e), 0,
								evalFreq * 1000);
						// cancel t1 at absEnd
						t2.schedule(new PullData(t1, false), absEnd);
						/* FIXME: absEnd should be absEnd of window */
						// store in case it ever needs to be reinstiated
						executionTimers.put(t1, e);
					} else {
						// Out of absolute window
						// Check if currentTime is before startTime
						// Then it will open sometime in future, otherwise
						// it will never open
						// Schedule it to start using t2 if it will open
						// sometime in future
					}
				case Window.RELATIVE:
					if (e.getWindow().withinWindow()) {
						t1.scheduleAtFixedRate(new PullData(e), 0,
								evalFreq * 1000);
						t2.schedule(new PullData(t1, false), relEnd);
						/* FIXME: relEnd should be relEnd of window */
						executionTimers.put(t1, e);
					} else {
						executionTimers.put(t1, e);
						t2.schedule(new PullData(t1, true, evalFreq), relStart);
					}
				case Window.INFINITE:
					// Window never closes, no need for t2
					t1.scheduleAtFixedRate(new PullData(e), 0, evalFreq * 1000);
					executionTimers.put(t1, e);
				}

			} catch (Exception exn) {
				// EvalFreq is infinite, so even though the window is open, we
				// won't schedule this event, because it will never evaluate to
				// true
			}

		}

		/*
		 * Called when the time window of a TFM event closes.
		 */
		void remove(TFMEvent e) {
			// Timer t = timers.get(e);
			// t.cancel();
			// timers.remove(e);
		}

		private class PullData extends TimerTask {

			TFMEvent e;
			Timer t;
			boolean activityFlag;
			int evalFreq;

			PullData(TFMEvent e) {

				this.e = e;
			}

			PullData(Timer t, boolean activityFlag) {

				this.t = t;
				this.activityFlag = activityFlag;
			}

			PullData(Timer t, boolean activityFlag, int evalFreq) {

				this.t = t;
				this.activityFlag = activityFlag;
				this.evalFreq = evalFreq;
			}

			@Override
			public void run() {

				if (e != null) {

					// Active window, pull data.

					Event me = e.getModifiedEvent();

					System.out.println("Pull data at:"
							+ System.currentTimeMillis());

					// findSensors(me);
				}

				if (t != null) {

					// Management timer, check if t is to be rescheduled or
					// canceled

					if (activityFlag) {
						t.scheduleAtFixedRate(new PullData(executionTimers
								.get(t)), 0, evalFreq * 1000);
					}

					else {
						// Ending an absolute event
						t.cancel();
					}

				}
			}

			void findSensors(Event e) {
				Set<Sensor> s = new HashSet<Sensor>();
				e.addSensorsTo(s);
				Iterator<Sensor> iter = s.iterator();
				while (iter.hasNext()) {
					state.pull(iter.next().getType());
				}

			}

		}

	}

	private class Logic implements AtlasClient {

		public void ReceivedData(String data, Properties props) {

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

		// This method is called when the RE enters the RUN mode
		// or when the SET command is used AND the RE is in RUN mode

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
						int sType = simpleEvent.getSensorType();

						// Subscribe to this one sensor
						state.subscribe(sType);

					} else {
						/* CompositeEvent or TFMEvent */
						// Find all the sensors that the event uses
						Set<Sensor> s = new HashSet<Sensor>();
						r.getEvent().addSensorsTo(s);

						// Subscribe to each of them
						Iterator<Sensor> iter = s.iterator();
						while (iter.hasNext()) {
							state.subscribe(iter.next().getType());
						}

					}

				}

			}

		}

		public void start() {
			state.setRunning(true);
			subscriptionManager();

		}

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

	void defineEvent(String name, Event e) {

		// Debugging snippet to see the difference between expression
		// and expansion

		// System.err.println("Expression is " + expression);
		// System.err.println("Expansion is " + expansion);

		/*
		 * FIXME: Debug snippet shows expression and expansion to be the same. ?
		 */

		if (!state.isRunning()) {
			if (!state.eventExists(name)) {
				e.setName(name);
				state.add(name, e);

				if (e.isTFM()) {
					// Cast is safe, since we just checked that event is TFM
					scheduler.add((TFMEvent) e);
				}
			} else {
				error("Event '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

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

	void defineAction(String name, Action a) {
		if (!state.run) {
			if (!state.actionExists(name)) {
				a.setName(name);
				state.add(name, a);
			} else {
				error("Action '" + name + "' already exists");
			}
		} else {
			error("Cannot DEFINE while running.  STOP first.");
		}
	}

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

	void setCondition(String name, boolean b) {
		if (state.conditionExists(name)) {
			state.getCondition(name).set(b);

			if (state.isRunning()) {
				logic.subscriptionManager();
			}

		} else {
			error("Condition '" + name
					+ "' does not exist.  Please define it first.");
		}
	}

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

	void loadFile(String path) {
		Scanner sc;
		try {
			sc = new Scanner(new File(path));
			while (sc.hasNextLine()) {
				parse(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
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

	Action getAction(String nodeID) {
		return state.getAction(nodeID);
	}

	Event getEvent(String nodeID) {
		return state.getEvent(nodeID);
	}
	
	Rule getRule(String nodeID) {
		return state.getRule(nodeID);
	}

	/*
	 * Construct a SimpleEvent given a sensor's node ID and a range of
	 * triggering values
	 */
	SimpleEvent createEvent(String nodeID, Integer min, Integer max) {
		if (state.sensorExists(nodeID)) {
			return new SimpleEvent(state.getSensor(nodeID), min, max);
		} else {
			error("Sensor '" + nodeID + "' does not exist.");
			return null;
		}
	}

	/*
	 * Construct a SimpleEvent given a sensor's node ID and a triggering sensor
	 * reading
	 */
	Event createEvent(String nodeID, Integer value) {
		return createEvent(nodeID, value, value);
	}

	/*
	 * Construct a new Condition
	 */
	Condition createCondition(boolean b) {
		return new Condition(b);
	}

	/*
	 * Construct a SimpleAction given an actuator's nodeID and a value to which
	 * to actuate
	 */
	Action createAction(String nodeID, Integer value) {
		if (state.actuatorExists(nodeID)) {
			return new SimpleAction(state.getActuator(nodeID), value);
		} else {
			error("Actuator '" + nodeID + "' does not exist.");
			return null;
		}
	}

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

	/* Core Methods */
	/*
	 * These methods are called by Core.
	 */

	void addDevice(ServiceReference sref, AtlasService dev) {
		state.addDevice(sref, dev);
	}

	void removeDevice(ServiceReference sref) {
		state.removeDevice(sref);
	}

	public Condition getCondition(String i) {
		return state.getCondition(i);
	}
}
