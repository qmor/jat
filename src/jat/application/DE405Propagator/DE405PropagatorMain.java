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

import jat.application.DE405Propagator.ParameterSet.earthMoonMEOP;
import jat.application.DE405Propagator.ParameterSet.earthOrbitECI;
import jat.application.DE405Propagator.ParameterSet.sunOrbit;
import jat.application.DE405Propagator.ParameterSet.testOrbit;
import jat.application.missionPlan.Flight;
import jat.core.ephemeris.DE405Frame.frame;
import jat.core.ephemeris.DE405Plus;
import jat.core.util.PathUtil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;

public class DE405PropagatorMain extends JApplet {

	private static final long serialVersionUID = -6469847628986520750L;
	DE405PropagatorGUI dpGUI;
	DE405PropagatorPlot dpPlot;
	DE405PropagatorParameters dpParam;
	DE405PropagatorGlobals dpGlobals;
	List<DE405PropagatorParameters> ParameterSetList = new ArrayList<DE405PropagatorParameters>();

	public void init() {
	}

	public void start() {
		ParameterSetList.add(new earthOrbitECI());
		// dpParam=new DE405PropagatorParameters();
		dpParam = new testOrbit();
		dpParam = new sunOrbit();
		dpParam = new earthOrbitECI();
		dpParam = new earthMoonMEOP();
		//dpParam.Frame=frame.ECI;
		dpGlobals = new DE405PropagatorGlobals();

		PathUtil path = new PathUtil(this);
		dpParam.Eph = new DE405Plus(path);
		dpParam.Eph.setUnitsMaster(dpGlobals.uc);
		dpGlobals.uc.addUser(dpParam.Eph.getUnits());
		dpGlobals.uc.addUser(dpGlobals.sb.getUnits());
		//dpParam.Eph.printSteps = true;

		Container level1_Pane;
		level1_Pane = getContentPane();
		dpGUI = new DE405PropagatorGUI(this);
		level1_Pane.add(dpGUI, BorderLayout.WEST);
		dpGUI.updateGUI();

		dpPlot = new DE405PropagatorPlot(this);
		dpPlot.make_plot();

		level1_Pane.add(dpPlot.plot);

		dpGlobals.uc.printList();

	}
}
