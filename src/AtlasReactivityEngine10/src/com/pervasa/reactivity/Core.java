package com.pervasa.reactivity;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import org.osgi.framework.ServiceReference;

import com.pervasa.atlas.dev.service.AtlasService;

class Core {
	
	GUI g;
	Engine e;
	Interpreter i;
	
	Thread interpreterThread;

	Core() {
		//FIXME
		System.err.println("Core init");
		try {
			PipedReader pin = new PipedReader();
			PipedWriter pout = new PipedWriter(pin);

			g = new GUI(pout);
			e = new Engine(g, g, pout);
			i = new Interpreter(g, e, pin);

			interpreterThread = new Thread(i);
			interpreterThread.start();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	void addDevice(ServiceReference sref, AtlasService dev) {
		e.addDevice(sref, dev);
	}

	void removeDevice(ServiceReference sref) {
		e.removeDevice(sref);
	}
	
	void close() {
		// Perform cleanup
		// FIXME: Is stopping all threads an acceptable clean up?
		// interpreterThread.stop();
		g.dispose();
		e.close();
	}
	
}
