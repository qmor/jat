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
import jat.core.ephemeris.DE405Plus;
import jat.core.ephemeris.EphemerisPlotData;
import jat.core.plot.plot.Plot3DPanel;
import jat.core.plot.plot.PlotPanel;
import jat.core.plot.plot.plots.LinePlot;
import jat.coreNOSA.math.MatrixVector.data.VectorN;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class DE405PropagatorPlot extends JPanel {

	private static final long serialVersionUID = 4814672707864836820L;
	Plot3DPanel plot;
	int steps = 200;
	double[][] XYZ = new double[steps][3];
	double[][] points = new double[1][3];
	int step;
	DE405PropagatorMain dpMain;
	DE405Plus Eph;
	DE405PropagatorParameters dpParam;
	static boolean print = false;
	double plotBounds;

	public DE405PropagatorPlot(DE405PropagatorMain dpMain) {
		this.dpMain = dpMain;
		this.dpParam = dpMain.dpParam;
		this.Eph = dpMain.dpParam.Eph;
	}

	public void add_scene() {

		plot.addSpherePlot("Earth", 6378.1);
		doExample();
		// Vector3D y0v=new Vector3D(dpParam.y0[0],dpParam.y0[1],dpParam.y0[2]);
		// double plotBounds = 2*y0v.getNorm();
		plot.setFixedBounds(0, -plotBounds, plotBounds);
		plot.setFixedBounds(1, -plotBounds, plotBounds);
		plot.setFixedBounds(2, -plotBounds, plotBounds);
		plot.setLegendOrientation(PlotPanel.SOUTH);
	}

	public void make_plot() {
		// create your PlotPanel (you can use it as a JPanel) with a legend at
		// SOUTH
		plot = new Plot3DPanel("SOUTH");
		// add grid plot to the PlotPanel
		add_scene();
	}

	void doExample() {

		// Spacecraft Trajectory
		double[] y = new double[6];
		for (body b : body.values()) {
			Eph.bodyGravOnOff[b.ordinal()] = dpParam.bodyGravOnOff[b.ordinal()];
		}
		dpParam.y0[0] = (Double) dpMain.dpGUI.tf_x.getValue();
		dpParam.y0[1] = (Double) dpMain.dpGUI.tf_y.getValue();
		dpParam.y0[2] = (Double) dpMain.dpGUI.tf_z.getValue();
		dpParam.y0[3] = (Double) dpMain.dpGUI.tf_vx.getValue();
		dpParam.y0[4] = (Double) dpMain.dpGUI.tf_vy.getValue();
		dpParam.y0[5] = (Double) dpMain.dpGUI.tf_vz.getValue();
		dpParam.tf = (Double) dpMain.dpGUI.tf_tf.getValue();

		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, dpParam.tf / 10.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(Eph.stepHandler);
		FirstOrderDifferentialEquations ode = Eph;
		Eph.setIntegrationStartTime(dpParam.simulationDate);
		Eph.reset();

		dp853.integrate(ode, 0.0, dpParam.y0, dpParam.tf, y);
		if (print) {
			String nf = "%10.3f ";
			String format = nf + nf + nf + nf + nf;
			System.out.printf(format, dpParam.tf, y[0], y[1], y[2], Eph.energy(dpParam.tf, y));
			System.out.println();
		}

		LinePlot l1 = new LinePlot("spacecraft", Color.RED, getXYZforPlot(Eph.xsol, Eph.ysol, Eph.zsol));
		l1.closed_curve = false;
		plot.addPlot(l1);


		
		
		VectorN EarthPos = null;
		VectorN MoonPost0 = null;
		VectorN MoonPostf = null;
		VectorN SunPos = null;
		try {
			SunPos = Eph.get_planet_pos(body.SUN, dpMain.dpParam.simulationDate);
			EarthPos = Eph.get_planet_pos(body.EARTH, dpMain.dpParam.simulationDate);
			MoonPost0 = Eph.get_planet_pos(body.MOON, dpMain.dpParam.simulationDate);
			MoonPostf = Eph.get_planet_pos(body.MOON, dpMain.dpParam.simulationDate.plus(dpMain.dpParam.tf));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// addPoint(plot, "Sun", java.awt.Color.ORANGE, SunPos.x[0],
		// SunPos.x[1], SunPos.x[2]);
		addPoint(plot, "Moon t0", java.awt.Color.GRAY, MoonPost0.x[0], MoonPost0.x[1], MoonPost0.x[2]);
		addPoint(plot, "Moon tf", java.awt.Color.GRAY, MoonPostf.x[0], MoonPostf.x[1], MoonPostf.x[2]);
		// addPoint(plot, "Earth", java.awt.Color.MAGENTA, EarthPos.x[0],
		// EarthPos.x[1], EarthPos.x[2]);

		EphemerisPlotData epd=new EphemerisPlotData(dpMain.dpParam.Eph,body.MOON,dpMain.dpParam.simulationDate,dpParam.tf,100);		
		LinePlot lMoon = new LinePlot("Moon", Color.green, epd.XYZ);
		lMoon.closed_curve = false;
		plot.addPlot(lMoon);
		
		
		
		
	}

	double[][] getXYZforPlot(ArrayList<Double> xsol, ArrayList<Double> ysol, ArrayList<Double> zsol) {
		int arraySize = xsol.size();
		double[] xsolArray = ArrayUtils.toPrimitive(xsol.toArray(new Double[arraySize]));
		double[] ysolArray = ArrayUtils.toPrimitive(ysol.toArray(new Double[arraySize]));
		double[] zsolArray = ArrayUtils.toPrimitive(zsol.toArray(new Double[arraySize]));
		double[][] XYZ = new double[arraySize][3];
		plotBounds = 0.;
		for (int i = 0; i < arraySize; i++) {
			XYZ[i][0] = xsolArray[i];
			XYZ[i][1] = ysolArray[i];
			XYZ[i][2] = zsolArray[i];
			plotBounds=Math.max(plotBounds, Math.abs(XYZ[i][0]));
			plotBounds=Math.max(plotBounds, Math.abs(XYZ[i][1]));
			plotBounds=Math.max(plotBounds, Math.abs(XYZ[i][2]));
		}
		plotBounds/=1.5;
		return XYZ;
	}

	void addPoint(Plot3DPanel p, String s, Color c, double x, double y, double z) {
		double[][] points = new double[1][3];
		points[0][0] = x;
		points[0][1] = y;
		points[0][2] = z;
		p.addScatterPlot(s, c, points);
	}

}
