package jat.application.AttitudeSimulator;

import jat.core.util.FileUtil2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelpWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelpWindow(String name) throws HeadlessException {

		super("Simulation Instruction");
		Dimension windowSize = new Dimension(150, 20);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JLabel emptyLabel = new JLabel("");
		emptyLabel.setPreferredSize(new Dimension(175, 100));
		//getContentPane().add(createEditorPane("ConstantTorque.html"),
			//	BorderLayout.CENTER);
		getContentPane().add(createEditorPane(name),
				BorderLayout.CENTER);

		pack();
		setVisible(true);
	}

	private JEditorPane createEditorPane(String fileName) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBackground(Color.white);
		Dimension mysize = new Dimension(600, 400);
		editorPane.setPreferredSize(mysize);
		String s = null;
		FileUtil2 f = new FileUtil2();

		try {

			System.out.println(f.root_path);

			s = "file:" + f.find_attitude_help_folder() + fileName;
			System.out.println(s);

			URL helpURL2 = new URL(s);

			displayURL(helpURL2, editorPane, fileName);

		} catch (Exception e) {
			System.err.println("Couldn't create help URL!!: " + s);
			System.exit(0);
		}

		return editorPane;
	}

	/**
	 * Displays URL
	 * 
	 * @author Sun's Swing tutorial example
	 * @param url
	 *            (URL)
	 * @param editorPane
	 *            (JEditorPane)
	 * @param fileName
	 *            (String)
	 */
	private void displayURL(URL url, JEditorPane editorPane, String fileName) {
		try {
			editorPane.setPage(url);
		} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + url);
		}
	}

}
