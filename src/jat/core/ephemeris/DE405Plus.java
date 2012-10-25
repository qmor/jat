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

package jat.core.ephemeris;

import jat.core.ephemeris.DE405Body.body;
import jat.core.ephemeris.DE405Frame.frame;
import jat.core.util.PathUtil;
import jat.core.util.jatMessages;
import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.Time;

import java.io.IOException;

/**
 * The DE405 Ephemeris data files from JPL are given in the ICRF frame. This
 * class allows to choose the frame for which position and velocity are
 * calculated (See DE405Frame)
 * 
 */
public class DE405Plus extends DE405APL {

	public frame ephFrame;
	jatMessages messages;


	//TODO: this for applications. to be tested.
	public DE405Plus() {
		super();
		ephFrame = frame.ICRF;
	}

	public DE405Plus(PathUtil path, jatMessages messages) {
		this.path = path;
		DE405_path = path.DE405Path;
		if (messages != null)
			messages.addln("[DE405Plus] " + DE405_path);
		ephFrame = frame.ICRF;
	}

	public DE405Plus(PathUtil path) {
		this.path = path;
		DE405_path = path.DE405Path;
		ephFrame = frame.ICRF;
	}

	// public DE405Plus(Applet myApplet) {
	// super(myApplet);
	// ephFrame = frame.ICRF;
	// }

	public void setFrame(frame ephFrame) {

		this.ephFrame = ephFrame;

	}

	public VectorN get_planet_posvel(body bodyEnum, Time t) throws IOException {
		VectorN in = get_planet_posvel(bodyEnum, t.jd_tt());

		VectorN out;
		switch (ephFrame) {
		case ICRF:
			out = new VectorN(in);
			break;
		case HEE:
			out = new VectorN(ecliptic_obliquity_rotate(new VectorN(in)));
			break;
		default:
			out = new VectorN(in);
			break;
		}

		return out;

	}

	public VectorN get_planet_pos(body bodyEnum, Time t) throws IOException {
		VectorN in = get_planet_posvel(bodyEnum, t);
		VectorN out = new VectorN(3);

		out.x[0] = in.x[0];
		out.x[1] = in.x[1];
		out.x[2] = in.x[2];

		return out;
	}

	public VectorN get_planet_vel(body bodyEnum, Time t) throws IOException {
		VectorN in = get_planet_posvel(bodyEnum, t);
		VectorN out = new VectorN(3);

		out.x[0] = in.x[3];
		out.x[1] = in.x[4];
		out.x[2] = in.x[5];

		return out;

	}

	VectorN ecliptic_obliquity_rotate(VectorN rv) {
		VectorN returnval = new VectorN(6);
		double x, y, z, vx, vy, vz, eps, c, s;
		x = rv.get(0);
		y = rv.get(1);
		z = rv.get(2);
		eps = cm.Rad(Constants.eps);
		c = Math.cos(eps);
		s = Math.sin(eps);
		returnval.x[0] = x;
		returnval.x[1] = c * y + s * z;
		returnval.x[2] = -s * y + c * z;
		vx = rv.get(3);
		vy = rv.get(4);
		vz = rv.get(5);
		returnval.x[3] = vx;
		returnval.x[4] = c * vy + s * vz;
		returnval.x[5] = -s * vy + c * vz;

		return returnval;
	}

}