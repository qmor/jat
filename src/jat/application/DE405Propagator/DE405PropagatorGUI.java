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
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class DE405PropagatorGUI extends JPanel {
	private static final long serialVersionUID = 1321470082814219656L;
	DE405PropagatorEvents dpE;
	JPanel level2_Pane_Plot;
	DE405PropagatorMain dpMain;
	public JCheckBox chckbxRotation;
	public JFormattedTextField tf_x, tf_y, tf_z, tf_vx, tf_vy, tf_vz;
	public JButton btnPlot;
	JDatePicker depart_date_picker;

	public DE405PropagatorGUI(DE405PropagatorMain dpMain) {
		this.dpMain = dpMain;
		dpE = new DE405PropagatorEvents(dpMain);

		depart_date_picker = JDateComponentFactory.createJDatePicker();
		depart_date_picker.setTextEditable(true);
		depart_date_picker.setShowYearButtons(true);

		setLayout(new BorderLayout(0, 0));
		JPanel level1_Pane = new JPanel();

		add(level1_Pane, BorderLayout.WEST);
		GridBagLayout gbl_level1_Pane = new GridBagLayout();
		gbl_level1_Pane.columnWidths = new int[] { 0, 0 };
		gbl_level1_Pane.rowHeights = new int[] { 60, 60, 60, 60, 0 };
		gbl_level1_Pane.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_level1_Pane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		level1_Pane.setLayout(gbl_level1_Pane);

		JPanel panelPlanets = new JPanel();
		GridBagConstraints gbc_panelPlanets = new GridBagConstraints();
		gbc_panelPlanets.fill = GridBagConstraints.BOTH;
		gbc_panelPlanets.insets = new Insets(0, 0, 5, 0);
		gbc_panelPlanets.gridx = 0;
		gbc_panelPlanets.gridy = 0;
		level1_Pane.add(panelPlanets, gbc_panelPlanets);
		panelPlanets.setBorder(new TitledBorder(null, "Planet On/Off", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panelPlanets.setLayout(new GridLayout(0, 1, 0, 0));

		JCheckBox checkBox = new JCheckBox("Mercury");
		panelPlanets.add(checkBox);

		JCheckBox chckbxEarth = new JCheckBox("Earth");
		panelPlanets.add(chckbxEarth);

		JCheckBox checkBoxJupiter = new JCheckBox("Jupiter");
		panelPlanets.add(checkBoxJupiter);

		JPanel panelDate = new JPanel();
		GridBagConstraints gbc_panelDate = new GridBagConstraints();
		gbc_panelDate.fill = GridBagConstraints.BOTH;
		gbc_panelDate.insets = new Insets(0, 0, 5, 0);
		gbc_panelDate.gridx = 0;
		gbc_panelDate.gridy = 1;
		level1_Pane.add(panelDate, gbc_panelDate);
		panelDate.add((JComponent) depart_date_picker);

		JPanel panelIC = new JPanel();
		GridBagConstraints gbc_panelIC = new GridBagConstraints();
		gbc_panelIC.fill = GridBagConstraints.BOTH;
		gbc_panelIC.insets = new Insets(0, 0, 5, 0);
		gbc_panelIC.gridx = 0;
		gbc_panelIC.gridy = 2;
		level1_Pane.add(panelIC, gbc_panelIC);
		panelIC.setBorder(new TitledBorder(null, "Initial Condition", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panelIC.setLayout(new GridLayout(10, 1, 0, 0));

		JLabel lblNewLabel = new JLabel("x y z [km]");
		panelIC.add(lblNewLabel);

		tf_x = new JFormattedTextField();
		panelIC.add(tf_x);

		tf_y = new JFormattedTextField();
		panelIC.add(tf_y);

		tf_z = new JFormattedTextField();
		panelIC.add(tf_z);

		JLabel lblNewLabel_1 = new JLabel("vx vy vz [km/s]");
		panelIC.add(lblNewLabel_1);

		tf_vx = new JFormattedTextField();
		panelIC.add(tf_vx);

		tf_vy = new JFormattedTextField();
		panelIC.add(tf_vy);

		tf_vz = new JFormattedTextField();
		panelIC.add(tf_vz);

		btnPlot = new JButton("Plot");
		panelIC.add(btnPlot);

		chckbxRotation = new JCheckBox("Rotate");
		GridBagConstraints gbc_chckbxRotation = new GridBagConstraints();
		gbc_chckbxRotation.fill = GridBagConstraints.BOTH;
		gbc_chckbxRotation.gridx = 0;
		gbc_chckbxRotation.gridy = 3;
		level1_Pane.add(chckbxRotation, gbc_chckbxRotation);

		chckbxRotation.addItemListener(dpE);
		btnPlot.addActionListener(dpE);
	}


	public void updateGUI() {

		tf_x.setValue(dpMain.dpParam.y0[0]);
		tf_y.setValue(dpMain.dpParam.y0[1]);
		tf_z.setValue(dpMain.dpParam.y0[2]);
		tf_vx.setValue(dpMain.dpParam.y0[3]);
		tf_vy.setValue(dpMain.dpParam.y0[4]);
		tf_vz.setValue(dpMain.dpParam.y0[5]);

	}
}
