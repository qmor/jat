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

import jat.coreNOSA.algorithm.integrators.Printable;
import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.TwoBody;
import jat.coreNOSA.math.MatrixVector.data.Matrix;
import jat.coreNOSA.math.MatrixVector.data.VectorN;

public class TwoBodyAPL extends TwoBody {
	double initial_ta;

	public TwoBodyAPL(double mu, VectorN r, VectorN v) {
		super(mu, r, v);
		initial_ta = ta;
	}

	public TwoBodyAPL(double d, double e, double f, double g, double h, double i) {
	}

	public VectorN position(double t) {

		double[] temp = new double[6];

		// Determine step size
		double n = this.meanMotion();

		// determine initial E and M
		double sqrome2 = Math.sqrt(1.0 - this.e * this.e);
		double cta = Math.cos(this.ta);
		double sta = Math.sin(this.ta);
		double sine0 = (sqrome2 * sta) / (1.0 + this.e * cta);
		double cose0 = (this.e + cta) / (1.0 + this.e * cta);
		double e0 = Math.atan2(sine0, cose0);

		double ma = e0 - this.e * Math.sin(e0);
		double q = Math.sqrt((1.0 + this.e) / (1.0 - this.e));

		ma = ma + n * t;
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

		temp = this.randv();
		this.rv = new VectorN(temp);

		// Reset everything to before
		this.ta = initial_ta;

		VectorN out = new VectorN(3);
		out.x[0] = temp[0];
		out.x[1] = temp[1];
		out.x[2] = temp[2];
		//out.print("sat pos at t");
		return out;

	}

	// propagates from whatever ta is starting from time t=0 relative to the starting point 
	public void propagate(double t0, double tf, Printable pr, boolean print_switch, double steps) {
		double[] temp = new double[6];
		// double ta_save = this.ta;
		this.steps = steps;

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

		// determine sqrt(1+e/1-e)

		double q = Math.sqrt((1.0 + this.e) / (1.0 - this.e));

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
		// Reset everything to before
		this.ta = initial_ta;

	}

	public double[] randv(double ta) {
		double p = a * (1.0 - e * e);
		double cta = Math.cos(ta);
		double sta = Math.sin(ta);
		double opecta = 1.0 + e * cta;
		double sqmuop = Math.sqrt(this.mu / p);

		VectorN xpqw = new VectorN(6);
		xpqw.x[0] = p * cta / opecta;
		xpqw.x[1] = p * sta / opecta;
		xpqw.x[2] = 0.0;
		xpqw.x[3] = -sqmuop * sta;
		xpqw.x[4] = sqmuop * (e + cta);
		xpqw.x[5] = 0.0;

		Matrix cmat = PQW2ECI();

		VectorN rpqw = new VectorN(xpqw.x[0], xpqw.x[1], xpqw.x[2]);
		VectorN vpqw = new VectorN(xpqw.x[3], xpqw.x[4], xpqw.x[5]);

		VectorN rijk = cmat.times(rpqw);
		VectorN vijk = cmat.times(vpqw);

		double[] out = new double[6];

		for (int i = 0; i < 3; i++) {
			out[i] = rijk.x[i];
			out[i + 3] = vijk.x[i];
		}

		return out;
	}

	public double eccentricAnomaly(double ta) {
		double cta = Math.cos(ta);
		double e0 = Math.acos((e + cta) / (1.0 + e * cta));
		return e0;
	}

	public double meanAnomaly(double t) {

		return 2. * Math.PI * t / period();
	}

	public double t_from_ta() {

		double M = meanAnomaly();
		double P = period();

		return P * M / 2. / Math.PI;

	}

	public double ta_from_t(double t) {

		double M = meanAnomaly(t);
		double ea = solveKepler(M, this.e);

		double sinE = Math.sin(ea);
		double cosE = Math.cos(ea);
		double den = 1.0 - this.e * cosE;
		double sqrome2 = Math.sqrt(1.0 - this.e * this.e);
		double sinv = (sqrome2 * sinE) / den;
		double cosv = (cosE - this.e) / den;

		double ta = Math.atan2(sinv, cosv);
		if (this.ta < 0.0) {
			this.ta = this.ta + 2.0 * Constants.pi;
		}

		return ta;
	}

}

/*
 * public void propagate(double t0, double tf) { double[] temp = new double[6];
 * // this.ta = 0;
 * 
 * // Determine step size double n = this.meanMotion(); double period =
 * this.period(); double dt = period / steps; if ((t0 + dt) > tf) // check to
 * see if we're going past tf { dt = tf - t0; }
 * 
 * // determine initial E and M double sqrome2 = Math.sqrt(1.0 - this.e *
 * this.e); double cta = Math.cos(this.ta); double sta = Math.sin(this.ta);
 * double sine0 = (sqrome2 * sta) / (1.0 + this.e * cta); double cose0 = (this.e
 * + cta) / (1.0 + this.e * cta); double e0 = Math.atan2(sine0, cose0);
 * 
 * double ma = e0 - this.e * Math.sin(e0);
 * 
 * // determine sqrt(1+e/1-e)
 * 
 * double q = Math.sqrt((1.0 + this.e) / (1.0 - this.e));
 * 
 * // initialize t
 * 
 * double t = t0;
 * 
 * while (t < tf) { ma = ma + n * dt; double ea = solveKepler(ma, this.e);
 * 
 * double sinE = Math.sin(ea); double cosE = Math.cos(ea); double den = 1.0 -
 * this.e * cosE;
 * 
 * double sinv = (sqrome2 * sinE) / den; double cosv = (cosE - this.e) / den;
 * 
 * this.ta = Math.atan2(sinv, cosv); if (this.ta < 0.0) { this.ta = this.ta +
 * 2.0 * Constants.pi; }
 * 
 * t = t + dt;
 * 
 * temp = this.randv(); this.rv = new VectorN(temp);
 * 
 * if ((t + dt) > tf) { dt = tf - t; }
 * 
 * } }
 */

/*
 * public void propagate(double t0, double tf, Printable pr, boolean
 * print_switch) { double[] temp = new double[6];
 * 
 * // Determine step size double n = this.meanMotion(); double period =
 * this.period(); double dt = period / steps; if ((t0 + dt) > tf) // check to
 * see if we're going past tf { dt = tf - t0; }
 * 
 * // determine initial E and M double sqrome2 = Math.sqrt(1.0 - this.e *
 * this.e); double cta = Math.cos(this.ta); double sta = Math.sin(this.ta);
 * double sine0 = (sqrome2 * sta) / (1.0 + this.e * cta); double cose0 = (this.e
 * + cta) / (1.0 + this.e * cta); double e0 = Math.atan2(sine0, cose0);
 * 
 * double ma = e0 - this.e * Math.sin(e0);
 * 
 * // initialize t
 * 
 * double t = t0;
 * 
 * if (print_switch) { temp = this.randv(); pr.print(t, temp); }
 * 
 * while (t < tf) { ma = ma + n * dt; double ea = solveKepler(ma, this.e);
 * 
 * double sinE = Math.sin(ea); double cosE = Math.cos(ea); double den = 1.0 -
 * this.e * cosE;
 * 
 * double sinv = (sqrome2 * sinE) / den; double cosv = (cosE - this.e) / den;
 * 
 * this.ta = Math.atan2(sinv, cosv); if (this.ta < 0.0) { this.ta = this.ta +
 * 2.0 * Constants.pi; }
 * 
 * t = t + dt;
 * 
 * temp = this.randv(); this.rv = new VectorN(temp);
 * 
 * if (print_switch) { pr.print(t, temp); }
 * 
 * if ((t + dt) > tf) { dt = tf - t; }
 * 
 * } }
 */
