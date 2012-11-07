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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class DE405PropagatorEvents implements ActionListener, ItemListener {
	DE405PropagatorGUI dpGUI;
	DE405PropagatorMain dpMain;

	public DE405PropagatorEvents(DE405PropagatorMain dpMain) {
		this.dpMain = dpMain;
		this.dpGUI = dpMain.dpGUI;
	}

	public void actionPerformed(ActionEvent ev) {

		// Read in values
		// if (ev.getSource() == dpMain.dpGUI.setButton) {
		// System.out.println("button 1 pressed");

		// dpMain.dpPlot.a = (Double) dpMain.dpGUI.semimajorfield.getValue();
		boolean later = false;
		if (later) {
			dpMain.dpPlot.plot.removeAllPlots();
			dpMain.dpPlot.add_scene();
		}
	}

	// }// End of ActionPerformed

	@Override
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();

		if (source == dpMain.dpGUI.chckbxRotation) {
			System.out.println("rot");
			if (dpMain.dpGUI.chckbxRotation.isSelected())
				dpMain.dpPlot.plot.plotCanvas.timer.start();
			else
				dpMain.dpPlot.plot.plotCanvas.timer.stop();

		}
	}

}
