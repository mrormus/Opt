package com.pervasa.reactivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
	Map<String, String> basicEvents = new HashMap<String, String>();
	Map<String, String> basicActions = new HashMap<String, String>();
	Map<String, AtomicEvent> eventList = new ConcurrentHashMap<String, AtomicEvent>();
	Map<String, AtomicEvent> eventList2 = new ConcurrentHashMap<String, AtomicEvent>();
	Map<String, Condition> runtimeConditions = new ConcurrentHashMap<String, Condition>();
	Map<String, Action> runtimeActions = new ConcurrentHashMap<String, Action>();
	Map<String, Rule> rules = new HashMap<String, Rule>();	
	// Node-Id to Type of Sensor
	Map<String, Integer> sensorType = new HashMap<String, Integer>();

	//Digital Contact sensor is on or off
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
			basicEvents.put(nodeId, nodeId + ",Pressure,[0,1000]");
			
			// Put sensor in a HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(), SensorType.PRESSURE);
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
			basicEvents.put(nodeId, nodeId + ",Contact,[0,1]");
			
			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(), SensorType.CONTACT);
		}

		// 
		else if (dev instanceof TemperatureSensor) {
			
			// Set references to Temperature sensor emulator
			refTemp = sref;
			sensorTemp = (TemperatureSensor) dev;
			
			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicEvents.put(nodeId, nodeId + ",Temperature,[-100,300]");		
			
			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(), SensorType.TEMP);
		}
		
		else if (dev instanceof HumiditySensor) {
			
			// Set references to Humidity sensor emulator
			refHumid = sref;
			sensorHumid = (HumiditySensor) dev;

			
			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			basicEvents.put(nodeId, nodeId + ",Humidity,[0,100]");
			
			// Put sensor in HashMap of SENSOR TYPES
			sensorType.put(sref.getProperty("Node-Id").toString(), SensorType.HUMIDITY);
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

		String sensorMeasure = new String("Unknown");
		
		// Stores the Property keys in an array
		String[] keysarr = new String[props.size()];
		props.keySet().toArray(keysarr);

		// the sensor service bundles are configured so that the
		// "measure-type" property will identify the sensor for this application
		if (props.containsKey("measure-type"))
			sensorMeasure = props.getProperty("measure-type");
		else {
			/* FIXME: Handle this well, give user indication of a potentially fatal problem */
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
			}
			else {
				if (!isSwitchOn) {
					isSwitchOn = true;
				}
			}
		}
	}

	// function to display the values in a map
	public void showMap(Map<String, AtomicEvent> basicEvents) {
		System.out.println("Map is now:");
		Iterator<AtomicEvent> i = basicEvents.values().iterator();
		String nodeids;
		while (i.hasNext()) {
			nodeids = i.next().toString();
			System.out.println(nodeids);
		}
	}

	// FIXME: Engine logic rules could be separated

	// function evaluates a composite event, something like e1*seconds*e2
	public boolean evaluateCompositeEvent(String expr) {
		String event[];
		String event1;
		String event2;
		String timeDiff;
		event = expr.split("&");
		event1 = event[0];
		timeDiff = event[1];
		event2 = event[2].split("&")[0];
		AtomicEvent ev;
		boolean event1Truth = false, event2Truth = false;
		long event1Time = 0, event2Time = 0;
		long timeDuration = Long.parseLong(timeDiff);

		Iterator<AtomicEvent> evItr = eventList.values().iterator();

		while (evItr.hasNext()) {
			ev = evItr.next();
			System.out.println("Got new event " + ev.expansion
					+ " Checking with " + event1 + " and " + event2);
			if (ev.expansion.equalsIgnoreCase(event1)) {
				event1Time = ev.startTime;
				event1Truth = ev.getTruthValue();
				System.out.println("Got event1 value as " + event1Truth + ":"
						+ event1Time);
				continue;
			}
			if (ev.expansion.equalsIgnoreCase(event2)) {
				event2Time = ev.startTime;
				event2Truth = ev.getTruthValue();
				System.out.println("Got event2 value as " + event2Truth + ":"
						+ event2Time);
				continue;
			}
		}
		System.out.println("Got values as : " + event1Truth + ":" + event1Time
				+ " for event1 and " + event2Truth + ":" + event2Time);
		if (event1Truth == true && event2Truth == true) {
			if (((event2Time - event1Time) <= (timeDuration * 1000))
					&& (event1Time != 0 && event2Time != 0)) {
				System.out.println("Time less!!! diff is "
						+ (event2Time - event1Time) + " needed is "
						+ (timeDuration * 1000));
				return true;
			}
		}
		return false;
	}

	// function evaluates the truth value of an expression like q11(30) or
	// q23[20,50]. returns true or false
	public boolean evaluate(String expr) {
		// assume u get someting like q11(30) or q23[20,50]
		// split accordingly and evaluate using nodeValues hashtable which
		// contains
		// values of readings for all nodes
		String nodeId;
		String valueLower;
		String valueHigher;
		String value;
		String sensorValue;
		StringTokenizer strTok = new StringTokenizer(expr, "()[]");
		StringTokenizer rangeTok = null;
		int sensorVal = 0;

		// System.out.println("Evaluating: " + expr);
		try {
			if (expr.contains("&")) {
				// handle *seconds* type events here
				return evaluateCompositeEvent(expr);
			} else if (!expr.contains(",")) {
				nodeId = strTok.nextToken();
				value = strTok.nextToken();
				sensorValue = nodeValues.get(nodeId);
				if (sensorValue == null) {
					return false;
				}
				// System.out.println("Sensor val:" +
				// Integer.parseInt(sensorValue) + " " + nodeId + ":" + value);

				if (Integer.parseInt(value) == Integer.parseInt(sensorValue)) {
					return true;
				} else {
					return false;
				}
			} else {
				nodeId = strTok.nextToken();
				value = strTok.nextToken();
				rangeTok = new StringTokenizer(value, ",");
				valueLower = rangeTok.nextToken();
				valueHigher = rangeTok.nextToken();
				sensorValue = nodeValues.get(nodeId);
				if (sensorValue == null) {
					return false;
				} else {
					sensorVal = Integer.parseInt(sensorValue);
				}
				// System.out.println("Sensor val:" + sensorVal + " " + nodeId +
				// ":" + valueLower + ":" + valueHigher);

				if (sensorVal > Integer.parseInt(valueLower)
						&& sensorVal < Integer.parseInt(valueHigher)) {
					return true;
				} else {
					return false;
				}
			}

		} catch (Exception e) {
			System.out.println("ERROR: " + expr);
			e.printStackTrace();
			return false;
		}

		// return false;
	}

	// function replaces like q11(30) or q23[20,50] with T or F. this input
	// string consisting of T and F will then be passed to parse
	public String replaceWithTruthValues(String expr) {
		// while replacing, you may not have the values of all sensors, the ones
		// that
		// did not send data yet. make sure you check this

		// split expr by +, *
		// pass each split to evaluate function to get truth value
		// if u get e*sec*e then directly put truth value
		
		String token;

		int i = 0, start = 0, end = 0;
		char ch;
		StringBuilder str = new StringBuilder(expr);
		while (i < str.length()) {
			ch = str.charAt(i);
			if (ch == '*') {
				start = i;
				i++;
				if (Character.isDigit(str.charAt(i))) {
					while (Character.isDigit(str.charAt(i))) {
						i++;
					}
					if (str.charAt(i) != '*') {
						System.out.println("Unexpected character "
								+ str.charAt(i) + " in string " + str
								+ " Quitting...");
						System.exit(0);
					} else {
						end = i++;
						str.setCharAt(start, '&');
						str.setCharAt(end, '&');
					}
				} else {
					continue;
				}
			} else {
				i++;
				continue;
			}

		}

		// System.out.println("In replaceWithTruthValues::Got input: " + expr);
		String truthExpr = str.toString();
		expr = str.toString();
		StringTokenizer strTok = new StringTokenizer(expr, "+*");
		while (strTok.hasMoreTokens()) {
			token = strTok.nextToken();
			// System.out.println("Sent for eval: "+ token);
			boolean tVal = evaluate(token);
			if (tVal == true) {
				truthExpr = truthExpr.replace(token, "T");
			} else if (tVal == false) {
				truthExpr = truthExpr.replace(token, "F");
			} else {
				// this should be a digit (*30* etc types) ignore
				continue;
			}
			// System.out.println("New truthexpr is: " + truthExpr);
		}
		// System.out.println("Returning from replaceWithTruthValues:" +
		// truthExpr);
		return truthExpr;
	}

	// function determines the value of operations like T*F or T+T etc
	public char atomicTruthEval(char op1, char op2, char opr) {
		if (opr == '*') {
			if (op1 == op2 && op1 == 'T') {
				return 'T';
			} else {
				return 'F';
			}
		} else if (opr == '+') {
			if (op1 == 'T' || op2 == 'T') {
				return 'T';
			} else {
				return 'F';
			}
		} else {
			return 'F';
		}
	}

	// function parses the input string using a stack and returns a single truth
	// value of the event.

	public boolean parseEventValues(String expr) {
		// use stack to shift and reduce to a single truth value
		// input contains only T,F,*,+
		Stack<Character> stack = new Stack<Character>();
		StringBuilder input = new StringBuilder(expr);
		char[] inputChar = new char[1];
		char op1;
		char op2;
		char opr;
		try {
			// i = 0;
			for (int i = 0; i < input.length(); i++) {
				inputChar[0] = input.charAt(i);
				// System.out.println("Char is :" + inputChar[0]);
				switch (inputChar[0]) {
				case 'T': {
					stack.push(inputChar[0]);
					// System.out.println("Pushed:" + inputChar[0]);
					break;
				}
				case 'F': {
					stack.push(inputChar[0]);
					// System.out.println("Pushed:" + inputChar[0]);
					break;
				}
				case '+': {
					stack.push(inputChar[0]);
					// System.out.println("Pushed:" + inputChar[0]);
					break;
				}
				case '*': {
					// op1 = 'T';
					// op1 = stack.pop()[0];
					op1 = stack.pop();
					// op1 = test[0];
					// System.out.println("POP:" + op1);
					i++;
					op2 = input.charAt(i);
					// System.out.println("Got *, Popped and sent:" + op1 + ":"
					// + op2);
					op2 = atomicTruthEval(op1, op2, '*');
					// System.out.println("Got truth val as:" + op2);
					inputChar[0] = op2;
					stack.push(inputChar[0]);
					// System.out.println("Pushed:" + inputChar[0]);
					break;
				}
				}

			}

			// System.out.println("Begin Eval in stack");
			while (stack.size() > 1) {
				op1 = stack.pop();
				opr = stack.pop();
				op2 = stack.pop();

				// System.out.println("Popped and sent:" + op1 + ":" + op2 + ":"
				// + opr);
				inputChar[0] = atomicTruthEval(op1, op2, opr);
				// System.out.println("Got truth val as:" + inputChar[0]);

				stack.push(inputChar[0]);
			}

			op1 = stack.pop();

			if (op1 == 'T') {
				// System.out.println("Returning T");
				return true;
			} else {
				// System.out.println("Returning F");
				return false;
			}
		} catch (Exception e) {
			System.out.println("Error Parsing");
			return false;
		}

	}

	// this is the function called in receivedData method which triggers the
	// checking of rules (events)
	public void updateEvents() {
		// get the sensor reading here and update all events in the
		// eventsList hashtable
		String truthValue;
		boolean eventValue = false;
		AtomicEvent a;
		Collection<AtomicEvent> c = eventList.values();
		Iterator<AtomicEvent> kItr = c.iterator();
		// eventList.clear();
		while (kItr.hasNext()) {
			a = kItr.next();
			if (a != null) {
				// System.out.println("Sending Expansion: " + a.expansion);
				truthValue = replaceWithTruthValues(a.expansion);
				eventValue = parseEventValues(truthValue);
				if (a.value != eventValue) {
					System.out.println("Updated value of " + a.expansion
							+ " to " + eventValue);
					a.value = eventValue;
					a.startTime = Calendar.getInstance().getTimeInMillis();
				}
				eventList2.put(a.name, a);
				// System.out.println("Value of "+ a.expansion + " is " +
				// eventValue);
			}
		}
		eventList.clear();
		eventList.putAll(eventList2);
		System.out.println("After updating events, map is:");
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

		for (Map.Entry<String, String> e : basicEvents.entrySet()) {
			s.append(e.getValue() + "\n");

		}
		return s;
	}

	private StringBuffer appendUserEvents(StringBuffer s) {
		AtomicEvent a;
		s.append("\n----USER DEFINED EVENTS----\n");
		for (Map.Entry<String, AtomicEvent> e : eventList.entrySet()) {
			String key = e.getKey();
			a = e.getValue();
			if (a.expression == null || a.expression == "")
				s.append(key + "=" + a.expansion + "\n");
			else
				s.append(key + "=" + a.expression + "\n");

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
			if (atomicEventExists(e)) {
				if (conditionExists(c)) {
					if (actionExists(a)) {
						if (!ruleExists(name)) {
							Rule r = new Rule(name, e, c, a);
							rules.put(name, r);
						} else {gui.error("Rule '" + name + "' already exists"); }
					} else {gui.error("Action '" + a + "' does not exist"); }
				} else {gui.error("Condition '" + c + "' does not exist"); }
			} else {gui.error("Event '" + e + "' does not exist");}
		} else {gui.error("Cannot DEFINE while running.  STOP first.");}
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

	void defineEvent(String name, String expression, String expansion) {
		
		// Debugging snippet to see the difference between expression
		// and expansion
		
		//System.err.println("Expression is " + expression);
		//System.err.println("Expansion is " + expansion);
		
		/*FIXME: Debug snippet shows expression and expansion to be the same. ? */
		
		if (!run) {
			if (!atomicEventExists(name)) {
				AtomicEvent e = new AtomicEvent(name, expression, expansion);
				eventList.put(name, e);
			} else {
				gui.error("Event '" + name + "' already exists");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
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
			
			if (sensorContact != null){ 
				sensorContact.unsubscribeFromContactData(this);
			}
			if (sensorPressure != null){
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
			boolean eventVal = eventList.get(rule.event).getTruthValue();
			System.out.println("Rule Eval:" + rule.name + ":" + eventVal);
			if (eventVal == true) {
				// event has occured
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
	
	void subscriptionManager () {
		
		// iterate through rules checking if the condition is true
		// and subscribing to the appropriate sensors if required.
		
		Iterator<String> rItr = rules.keySet().iterator();
		String ruleid;
		Rule rule;
		while (rItr.hasNext()) {
			ruleid = rItr.next();
			rule = rules.get(ruleid);
			
			boolean condVal = runtimeConditions.get(rule.condition).getValue();
			if (condVal) {
				
				// Debugging snippet
				
				System.err.println("Evaluating " + ruleid);
				
				// Subscribe to the sensors 
				
				AtomicEvent a = eventList.get(rule.event);
				// Basically reduces something like N56(100) to N56 so that it can match 
				// with sensorType
				String exp = new String (a.expansion.toCharArray(), 0, a.expansion.lastIndexOf('('));
				
				if (!exp.contains("+") && !exp.contains("*")) {
					
					// Simple atomic event
					
					Integer sType = sensorType.get(exp);
					
					System.out.println(sType);
					
					switch (sType) {
					
					case SensorType.CONTACT : sensorContact.subscribeToContactData(this); 
						System.out.println("Subscribed to Contact Data"); break;
					case SensorType.PRESSURE : sensorPressure.subscribeToPressureData(this); break;
					case SensorType.HUMIDITY : sensorHumid.subscribeToSensorData(this); break;
					case SensorType.TEMP : sensorTemp.subscribeToSensorData(this); break;
					
					}
					
				}
				
				else {
					
					// Not a simple atomic event
					
				}
				
			}

		}
		
	}

}
