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

package jat.examples.ephemeris;

import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame;
import jat.core.ephemeris.DE405Plus;
import jat.core.plot.plot.FrameView;
import jat.core.plot.plot.Plot2DPanel;
import jat.core.plot.plot.PlotPanel;
import jat.core.plot.plot.plots.LinePlot;
import jat.core.spacetime.TimeAPL;
import jat.core.util.PathUtil;
import jat.coreNOSA.math.MatrixVector.data.VectorN;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class DE405PropagatePlot {
	static boolean print = false;

	void doExample() {
		double tf = 3600*24*300;
		double[] y = { 2e8, 0, 0, 0, 24.2, 0 }; // initial state

		PathUtil path = new PathUtil();
		DE405Plus Eph = new DE405Plus(path);
		Eph.setFrame(DE405Frame.frame.HEE);
		Eph.printSteps = true;
		TimeAPL myTime = new TimeAPL(2003, 3, 1, 12, 0, 0);
		Eph.setIntegrationStartTime(myTime);

		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, tf / 10.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(Eph.stepHandler);
		FirstOrderDifferentialEquations ode = Eph;

		dp853.integrate(ode, 0.0, y, tf, y); // now y contains final state at
												// time tf
		if (print) {
			String nf = "%10.3f ";
			String format = nf + nf + nf + nf + nf;
			System.out.printf(format, tf, y[0], y[1], y[2], Eph.energy(y));
			System.out.println();
		}

		int arraySize = Eph.time.size();
		double[] timeArray = ArrayUtils.toPrimitive(Eph.time.toArray(new Double[arraySize]));
		double[] xsolArray = ArrayUtils.toPrimitive(Eph.xsol.toArray(new Double[arraySize]));
		double[] ysolArray = ArrayUtils.toPrimitive(Eph.ysol.toArray(new Double[arraySize]));
		double[][] XY = new double[timeArray.length][2];
		for (int i = 0; i < timeArray.length; i++) {
			XY[i][0] = xsolArray[i];
			XY[i][1] = ysolArray[i];
		}

		Plot2DPanel p = new Plot2DPanel();
		LinePlot l = new LinePlot("spacecraft", Color.RED, XY);
		l.closed_curve = false;
		p.addPlot(l);
		VectorN EarthPos = null;
		try {
			EarthPos = Eph.get_planet_pos(body.EARTH,myTime );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addPoint(p, "Earth", java.awt.Color.BLUE, EarthPos.x[0], EarthPos.x[1]);

		p.setLegendOrientation(PlotPanel.SOUTH);
		double plotSize = 2e8;
		p.setFixedBounds(0, -plotSize, plotSize);
		p.setFixedBounds(1, -plotSize, plotSize);
		new FrameView(p).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	void addPoint(Plot2DPanel p, String s, Color c, double x, double y) {
		double[][] points = new double[1][2];
		points[0][0] = x;
		points[0][1] = y;
		p.addScatterPlot(s, c, points);
	}

	public static void main(String[] args) {

		DE405PropagatePlot ex = new DE405PropagatePlot();
		ex.doExample();

	}

}
