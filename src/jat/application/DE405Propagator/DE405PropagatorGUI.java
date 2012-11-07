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

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import java.awt.GridLayout;

public class DE405PropagatorGUI extends JPanel {
	private static final long serialVersionUID = 1321470082814219656L;
	DE405PropagatorEvents myb;
	JPanel level2_Pane_Plot;
	DE405PropagatorMain dpMain;
	public JCheckBox chckbxRotation;	
	
	
	public DE405PropagatorGUI(DE405PropagatorMain dpMain) {
		this.dpMain=dpMain;
		myb = new DE405PropagatorEvents(dpMain);
		setLayout(new BorderLayout(0, 0));
		JPanel level1_Pane = new JPanel();

		add(level1_Pane);
		level1_Pane.setLayout(new GridLayout(2, 1, 0, 0));


		JPanel level2_Pane_Control = new JPanel();
		level1_Pane.add(level2_Pane_Control);
		level2_Pane_Control.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Planet On/Off", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		level2_Pane_Control.add(panel);
		
		JCheckBox chckbxMercury = new JCheckBox("Mercury");
		panel.add(chckbxMercury);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Initial Condition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		level2_Pane_Control.add(panel_1);
		
		chckbxRotation = new JCheckBox("Rotate");
		level2_Pane_Control.add(chckbxRotation);


	}
}
