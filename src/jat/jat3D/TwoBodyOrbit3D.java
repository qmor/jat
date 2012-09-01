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

package jat.jat3D;

import jat.core.algorithm.integrators.*;
import jat.core.cm.*;
import jat.core.math.matvec.data.VectorN;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * @author Tobias Berthold
 * @version 1.0
 */
public class TwoBodyOrbit3D extends Shape3D implements Printable {
	public double[] coords;
	public double[] t, x, y, z;
	double tof; // time of flight
	int j = 0;
	Color3f Color = Colors.pink;
	private int steps = 500;
	Constants c = new Constants();
	double mu = Constants.GM_Sun / 1.e9;
	VectorN r = new VectorN(100000000, 0, 0);
	VectorN v = new VectorN(0, 30, 0);

	public TwoBodyOrbit3D(double[] coords) {
		this.coords = coords;
	}

	public TwoBodyOrbit3D() {
		draw_orbit();
	}

	public TwoBodyOrbit3D(double mu, VectorN r, VectorN v) {
		this.mu = mu;
		this.r = r;
		this.v = v;
		draw_orbit();
	}

	public TwoBodyOrbit3D(double mu, VectorN r, VectorN v, double tof) {
		this.tof = tof;
		this.mu = mu;
		this.r = r;
		this.v = v;
		draw_orbit();
	}

	public void print(double time, double[] pos) {
		// also print to the screen for warm fuzzy feeling
		// System.out.println(j+"  "+time + " " + pos[0] + " " + pos[1] + " " +
		// pos[2]);
		t[j] = time;
		x[j] = pos[0];
		y[j] = pos[1];
		z[j] = pos[2];
		// coords[j + 0] = pos[0];
		// coords[j + 1] = pos[1];
		// coords[j + 2] = pos[2];
		j++;
	}

	private void draw_orbit() {
		// int steps = 10000;
		// number of steps in propagate in TwoBody should be made a parameter
		coords = new double[3 * steps + 6];

		t = new double[steps + 2];
		x = new double[steps + 2];
		y = new double[steps + 2];
		z = new double[steps + 2];

		// create a TwoBody orbit using orbit elements
		TwoBody sat = new TwoBody(mu, r, v);

		// sat.printElements("Orbit");

		// find out the period of the orbit
		double tf = sat.period();

		// propagate the orbit
		// sat.propagate(0., tf, x, true);
		//sat.propagate(0., tf, this, true, steps);
		sat.propagate(0., tof, this, true, steps);

		// Copy data into coords array
		coords = new double[steps * 3];
		for (int k = 0; k < steps; k++) {
			coords[k * 3 + 0] = x[k];
			coords[k * 3 + 1] = y[k];
			coords[k * 3 + 2] = z[k];
		}
		int num_vert = coords.length / 3;
		int[] stripLengths = { num_vert };

		LineStripArray myLines = new LineStripArray(num_vert, GeometryArray.COORDINATES | GeometryArray.COLOR_3,
				stripLengths);
		Color3f colors[] = new Color3f[num_vert];
		for (int i = 0; i < num_vert; i++)
			colors[i] = Color;
		myLines.setColors(0, colors);
		myLines.setCoordinates(0, coords);

		this.setGeometry(myLines);
	}
}
