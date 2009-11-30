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
import java.io.IOException;
import java.io.Writer;

import javax.swing.JFrame;



// the AtlasClient interface is for applications that want to be able
// to access services provided by the Atlas platform
// JFrame is just a base class for Java GUIs. If you're creating an
// Atlas application that doesn't have a GUI, you don't need to
// extend JFrame
class GUI extends JFrame implements Console {

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

	private javax.swing.JButton okButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTextField commandLine;
	private javax.swing.JTextArea console;

	private javax.swing.JTextArea lastCommand;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JLabel instructionLabel;

	private Writer commandLineParser;

	// KitSampleApp constructor
	// will be called by bundle's Activator class when started in Knopflerfish
	GUI(Writer commandLineParser) {
		this.commandLineParser = commandLineParser;
		this.setVisible(true);
		initGUI();
	}

	// Sets the console text area to display String s
	public void update(String s) {
		console.setText(s);
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
		okButton = new javax.swing.JButton();
		titleLabel = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		lastCommand = new javax.swing.JTextArea();
		instructionLabel = new javax.swing.JLabel();

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

		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		getContentPane().add(okButton);
		okButton.setBounds(600, 100, 110, 30);

		titleLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 24));
		titleLabel.setText("Reactive Engine");
		getContentPane().add(titleLabel);
		titleLabel.setBounds(320, 20, 210, 30);

		jScrollPane3
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		lastCommand.setColumns(20);
		lastCommand.setRows(1);
		lastCommand.setBorder(null);
		/*
		 * jTextArea3.addKeyListener(new java.awt.event.KeyAdapter() { public
		 * void keyPressed(java.awt.event.KeyEvent evt) {
		 * jTextArea3KeyPressed(evt); } });
		 */
		jScrollPane3.setViewportView(lastCommand);

		getContentPane().add(jScrollPane3);
		jScrollPane3.setBounds(80, 190, 630, 20);

		instructionLabel.setText("Enter the Command");
		getContentPane().add(instructionLabel);
		instructionLabel.setBounds(90, 100, 150, 40);

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
		try {
			commandLineParser.write(commandLine.getText() + "\n");
			Thread.yield();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("BEFORE PARSE+" + commandLine.getText());

		lastCommand.setText(">" + commandLine.getText());
		commandLine.setText("");
	}

	private void formComponentResized(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentMoved(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentHidden(java.awt.event.ComponentEvent evt) {

	}

	private void formComponentShown(java.awt.event.ComponentEvent evt) {

	}

}
