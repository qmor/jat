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

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

public class PorkChopPlot_GUI extends JPanel {
	private static final long serialVersionUID = 7907463395861359469L;
	PorkChopPlot_Events pcpE;
	private JPanel SearchIntervalPanel;
	private JPanel MissionSelectPanel;
	private JPanel DatesPanel;
	private JPanel IntervalPanel;
	private JPanel KeyboardPanel;
	public JButton btn_make_plot;
	private JLabel lbldepart;
	private JLabel lblarrive;
	JDatePicker depart_date_picker;
	JDatePicker arrival_date_picker;
	JDatePickerImpl datePicker1;
	JSpinner spinner_days;
	private JLabel lblDays;
	JSpinner spinner_steps;
	private JLabel lblSteps;
	private JLabel lblDepartureDate;
	public JFormattedTextField field_selected_departure_date;
	private JLabel lblArrivalDate;
	public JFormattedTextField field_selected_arrival_date;
	private JLabel lblTotalDeltav;
	public JFormattedTextField field_total_deltav;
	public JRadioButton rdbtnPlotRotate;
	public JRadioButton rdbtnFlightSelect;
	public JButton btnGoMin;
	private JLabel lblNewLabel;
	public JButton btnStep;

	public PorkChopPlot_GUI() {
		pcpE = new PorkChopPlot_Events(this);
		SearchIntervalPanel = new JPanel();
		MissionSelectPanel = new JPanel();
		DatesPanel = new JPanel();
		IntervalPanel = new JPanel();
		DatesPanel.setLayout(new GridLayout(5, 1, 5, 5));
		IntervalPanel.setLayout(new GridLayout(2, 2, 5, 5));
		KeyboardPanel = new JPanel();

		SearchIntervalPanel.setBorder(new TitledBorder(null, "Search Interval", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		MissionSelectPanel.setBorder(new TitledBorder(null, "Flight Selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		KeyboardPanel.setBorder(new TitledBorder(null, "Keyboard Mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		depart_date_picker = JDateComponentFactory.createJDatePicker();
		//depart_date_picker = (JDatePickerImpl) JDateComponentFactory.createJDatePicker();
		depart_date_picker.setTextEditable(true);
		depart_date_picker.setShowYearButtons(true);
		//depart_date_picker.getModel().setYear(2012);
		//depart_date_picker.getModel().setSelected(true);

		arrival_date_picker = JDateComponentFactory.createJDatePicker();
		depart_date_picker.setTextEditable(true);
		depart_date_picker.setShowYearButtons(true);

		add(SearchIntervalPanel);

		SearchIntervalPanel.setLayout(new BoxLayout(SearchIntervalPanel, BoxLayout.Y_AXIS));
		SearchIntervalPanel.add(DatesPanel);
		SearchIntervalPanel.add(IntervalPanel);

		lbldepart = new JLabel("Departure");
		DatesPanel.add(lbldepart);
		DatesPanel.add((JComponent) depart_date_picker);

		lblarrive = new JLabel("Earliest Arrival");
		DatesPanel.add(lblarrive);

		DatesPanel.add((JComponent) arrival_date_picker);
		
		lblNewLabel = new JLabel("Interval");
		DatesPanel.add(lblNewLabel);

		spinner_days = new JSpinner();
		IntervalPanel.add(spinner_days);
		spinner_days.setModel(new SpinnerNumberModel(new Integer(500), null, null, new Integer(1)));

		lblDays = new JLabel("Days");
		IntervalPanel.add(lblDays);

		spinner_steps = new JSpinner();
		IntervalPanel.add(spinner_steps);
		spinner_steps.setModel(new SpinnerNumberModel(new Integer(10), null, null, new Integer(1)));

		lblSteps = new JLabel("Steps");
		IntervalPanel.add(lblSteps);

		btn_make_plot = new JButton("Make Plot");
		SearchIntervalPanel.add(btn_make_plot);

		btn_make_plot.addActionListener(pcpE);
		
		add(KeyboardPanel);
		KeyboardPanel.setLayout(new BoxLayout(KeyboardPanel, BoxLayout.Y_AXIS));
		
		rdbtnPlotRotate = new JRadioButton("Plot Rotate/Zoom");
		rdbtnPlotRotate.setSelected(true);
		KeyboardPanel.add(rdbtnPlotRotate);
		
		rdbtnFlightSelect = new JRadioButton("Flight Select");
		KeyboardPanel.add(rdbtnFlightSelect);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnPlotRotate);
		group.add(rdbtnFlightSelect);
		
		
		add(MissionSelectPanel);
		// MissionSelectPanel.setLayout(new BoxLayout(MissionSelectPanel,
		// BoxLayout.Y_AXIS));
		MissionSelectPanel.setLayout(new GridLayout(8, 1));

		lblDepartureDate = new JLabel("Departure Date");
		MissionSelectPanel.add(lblDepartureDate);

		field_selected_departure_date = new JFormattedTextField();

		// field_selected_departure_date.setMaximumSize(maximumSize);
		MissionSelectPanel.add(field_selected_departure_date);

		lblArrivalDate = new JLabel("Arrival Date");
		MissionSelectPanel.add(lblArrivalDate);

		field_selected_arrival_date = new JFormattedTextField();
		MissionSelectPanel.add(field_selected_arrival_date);

		lblTotalDeltav = new JLabel("Total DeltaV");
		MissionSelectPanel.add(lblTotalDeltav);

		field_total_deltav = new JFormattedTextField();
		MissionSelectPanel.add(field_total_deltav);
		
		btnGoMin = new JButton("Go To Minimum");
		btnGoMin.setEnabled(false);
		MissionSelectPanel.add(btnGoMin);
		btnGoMin.addActionListener(pcpE);
		
		btnStep = new JButton("Step");
		btnStep.setEnabled(false);
		MissionSelectPanel.add(btnStep);
		btnStep.addActionListener(pcpE);
		
		rdbtnPlotRotate.addActionListener(pcpE);
		rdbtnFlightSelect.addActionListener(pcpE);
	}

	// private void JCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {
	// plot.setParallelProjection(!jCheckBox2.isSelected());
	// }
	//
	// private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {
	// surf.setLogZscaling(jCheckBox1.isSelected());
	// }

}