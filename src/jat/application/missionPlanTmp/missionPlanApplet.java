package jat.application.missionPlanTmp;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class missionPlanApplet extends JApplet {
	private static final long serialVersionUID = -5650262458365835499L;
	public MissionPlanGUI mpGUI;
	public MissionPlanPlot mpPlot;
	public MissionPlanParameters mpParam;
	JFrame sFrame;
	List<Flight> flightList = new ArrayList<Flight>();

	public void init() {
		// Execute a job on the event-dispatching thread; creating this applet's
		// GUI.
		// try {
		// SwingUtilities.invokeAndWait(new Runnable() {
		// public void run() {
		this.setSize(700, 500);
		System.out.println("Attempting to load MissionPlanGUI");
		mpGUI = new MissionPlanGUI(this);
		System.out.println("Attempting to load MissionPlanPlot");
		//mpPlot = new MissionPlanPlot(this);
		System.out.println("Attempting to load MissionPlanParameters");
		mpParam = new MissionPlanParameters();

		add(mpGUI, BorderLayout.WEST);
		//add(mpPlot, BorderLayout.CENTER);

		JLabel lbl = new JLabel("Hello World");
		add(lbl);
		// }
		// });
		// } catch (Exception e) {
		// System.err.println("createGUI didn't complete successfully");
		// }
	}
}