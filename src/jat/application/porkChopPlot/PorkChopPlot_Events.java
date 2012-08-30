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

import jat.core.algorithm.optimization.DataArraySearch;
import jat.core.spacetime.TimeAPL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

public class PorkChopPlot_Events implements ActionListener {

	PorkChopPlot_GUI pcpGUI;
	PorkChopPlot_main main;

	public PorkChopPlot_Events(PorkChopPlot_GUI pcpGUI) {
		this.pcpGUI = pcpGUI;
	}

	public void setMain(PorkChopPlot_main main) {
		this.main = main;
	}

	public void actionPerformed(ActionEvent ev) {

		if (ev.getSource() == pcpGUI.btn_make_plot) {
			// System.out.println("make plot button");
			int dep_year = pcpGUI.depart_date_picker.getModel().getYear();
			int dep_month = pcpGUI.depart_date_picker.getModel().getMonth() + 1;
			int dep_day = pcpGUI.depart_date_picker.getModel().getDay();
			int arr_year = pcpGUI.arrival_date_picker.getModel().getYear();
			int arr_month = pcpGUI.arrival_date_picker.getModel().getMonth() + 1;
			int arr_day = pcpGUI.arrival_date_picker.getModel().getDay();

			// System.out.println(year+"/"+day);
			Object sp2 = main.pcpGUI.spinner_days.getValue();
			int days = Integer.parseInt(sp2.toString());
			Object sp1 = main.pcpGUI.spinner_steps.getValue();
			int steps = Integer.parseInt(sp1.toString());

			// main.surf.pcplot_data = new pcplot_Jat3D_Data();

			main.pcpPlot.pcplot_data.search_depart_time_start = new TimeAPL(dep_year, dep_month, dep_day, 1, 1, 1);
			main.pcpPlot.pcplot_data.search_arrival_time_start = new TimeAPL(arr_year, arr_month, arr_day, 1, 1, 1);

			try {
				main.pcpPlot.pcplot_data.p.make_porkchop_plot(main.pcpPlot.pcplot_data.search_depart_time_start,
						main.pcpPlot.pcplot_data.search_arrival_time_start, days, steps);
				main.pcpPlot.pcplot_data.update();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(main, "DE405 Ephemeris data file not found.");
				e.printStackTrace();
				System.exit(0);
			}

			// main.remove(main.surf);
			// main.surf = new SurfacePlot3D();
			main.pcpPlot.setData(main.pcpPlot.pcplot_data);
			main.pcpPlot.keyBehavior_u.reset();
			// main.add(main.surf, BorderLayout.CENTER);
			// main.revalidate();
			main.repaint();

		}

		if (ev.getSource() == pcpGUI.btnGoMin) {
			goToMinimum();
		}

		if (ev.getSource() == pcpGUI.btnStep) {
			step();
		}

		if (pcpGUI.rdbtnPlotRotate.isSelected()) {
			main.pcpPlot.requestFocusInWindow();
			main.pcpPlot.keyBehaviorSwitch.setWhichChild(0);
			main.pcpPlot.flightSelectorSwitch.setWhichChild(0);
			main.pcpGUI.btnGoMin.setEnabled(false);
			main.pcpGUI.btnStep.setEnabled(false);
		}

		if (pcpGUI.rdbtnFlightSelect.isSelected()) {
			main.pcpPlot.requestFocusInWindow();
			main.pcpPlot.keyBehaviorSwitch.setWhichChild(2);
			main.pcpPlot.flightSelectorSwitch.setWhichChild(1);
			main.pcpPlot.keyBehavior_u.updateMarker();
			main.pcpGUI.btnGoMin.setEnabled(true);
			main.pcpGUI.btnStep.setEnabled(true);
		}
	}

	public void step() {
		int x_index = main.pcpPlot.keyBehavior_u.x_index;
		int y_index = main.pcpPlot.keyBehavior_u.y_index;
		DataArraySearch d = new DataArraySearch(main.pcpPlot.pcplot_data.data, x_index, y_index);
		d.step();
		main.pcpPlot.keyBehavior_u.x_index = d.x_index;
		main.pcpPlot.keyBehavior_u.y_index = d.y_index;
		main.pcpPlot.keyBehavior_u.updateMarker();
	}

	public void goToMinimum() {
		int x_index = main.pcpPlot.keyBehavior_u.x_index;
		int y_index = main.pcpPlot.keyBehavior_u.y_index;
		DataArraySearch d = new DataArraySearch(main.pcpPlot.pcplot_data.data, x_index, y_index);
		d.goToLocalMinimum();
		main.pcpPlot.keyBehavior_u.x_index = d.x_index;
		main.pcpPlot.keyBehavior_u.y_index = d.y_index;
		main.pcpPlot.keyBehavior_u.updateMarker();
	}

}
