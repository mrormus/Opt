package com.pervasa.reactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
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
	Map<String, HS322Servo> servoMap = new HashMap<String, HS322Servo>();
	Map<String, Device> sensorMap = new HashMap<String, Device>();
	Map<String, String> basicActions = new HashMap<String, String>();
	Map<String, OptEvent> eventList = new ConcurrentHashMap<String, OptEvent>();
	Map<String, Condition> runtimeConditions = new ConcurrentHashMap<String, Condition>();
	Map<String, Action> runtimeActions = new ConcurrentHashMap<String, Action>();
	Map<String, Rule> rules = new HashMap<String, Rule>();

	public ReactiveEngine(GUI gui) {
		this.gui = gui;
	}
	
	/* These functions communicate with the middleware */

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
			sensorMap.put(nodeId, new Device(nodeId,DeviceType.PRESSURE,Device.NULL));

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
			sensorMap.put(nodeId, new Device(nodeId,DeviceType.CONTACT,Device.NULL));

		}

		// 
		else if (dev instanceof TemperatureSensor) {

			// Set references to Temperature sensor emulator
			refTemp = sref;
			sensorTemp = (TemperatureSensor) dev;

			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			sensorMap.put(nodeId, new Device(nodeId, DeviceType.TEMP, Device.NULL));

		}

		else if (dev instanceof HumiditySensor) {

			// Set references to Humidity sensor emulator
			refHumid = sref;
			sensorHumid = (HumiditySensor) dev;

			// Put sensor in HashMap of BASIC EVENTS
			String nodeId = sref.getProperty("Node-Id").toString();
			sensorMap.put(nodeId, new Device(nodeId, DeviceType.HUMIDITY, Device.NULL));

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
			
			// set the reading of sensor to NULL (we could always remove it from 
			// the map but since events may have been defined so to ensure those 
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
			// Probably should not come here
		}

		// Update sensor reading value in nodeValues HashMap
		
		String nodeId = props.getProperty("Node-Id");
		
		Device d = sensorMap.get(nodeId);
		
		d.update(Integer.parseInt(data));


		// Change the truth values for the events
		for (OptEvent e: eventList.values()) {
			e.update();
		}
		// Check whether any rules need to be triggered
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
	
	/* Display methods */

	// FIXME: Command parsing stuff could be separated.

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

		for (Device d : sensorMap.values()) {
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

	/* Command parsing for DEFINE methods */

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
				e.setName(name);
				eventList.put(name, e);
				//FIXME Debug output
				System.err.println(e);
				
			} else {
				gui.error("Event '" + name + "' already exists");
			}
		} else {
			gui.error("Cannot DEFINE while running.  STOP first.");
		}
	}
	
	Device getDevice(String nodeID) {
		if (sensorMap.containsKey(nodeID)) {
			return sensorMap.get(nodeID);
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
			
			
			if (run == true) {
				subscriptionManager();
			}
			

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
		return sensorMap.containsKey(s);
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
	
	// This method is called when the RE enters the RUN mode
	// or when the SET command is used AND the RE is in RUN mode
	
	void subscriptionManager() {

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

				OptEvent e = rule.event;

				if (e.isSimple()) {

					// Simple atomic event

					int sType = e.getSensorType();

					System.out.println(sType);
					
					subscribe(sType);

				} else {

					// Not a simple atomic event
					
					findSimpleEvents(e);

				}

			}

		}

	}
	
	// Had to create a new function so that I could recurse :)
	void findSimpleEvents (OptEvent compositeEvent) {
		
		
		if (compositeEvent.left ==  null && compositeEvent.right == null) {
			//We've reached a simple event, find it's sensor type and subscribe.
			int sType = compositeEvent.getSensorType();
			subscribe(sType);
		}
		
		else {
			if (compositeEvent.left != null) {
				findSimpleEvents(compositeEvent.left);
			}
			if (compositeEvent.right != null) {
				findSimpleEvents(compositeEvent.right);
			}
		}
		
	}
	
	// Subscribes to the appropriate sensor.
	void subscribe (int sType) {
		
		switch (sType) {

		case DeviceType.CONTACT:
			sensorContact.subscribeToContactData(this);
			System.out.println("Subscribed to Contact Data");
			break;
		case DeviceType.PRESSURE:
			sensorPressure.subscribeToPressureData(this);
			System.out.println("Subscribed to Pressure Data");
			break;
		case DeviceType.HUMIDITY:
			sensorHumid.subscribeToSensorData(this);
			System.out.println("Subscribed to Humidity Data");
			break;
		case DeviceType.TEMP:
			sensorTemp.subscribeToSensorData(this);
			System.out.println("Subscribed to Temperature Data");
			break;
		}
		
	}

}
