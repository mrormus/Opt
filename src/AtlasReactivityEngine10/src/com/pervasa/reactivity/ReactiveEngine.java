package com.pervasa.reactivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import org.osgi.framework.ServiceReference;
import org.sensorplatform.actuators.servo.hs322.HS322Servo;
import org.sensorplatform.sensors.digitalcontact.DigitalContactSensor;
import org.sensorplatform.sensors.humidity.HumiditySensor;
import org.sensorplatform.sensors.pressure.InterlinkPressureSensor;
import org.sensorplatform.sensors.temperature.TemperatureSensor;

import com.pervasa.atlas.dev.service.AtlasClient;
import com.pervasa.atlas.dev.service.AtlasService;

public class ReactiveEngine implements AtlasClient {

	private GUI gui;

	boolean run = false;

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
	Map<String, String> nodeValues = new ConcurrentHashMap<String, String>();
	Map<String, HS322Servo> servoMap = new HashMap<String, HS322Servo>();
	Map<String, Device> basicEvents = new HashMap<String, Device>();
	Map<String, String> basicActions = new HashMap<String, String>();
	Map<String, OptEvent> eventList = new ConcurrentHashMap<String, OptEvent>();
	Map<String, OptEvent> eventList2 = new ConcurrentHashMap<String, OptEvent>();
	Map<String, Condition> runtimeConditions = new ConcurrentHashMap<String, Condition>();
	Map<String, Action> runtimeActions = new ConcurrentHashMap<String, Action>();
	Map<String, Rule> rules = new HashMap<String, Rule>();
	// Node-Id to Type of Sensor
	Map<String, Integer> sensorType = new HashMap<String, Integer>();

	// Digital Contact sensor is on or off
	protected boolean isSwitchOn;

	public ReactiveEngine(GUI gui) {
		this.gui = gui;
		isSwitchOn = false;
	}

	// FIXME: Interface with middleware could be separated, probably

	// this method is called by the Reactive Engine bundle's Activator when any
	// service running
	// in Knopflerfish starts or changes
	// sref is the Knopflerfish ServiceReference ID of the new/changed service
	// dev is a direct reference to the new/changed service
	// AtlasService is an interface implemented by all Atlas sensor and actuator
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
			// FIXME: second argument needs to be actual sensor reading
			basicEvents.put(nodeId, new Device(nodeId,DeviceType.PRESSURE,0));

			// Put sensor in a HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(),
					DeviceType.PRESSURE);
		}

		else if (dev instanceof HS322Servo) {

			// Set references to the Servo sensor emulator
			refServo = sref;
			actuatorServo = (HS322Servo) dev;

			// Put actuator in a HashMap of BASIC ACTIONS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicActions.put(nodeId, nodeId + ",Servo,[0,180]");

			// Put actuator in a ServoMap
			servoMap.put(nodeId, actuatorServo);

		}

		else if (dev instanceof DigitalContactSensor) {

			// Set references to the Digital Contact sensor emulator
			refContact = sref;
			sensorContact = (DigitalContactSensor) dev;

			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicEvents.put(nodeId, new Device(nodeId,DeviceType.CONTACT,0));

			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(),
					DeviceType.CONTACT);
		}

		// 
		else if (dev instanceof TemperatureSensor) {

			// Set references to Temperature sensor emulator
			refTemp = sref;
			sensorTemp = (TemperatureSensor) dev;

			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicEvents.put(nodeId, new Device(nodeId, DeviceType.TEMP, 0));

			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(),
					DeviceType.TEMP);
		}

		else if (dev instanceof HumiditySensor) {

			// Set references to Humidity sensor emulator
			refHumid = sref;
			sensorHumid = (HumiditySensor) dev;

			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicEvents.put(nodeId, new Device(nodeId, DeviceType.HUMIDITY, 0));

			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(),
					DeviceType.HUMIDITY);
		}

	}

	// this method is called by the Reactive Engine bundle's Activator when any
	// service
	// running in Knopflerfish goes offline
	// sref is the Knopflerfish ServiceReference ID of the departing service
	// because the service has already been unbound in OSGi, we cannot do the
	// "instanceof" check like in addDevice (the "dev" parameter in that
	// method would be null here). This is why addDevice must record the
	// ServiceReference.
	public void removeDevice(ServiceReference sref) {

		if (sref == refPressure) {
			refPressure = null;
			sensorPressure = null;

		}

		else if (sref == refServo) {
			refServo = null;
			actuatorServo = null;

		}

		else if (sref == refContact) {
			refContact = null;
			sensorContact = null;
		}

		else if (sref == refTemp) {
			refTemp = null;
			sensorTemp = null;

		}

		else if (sref == refHumid) {
			refHumid = null;
			sensorTemp = null;

		}
	}

	// this is basically a callback method called by any Atlas service
	// bundle to which the application has subscribed (in this case,
	// the pressure sensor service and the digital contact sensor
	// service).
	// data contains the reading produced by the sensor
	// props contains information about the sensor that produced the data
	// (name, label, channel to which the device is connected, etc.
	public void ReceivedData(String data, Properties props) {
		
		//FIXME debug
		error("Received data");

		String sensorMeasure = new String("Unknown");

		// Stores the Property keys in an array
		String[] keysarr = new String[props.size()];
		props.keySet().toArray(keysarr);

		// the sensor service bundles are configured so that the
		// "measure-type" property will identify the sensor for this application
		if (props.containsKey("measure-type"))
			sensorMeasure = props.getProperty("measure-type");
		else {
			/*
			 * FIXME: Handle this well, give user indication of a potentially
			 * fatal problem
			 */
		}

		// Update sensor reading value in nodeValues HashMap
		int sensorReading = Integer.parseInt(data);
		String nodeId = props.getProperty("Node-Id");

		if (nodeValues.containsKey(nodeId)) {
			nodeValues.remove(nodeId);
		}

		nodeValues.put(nodeId, data.toString());

		// Change the truth values for the events
		updateEvents();
		// Check whether any rules need to be trigger
		checkRules();

		// Update value of isSwitchOn if Contact sensor
		if (sensorMeasure.equalsIgnoreCase("contact")) {
			if (sensorReading == 1) {
				if (isSwitchOn) {
					isSwitchOn = false;
				}
			} else {
				if (!isSwitchOn) {
					isSwitchOn = true;
				}
			}
		}
	}

	// function to display the values in a map
	public void showMap(Map<String, OptEvent> basicEvents) {
		System.out.println("Map is now:");
		Iterator<OptEvent> i = basicEvents.values().iterator();
		String nodeids;
		while (i.hasNext()) {
			nodeids = i.next().toString();
			System.out.println(nodeids);
		}
	}

	
	// this is the function called in receivedData method which triggers the
	// checking of rules (events)
	public void updateEvents() {
		// get the sensor reading here and update all events in the
		// eventsList hashtable
		for (OptEvent e: eventList.values()) {
			e.update();
		}
		showMap(eventList);
	}

	// This function checks if any rules now need to be fired after the event
	// updates
	public void checkRules() {
		Iterator<String> rItr = rules.keySet().iterator();
		String ruleid;
		Rule rule;
		while (rItr.hasNext()) {
			ruleid = rItr.next();
			rule = rules.get(ruleid);
			evaluateRule(rule);
		}
	}

	// This function moves the servo specified by the nodeid to a new position
	public void moveServo(String nodeId, int move) {
		System.out.println("Move servo " + nodeId + " by " + move);
		try {
			move = ((move * 100) / 180) + 1;
			servoMap.get(nodeId).moveServo(move);
		} catch (NullPointerException ex) {
			System.out.println("Servo is not in the servo map!");
		} catch (Exception e) {
			System.out.println("Error moving servo " + nodeId + " by " + move);
			e.printStackTrace();
		}
	}

	// FIXME: Command parsing stuff could be separated.

	// Helper methods for LIST command implementation
	public void listAll() {
		StringBuffer s = new StringBuffer();
		s = appendBasicEvents(s);
		s = appendBasicActions(s);
		s = appendUserEvents(s);
		s = appendUserActions(s);
		s = appendConditions(s);
		s = appendRules(s);
		gui.updateConsole(s + "\n");
	}

	public void listEvents() {
		StringBuffer s = new StringBuffer();
		s = appendBasicEvents(s);
		s = appendUserEvents(s);
		gui.updateConsole(s + "\n");
	}

	public void listActions() {
		StringBuffer s = new StringBuffer();
		s = appendBasicActions(s);
		s = appendUserActions(s);
		gui.updateConsole(s + "\n");
	}

	public void listRules() {
		StringBuffer s = new StringBuffer();
		s = appendRules(s);
		gui.updateConsole(s + "\n");
	}

	public void listConditions() {
		StringBuffer s = new StringBuffer();
		s = appendConditions(s);
		gui.updateConsole(s + "\n");
	}

	private StringBuffer appendBasicEvents(StringBuffer s) {
		s.append("\n----BASIC EVENTS----\n");

		for (Device d : basicEvents.values()) {
			s.append(d + "\n");

		}
		return s;
	}

	private StringBuffer appendUserEvents(StringBuffer s) {
		s.append("\n----USER DEFINED EVENTS----\n");
		for (Map.Entry<String, OptEvent> e : eventList.entrySet()) {
			s.append(e.getKey() + "=" + e.getValue().toString() + "\n");
		}

		return s;
	}

	private StringBuffer appendConditions(StringBuffer s) {
		Condition print;
		s.append("\n----SET CONDITIONS----\n");
		for (Map.Entry<String, Condition> e : runtimeConditions.entrySet()) {
			print = e.getValue();
			s.append(e.getKey() + "=" + print.value + "\n");
		}

		return s;
	}

	private StringBuffer appendUserActions(StringBuffer s) {
		Action app;

		s.append("\n----USER DEFINED ACTIONS----\n");
		for (Map.Entry<String, Action> e : runtimeActions.entrySet()) {
			app = e.getValue();
			s.append(e.getKey() + "=" + app.actionDisplay + "\n");
		}

		return s;
	}

	private StringBuffer appendBasicActions(StringBuffer s) {
		s.append("\n----BASIC ACTIONS----\n");

		for (Map.Entry<String, String> p : basicActions.entrySet()) {
			s.append(p.getValue() + "\n");
		}

		return s;
	}

	private StringBuffer appendRules(StringBuffer s) {
		Rule rp;

		s.append("\n----USER DEFINED RULES----\n");
		for (Map.Entry<String, Rule> e : rules.entrySet()) {
			rp = e.getValue();
			s.append(e.getKey() + "=" + rp.event + "," + rp.condition + ","
					+ rp.action + "\n");
		}

		return s;
	}

	// helper methods for BASIC command

	public void listBasic() {
		StringBuffer s = new StringBuffer();
		s = appendBasicEvents(s);
		s = appendBasicActions(s);
		gui.updateConsole(s + "\n");
	}

	public void listBasicEvents() {
		StringBuffer s = new StringBuffer();
		s = appendBasicEvents(s);
		gui.updateConsole(s + "\n");
	}

	public void listBasicActions() {
		StringBuffer s = new StringBuffer();
		s = appendBasicActions(s);
		gui.updateConsole(s + "\n");
	}

	// end BASIC command

	/*
	String evaluateAtomicEvent(String s) {
		String k;
		AtomicEvent a;

		for (Map.Entry<String, AtomicEvent> p : eventList.entrySet()) {
			k = p.getKey();
			a = p.getValue();
			if (k.matches(s)) {
				return a.expansion;
			}
		}
		return "invalid";
	}
	*/

	String evaluateAction(String s) {
		String k;
		Action a;
		for (Map.Entry<String, String> p : basicActions.entrySet()) {
			k = p.getKey();
			if (k.matches(s))
				return k;
		}
		for (Map.Entry<String, Action> p : runtimeActions.entrySet()) {
			k = p.getKey();
			if (k.matches(s)) {
				a = p.getValue();
				return a.actionList;
			}
		}
		return "invalid";
	}

	void defineRule(String name, String e, String c, String a) {
		if (!run) {
			if (userEventExists(e)) {
				if (conditionExists(c)) {
					if (actionExists(a)) {
						if (!ruleExists(name)) {
							Rule r = new Rule(name, eventList.get(e), c, a);
							rules.put(name, r);
						} else {
							gui.error("Rule '" + name + "' already exists");
						}
					} else {
						gui.error("Action '" + a + "' does not exist");
					}
				} else {
					gui.error("Condition '" + c + "' does not exist");
				}
			} else {
				gui.error("Event '" + e + "' does not exist");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
		}
	}

	void defineCondition(String name, boolean b) {
		if (!run) {
			if (!conditionExists(name)) {
				Condition c = new Condition(name, b);
				runtimeConditions.put(name, c);
			} else {
				gui.error("Condition '" + name + "' already exists");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
		}
	}

	void defineEvent(String name, OptEvent e) {

		// Debugging snippet to see the difference between expression
		// and expansion

		// System.err.println("Expression is " + expression);
		// System.err.println("Expansion is " + expansion);

		/* FIXME: Debug snippet shows expression and expansion to be the same. ? */

		if (!run) {
			if (!userEventExists(name)) {
				e.assignName(name);
				eventList.put(name, e);
			} else {
				gui.error("Event '" + name + "' already exists");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
		}
	}
	
	Device getDevice(String nodeID) {
		if (basicEvents.containsKey(nodeID)) {
			return basicEvents.get(nodeID);
		} else {
			error("Device '" + nodeID + "' does not exist.");
			return null;
		}
	}
	
	OptEvent getEvent(String name) {
		if (eventList.containsKey(name)) {
			return eventList.get(name);
		} else {
			error("Event '" + name + "' does not exist.");
			return null;
		}
	}

	void defineAction(String name, String actionlist, String actionDisplay) {
		if (!run) {
			if (!actionExists(name)) {
				Action a = new Action(name, actionlist, actionDisplay);
				runtimeActions.put(name, a);
			} else {
				gui.error("Action '" + name + "' already exists");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
		}
	}

	void setCondition(String name, boolean b) {
		if (conditionExists(name)) {
			runtimeConditions.get(name).value = b;

			subscriptionManager();

		} else {
			gui.error("Condition '" + name
					+ "' does not exist.  Please define it first.");
		}
	}

	void runCommand() {
		if (run) {
			JOptionPane.showMessageDialog(gui, "RUN mode is already on");
			return;
		} else {
			run = true;

			subscriptionManager();

		}

	}

	void stopCommand() {
		if (!run) {
			JOptionPane.showMessageDialog(gui, "RUN mode already off");
			return;
		} else {
			run = false;

			// Unsubscribe from all the sensors once the engine stops running

			if (sensorContact != null) {
				sensorContact.unsubscribeFromContactData(this);
			}
			if (sensorPressure != null) {
				sensorPressure.unsubscribeFromPressureData(this);
			}
			if (sensorHumid != null) {
				sensorHumid.unsubscribeFromSensorData(this);
			}
			if (sensorTemp != null) {
				sensorTemp.unsubscribeFromSensorData(this);
			}
		}
	}

	public boolean basicActionExists(String s) {
		return basicActions.containsKey(s);
	}

	public boolean basicEventExists(String s) {
		return basicEvents.containsKey(s);
	}

	public boolean atomicEventExists(String s) {
		return eventList.containsKey(s);
	}
	
	public boolean userEventExists(String s) {
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

	public void evaluateRule(Rule rule) {
		try {
			boolean eventVal = rule.event.evaluate();
			System.out.println("Rule Eval:" + rule.name + ":" + eventVal);
			if (eventVal == true) {
				// event has occurred
				// check conditions here
				boolean condVal = runtimeConditions.get(rule.condition)
						.getValue();
				if (condVal) {
					// condition is true, trigger action
					performAction(runtimeActions.get(rule.action));
				}
			}
		} catch (NullPointerException ne) {
			System.out
					.println("Condition, event or action was not found in the maps!");
			ne.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void takeAction(String command) {
		System.out.println("Taking Action:" + command);
		StringTokenizer strTok = new StringTokenizer(command, "_()");
		String nodeId;
		String movement;
		double move;
		try {
			nodeId = strTok.nextToken();
			movement = strTok.nextToken();
			move = Integer.parseInt(movement);
			// FIXME this should probably be contained better

			// servo is the only actuator, hence this call. Else need a class
			// hierarchy
			// of actuators and fire the appropriate methods
			// move = (move*100)/180;
			moveServo(nodeId, (int) move);
		} catch (Exception e) {
			System.out.println("Invalid action: " + command);
		}

	}

	public void performAction(Action action) {
		try {
			StringTokenizer strTok = new StringTokenizer(action.actionList, ";");
			while (strTok.hasMoreTokens()) {
				takeAction(strTok.nextToken());
			}
		} catch (NullPointerException ne) {
			System.out.println("Problem with the action list "
					+ action.actionList);
		}

	}

	void parse(String cmd) {
		Lexer l = new Lexer(new StringReader(cmd));
		parser p = new parser(l, this);
		try {
			p.parse();
		} catch (Exception e) {
			// Syntax errors are handled in the parser
		}

	}

	void error(String e) {
		gui.error(e);
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

	void subscriptionManager() {

		// iterate through rules checking if the condition is true
		// and subscribing to the appropriate sensors if required.
		//FIXME
		System.err.println("subscription manager");

		Iterator<String> rItr = rules.keySet().iterator();
		String ruleid;
		Rule rule;
		while (rItr.hasNext()) {
			//FIXME
			System.err.println("checking a rule");
			
			ruleid = rItr.next();
			rule = rules.get(ruleid);
			
			boolean condVal = runtimeConditions.get(rule.condition).getValue();
			if (condVal) {
				System.err.println("condition is true");

				// Debugging snippet

				System.err.println("Evaluating " + ruleid);

				// Subscribe to the sensors

				OptEvent e = rule.event;
			
				System.err.println("got event");

				if (e.isSimple()) {
					System.err.println("simple event");

					// Simple atomic event

					int sType = e.getSensorType();

					System.out.println(sType);

					switch (sType) {

					case DeviceType.CONTACT:
						sensorContact.subscribeToContactData(this);
						System.out.println("Subscribed to Contact Data");
						break;
					case DeviceType.PRESSURE:
						sensorPressure.subscribeToPressureData(this);
						break;
					case DeviceType.HUMIDITY:
						sensorHumid.subscribeToSensorData(this);
						break;
					case DeviceType.TEMP:
						sensorTemp.subscribeToSensorData(this);
						break;

					}

				} else {
					System.err.println("composite event");

					// Not a simple atomic event
					sensorContact.subscribeToContactData(this);
					sensorPressure.subscribeToPressureData(this);
					sensorHumid.subscribeToSensorData(this);
					sensorTemp.subscribeToSensorData(this);

				}

			}

		}

	}

}
