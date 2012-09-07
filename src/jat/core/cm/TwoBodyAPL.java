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

package jat.core.cm;

import jat.core.algorithm.integrators.Printable;
import jat.core.math.matvec.data.VectorN;

public class TwoBodyAPL extends TwoBody {

	public TwoBodyAPL(double d, double e, double f, double g, double h, double i) {
	}

	public TwoBodyAPL(double mu, VectorN r, VectorN v) {
		super(mu, r, v);
	}

	// modified so that it can be used repeatedly. Currently, propagates from
	// last position, rather than from t0.
	public void propagate(double t0, double tf, Printable pr, boolean print_switch) {
		double[] temp = new double[6];
		this.ta = 0;

		// Determine step size
		double n = this.meanMotion();
		double period = this.period();
		double dt = period / steps;
		if ((t0 + dt) > tf) // check to see if we're going past tf
		{
			dt = tf - t0;
		}

		// determine initial E and M
		double sqrome2 = Math.sqrt(1.0 - this.e * this.e);
		double cta = Math.cos(this.ta);
		double sta = Math.sin(this.ta);
		double sine0 = (sqrome2 * sta) / (1.0 + this.e * cta);
		double cose0 = (this.e + cta) / (1.0 + this.e * cta);
		double e0 = Math.atan2(sine0, cose0);

		double ma = e0 - this.e * Math.sin(e0);

		// initialize t

		double t = t0;

		if (print_switch) {
			temp = this.randv();
			pr.print(t, temp);
		}

		while (t < tf) {
			ma = ma + n * dt;
			double ea = solveKepler(ma, this.e);

			double sinE = Math.sin(ea);
			double cosE = Math.cos(ea);
			double den = 1.0 - this.e * cosE;

			double sinv = (sqrome2 * sinE) / den;
			double cosv = (cosE - this.e) / den;

			this.ta = Math.atan2(sinv, cosv);
			if (this.ta < 0.0) {
				this.ta = this.ta + 2.0 * Constants.pi;
			}

			t = t + dt;

			temp = this.randv();
			this.rv = new VectorN(temp);

			if (print_switch) {
				pr.print(t, temp);
			}

			if ((t + dt) > tf) {
				dt = tf - t;
			}

		}
	}

}