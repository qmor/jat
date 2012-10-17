/* JAT: Java Astrodynamics Toolkit
 * 
  Copyright 2012 Tobias Berthold

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package jat.application.porkChopPlot;

import jat.core.ephemeris.DE405APL;
import jat.core.util.PathUtil;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class PorkChopPlot_main extends JApplet {
	private static final long serialVersionUID = 1122861326294482666L;
	static int appletwidth = 900; // Width of Applet
	static int appletheight = 700;
	public PorkChopPlot_GUI pcpGUI;
	public PorkChopPlot_Plot pcpPlot;
	Container level1_Pane;
	public PorkChopPlot_ReturnValue pReturn = new PorkChopPlot_ReturnValue();
	PorkChopPlot_Parameters params;

	public PorkChopPlot_main(PathUtil p) {
		// PathUtil p =new PathUtil(this);
		params = new PorkChopPlot_Parameters(DE405APL.body.EARTH_MOON_BARY, DE405APL.body.MARS, 2003, 1, 1, 2003, 7, 1,
				500, 10);
		// params = new PorkChopPlot_Parameters(DE405APL.MERCURY, DE405APL.MARS,
		// 2003, 1, 1, 2003, 7, 1, 500, 10);
		// params = new PorkChopPlot_Parameters(DE405APL.VENUS, DE405APL.MARS,
		// 2003, 1, 1, 2003, 7, 1, 500, 10);
		pcpGUI = new PorkChopPlot_GUI(this);
		pcpGUI.pcpE.setMain(this);
		pcpPlot = new PorkChopPlot_Plot(this);

		level1_Pane = getContentPane();
		level1_Pane.add(pcpGUI, BorderLayout.WEST);
		level1_Pane.add(pcpPlot, BorderLayout.CENTER);
		params.p = p;
	}

	public PorkChopPlot_main() {
	}

	public void init() {

	}

	public static void main(String[] args) {
		PorkChopPlot_main pApplet = new PorkChopPlot_main();
		pApplet.init();

		JFrame pFrame = new JFrame();
		pFrame.setTitle("Optimal Launch Date Finder");
		pFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// When running this file as a stand-alone app, add the applet to
		// the frame.
		pFrame.getContentPane().add(pApplet, BorderLayout.CENTER);
		pFrame.setSize(appletwidth, appletheight);
		pFrame.setVisible(true);

		// sApplet.ssp.mouseZoom.setupCallback(sApplet.ssE);
		pApplet.pcpPlot.requestFocusInWindow();

		// sApplet.ssE.timer.start();

	}// End of main()

}
