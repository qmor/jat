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

import java.io.IOException;

import jat.core.cm.Constants;
import jat.core.cm.cm;
import jat.core.ephemeris.DE405APL;
import jat.core.math.matvec.data.VectorN;
import jat.core.spacetime.Time;
import jat.core.spacetime.TimeUtils;
import jat.core.util.FileUtil;
import jat.core.util.PathUtil;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

/**
 * @author Tobias Berthold
 * 
 */
public class Ephemeris3D extends Body3D {
	float size;
	DE405APL myEph;
	DE405APL.body body;
	double jd;
	int steps = 100;
	public double[] coords;
	Color3f Color = Colors.gray;
	Shape3D s;
	Time startTime;

	// Matrix MRot;

	// public Ephemeris3D(DE405APL.body body, double jd_start, double jd_end) {
	// this.body = body;
	// this.jd = jd_start;
	// String fs = FileUtil.file_separator();
	// my_eph = new DE405(FileUtil.getClassFilePath("jat.eph", "DE405") + fs +
	// "DE405data" + fs);
	// // MRot=new RotationMatrix(1,cm.Rad(Constants.eps));
	// draw();
	// }

	public Ephemeris3D(DE405APL myEph, DE405APL.body body, Time startTime, int days) {
		this.myEph = myEph;
		this.body = body;
		this.startTime = startTime;
		this.steps = days;

		// PathUtil f = new PathUtil();
		// String fs = FileUtil.file_separator();
		// String DE405_data_folder = f.root_path + "data" + fs + "core" + fs +
		// "ephemeris" + fs + "DE405data" + fs;
		// my_eph = new DE405(DE405_data_folder);
		// MRot = new RotationMatrix(1, cm.Rad(Constants.eps));
		draw();
	}

	private void draw() {
		// double rv[];
		VectorN r;
		Time time = startTime;
		// Create coords array
		coords = new double[steps * 3];
		for (int k = 0; k < steps; k++) {
			// double mjd_tt = TimeUtils.JDtoMJD(jd);
			// rv = MRot.times(new VectorN(my_eph.get_planet_pos(body,
			// mjd_tt+k)));
			time.step_seconds(k *100);
			try {
				r = new VectorN(myEph.get_planet_pos(body, time));
				double x, y, z, eps, c, s;
				x = r.get(0);
				y = r.get(1);
				z = r.get(2);
				eps = cm.Rad(Constants.eps);
				c = Math.cos(eps);
				s = Math.sin(eps);
				coords[k * 3 + 0] = x;
				coords[k * 3 + 1] = c * y + s * z;
				coords[k * 3 + 2] = -s * y + c * z;
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		s = new Shape3D();
		s.setGeometry(myLines);
		addChild(s);

	}
}
