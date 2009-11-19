package com.pervasa.demo.kitsample.impl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.sensorplatform.actuators.servo.hs322.HS322Servo;
import org.sensorplatform.sensors.digitalcontact.DigitalContactSensor;
import org.sensorplatform.sensors.humidity.HumiditySensor;
import org.sensorplatform.sensors.pressure.InterlinkPressureSensor;
import org.sensorplatform.sensors.temperature.TemperatureSensor;

import com.pervasa.atlas.dev.service.AtlasClient;
import com.pervasa.atlas.dev.service.AtlasService;


public class ReactiveEngine implements AtlasClient {
	
	//FIXME: Not sure why eclipse thinks this variable is unused.  It is used in the constructor
	// could be indicative of a problem.
	private BundleContext context;
	private GUI gui;
	private JTextArea jTextArea2;
	
    int run=0; /*FIXME: Make this boolean*/
    
    /*FIXME: Remove, variables not used */
 // Knopflerfish's ServiceReference ID for the pressure sensor service
	//   only used to detect if the pressure sensor service goes offline
	private ServiceReference refPressure = null;
	// Local reference to the pressure sensor service
	//   used to subscribe to pressure sensor data stream, manually pull readings, etc.
	private InterlinkPressureSensor sensorPressure = null;
	// Knopflerfish's ServiceReference ID for the servo actuator service
	//   only used to detect if the servo actuator service goes offline

	private ServiceReference refServo = null;
	// Local reference to the servo actuator service
	//   used to rotate servo left or right, move to a specific angle, etc.
	private HS322Servo actuatorServo = null;
	// Knopflerfish's ServiceReference ID for the digital contact sensor service
	

	private ServiceReference refContact = null;
	// Local reference to the contact sensor service
	//   used to subscribe to contact sensor data stream, manually pull readings, etc.
	private DigitalContactSensor sensorContact = null;
	

	//ameya
	//declared temp and humidity sensors
	private TemperatureSensor sensorTemp = null;
	private HumiditySensor sensorHumid = null;
	

	//ameya declared service references for humidity and temp sensors
	private ServiceReference refHumid = null;
	private ServiceReference refTemp = null;

	//ameya declared maps to use here
	Map<String, String> basicEvents = new HashMap<String, String>();
	Map<String,AtomicEvent> eventList=new ConcurrentHashMap<String,AtomicEvent>();
	//needed for concurrent modification of eventList
	Map<String,AtomicEvent> eventList2=new ConcurrentHashMap<String,AtomicEvent>();
	Map<String, String> nodeValues = new ConcurrentHashMap<String,String>();
	
	//define the condition map
	Map<String, Condition> runtimeConditions = new ConcurrentHashMap<String,Condition>();
	
	//define the actions map
	Map<String, Action> runtimeActions = new ConcurrentHashMap<String,Action>();
	
	//define the basic actions map
	Map<String, String> basicActions = new HashMap<String,String>();
	
	//define the rule map
	Map<String, Rule> rules = new HashMap<String,Rule>();
	
	//define a map here to store the servos that are online
	Map<String,HS322Servo> servoMap = new HashMap<String,HS322Servo>();
	
	//ameya:test variable here
	public boolean filled = false;
	
	//lock variable
	public Object lock;
	
	protected boolean isSwitchOn;

	public ReactiveEngine(BundleContext context, GUI gui, JTextArea jTextArea2) {
		this.jTextArea2 = jTextArea2;
		this.gui = gui;
		this.context = context;
		isSwitchOn = false;
	}
	
	//FIXME: Interface with middleware could be separated, probably

	// this method is called by the KitSampleApp bundle's Activator when any service running
	//   in Knopflerfish starts or changes
	//   sref is the Knopflerfish ServiceReference ID of the new/changed service
	//   dev is a direct reference to the new/changed service
	//     AtlasService is an interface implemented by all Atlas sensor and actuator
	//     services, and is what allows an AtlasClient implementor (like KitSampleApp)
	//     to bind with attached Atlas devices.
	public void addDevice(ServiceReference sref, AtlasService dev) {
		// if the pressure sensor service comes online, grab a reference to it,
		//   subscribe to its data stream, and update the pressure sensor
		//   service availability icon
		if (dev instanceof InterlinkPressureSensor) {
			refPressure = sref;
			sensorPressure = (InterlinkPressureSensor)dev;
			sensorPressure.subscribeToPressureData(this);
			//updateForce(true);
			
			//ameya added function to put this sensor and range into hashmap
			String nodeId = sref.getProperty("Node-Id").toString();
			addToBasicMap(nodeId,"Pressure","[0,1000]");
		}
		// if the servo service comes online, grab a reference to it and
		//   update the availability icon
		else if (dev instanceof HS322Servo) {
			refServo = sref;
			actuatorServo = (HS322Servo)dev;
			//updateServo(true);
			
			//ameya added action for this node in actions map
			String nodeId = sref.getProperty("Node-Id").toString();
			addToBasicActions(nodeId,"Servo","[0,180]");
			
			//also add this servo to the servomap
			addToServoMap(nodeId,actuatorServo);
		}
		// if the digital contact sensor service comes online, grab a reference to it,
		//   subscribe to its data stream, and update the availability icon
		else if (dev instanceof DigitalContactSensor) {
			writeStatusFile("Node ONLINE");
			refContact = sref;
			sensorContact = (DigitalContactSensor)dev;
			sensorContact.subscribeToContactData(this);
			//updateContact(true);
			
			//ameya added function to put this sensor and range into hashmap
			String nodeId = sref.getProperty("Node-Id").toString();
			addToBasicMap(nodeId,"Contact","[0,1]");
		}
		
		//ameya to detect temp sensors
		else if (dev instanceof TemperatureSensor){
			refTemp = sref;
			sensorTemp = (TemperatureSensor)dev;
			sensorTemp.subscribeToSensorData(this);
			System.out.println("Got a temp sensor");
			String nodeId = sref.getProperty("Node-Id").toString();
			addToBasicMap(nodeId,"Temperature","[-100,300]");
		}
		//ameya to detect temp sensors
		else if (dev instanceof HumiditySensor){
			refHumid = sref;
			sensorHumid = (HumiditySensor)dev;
			sensorHumid.subscribeToSensorData(this);
			System.out.println("Got a humid sensor");
			String nodeId = sref.getProperty("Node-Id").toString();
			addToBasicMap(nodeId,"Humidity","[0,100]");
		}
	}	

	// this method is called by the KitSampleApp bundle's Activator when any service
	//   running in Knopflerfish goes offline
	//   sref is the Knopflerfish ServiceReference ID of the departing service
	//   because the service has already been unbound in OSGi, we cannot do the
	//   "instanceof" check like in addDevice (the "dev" parameter in that
	//   method would be null here). This is why addDevice must record the
	//   ServiceReference.
	public void removeDevice(ServiceReference sref) {
		// if the pressure sensor service goes offline, clear out the local
		//   references and update the service availability icon and readings
		if (sref == refPressure) {
			refPressure = null;
			sensorPressure = null;
			//updateForce(false);
		}
		// if the servo actuator service goes offline, clear out the local
		//   references and update the service availability icon and UI
		else if (sref == refServo) {
			refServo = null;
			actuatorServo = null;
			//updateServo(false);
		}
		// if the digital contact sensor service goes offline, clear out the local
		//   references and update the availability icon and reading
		else if (sref == refContact) {
			writeStatusFile("Node OFFLINE");
			refContact = null;
			sensorContact = null;
			//updateContact(false);
		}
		
		//ameya added remove of humid and temp sensors
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
	//   bundle to which the application has subscribed (in this case,
	//   the pressure sensor service and the digital contact sensor
	//   service).
	// data contains the reading produced by the sensor
	// props contains information about the sensor that produced the data
	//   (name, label, channel to which the device is connected, etc.
	public void ReceivedData(String data, Properties props) {
		// write the received data to the Knopflerfish console
		//   this can get rather busy as the two sensors rapidly stream
		//   data, so this is commented out by default
		//System.out.println("Received data: " + data);
		//System.out.println("AMEYA" + props.toString() + "Data :" + data);
		
		//this is a dummy code just to fill the eventList. this will later come from the user interface. the maps will be populated via user input
//		if (filled == false){
//			//fill();
//			filled = true;
//			//System.out.println("eventlist is now:");
////			Iterator<AtomicEvent> i = eventList.values().iterator();
////			AtomicEvent nodeids;
////			while(i.hasNext()){
////				nodeids = i.next();
////				//System.out.println("Events:" + nodeids.toString());
////			}
//			System.out.println("Basic op is here");
//			showMap(basicEvents);
//			showMap(basicActions);
//			showMap(runtimeActions);
//			showMap(runtimeConditions);
//			showMap(rules);
//			System.out.println("Basic op end");
//		}
		
		
//	this is just a test snippet to show the contents of the basic events map
//		try {
//			showMap();
//		}
//		catch(Exception e){
//			System.out.println(e.getMessage());
//		}
		
		String sensorMeasure = new String("Unknown");
		String[] keysarr = new String[props.size()];
		props.keySet().toArray(keysarr);
		
		// the sensor service bundles are configured so that the
		//   "measure-type" property will identify the sensor for this application
		if(props.containsKey("measure-type"))
			sensorMeasure = props.getProperty("measure-type");
		int sensorReading = Integer.parseInt(data);

		//ameya: inserted code here to update event values on receiving this data
		String nodeId = props.getProperty("Node-Id");
		//update the node value in the nodeValues. nodeValues is a Map which stores all the values of the nodes online now.
		//a value is updated every time data is received
		if(nodeValues.containsKey(nodeId)){
				nodeValues.remove(nodeId);
		}
		nodeValues.put(nodeId, data.toString());
		
		
//		System.out.println("Current node values are here:");
//		showMap(nodeValues);
		
		//check and update the events that may have changed. actually a new thread can be spawned to do this. need to check feasibility of this
		//also if performance takes a hit, we might need to implementing a queue. need not be serialized cause this recvdData method will 
		//write to the queue and our thread will read from it.
		if (run==1){
			updateEvents();
			//evaluate and trigger any rules
			checkRules();
		}
		
		
		//ameya: code end
		
		
		// if the reading comes from the pressure sensor, update the 
		//   "force meter" (progress bar)
		if (sensorMeasure.equalsIgnoreCase("pressure")) {
			//forceOutput.setValue(sensorReading);
			//forceOutput.revalidate();
		}
		// if the reading comes from the digital contact sensor, update
		//   its icon and description appropriately 
		else if (sensorMeasure.equalsIgnoreCase("contact")) {
			// (1 means the contact sensor is unpressed)
			if (sensorReading == 1) {
	            if (isSwitchOn) {
	            	//updateContactReading(false);
		            isSwitchOn = false;
	            }
			}
			// (0 [the only other value a digital sensor allows] means the sensor is pressed)
			else {
	            if (!isSwitchOn) {
	            	//updateContactReading(true);
		            isSwitchOn = true;
	            }
			}
		}
	}
	
	protected void writeStatusFile(String s) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream("C:\\reboot_test_1.txt", true));
			ps.println(System.currentTimeMillis() + ": " + s);
		}
		catch (IOException ioe) {
			System.out.println("Could not open status file: " + System.currentTimeMillis());
		}
	}
	
	//ameya: added functions here for custom processing
	
	public void addToServoMap(String nodeid, HS322Servo servo){
		System.out.println("Servo " + nodeid);
		servoMap.put(nodeid, servo);
	}
	public void addToBasicMap(String nodeid, String type, String range){
		basicEvents.put(nodeid, nodeid+","+type+","+range);
	}
	
	public void addToBasicActions(String nodeid, String type, String range){
		basicActions.put(nodeid, "Move Servo " + nodeid+", "+range);
	}
	//function to display the values in a map
	public void showMap(Map<String, AtomicEvent> basicEvents){
		System.out.println("Map is now:");
		Iterator<AtomicEvent> i = basicEvents.values().iterator();
		String nodeids;
		while(i.hasNext()){
			nodeids = i.next().toString();
			System.out.println(nodeids);
		}
	}
	
	//FIXME: Engine logic rules could be separated
	
	//dummy function to fill the eventList with samples to test. look at the node id in knopplerfish console and modify it here.
	//the events will then have values T or F. the node id's will come from the user input directly into this map
	public void fill()
	{
		AtomicEvent a;
		a = new AtomicEvent("e1","","W47(500)");
		eventList.put("e1",a);
		a = new AtomicEvent("e2","", "S42[400,600]");
		eventList.put("e2", a);
		a = new AtomicEvent("e3", "j56(60)+e1*e2", "gh4(60)+W47(500)*5*S42[400,600]");
		eventList.put("e3", a);
		
		//need to fill these up. this will be done by user interface
		Action act;
		act = new Action("a1","servo_L81(30)","");
		runtimeActions.put("a1", act);
		act = new Action("a2","servo_T5(45)","");
		runtimeActions.put("a2", act);
		act = new Action("a3","servo_L81(30);servo_T5(45)","a1;a2");
		runtimeActions.put("a3", act);
		
		Rule r;
		r = new Rule("r1","e1","c1","a1");
		rules.put("r1", r);
		
		r = new Rule("r2","e2","c2","a2");
		rules.put("r2", r);
		
		Condition c;
		
		c = new Condition("c1","true");
		runtimeConditions.put("c1", c);
		
		c = new Condition("c2",true);
		runtimeConditions.put("c2", c);
		
		c = new Condition("c3","true");
		runtimeConditions.put("c3", c);
	}
	
	//function evaluates a composite event, something like e1*seconds*e2
	public boolean evaluateCompositeEvent(String expr){
		String event[];
		String event1;
		String event2;
		String timeDiff;
		event = expr.split("&");
		event1 = event[0];
		timeDiff = event[1];
		event2 = event[2].split("&")[0];
		AtomicEvent ev;
		boolean event1Truth = false,event2Truth = false;
		long event1Time = 0,event2Time = 0;
		long timeDuration = Long.parseLong(timeDiff);
		
		Iterator<AtomicEvent> evItr = eventList.values().iterator();
		
		while (evItr.hasNext()) {
			ev = evItr.next();
			System.out.println("Got new event " + ev.expansion + " Checking with " + event1 + " and " + event2);
			if (ev.expansion.equalsIgnoreCase(event1)){
				event1Time = ev.startTime;
				event1Truth = ev.getTruthValue();
				System.out.println("Got event1 value as " + event1Truth + ":" + event1Time);
				continue;
			}
			if (ev.expansion.equalsIgnoreCase(event2)){
				event2Time = ev.startTime;
				event2Truth = ev.getTruthValue();
				System.out.println("Got event2 value as " + event2Truth + ":" + event2Time);
				continue;
			}
		}
		System.out.println("Got values as : " + event1Truth + ":" + event1Time + " for event1 and " + event2Truth + ":" + event2Time);
		if (event1Truth == true && event2Truth == true) {
			if ( ((event2Time - event1Time) <= (timeDuration*1000)) && (event1Time!=0 && event2Time!=0)){
				System.out.println("Time less!!! diff is " + (event2Time - event1Time) + " needed is " + (timeDuration*1000));
				return true;
			}
		}
		return false;
	}
	//function evaluates the truth value of an expression like q11(30) or q23[20,50]. returns true or false 
	public boolean evaluate(String expr){
		//assume u get someting like q11(30) or q23[20,50]
		//split accordingly and evaluate using nodeValues hashtable which contains
		//values of readings for all nodes
		String nodeId;
		String valueLower;
		String valueHigher;
		String value;
		String sensorValue;
		StringTokenizer strTok = new StringTokenizer(expr,"()[]");
		StringTokenizer rangeTok = null;
		int sensorVal = 0;
		
		//System.out.println("Evaluating: " + expr);
		try {
			if (expr.contains("&")){
				//handle *seconds* type events here
				return evaluateCompositeEvent(expr);
			}
			else if (!expr.contains(",")){
				nodeId = strTok.nextToken();
				value = strTok.nextToken();
				sensorValue = nodeValues.get(nodeId);
				if (sensorValue == null){
					return false;
				}
				//System.out.println("Sensor val:" + Integer.parseInt(sensorValue) + " " + nodeId + ":" + value);
				
				if (Integer.parseInt(value) == Integer.parseInt(sensorValue)){
					return true;
				}
				else {
					return false;
				}
			}
			else {
				nodeId = strTok.nextToken();
				value = strTok.nextToken();
				rangeTok = new StringTokenizer(value,",");
				valueLower = rangeTok.nextToken();
				valueHigher = rangeTok.nextToken();
				sensorValue = nodeValues.get(nodeId);
				if (sensorValue == null){
					return false;
				}
				else {
					sensorVal = Integer.parseInt(sensorValue);
				}
				//System.out.println("Sensor val:" + sensorVal + " " + nodeId + ":" + valueLower + ":" + valueHigher);
				
				if (sensorVal > Integer.parseInt(valueLower) && sensorVal < Integer.parseInt(valueHigher)){
					return true;
				}
				else {
					return false;
				}
			}
		
		}
		catch (Exception e){
			System.out.println("ERROR: " + expr);
			e.printStackTrace();
			return false;
		}
			
		//return false;
	}
	
	//function replaces like q11(30) or q23[20,50] with T or F. this input string consisting of T and F will then be passed to parse
	public String replaceWithTruthValues(String expr){
		//while replacing, you may not have the values of all sensors, the ones that 
		//did not send data yet. make sure you check this
		
		//split expr by +, * 
		//pass each split to evaluate function to get truth value
		//if u get e*sec*e then directly put truth value
		String token;
		
		int i = 0,start = 0,end = 0;
		char ch;
		StringBuilder str = new StringBuilder(expr);
		while(i<str.length()){
			ch = str.charAt(i);
			if (ch=='*'){
				start = i;
				i++;
				if (Character.isDigit(str.charAt(i))){
					while (Character.isDigit(str.charAt(i))){
						i++;
					}
					if (str.charAt(i)!='*'){
						System.out.println("Unexpected character " + str.charAt(i) + " in string " + str + " Quitting...");
						System.exit(0);
					}
					else {
						end = i++;
						str.setCharAt(start, '&');
						str.setCharAt(end, '&');
					}
				}
				else {
					continue;
				}
			}
			else {
				i++;
				continue;
			}
			
		}
		
		//System.out.println("In replaceWithTruthValues::Got input: " + expr);
		String truthExpr = str.toString();
		expr = str.toString();
		StringTokenizer strTok = new StringTokenizer(expr,"+*");
		while(strTok.hasMoreTokens()){
			token = strTok.nextToken();
			//System.out.println("Sent for eval: "+ token);
			boolean tVal = evaluate(token);
			if (tVal==true){
				truthExpr = truthExpr.replace(token, "T");
			}
			else if (tVal == false){
				truthExpr = truthExpr.replace(token, "F");
			}
			else {
				//this should be a digit (*30* etc types) ignore
				continue;
			}
			//System.out.println("New truthexpr is: " + truthExpr);
		}
		//System.out.println("Returning from replaceWithTruthValues:" + truthExpr);
		return truthExpr;
	}
	
	//function determines the value of operations like T*F or T+T etc
	public char atomicTruthEval(char op1,char op2, char opr){
		if (opr == '*'){
			if(op1 == op2 && op1== 'T'){
				return 'T';
			}
			else {
				return 'F';
			}
		}
		else if (opr == '+'){
			if (op1 == 'T' || op2 == 'T'){
				return 'T';
			}
			else {
				return 'F';
			}
		}
		else {
			return 'F';
		}
	}
	
	//function parses the input string using a stack and returns a single truth value of the event.
	
	public boolean parseEventValues(String expr){
		//use stack to shift and reduce to a single truth value
		//input contains only T,F,*,+
		Stack<Character> stack = new Stack<Character>();
		StringBuilder input = new StringBuilder(expr);
		char[] inputChar = new char[1];
		char op1;
		char op2;
		char opr;
		try {
			//i = 0;
			for(int i=0;i<input.length();i++){
				inputChar[0] = input.charAt(i);
				//System.out.println("Char is :" + inputChar[0]);
				switch(inputChar[0]){
					case 'T': {
						stack.push(inputChar[0]);
						//System.out.println("Pushed:" + inputChar[0]);
						break;
					}
					case 'F': {
						stack.push(inputChar[0]);
						//System.out.println("Pushed:" + inputChar[0]);
						break;
					}
					case '+': {
						stack.push(inputChar[0]);
						//System.out.println("Pushed:" + inputChar[0]);
						break;
					}
					case '*': {
						//op1 = 'T';
						//op1 = stack.pop()[0];
						op1  = stack.pop();
						//op1 = test[0];
						//System.out.println("POP:" + op1);
						i++;
						op2 = input.charAt(i);
						//System.out.println("Got *, Popped and sent:" + op1 + ":" + op2);
						op2 = atomicTruthEval(op1,op2,'*');
						//System.out.println("Got truth val as:" + op2);
						inputChar[0] = op2;
						stack.push(inputChar[0]);
						//System.out.println("Pushed:" + inputChar[0]);
						break;
					}
				}
				
			}
			
			//System.out.println("Begin Eval in stack");
			while (stack.size()>1){
				op1 = stack.pop();
				opr = stack.pop();
				op2 = stack.pop();
				
				//System.out.println("Popped and sent:" + op1 + ":" + op2 + ":" + opr);
				inputChar[0] = atomicTruthEval(op1, op2, opr);
				//System.out.println("Got truth val as:" + inputChar[0]);
				
				stack.push(inputChar[0]);
			}
			
			op1 = stack.pop();
			
			if (op1 == 'T'){
				//System.out.println("Returning T");
				return true;
			}
			else {
				//System.out.println("Returning F");
				return false;
			}
		}
		catch (Exception e){
			System.out.println("Error Parsing");
			return false;
		}
		
	}
	

	//this is the function called in receivedData method which triggers the checking of rules (events)
	public void updateEvents(){
		//get the sensor reading here and update all events in the 
		//eventsList hashtable
		String truthValue;
		boolean eventValue = false;
		AtomicEvent a;
		Collection<AtomicEvent> c = eventList.values();
		Iterator<AtomicEvent> kItr = c.iterator();
		//eventList.clear();
		while(kItr.hasNext()){
			a = kItr.next();
			if (a != null){
				//System.out.println("Sending Expansion: " + a.expansion);
				truthValue = replaceWithTruthValues(a.expansion);
				eventValue = parseEventValues(truthValue);
				if (a.value != eventValue){
					System.out.println("Updated value of " + a.expansion + " to " + eventValue);
					a.value = eventValue;
					a.startTime = Calendar.getInstance().getTimeInMillis();
				}
				eventList2.put(a.name, a);
				//System.out.println("Value of "+ a.expansion + " is " + eventValue);
			}
		}
		eventList.clear();
		eventList.putAll(eventList2);
		System.out.println("After updating events, map is:");
		showMap(eventList);
	}
	
	
	//This function checks if any rules now need to be fired after the event updates
	public void checkRules() {
		Iterator<String> rItr = rules.keySet().iterator();
		String ruleid;
		Rule rule;
		while (rItr.hasNext()){
			ruleid = rItr.next();
			rule = rules.get(ruleid);
			evaluateRule(rule);
		}
	}
	
	//This function moves the servo specified by the nodeid to a new position
	public void moveServo(String nodeId, int move) {
		System.out.println("Move servo " + nodeId + " by " + move);
		try {
			move = ((move * 100) / 180 ) + 1;
			servoMap.get(nodeId).moveServo(move);
		}
		catch (NullPointerException ex){
			System.out.println("Servo is not in the servo map!");
		}
		catch (Exception e) {
			System.out.println("Error moving servo " + nodeId + " by " + move);
			e.printStackTrace();
		}
	}
	//ameya:functions end
	
	
	
	//rakesh function s begin
	
	//FIXME: Command parsing stuff could be separated.
	
	
	
	 void listCommand(String str)
	    {
	        String trimString=str.trim();
	        String strpp[]=trimString.split("\\s");
	        StringBuffer str1=new StringBuffer();
	        String str5;
	        AtomicEvent a;
	        if(strpp.length>2)
	        {
	        	 JOptionPane.showMessageDialog(gui, "Invalid usage of LIST");
		            return;
	        }
	        if((trimString.endsWith("LIST"))&&(strpp.length<2))
	        {
	        	str1.append("\n----BASIC EVENTS----\n");
	            
	        	for(Map.Entry<String,String> e : basicEvents.entrySet())
	            {
	                 str1.append(e.getValue()+"\n");
	                    
	            }
	        	str1.append("\n----BASIC ACTIONS----\n");
	        	
	            for(Map.Entry<String,String> p: basicActions.entrySet())
	            {
	                str1.append(p.getValue()+"\n");
	            }
	        	
	        	
	        	
	        	
	        	str1.append("\n----USER DEFINED EVENTS----\n");
	        	
	        	for(Map.Entry<String,AtomicEvent> e : eventList.entrySet())
	            {
	                str5=e.getKey();
	                a=e.getValue();
	                if(a.expression==null || a.expression=="")
	                    str1.append(str5+"="+a.expansion+"\n");
	                else
	                    str1.append(str5+"="+a.expression+"\n");
	                    
	            }
	       
	            Condition print;
	            
	            str1.append("\n----SET CONDITIONS----\n");
	            for(Map.Entry<String,Condition> e : runtimeConditions.entrySet())
	            {
	                print=e.getValue();
	                str1.append(e.getKey()+"="+print.value+"\n");  
	            }
	            Action apo;
	            str1.append("\n----USER DEFINED ACTIONS----\n");
	            for(Map.Entry<String,Action> e: runtimeActions.entrySet())
	            {
	                apo=e.getValue();
	                str1.append(e.getKey()+"="+apo.actionDisplay+"\n");
	            }
	             Rule  rpo;
	             str1.append("\n----DEFINED RULES----\n");
	            for(Map.Entry<String,Rule> e: rules.entrySet())
	            {
	                rpo=e.getValue();
	                str1.append(e.getKey()+"="+rpo.event+","+rpo.condition+","+rpo.action+"\n");
	            }  
	        }
	        else if(strpp[1].matches("event"))
	        {
	        		str1.append("\n----BASIC EVENTS----\n");
	            
	        	for(Map.Entry<String,String> e : basicEvents.entrySet())
	            {
	                 str1.append(e.getValue()+"\n");
	                    
	            }
	        	str1.append("\n----USER DEFINED EVENTS----\n");
	           for(Map.Entry<String,AtomicEvent> e : eventList.entrySet())
	            {
	                str5=e.getKey();
	                a=e.getValue();
	                if(a.expression==null || a.expression=="")
	                    str1.append(str5+"="+a.expansion+"\n");
	                else
	                    str1.append(str5+"="+a.expression+"\n");
	                    
	            }
	        }
	        else if(strpp[1].matches("condition"))
	        {
	            Condition print;
	            str1.append("\n----SET CONDITIONS----\n");
	            for(Map.Entry<String,Condition> e : runtimeConditions.entrySet())
	            {
	                print=e.getValue();
	                str1.append(e.getKey()+"="+print.value+"\n");  
	            }
	        }
	        else if(strpp[1].matches("action"))
	        {
	            Action app;
	            
	            str1.append("\n----BASIC ACTIONS----\n");
	        	
	            for(Map.Entry<String,String> p: basicActions.entrySet())
	            {
	                str1.append(p.getValue()+"\n");
	            }
	        	
	            str1.append("\n----USER DEFINED ACTIONS----\n");
	            for(Map.Entry<String,Action> e: runtimeActions.entrySet())
	            {
	                app=e.getValue();
	                str1.append(e.getKey()+"="+app.actionDisplay+"\n");
	            }
	        }
	        else if(strpp[1].matches("rule"))
	        {
	            Rule rp;
	            str1.append("\n----USER DEFINE RULES----\n");
	           for(Map.Entry<String,Rule> e: rules.entrySet())
	            {
	               rp=e.getValue();
	                str1.append(e.getKey()+"="+rp.event+","+rp.condition+","+rp.action+"\n");
	            }  
	        }
	        else
	        {
	            JOptionPane.showMessageDialog(gui, "Invalid usage of List");  
	            return;
	        }
	        String str3=str1+"\n";
	        jTextArea2.setText(str3); 
	        
	    }
	    
	    void basicCommand(String str)
	    {
	        String trimString=str.trim();
	        String strpn[]=trimString.split("\\s");
	         StringBuffer str4=new StringBuffer();
	        if(strpn.length>2)
	        {
	        	 JOptionPane.showMessageDialog(gui, "Invalid usage of BASIC");
		            return;
	        }
	        if(trimString.endsWith("BASIC")&&(strpn.length<2))
	        {
	        	 str4.append("\n\n\n----BASIC EVENTS----\n");
	            for(Map.Entry<String,String> e : basicEvents.entrySet())
	            {
	                 str4.append(e.getValue()+"\n");
	                    
	            }
	            str4.append("\n\n\n----BASIC ACTIONS----\n");
	            for(Map.Entry<String,String> p: basicActions.entrySet())
	            {
	                str4.append(p.getValue()+"\n");
	            }
	        }
	        
	        
	        else if(strpn[1].matches("event"))
	        {
	        	 str4.append("\n\n\n----BASIC EVENTS----\n");
	             for(Map.Entry<String,String> e : basicEvents.entrySet())
	            {
	                 str4.append(e.getValue()+"\n");
	                    
	            }
	        }
	        
	        else if(strpn[1].matches("action"))
	        {
	            for(Map.Entry<String,String> p: basicActions.entrySet())
	            {
	            	 str4.append("\n\n\n----BASIC ACTIONS----\n");
	                str4.append(p.getValue()+"\n");
	            }
	        }
	        else
	        {
	            JOptionPane.showMessageDialog(gui, "Invalid usage of BASIC");
	            return;
	        }
	        String str6=str4+"\n";
	        jTextArea2.setText(str6);
	    }
	    
	    
	    
	    String evaluateAtomicEvent(String s)
	    {
	        String k;
	        AtomicEvent a;

	        for(Map.Entry<String,AtomicEvent> p: eventList.entrySet())
	        {
	            k=p.getKey();
	            a=p.getValue();
	            if(k.matches(s))
	            {
	                return a.expansion;
	            }
	        }
	        return "invalid";
	    }
	            
	    String evaluateAction(String s)
	    {
	        String k;
	        Action a;
	        for(Map.Entry<String,String> p: basicActions.entrySet())
	        {
	            k=p.getKey();
	            if(k.matches(s))
	                return k;
	        }
	        for(Map.Entry<String,Action> p: runtimeActions.entrySet())
	        {
	            k=p.getKey();
	            if(k.matches(s))
	            {
	                a=p.getValue();
	                return a.actionList;
	            }
	        }
	        return "invalid";
	    }
	         
	    
	    
	    
	    void defineCommand(String str)
	    {
	        System.out.println("DEBUGG");
	        str = str.trim();
	        if(run==0)
	        {

	            System.out.println("IN RUN " + str);
	            String strsplit[]=str.split("\\s");
	            
	            String returnString;
	            StringBuffer appendString=new StringBuffer();
	          /*  while(s[l]=='D')
	            {
	                l++;
	            }
	           
	            while(s[l]!=' ')
	            {
	             l++;   
	            }
	            String substr=str.substring(l);*/
	            
	            System.out.println(strsplit[0]+" str split here "+ strsplit[1]+"\n\n\n\n\n");
	            if(strsplit[1].matches("event"))
	            {
	                int atom=0;
	                String strspl[]=str.split("=");
	              // System.out.println(strspl[0]+"\n"+strspl[1]);
	                String strsl[]=strspl[0].split("\\s");
	                if(strspl.length<2)
	                {
	                  JOptionPane.showMessageDialog(gui, "Invalid usage of Define event");
	                    return;  
	                }
	                if(strspl.length>2)
	                {
	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define event");
	                return;
	                }
	                StringTokenizer strtokensp=new StringTokenizer(strspl[1],"+*");
	                List<String> strtokens=new ArrayList<String>();
	                  while (strtokensp.hasMoreTokens()) 
	                 {
	                    strtokens.add(strtokensp.nextToken());
	                 }
	                char temp[]=strspl[1].toCharArray();
	                List<String> plustar=new ArrayList<String>();
	                for(int r=0;r<temp.length;r++)
	                {
	                    if((temp[r]=='+')||(temp[r]=='*'))
	                    {
	                        plustar.add(temp[r]+"");
	                        
	                    }
	                }
	                plustar.add("|");
	                 String pp;
	                 int yes=0;
	                for(int i=0;i<strtokens.size();i++)
	                {
	                    if(strtokens.get(i).contains("(")||strtokens.get(i).contains("["))
	                    {
	                        for(Map.Entry<String,String> p: basicEvents.entrySet())
	                        {
	                            pp=p.getKey();
	                            if(strtokens.get(i).contains(pp))
	                                yes=1;
	                        }
	                            if(yes==1){}
	                            else
	                            {
	                                JOptionPane.showMessageDialog(gui, "Servo with specified node ID not present"); 
	                                 return;
	                            }
	                        
	                    }
	                    else
	                    {
	                        atom=1;
	                        break;
	                    }
	                   
	                   
	                }
	                if(atom==0)
	                    strspl[1]=null;
	                for(int i=0;i<strtokens.size();i++)
	                {
	                    if(strtokens.get(i).contains("(")||strtokens.get(i).contains("["))
	                    {
	                        
	                    }
	                    else
	                    {
	                    	
	                    	//FIXME: Weird try catch block.  Why not an if then??
	                    	try{
	                            int k=Integer.parseInt(strtokens.get(i));
	                            continue;
	                        }
	                    
	                        catch(Exception e){
	                        returnString=evaluateAction(strtokens.get(i));
	                        if(returnString.matches("invalid"))
	                        {
	                            JOptionPane.showMessageDialog(gui, strtokens.get(i)+"is not defined");
	                            return;
	                        }
	                        strtokens.set(i,returnString);
	                        }
	                    }
	                    
	                   
	                }
	                for(int i=0;i<strtokens.size();i++)
	                {
	                    appendString.append(strtokens.get(i));
	                    appendString.append(plustar.get(i));
	                }
	                String append1=appendString.substring(0, (appendString.length()-1));
	                AtomicEvent a=new AtomicEvent(strsl[2],strspl[1],append1);
	                eventList.put(strsl[2], a);
	                
	            }
	            
	            
	            
	            
	            
	            
	            
	            
	            else if(strsplit[1].matches("condition"))
	            {
	                
	                String strspl[]=str.split("=");
	                String strsl[]=strspl[0].split("\\s");
	                if(strspl.length<2)
	                {
	                    JOptionPane.showMessageDialog(gui, "Invalid usage of Define Condition");
	                    return;
	                }
	                else if(strspl.length>2)
	                {
	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define Condition");
	                return;
	                }
	                else if(strspl[1].trim().matches("true"))
	                {
	                    Condition ins=new Condition(strsl[2],true);
	                    runtimeConditions.put(strsl[2], ins);
	                }
	                else if(strspl[1].trim().matches("false"))
	                {
	                    Condition ins1=new Condition(strsl[2],false);
	                   runtimeConditions.put(strsl[2], ins1);
	                }
	                else
	                {
	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define Condition,can only take true or false");
	                return;
	                }
	                
	                
	            }
	            
	            else if(strsplit[1].matches("action"))
	            {
	                String strspl[]=str.split("=");
	                String strsl[]=strspl[0].split("\\s");
	                if(strspl.length>2)
	                {
	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define action");
	                return;
	                }
	                String strtokens[]=strspl[1].split(";");
	                
	                System.out.print("A\n");
	                 String pp;int yes=0;      
	                for(int i=0;i<strtokens.length;i++)
	                {
	                    System.out.println("ff\n");
	                    if(strtokens[i].contains("(")||strtokens[i].contains("["))
	                    {
	                        System.out.println("ppp");
	                         for(Map.Entry<String,String> p: basicActions.entrySet())
	                        {
	                            pp=p.getKey();
	                          //  String s1[]=strtokens[i].split("_");
	                            //String s2[]=s1[1].split("(");
	                            if(strtokens[i].contains(pp))
	                                yes=1;
	                         }
	                            if(yes==1){}
	                            else
	                            {
	                                JOptionPane.showMessageDialog(gui, "Servo with specified node ID not present"); 
	                                 return;
	                            }
	                        
	                        //strtokens[i]=returnString;
	                    }
	                    else
	                    {
	                        returnString = evaluateAtomicEvent(strtokens[i]);
	                        if(returnString.matches("invalid"))
	                        {
	                            JOptionPane.showMessageDialog(gui, strtokens[i]+"is invalid action, first define it"); 
	                            return;
	                        }
	                        else
	                            strtokens[i]=returnString;
	                       
	                    }
	                }
	                 
	                
	                  System.out.println("C\n");
	                  for(int i=0;i<strtokens.length;i++)
	                {
	                    appendString.append(strtokens[i]+";");
	                }
	                 System.out.println("B\n");
	                String appendString1=appendString.substring(0, (appendString.length()-1));
	                Action a=new Action(strsl[2],appendString1,strspl[1]);
	                runtimeActions.put(strsl[2], a);
	                    
	            }
	            else if(strsplit[1].matches("rule"))
	            {
	                
	               String strspl[]=str.split("=");
	                String strsl[]=strspl[0].split("\\s");
	                if(strspl.length>2)
	                {
	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define rule Condition");
	                return;
	                }
	                else 
	                {
	                    String strclass[]=strspl[1].split(",");
	                    	if(strclass.length>3){
	    	                JOptionPane.showMessageDialog(gui, "Invalid usage of Define rule Condition");
	    	                return;
	    	                }
	                    	if(strclass.length<3)
	                    	{
	                    		JOptionPane.showMessageDialog(gui, "Invalid usage of Define rule Condition");
		    	                return;
	                    	}
	                    int check=0,check1=0,check2=0;
	                    String str5;
	                    
	                    for(Map.Entry<String,AtomicEvent> e : eventList.entrySet())
	                    {
	                        str5=e.getKey();
	                       if(str5.matches(strclass[0]))
	                       {
	                           check=1;
	                           break;
	                       }
	                                         
	                    }
	       
	        
	            for(Map.Entry<String,Condition> e : runtimeConditions.entrySet())
	            {
	                  str5=e.getKey();
	                  if(str5.matches(strclass[1]))
	                  {
	                      check1=1;
	                      break;
	                  }
	            }
	            
	            for(Map.Entry<String,Action> e: runtimeActions.entrySet())
	            {
	                str5=e.getKey();
	                if(str5.matches(strclass[2]))
	                {
	                    check2=1;
	                    break;
	                }
	            }
	             
	                    if((check==1)&&(check1==1)&&(check2==1))
	                    {
	                        Rule r=new Rule(strsl[2],strclass[0],strclass[1],strclass[2]);
	                        rules.put(strsl[2],r);
	                    }
	                    else
	                    {
	                        JOptionPane.showMessageDialog(gui, "Invalid rule - verify the entered event,condition and action");
	                        return;
	                    }
	                }
	            }
	            else
	             {
	                 JOptionPane.showMessageDialog(gui, "Invalid usage of Define");
	                 return;
	            }
	        } 
	        else
	        {
	            JOptionPane.showMessageDialog(gui, "RUN Mode is on, Stop and Define");
	            return;
	        }
	    }
	    
	    void setCommand(String str)
	    {
	        /*String trimString=str.trim();
	         char c[]=str.toCharArray();
	        StringBuffer str7=new StringBuffer();
	        StringBuffer str8=new StringBuffer();
	        int i=0;
	        while(c[i]!=' ')
	        {
	            i++;
	        }
	       i++;
	        while(c[i]!='=')
	        {
	            
	            str7.append(c[i]);
	            System.out.println(str7);
	            i++;
	            
	        }
	        i++;
	        for(;i<(str.length());i++)
	        {
	          str8.append(c[i]);
	          System.out.println(str8);
	        }
	        
	        
	        String str9=str7+"";
	        String str10=str8+"";*/
	    	str=str.trim();
	        String cond[]=str.split("=");
	        String  condclass[]=cond[0].split("\\s");
	        String chk;
	        int check=0;
	         for(Map.Entry<String,Condition> e : runtimeConditions.entrySet())
	            {
	                chk=e.getKey();
	                if(condclass[1].matches(chk))
	                {
	                    check=1;
	                    break;
	                }
	            }
	        
	        if(check==1)
	        {
	            if(cond[1].matches("true"))
	            {
	                Condition a =new Condition(condclass[1],true);
	                runtimeConditions.put(condclass[1], a);
	            }
	                
	            else if(cond[1].matches("false"))
	            {
	                Condition a1=new Condition(condclass[1],false);
	                runtimeConditions.put(condclass[1],a1);
	            }
	            else
	            {
	                 JOptionPane.showMessageDialog(gui, "Condition variable can accept only either true or false");
	                 return;
	            }
	       }
	        else
	        {
	              JOptionPane.showMessageDialog(gui, "Condition variable not present,please define it first");
	              return;
	        }
	        
	        
	        
	        }
	            
	   void runCommand(String str)
	   {
	       if(run==1)
	       {
	           JOptionPane.showMessageDialog(gui, "RUN mode is already on");
	           return;
	       }
	       else
	       {
	       run=1;
	       }
	       
	   }
	    
	    void stopCommand(String str)
	    {
	       if(run==0)
	        {
	            JOptionPane.showMessageDialog(gui, "RUN mode already off");
	            return;
	        }
	       else
	       {
	        run=0;
	       }
	    }        
	        
	    
	    
	public void  parse(String str)
	{
	   String trimString=str.trim();
	   String strp[]=trimString.split("\\s");
	    if(strp[0].matches("LIST"))
	    {
	       listCommand(str);
	    }
	    
	    else if(strp[0].matches("BASIC"))
	    {
	       basicCommand(str);
	    }
	    
	    else if(strp[0].matches("DEFINE"))
	    {
	       defineCommand(str);
	    }
	    
	    else if(strp[0].matches("SET"))
	    {
	       setCommand(str);
	    }
	    else if(strp[0].matches("RUN"))
	    {
	       runCommand(str);
	    }
	    else if(strp[0].matches("STOP"))
	    {
	        stopCommand(str);
	    }
	    
	    else if(strp[0].matches("LOAD"))
	    {
	        String strload[]=trimString.split("\\s");
	        StringBuffer filecont=new StringBuffer();
	        int q=0;
	       // FileInputStream fis=new FileInputStream(strload[1]);
	        
	        try
	        {    
	            BufferedReader br=new BufferedReader(new FileReader(strload[1]));
	            q=br.read();
	            while(q!=-1)
	            {
	                filecont.append((char)q);
	                q=br.read();
	            }
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	              JOptionPane.showMessageDialog(gui, "FILE NOT FOUND");        
	           return;
	        }
	        String temp=filecont+"";
	        StringTokenizer strtokensp1=new StringTokenizer(temp.trim(),"?\n");
	        List<String> strtokens1=new ArrayList<String>();
	        while (strtokensp1.hasMoreTokens()) 
	         {
	             strtokens1.add(strtokensp1.nextToken());
	         }
	        for(int g=0;g<strtokens1.size();g++)
	        {
	            System.out.println(strtokens1.get(g));
	             
	            parse(strtokens1.get(g));
	        }
	    }
	    else
	    {
	           JOptionPane.showMessageDialog(gui, "Invalid Command");        
	           return;
	    }

	
	}
	
	public void evaluateRule(Rule rule){
		try {
			boolean eventVal = eventList.get(rule.event).getTruthValue();
			System.out.println("Rule Eval:" + rule.name + ":" + eventVal);
			if (eventVal == true){
				//event has occured
				//check conditions here
				boolean condVal = runtimeConditions.get(rule.condition).getValue();
				if (condVal) {
					//condition is true, trigger action
					performAction(runtimeActions.get(rule.action));
				}
			}
		}
		catch(NullPointerException ne){
			System.out.println("Condition, event or action was not found in the maps!");
			ne.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void takeAction(String command) {
		System.out.println("Taking Action:" + command);
		StringTokenizer strTok = new StringTokenizer(command,"_()");
		String nodeId;
		String movement;
		double move;
		try {
			nodeId = strTok.nextToken();
			movement = strTok.nextToken();
			move = Integer.parseInt(movement);
			//FIXME this should probably be contained better
			
			//servo is the only actuator, hence this call. Else need a class hierarchy
			//of actuators and fire the appropriate methods
			//move = (move*100)/180;
			moveServo(nodeId,(int)move);
		}
		catch(Exception e) {
			System.out.println("Invalid action: " + command);
		}
		
	}
	
	public void performAction(Action action){
		try {
			StringTokenizer strTok = new StringTokenizer(action.actionList,";");
			while (strTok.hasMoreTokens()) {
				takeAction(strTok.nextToken());
			}
		}
		catch (NullPointerException ne){
			System.out.println("Problem with the action list " + action.actionList);
		}
		
	}

}
