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

package jat.examples.TwoBodyExample;

import jat.core.cm.TwoBodyAPL;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class TwoBodyExample {

	public TwoBodyExample() {
	}

	public static void main(String[] args) {
		TwoBodyExample x = new TwoBodyExample();

		// create a TwoBody orbit using orbit elements
		TwoBodyAPL sat = new TwoBodyAPL(7000.0, 0.3, 0.0, 0.0, 0.0, 0.0);

		// find out the period of the orbit
		double period = sat.period();

		// set the final time = one orbit period
		double tf = period;

		// set the initial time to zero
		double t0 = 0.0;

		// propagate the orbit
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
		dp853.addStepHandler(sat.stepHandler);
		double[] y = new double[] { 7000.0, 0, 0, .0, 8, 0 }; // initial state

		dp853.integrate(sat, 0.0, y, tf, y); // now y contains final state at tf		
		
	}
}
