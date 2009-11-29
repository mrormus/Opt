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
package com.pervasa.reactivity;

// awt/swing GUI components
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.osgi.framework.ServiceReference;

import com.pervasa.atlas.dev.service.AtlasService;

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



	private static final long serialVersionUID = 1L; /*
													 * FIXME: No idea what this
													 * does
													 */
	// access to OSGi's information about the running KitSampleApp bundle
	// in this application, this is only used to get the root/working
	// directory of the bundle, for loading external images into the GUI

	// GUI elements declared here are dynamic
	// they either change based on data from sensor and actuator services
	// or by user input

	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextField commandLine;
	private javax.swing.JTextArea console;

	private javax.swing.JTextArea jTextArea3;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JLabel jLabel3;
	
	private ReactiveEngine re;


	// KitSampleApp constructor
	// will be called by bundle's Activator class when started in Knopflerfish
	public GUI() {
		this.re = new ReactiveEngine(this);
		this.setVisible(true);
		initGUI();
	}

	// Sets the console text area to display String s
	public void updateConsole(String s) {
		console.setText(s);
	}

	// pops up an error box
	public void error(String s) {
		JOptionPane.showMessageDialog(this, s);
		System.err.println(s);
	}

	public void addDevice(ServiceReference sref, AtlasService dev) {
		this.re.addDevice(sref, dev);
	}

	public void removeDevice(ServiceReference sref) {
		this.re.removeDevice(sref);
	}

	// this method generates the basic GUI for the application bundle
	// it also includes the example of actuator control
	protected void initGUI() {

		/*
		 * try { UIManager.setLookAndFeel(new
		 * SubstanceBusinessBlackSteelLookAndFeel()); } catch (Exception e) {
		 * System.out.println("Substance Raven Graphite failed to initialize");
		 * 
		 * }
		 */

		jLabel1 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		commandLine = new javax.swing.JTextField();
		jScrollPane2 = new javax.swing.JScrollPane();
		console = new javax.swing.JTextArea();
		jButton1 = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		jTextArea3 = new javax.swing.JTextArea();
		jLabel3 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

		jScrollPane1
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		commandLine.setColumns(20);
		commandLine.setAutoscrolls(false);
		commandLine.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				commandLineKeyPressed(evt);
			}
		});
		jScrollPane1.setViewportView(commandLine);

		getContentPane().add(jScrollPane1);
		jScrollPane1.setBounds(210, 104, 360, 24);

		console.setColumns(20);
		console.setRows(5);
		console.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1,
				1));
		console.setRequestFocusEnabled(false);
		jScrollPane2.setViewportView(console);

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
		jLabel2.setText("Reactive Engine");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(320, 20, 210, 30);

		jScrollPane3
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		jTextArea3.setColumns(20);
		jTextArea3.setRows(1);
		jTextArea3.setBorder(null);
		/*
		 * jTextArea3.addKeyListener(new java.awt.event.KeyAdapter() { public
		 * void keyPressed(java.awt.event.KeyEvent evt) {
		 * jTextArea3KeyPressed(evt); } });
		 */
		jScrollPane3.setViewportView(jTextArea3);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(80, 190, 630, 20);

		jLabel3.setText("Enter the Command");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(90, 100, 150, 40);

		pack();

	}

	private void commandLineKeyPressed(java.awt.event.KeyEvent evt) {
		if (evt.getKeyChar() == '\n') {
			parseCommandLine();
		}
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		parseCommandLine();
	}
	
	private void parseCommandLine() {
		re.parse(commandLine.getText());

		System.out.println("BEFORE PARSE+" + commandLine.getText());

		jTextArea3.setText(">" + commandLine.getText());
		commandLine.setText("");
	}

	// public void fill()
	// {
	// actionBasic.put("N12", "Move Servo [100-200]");
	// actionBasic.put("N17", "Move Servo [100-200]");
	// actionBasic.put("N20", "Move Servo [100-200]");
	// actionBasic.put("N19", "Move Servo [100-200]");
	// eventBasic.put("E12", "pressure [100-200]");
	// eventBasic.put("E17", "temperature [100-200]");
	// eventBasic.put("E20", "humidity [100-200]");
	// eventBasic.put("E19", "contact [100-200]");
	// }

	private void formComponentResized(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentMoved(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentHidden(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentShown(java.awt.event.ComponentEvent evt) {

	}

	// rakesh functions end

	// ameya: code end
}
