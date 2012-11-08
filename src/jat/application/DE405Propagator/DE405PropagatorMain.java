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

package jat.application.DE405Propagator;

import jat.core.ephemeris.DE405Frame;
import jat.core.ephemeris.DE405Plus;
import jat.core.ephemeris.DE405Body.body;
import jat.core.spacetime.TimeAPL;
import jat.core.util.PathUtil;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JFormattedTextField;

public class DE405PropagatorMain extends JApplet {

	private static final long serialVersionUID = -6469847628986520750L;
	static int appletwidth = 900; // Width of Applet
	static int appletheight = 700;
	JFormattedTextField yearfield;
	//DE405PropagatorEvents dpE;
	DE405PropagatorGUI dpGUI;
	DE405PropagatorPlot dpPlot;
	DE405PropagatorParameters dpParam;
	Container level1_Pane;
	public DE405Plus Eph;

	public void init() {
	}

	public void start() {
		PathUtil path = new PathUtil(this);
		Eph = new DE405Plus(path);
		Eph.setFrame(DE405Frame.frame.HEE);
		Eph.printSteps = true;
		TimeAPL myTime = new TimeAPL(2003, 3, 1, 12, 0, 0);
		Eph.setIntegrationStartTime(myTime);
		Eph.planetOnOff[body.SUN.ordinal()] = true;
		dpParam=new DE405PropagatorParameters();

		level1_Pane = getContentPane();
		dpGUI = new DE405PropagatorGUI(this);
		level1_Pane.add(dpGUI, BorderLayout.WEST);
		dpGUI.updateGUI();

		dpPlot = new DE405PropagatorPlot(this);
		dpPlot.make_plot();

		level1_Pane.add(dpPlot.plot);

	}
}
