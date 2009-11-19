/**
 *  The Atlas Education Kit Sample Application
 *  
 *    A GUI for viewing the data streams from two sensors
 *    (an analog pressure sensor and a digital contact 
 *    sensor) and for controlling the position of a servo.
 *   
 *  Application Implementation class
 *    (will be instantiated by Activator class)
 *  
 *  Jeff King
 *  support@pervasa.com
 *  
 *  March 6, 2007
 *  
 *  cheenu pushed
 */
package com.pervasa.demo.kitsample.impl;

// awt/swing GUI components
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;

import java.io.*;




import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

// used to access Knopflerfish's information about a bundle file
import org.osgi.framework.BundleContext;
// used to track sensor and actuator services and the come online and go offline
import org.osgi.framework.ServiceReference;

// the main set of interfaces needed to develop Atlas applications
import com.pervasa.atlas.dev.service.*;
// the interface for the pressure sensor service
import org.sensorplatform.sensors.pressure.InterlinkPressureSensor;
import org.sensorplatform.sensors.temperature.TemperatureSensor;
// the interface for the servo actuator service
import org.sensorplatform.actuators.servo.hs322.HS322Servo;
// the interface for the digital contact sensor service
import org.sensorplatform.sensors.digitalcontact.DigitalContactSensor;
import org.sensorplatform.sensors.humidity.HumiditySensor;

//import the temperature and pressure sensors
//import org.sensorplatform.sensors.humidity.HumiditySensor;

//import org.sensorplatform.sensors.temperature.TemperatureSensor;

// the AtlasClient interface is for applications that want to be able
//   to access services provided by the Atlas platform
// JFrame is just a base class for Java GUIs. If you're creating an
//   Atlas application that doesn't have a GUI, you don't need to
//   extend JFrame
public class GUI extends JFrame {
	/**
	 * 
	 */
	
	ReactiveEngine r;
	
	private static final long serialVersionUID = 1L; /*FIXME: No idea what this does */
	// access to OSGi's information about the running KitSampleApp bundle
	//   in this application, this is only used to get the root/working
	//   directory of the bundle, for loading external images into the GUI

	
	// GUI elements declared here are dynamic
	//   they either change based on data from sensor and actuator services
	//   or by user input

	private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JDialog jDialog2;


	
	
	 private javax.swing.JTextArea jTextArea3;
	    private javax.swing.JScrollPane jScrollPane3;
	    private javax.swing.JLabel jLabel3;



	// KitSampleApp constructor
	//   will be called by bundle's Activator class when started in Knopflerfish
	public GUI (BundleContext context) {
		r = new ReactiveEngine(context);
		this.setVisible(true);
		initGUI();
	}

	
	// this method generates the basic GUI for the application bundle
	// it also includes the example of actuator control
	protected void initGUI() {
		

		
		/*try {
		      UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
		    } catch (Exception e) {
		      System.out.println("Substance Raven Graphite failed to initialize");

		    }
		*/
		
		
		
		
		jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(null);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(90, 140, 150, 40);

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(1);
        jTextArea1.setAutoscrolls(false);
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(210, 104, 360, 24);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTextArea2.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(jTextArea2);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(80, 210, 630, 260);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(600, 100, 110, 30);

        jLabel2.setFont(new java.awt.Font("Trebuchet MS", 0, 24));
        jLabel2.setText("REACTIVE ENGINE");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(320, 20, 210, 30);

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(1);
        jTextArea3.setBorder(null);
      /*  jTextArea3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextArea3KeyPressed(evt);
            }
        });*/
        jScrollPane3.setViewportView(jTextArea3);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(80, 190, 630, 20);

        jLabel3.setText("Enter the Command");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(90, 100, 150, 40);

        pack();
		

		
	}
	
	
	
	
	
	private void jTextArea1KeyPressed(java.awt.event.KeyEvent evt) {                                      
		if(evt.getKeyChar()=='\n'){
		        String str=jTextArea1.getText();
		        System.out.println("BEFORE PARSE+"+str);
		        r.parse(str); /*FIXME: Requires redudant method which is present
		        in both GUI and ReactiveEngine*/
		        
		        jTextArea3.setText(">"+str);
		        jTextArea1.setText("");
		        
		}
	}
	
	
	
	
	
	
	
	
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
		String str=jTextArea1.getText();
		r.parse(str);  /*FIXME: Requires redundant method which is present in both GUI
		and ReactiveEngine */
		String str1=jTextArea1.getText();
        jTextArea3.setText(">"+str1);
        jTextArea1.setText("");
		}                                    
		
//		public void fill()
//	    {
//	        actionBasic.put("N12", "Move Servo [100-200]");
//	        actionBasic.put("N17", "Move Servo [100-200]");
//	        actionBasic.put("N20", "Move Servo [100-200]");
//	        actionBasic.put("N19", "Move Servo [100-200]");
//	        eventBasic.put("E12", "pressure [100-200]");
//	        eventBasic.put("E17", "temperature [100-200]");
//	        eventBasic.put("E20", "humidity [100-200]");
//	        eventBasic.put("E19", "contact [100-200]");
//	    }
	    

		private void formComponentResized(java.awt.event.ComponentEvent evt) {                                      

		}                                     

		private void formComponentMoved(java.awt.event.ComponentEvent evt) {                                    

		}                                   

		private void formComponentHidden(java.awt.event.ComponentEvent evt) {                                     
		
		}                                    

		private void formComponentShown(java.awt.event.ComponentEvent evt) {                                    

		}      

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
  
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//rakesh functions end
	
	
	

	
	
	
	
	
	//ameya: code end
}

