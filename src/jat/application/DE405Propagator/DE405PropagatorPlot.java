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

import jat.core.ephemeris.DE405Body;
import jat.core.ephemeris.DE405Plus;
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
	double max = 1e8;

	Plot3DPanel plot;
	int steps = 200;
	double[][] XYZ = new double[steps][3];
	double[][] points = new double[1][3];
	int step;
	DE405PropagatorMain dpMain;
	DE405Plus Eph;
	DE405PropagatorParameters dpParam;

	public DE405PropagatorPlot(DE405PropagatorMain dpMain) {
		this.dpMain = dpMain;
		this.Eph = dpMain.Eph;
		this.dpParam = dpMain.dpParam;
	}

	public void add_scene() {

		plot.addSpherePlot("sun", 1e6);
		doExample();
		double size = max;
		plot.setFixedBounds(0, -size, size);
		plot.setFixedBounds(1, -size, size);
		plot.setFixedBounds(2, -size, size);
		plot.setLegendOrientation(PlotPanel.SOUTH);
	}

	public void make_plot() {
		// create your PlotPanel (you can use it as a JPanel) with a legend at
		// SOUTH
		plot = new Plot3DPanel("SOUTH");
		// add grid plot to the PlotPanel
		add_scene();
	}

	static boolean print = true;

	void doExample() {
		// double tf = 3600 * 24 * 300;
		// double[] y0 = { 2e8, 0, 0, 0, 24.2, 0 }; // initial state

		dpParam.y0[0]=-1.394163164819393E8;
		dpParam.y0[1]=4.892838708144717E7;
		dpParam.y0[2]=-1458.2923902980983;
		
		double[] y = new double[6];

		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, dpParam.tf / 10.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(Eph.stepHandler);
		FirstOrderDifferentialEquations ode = Eph;
		Eph.reset();
		dp853.integrate(ode, 0.0, dpParam.y0, dpParam.tf, y); // now y contains
																// final
		// state at
		// time tf
		if (print) {
			String nf = "%10.3f ";
			String format = nf + nf + nf + nf + nf;
			System.out.printf(format, dpParam.tf, y[0], y[1], y[2], Eph.energy(dpParam.tf, y));
			System.out.println();
		}

		LinePlot l1 = new LinePlot("spacecraft", Color.RED, getXYZforPlot(Eph.xsol, Eph.ysol, Eph.zsol));
		l1.closed_curve = false;
		plot.addPlot(l1);

		Eph.planetOnOff[DE405Body.body.EARTH.ordinal()] = true;

		VectorN EarthPos = null;
		try {
			EarthPos = Eph.get_planet_pos(DE405Body.body.EARTH, dpMain.dpParam.simulationDate);
		} catch (IOException e) {
			e.printStackTrace();
		}

		addPoint(plot, "Earth", java.awt.Color.MAGENTA, EarthPos.x[0], EarthPos.x[1], EarthPos.x[2]);

	}

	double[][] getXYZforPlot(ArrayList<Double> xsol, ArrayList<Double> ysol, ArrayList<Double> zsol) {
		int arraySize = xsol.size();
		double[] xsolArray = ArrayUtils.toPrimitive(xsol.toArray(new Double[arraySize]));
		double[] ysolArray = ArrayUtils.toPrimitive(ysol.toArray(new Double[arraySize]));
		double[] zsolArray = ArrayUtils.toPrimitive(zsol.toArray(new Double[arraySize]));
		double[][] XYZ = new double[arraySize][3];
		for (int i = 0; i < arraySize; i++) {
			XYZ[i][0] = xsolArray[i];
			XYZ[i][1] = ysolArray[i];
			XYZ[i][2] = zsolArray[i];
		}

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
