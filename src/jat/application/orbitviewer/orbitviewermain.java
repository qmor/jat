package jat.application.orbitviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

public class orbitviewermain {
	private static orbitviewerGUI theApplet;
	static int appletwidth = 900; // Width of Applet
	static int appletheight = 700;
	JFormattedTextField yearfield;
	orbitviewerEvents myb;

	
	/**
	 * Used when run as an application
	 * 
	 * @param args
	 *            (String[]) Argument
	 */
	public static void main(String[] args) {
		theApplet = new orbitviewerGUI();
		JFrame theFrame = new JFrame();
		// Initialize the applet
		theApplet.init();
		theFrame.setTitle("Orbit Viewer");
		theFrame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		// When running this file as a stand-alone app, add the applet to
		// the frame.
		theFrame.getContentPane().add(theApplet, BorderLayout.CENTER);

		theFrame.setSize(appletwidth, appletheight);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		theFrame.setLocation((d.width - theFrame.getSize().width) / 2,
				(d.height - theFrame.getSize().height) / 2);
		theFrame.setVisible(true);
	}// End of main()


}
