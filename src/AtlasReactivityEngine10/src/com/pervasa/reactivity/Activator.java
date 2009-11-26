/**
 *  The Atlas Education Kit Sample Application
 *  
 *    A GUI for viewing the data streams from two sensors
 *    (an analog pressure sensor and a digital contact 
 *    sensor) and for controlling the position of a servo.
 *   
 *  Activator class
 *    (hooks application into OSGi framework)
 *  
 *  Jeff King
 *  support@pervasa.com
 *  
 *  March 6, 2007
 */
package com.pervasa.reactivity;

// Class implements BundleActivator (to hook the application into OSGi)
//   and ServiceListener (to hook other services into the application)
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceListener;
// Required to implemented BundleActivator, allows class to access
//   OSGi's information about a bundle
import org.osgi.framework.BundleContext;
// Fired when a service running in OSGi comes online, changes, or goes offline
import org.osgi.framework.ServiceEvent;
// Reference to the service that triggered the Event
import org.osgi.framework.ServiceReference;

// main set of interfaces used to develop Atlas sensor or actuator services
import com.pervasa.atlas.dev.service.*;

// An OSGi bundle is basically like a normal Java application, only instead of being
//   instantiated in a static main() method, it is instantiated by an Activator class.
//   Each bundle will have its own Activator. The Activator links the application into
//   OSGi, implementing the life-cycle management (start, stop, etc.) and service
//   discovery and collaboration support.
public class Activator implements BundleActivator, ServiceListener {
    private BundleContext context;
    private GUI gui;
    
    public void start(BundleContext context) throws Exception {
    	// the contextual information that OSGi knows about this bundle, which the
    	//   bundle might not know about itself (e.g., the full path and name of the
    	//   JAR file that contains the bundle).
    	this.context = context;
    	// instantiate the application
        gui = new GUI();         
        System.out.println("*** Starting Kit Sample Application ***");
        // register to listen for other OSGi services as they come online, change, or go offline
        context.addServiceListener(this, "(objectClass="+ AtlasService.class.getName() + ")");
        ServiceReference[] ref = context.getServiceReferences(null,"(objectClass=" + AtlasService.class.getName() + "*)");
        // force all other OSGi services that are running to announce themselves
        for (int i = 0; ref != null && i < ref.length; i++) {
            serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, ref[i]));
        }        
	}
    
    public void stop(BundleContext context) throws Exception {
    	System.out.println("*** Stopping Kit Sample Application ***");
    	// Free the memory allocated to the application. It doesn't need to be
    	//   allocated while the service is unavailable, but it wouldn't be
    	//   automatically cleaned by the Java garbage collector since this
    	//   Activator class would still have a reference to it.
    	gui.dispose();
	}
    
    public void serviceChanged(ServiceEvent event) {
    	// if any new OSGi service comes online, or an existing one changes, we'll want
    	//   to pass a reference to it over to the application, which will check to see if
    	//   it is an Atlas service the application wants to use
    	if ((event.getType() == ServiceEvent.REGISTERED) || (event.getType() == ServiceEvent.MODIFIED)) {
    		// the ServiceReference is an internal OSGi / Knopflerfish pointer to the actual
    		//   bundle class that implements the service
    		ServiceReference sRef = event.getServiceReference();
        	try {
        		// All Atlas sensor and actuator services implement the AtlasService interface,
        		//   so use OSGi and the ServiceReference to grab the actual bundle for that
        		//   service, try casting it as an AtlasService and pass it to the application
        		AtlasService newDevice = (AtlasService)context.getService(sRef);
        		gui.addDevice(sRef, newDevice);
   	      	}
        	catch (Exception ee1) {
   	   	    	System.out.println("Exception registering device in KitSampleApp: " + ee1);
   	      	}
        	// let the Activator release the service
        	//   even if the application is holding on to a reference 
        	context.ungetService(sRef);
        }
    	// if a running service goes offline, pass the ServiceReference to the application
    	//   it will check to see if that ServiceReference matches the ServiceReference for
    	//   any service it is using, and make adjustments until/unless an equivalent service
    	//   comes back
        if (event.getType() == ServiceEvent.UNREGISTERING) {
        	gui.removeDevice(event.getServiceReference());
        }
    }
    
}
