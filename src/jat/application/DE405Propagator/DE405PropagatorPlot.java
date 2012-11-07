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

import jat.core.ephemeris.DE405Plus;
import jat.core.plot.plot.Plot3DPanel;
import jat.core.plot.plot.PlotPanel;
import jat.core.plot.plot.plots.LinePlot;

import java.awt.Color;
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

	public DE405PropagatorPlot(DE405PropagatorMain dpMain) {
		this.dpMain = dpMain;
		this.Eph = dpMain.Eph;
	}

	public void add_scene() {

		// points=new double[1][3] ;
		// points[0][0]=sat.rv.x[0];
		// points[0][1]=sat.rv.x[1];
		// points[0][2]=sat.rv.x[2];

		// plot.addSpherePlot("earth", cm.earth_radius);
		// plot.addLinePlot("orbit", XYZ, true);
		// plot.addScatterPlot("satellite" ,1,5, points);
		doExample();
		double size = max;
		plot.setFixedBounds(0, -size, size);
		plot.setFixedBounds(1, -size, size);
		plot.setFixedBounds(2, -size, size);
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
		double tf = 3600 * 24 * 300;
		double[] y0 = { 2e8, 0, 0, 0, 24.2, 0 }; // initial state
		double[] y = new double[6];

		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, tf / 10.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(Eph.stepHandler);
		FirstOrderDifferentialEquations ode = Eph;

		dp853.integrate(ode, 0.0, y0, tf, y); // now y contains final state at
												// time tf
		if (print) {
			String nf = "%10.3f ";
			String format = nf + nf + nf + nf + nf;
			System.out.printf(format, tf, y[0], y[1], y[2], Eph.energy(tf, y));
			System.out.println();
		}

		// Plot2DPanel p = new Plot2DPanel();
		LinePlot l1 = new LinePlot("Jup. off", Color.RED, getXYZforPlot(Eph.xsol, Eph.ysol, Eph.zsol));
		l1.closed_curve = false;
		plot.addPlot(l1);

		// Eph.reset();
		// Eph.planetOnOff[body.JUPITER.ordinal()] = true;
		// dp853.integrate(ode, 0.0, y0, tf, y); // now y contains final state
		// at

		// LinePlot l2 = new LinePlot("Jup. on", Color.BLUE,
		// getXYforPlot(Eph.xsol,Eph.ysol));
		// l2.closed_curve = false;
		// p.addPlot(l2);

		// VectorN EarthPos = null;
		// try {
		// EarthPos = Eph.get_planet_pos(body.EARTH, myTime);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// addPoint(p, "Earth", java.awt.Color.BLUE, EarthPos.x[0],
		// EarthPos.x[1]);

		plot.setLegendOrientation(PlotPanel.SOUTH);
		// double plotSize = 2e8;
		// p.setFixedBounds(0, -plotSize, plotSize);
		// p.setFixedBounds(1, -plotSize, plotSize);
		// new FrameView(p).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			XYZ[i][2] = ysolArray[i];
		}

		return XYZ;
	}

}
