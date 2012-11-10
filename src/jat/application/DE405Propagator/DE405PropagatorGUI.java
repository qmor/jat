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

import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import javax.swing.JComboBox;

public class DE405PropagatorGUI extends JPanel {
	private static final long serialVersionUID = 1321470082814219656L;
	DE405PropagatorEvents dpE;
	JPanel level2_Pane_Plot;
	DE405PropagatorMain dpMain;
	public JCheckBox chckbxRotation;
	public JFormattedTextField tf_x, tf_y, tf_z, tf_vx, tf_vy, tf_vz;
	JFormattedTextField tf_tf;
	public JButton btnPlot;
	JDatePicker depart_date_picker;
	public JCheckBox chckbxMercury, chckbxEarth, chckbxJupiter;
	public JCheckBox chckbxVenus;
	public JCheckBox chckbxMars;
	public JCheckBox chckbxSaturn;
	public JCheckBox chckbxSun;
	public JCheckBox chckbxMoon;
	public JComboBox comboBoxFrame;

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
		gbl_level1_Pane.rowHeights = new int[] { 60, 60, 60, 60, 0, 0 };
		gbl_level1_Pane.columnWeights = new double[] { 1.0, 1.0 };
		gbl_level1_Pane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		level1_Pane.setLayout(gbl_level1_Pane);

		JPanel panelPlanets = new JPanel();
		GridBagConstraints gbc_panelPlanets = new GridBagConstraints();
		gbc_panelPlanets.fill = GridBagConstraints.BOTH;
		gbc_panelPlanets.insets = new Insets(0, 0, 5, 5);
		gbc_panelPlanets.gridx = 0;
		gbc_panelPlanets.gridy = 0;
		level1_Pane.add(panelPlanets, gbc_panelPlanets);
		panelPlanets.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Body On/Off",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPlanets.setLayout(new GridLayout(0, 1, 0, 0));

		chckbxSun = new JCheckBox("Sun");
		panelPlanets.add(chckbxSun);

		chckbxMercury = new JCheckBox("Mercury");
		panelPlanets.add(chckbxMercury);

		chckbxVenus = new JCheckBox("Venus");
		panelPlanets.add(chckbxVenus);

		chckbxEarth = new JCheckBox("Earth");
		panelPlanets.add(chckbxEarth);

		chckbxMoon = new JCheckBox("Moon");
		panelPlanets.add(chckbxMoon);

		chckbxMars = new JCheckBox("Mars");
		panelPlanets.add(chckbxMars);

		chckbxJupiter = new JCheckBox("Jupiter");
		panelPlanets.add(chckbxJupiter);

		chckbxSaturn = new JCheckBox("Saturn");
		panelPlanets.add(chckbxSaturn);

		
		JPanel panelFrame = new JPanel();
		panelFrame.setBorder(new TitledBorder(null, "Frame", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelFrame = new GridBagConstraints();
		gbc_panelFrame.fill = GridBagConstraints.BOTH;
		gbc_panelFrame.insets = new Insets(0, 0, 5, 5);
		gbc_panelFrame.gridx = 0;
		gbc_panelFrame.gridy = 1;
		level1_Pane.add(panelFrame, gbc_panelFrame);
		panelFrame.setLayout(new GridLayout(1, 1, 0, 0));
		
		comboBoxFrame = new JComboBox(frame.name);
		panelFrame.add(comboBoxFrame);

		
		
		JPanel panelDate = new JPanel();
		panelDate.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Start Date, Flight Time [s]",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelDate = new GridBagConstraints();
		gbc_panelDate.fill = GridBagConstraints.BOTH;
		gbc_panelDate.insets = new Insets(0, 0, 5, 5);
		gbc_panelDate.gridx = 0;
		gbc_panelDate.gridy = 2;
		level1_Pane.add(panelDate, gbc_panelDate);
		panelDate.add((JComponent) depart_date_picker);
		panelDate.setLayout(new GridLayout(2, 1, 0, 0));

		tf_tf = new JFormattedTextField();
		panelDate.add(tf_tf);
		

		JPanel panelIC = new JPanel();
		GridBagConstraints gbc_panelIC = new GridBagConstraints();
		gbc_panelIC.fill = GridBagConstraints.BOTH;
		gbc_panelIC.insets = new Insets(0, 0, 5, 5);
		gbc_panelIC.gridx = 0;
		gbc_panelIC.gridy = 3;
		level1_Pane.add(panelIC, gbc_panelIC);
		panelIC.setBorder(new TitledBorder(null, "Initial Condition", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panelIC.setLayout(new GridLayout(9, 1, 0, 0));

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
		gbc_chckbxRotation.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxRotation.fill = GridBagConstraints.BOTH;
		gbc_chckbxRotation.gridx = 0;
		gbc_chckbxRotation.gridy = 4;
		level1_Pane.add(chckbxRotation, gbc_chckbxRotation);

		chckbxRotation.addItemListener(dpE);
		chckbxSun.addItemListener(dpE);
		chckbxEarth.addItemListener(dpE);
		chckbxMoon.addItemListener(dpE);
		chckbxJupiter.addItemListener(dpE);
		btnPlot.addActionListener(dpE);
		comboBoxFrame.addActionListener(dpE);
	}

	public void updateGUI() {
		chckbxSun.setSelected(dpMain.dpParam.bodyGravOnOff[body.SUN.ordinal()]);
		chckbxMercury.setSelected(dpMain.dpParam.bodyGravOnOff[body.MERCURY.ordinal()]);
		chckbxVenus.setSelected(dpMain.dpParam.bodyGravOnOff[body.VENUS.ordinal()]);
		chckbxEarth.setSelected(dpMain.dpParam.bodyGravOnOff[body.EARTH.ordinal()]);
		chckbxMoon.setSelected(dpMain.dpParam.bodyGravOnOff[body.MOON.ordinal()]);		
		chckbxMars.setSelected(dpMain.dpParam.bodyGravOnOff[body.MARS.ordinal()]);
		chckbxJupiter.setSelected(dpMain.dpParam.bodyGravOnOff[body.JUPITER.ordinal()]);
		chckbxSaturn.setSelected(dpMain.dpParam.bodyGravOnOff[body.SATURN.ordinal()]);

		comboBoxFrame.setSelectedIndex(dpMain.dpParam.Eph.ephFrame.ordinal());		
		
		depart_date_picker.getModel().setYear(dpMain.dpParam.simulationDate.getYear());
		depart_date_picker.getModel().setMonth(dpMain.dpParam.simulationDate.getMonth());
		depart_date_picker.getModel().setDay(dpMain.dpParam.simulationDate.getDay());
		depart_date_picker.getModel().setSelected(true);
		tf_tf.setValue(dpMain.dpParam.tf);
		tf_x.setValue(dpMain.dpParam.y0[0]);
		tf_y.setValue(dpMain.dpParam.y0[1]);
		tf_z.setValue(dpMain.dpParam.y0[2]);
		tf_vx.setValue(dpMain.dpParam.y0[3]);
		tf_vy.setValue(dpMain.dpParam.y0[4]);
		tf_vz.setValue(dpMain.dpParam.y0[5]);

	}
}
