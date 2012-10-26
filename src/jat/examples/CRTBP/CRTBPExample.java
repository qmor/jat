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

package jat.examples.CRTBP;

import jat.core.cm.CRTBP;
import jat.core.plot.plot.FrameView;
import jat.core.plot.plot.Plot2DPanel;
import jat.core.plot.plot.PlotPanel;
import jat.core.plot.plot.plots.LinePlot;

import java.awt.Color;

import javax.swing.JFrame;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class CRTBPExample {

	public static void main(String[] args) {

		CRTBP myCRTBP = new CRTBP(0.15);
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8,
				100.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(myCRTBP.stepHandler);

		FirstOrderDifferentialEquations ode = myCRTBP;

		double tf = 10.;
		double[] y ; // initial state

		for (int i = 1; i < 2; i++) {
			tf=i*20.;
			y = new double[] { .0, .5, 0, .0, .5, 0 }; // initial state

			dp853.integrate(ode, 0.0, y, tf, y); // now y contains final state
													// at
													// time t=16.0
			System.out.printf("%9.6f %9.6f %9.6f %9.6f %9.6f", tf, y[0], y[1], y[2],
					myCRTBP.JacobiIntegral(y));
			System.out.println();
		}
		Double[] objArray = myCRTBP.time
				.toArray(new Double[myCRTBP.time.size()]);
		double[] timeArray = ArrayUtils.toPrimitive(objArray);
		double[] xsolArray = ArrayUtils.toPrimitive(myCRTBP.xsol
				.toArray(new Double[myCRTBP.time.size()]));
		double[] ysolArray = ArrayUtils.toPrimitive(myCRTBP.ysol
				.toArray(new Double[myCRTBP.time.size()]));

		double[][] XY = new double[timeArray.length][2];

		// int a=0;
		// System.arraycopy(timeArray,0,XY[a],0,timeArray.length);
		// System.arraycopy(ysolArray,0,XY[1],0,ysolArray.length);

		for (int i = 0; i < timeArray.length; i++) {
			// XY[i][0] = timeArray[i];
			XY[i][0] = xsolArray[i];
			XY[i][1] = ysolArray[i];
		}
		Plot2DPanel p = new Plot2DPanel();
		LinePlot l = new LinePlot("sin", Color.RED, XY);
		l.closed_curve = false;
		l.draw_dot = true;
		p.addPlot(l);
		p.setLegendOrientation(PlotPanel.SOUTH);

		new FrameView(p).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}