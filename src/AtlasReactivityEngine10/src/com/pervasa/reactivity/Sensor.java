package com.pervasa.reactivity;

import org.osgi.framework.ServiceReference;
import org.sensorplatform.actuators.servo.hs322.HS322Servo;
import org.sensorplatform.sensors.digitalcontact.DigitalContactSensor;
import org.sensorplatform.sensors.humidity.HumiditySensor;
import org.sensorplatform.sensors.pressure.InterlinkPressureSensor;
import org.sensorplatform.sensors.temperature.TemperatureSensor;

import com.pervasa.atlas.dev.service.AtlasClient;
import com.pervasa.atlas.dev.service.AtlasService;

interface Device {
	String toString();

	String getNodeID();

	void deregister();
}

class Sensor implements Device {

	static final int NULL = -999;

	private String nodeID;
	private int deviceType;
	private int value;
	private AtlasService dev;
	private boolean isRegistered;

	Sensor(String nodeID, int deviceType, AtlasService dev, int initialValue) {
		this.nodeID = nodeID;
		this.deviceType = deviceType;
		this.dev = dev;
		this.value = initialValue;
		this.isRegistered = true;
	}

	public void deregister() {
		this.dev = null;
		this.isRegistered = false;
	}

	int reading() {
		return this.value;
	}

	void update(int value) {
		this.value = value;
	}

	public String toString() {
		return nodeID + " = " + DeviceType.name(deviceType) + ", "
				+ DeviceType.range(deviceType);
	}

	public int getType() {
		return deviceType;
	}

	public String getNodeID() {
		return nodeID;
	}

	public void subscribe(AtlasClient ac) {
		if (isRegistered) {
			switch (deviceType) {
			case DeviceType.PRESSURE:
				InterlinkPressureSensor d = (InterlinkPressureSensor) dev;
				d.subscribeToPressureData(ac);
				break;
			case DeviceType.CONTACT:
				DigitalContactSensor d2 = (DigitalContactSensor) dev;
				d2.subscribeToContactData(ac);
				break;
			case DeviceType.TEMP:
				TemperatureSensor d3 = (TemperatureSensor) dev;
				d3.subscribeToSensorData(ac);
				break;
			case DeviceType.HUMIDITY:
				HumiditySensor d4 = (HumiditySensor) dev;
				d4.subscribeToSensorData(ac);
			}
		} else {
			System.err.println("Device '" + this + "' was deregistered.");
		}

	}

	void pull(AtlasClient ac) {
		if (isRegistered) {

			switch (deviceType) {

			case DeviceType.CONTACT:
				DigitalContactSensor d2 = (DigitalContactSensor) dev;
				d2.getContactReading(ac);
				break;
			case DeviceType.PRESSURE:
				InterlinkPressureSensor d = (InterlinkPressureSensor) dev;
				d.getPressureReading(ac);
				break;
			case DeviceType.HUMIDITY:
				HumiditySensor d4 = (HumiditySensor) dev;
				d4.getSensorReading(ac);
				break;
			case DeviceType.TEMP:
				TemperatureSensor d3 = (TemperatureSensor) dev;
				d3.getSensorReading(ac);
				break;
			}

			System.out.println("Pull from " + this);
		} else {
			System.err.println("Device '" + this + "' was deregistered.");
		}

	}

	public void unsubscribe(AtlasClient ac) {
		if (isRegistered) {
			switch (deviceType) {
			case DeviceType.PRESSURE:
				InterlinkPressureSensor d = (InterlinkPressureSensor) dev;
				d.unsubscribeFromPressureData(ac);
				break;
			case DeviceType.CONTACT:
				DigitalContactSensor d2 = (DigitalContactSensor) dev;
				d2.unsubscribeFromContactData(ac);
				break;
			case DeviceType.TEMP:
				TemperatureSensor d3 = (TemperatureSensor) dev;
				d3.unsubscribeFromSensorData(ac);
				break;
			case DeviceType.HUMIDITY:
				HumiditySensor d4 = (HumiditySensor) dev;
				d4.unsubscribeFromSensorData(ac);
			}
		} else {
			System.err.println("Device '" + this + "' was deregistered.");
		}
	}
}

class Actuator implements Device {

	private HS322Servo servo;
	private String nodeID;
	private boolean isRegistered;

	Actuator(String nodeID, HS322Servo servo) {
		this.nodeID = nodeID;
		this.servo = servo;
		this.isRegistered = true;
	}

	public void deregister() {
		this.servo = null;
		this.isRegistered = false;
	}

	public String toString() {
		return nodeID + " = MoveServo, [0, 180]";
	}

	void actuate(int value) {
		if (isRegistered) {
			servo.moveServo(((value * 100) / 180) + 1);
		} else {
			System.err.println("Device '" + this + "' was deregistered.");
		}
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
