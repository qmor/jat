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

import jat.jat3D.BodyGroup3D;
import jat.jat3D.ColorCube3D;
import jat.jat3D.Marker3D;
import jat.jat3D.jatScene3D;
import jat.jat3D.behavior.jat_KeyBehavior_Translate;
import jat.jat3D.plot3D.BoundingBox3D;
import jat.jat3D.plot3D.JatPlot3D;
import jat.jat3D.plot3D.NormalizedBinned2DData;
import jat.jat3D.plot3D.NormalizedBinned2DLogData;
import jat.jat3D.plot3D.SurfaceBuilder;

import java.io.IOException;

import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Switch;
import javax.swing.JOptionPane;

public class SurfacePlot3D extends JatPlot3D {
	private static final long serialVersionUID = 4491485448719846256L;
	private PorkChopPlot_main main;
	public pcplot_Jat3D_Data pcplot_data;
	NormalizedBinned2DData ndata;
	public SurfaceBuilder builder;
	public Node plot;
	public boolean logZscaling = false;
	public Switch flightSelectorSwitch;
	public Marker3D m;
	public PorkChopPlot_KeyBehavior keyBehavior_u;

	public SurfacePlot3D(PorkChopPlot_main main) {
		super();
		initialViewingPosition.x = -.5;
		initialViewingPosition.y = -1;
		initialViewingPosition.z = 2;
		this.main = main;
	}

	public Node createScene() {
		Group g = new Group();
		jatScene = new jatScene3D();

		try {
			pcplot_data = new pcplot_Jat3D_Data();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "DE405 Ephemeris data file not found.");
			e.printStackTrace();
			System.exit(0);
		}


		main.pcpGUI.depart_date_picker.getModel().setYear(pcplot_data.dep_year);
		main.pcpGUI.depart_date_picker.getModel().setMonth(pcplot_data.dep_month);
		main.pcpGUI.depart_date_picker.getModel().setDay(pcplot_data.dep_day);
		main.pcpGUI.depart_date_picker.getModel().setSelected(true);

		main.pcpGUI.arrival_date_picker.getModel().setYear(pcplot_data.arr_year);
		main.pcpGUI.arrival_date_picker.getModel().setMonth(pcplot_data.arr_month);
		main.pcpGUI.arrival_date_picker.getModel().setDay(pcplot_data.arr_day);
		main.pcpGUI.arrival_date_picker.getModel().setSelected(true);

		builder = new SurfaceBuilder();


		if (logZscaling)
			plot = builder.buildContent(new NormalizedBinned2DLogData(pcplot_data));
		else {
			ndata = new NormalizedBinned2DData(pcplot_data);
			plot = builder.buildContent(ndata);
		}
		float boxsize = 1;

		// setData(pcplot_data);
		jatScene.InitialTranslation.x = boxsize / 2;
		jatScene.InitialTranslation.y = boxsize / 2;
		jatScene.addChild(plot);

		viewingCenter.x = boxsize / 2;
		viewingCenter.y = boxsize / 2;
		viewingCenter.z = boxsize / 2;

		// RGBAxes3D r = new RGBAxes3D();
		// jatScene.addChild(r);

		g.addChild(jatScene);

		// initial zoom: exponent of ten times graph unit
		exponent = 0;
		bbox = new BoundingBox3D(0, 1);
		bbox.xAxisLabel = "Dep " + pcplot_data.p.A.RowLabels[0] + " to "
				+ pcplot_data.p.A.RowLabels[pcplot_data.xBins() - 1];
		bbox.yAxisLabel = "Arr " + pcplot_data.p.A.ColumnLabels[0] + " to "
				+ pcplot_data.p.A.ColumnLabels[pcplot_data.xBins() - 1];
		bbox.zAxisLabel = (int) pcplot_data.zMin()+" to " + (int) pcplot_data.zMax()+" km/s";
		bbox.createAxes(exponent);
		bboxgroup = new BodyGroup3D(bbox, "Box");

		g.addChild(bboxgroup);

		flightSelectorSwitch = new Switch();
		flightSelectorSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		flightSelectorSwitch.addChild(new ColorCube3D(.00001));

		m = new Marker3D();
		flightSelectorSwitch.addChild(m);
		flightSelectorSwitch.setWhichChild(0);
		g.addChild(flightSelectorSwitch);

		return g;
	}

	public void setData(pcplot_Jat3D_Data data) {
		this.pcplot_data = data;

		System.out.println(pcplot_data.p.A.ColumnLabels[0]);
		bbox.xAxisLabel = "Dep " + pcplot_data.p.A.RowLabels[0] + " to "
				+ pcplot_data.p.A.RowLabels[pcplot_data.xBins() - 1];
		bbox.yAxisLabel = "Arr " + pcplot_data.p.A.ColumnLabels[0] + " to "
				+ pcplot_data.p.A.ColumnLabels[pcplot_data.xBins() - 1];
		bbox.zAxisLabel = (int) pcplot_data.zMin()+" to " + (int) pcplot_data.zMax()+" km/s";
		bbox.setLabels(exponent);

		if (logZscaling)
			builder.updatePlot(new NormalizedBinned2DLogData(data));
		else {
			ndata = new NormalizedBinned2DData(pcplot_data);
			builder.updatePlot(ndata);
		}
	}

	public void addBehavior() {
		jat_KeyBehavior_Translate keyBehavior_t = new jat_KeyBehavior_Translate(this);
		keyBehavior_t.setSchedulingBounds(getDefaultBounds());
		keyBehaviorSwitch.addChild(keyBehavior_t);
		// pcplot_Jat3D_main main = null;
		keyBehavior_u = new PorkChopPlot_KeyBehavior(main);
		keyBehavior_u.setSchedulingBounds(getDefaultBounds());
		keyBehaviorSwitch.addChild(keyBehavior_u);
	}

	public boolean getLogZscaling() {
		return logZscaling;
	}

	public void setLogZscaling(boolean b) {
		// System.out.println("setting Log Scaling to: " + b + " from: " +
		// logZscaling);
		if (logZscaling != b) {
			logZscaling = b;
			if (pcplot_data != null) {
				if (logZscaling)
					builder.updatePlot(new NormalizedBinned2DLogData(pcplot_data));
				else
					builder.updatePlot(new NormalizedBinned2DData(pcplot_data));
			}
		}
	}
}
