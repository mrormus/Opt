package com.pervasa.reactivity;

class Device {
	

	private String nodeID;
	private int deviceType;
	private int value;
	
	Device (String nodeID, int deviceType, int initialValue) {
		this.nodeID = nodeID;
		this.deviceType = deviceType;
		this.value = initialValue;
	}
	
	int reading() {
		return this.value;
	}
	
	void update(int value) {
		this.value = value;
	}
	
	public String toString() {
		return nodeID + ", " + DeviceType.name(deviceType) + ", " + DeviceType.range(deviceType);
	}
	
	public int getType() {
		return deviceType;
	}
	
	public String getNodeID() {
		return nodeID;
	}
}

class DeviceType {

	static final int PRESSURE = 0;
	static final int CONTACT = 1;
	static final int TEMP = 2;
	static final int HUMIDITY = 3;
	
	static String range(int deviceType) {
		String ret = "";
		switch (deviceType) {
		case PRESSURE: 
			ret = "[0,1000]";
			break;
		case CONTACT:
			ret = "[0,1]";
			break;
		case TEMP:
			ret = "[-100,300]";
			break;
		case HUMIDITY: 
			ret = "[0,100]";
			break;
		}
		return ret;
	}
	
	static String name(int deviceType) {
		String ret = "";
		switch (deviceType) {
		case PRESSURE: 
			ret = "Pressure";
			break;
		case CONTACT:
			ret = "Contact";
			break;
		case TEMP:
			ret = "Temperature";
			break;
		case HUMIDITY: 
			ret = "Humidity";
			break;
		}
		return ret;
	}
	
	
}