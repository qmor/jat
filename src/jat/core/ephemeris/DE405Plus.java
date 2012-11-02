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
import java.util.EnumSet;

/**
 * The DE405 Ephemeris data files from JPL are given in the ICRF frame. This
 * class allows to choose the frame for which position and velocity are
 * calculated (See DE405Frame.java)
 * 
 */
public class DE405Plus extends DE405APL {

	public frame ephFrame;
	jatMessages messages;
	VectorN[] posvelICRF, posvel;

	public DE405Plus() {
		super();
		ephFrame = frame.ICRF;
		posvelICRF = new VectorN[12];
		posvel = new VectorN[12];
	}

	public DE405Plus(PathUtil path, jatMessages messages) {
		this.path = path;
		DE405_path = path.DE405Path;
		if (messages != null)
			messages.addln("[DE405Plus] " + DE405_path);
		ephFrame = frame.ICRF;
		posvelICRF = new VectorN[12];
		posvel = new VectorN[12];
	}

	public DE405Plus(PathUtil path) {
		this.path = path;
		DE405_path = path.DE405Path;
		ephFrame = frame.ICRF;
		posvelICRF = new VectorN[12];
		posvel = new VectorN[12];
	}

	public void setFrame(frame ephFrame) {
		this.ephFrame = ephFrame;
	}

	public void update_posvel_and_frame(Time t) throws IOException {

		update_planetary_ephemeris(t);

		// first get ICRF ephemeris
		double[] pv = new double[6];
		for (body q : EnumSet.allOf(body.class)) {
			int bodyNumber = q.ordinal();
			double daysec = 3600. * 24.;
			pv[0] = planet_r[bodyNumber][1];
			pv[1] = planet_r[bodyNumber][2];
			pv[2] = planet_r[bodyNumber][3];
			pv[3] = planet_rprime[bodyNumber][1] / daysec;
			pv[4] = planet_rprime[bodyNumber][2] / daysec;
			pv[5] = planet_rprime[bodyNumber][3] / daysec;

			posvelICRF[bodyNumber] = new VectorN(pv);
		}

		// Now transform posvel to desired reference frame
		for (body q : EnumSet.allOf(body.class)) {
			int bodyNumber = q.ordinal();
			VectorN in = posvelICRF[bodyNumber];
			switch (ephFrame) {
			case ICRF:
				posvel[bodyNumber] = in;
				break;
			case HEE:
				posvel[bodyNumber] = ecliptic_obliquity_rotate(in);
				break;
			case ECI:

				posvel[bodyNumber] = ICRF_to_ECI(in, t);
				break;
			default:
				posvel[bodyNumber] = in;
				break;
			}
		}

	}

	public VectorN get_planet_posvel(body bodyEnum, Time t) throws IOException {
		update_posvel_and_frame(t);
		return posvel[bodyEnum.ordinal()];
	}

	public VectorN get_planet_pos(body bodyEnum, Time t) throws IOException {

		update_posvel_and_frame(t);

		double[] pos = new double[3];
		int bodyNumber = bodyEnum.ordinal();
		pos[0] = posvel[bodyNumber].x[0];
		pos[1] = posvel[bodyNumber].x[1];
		pos[2] = posvel[bodyNumber].x[2];

		VectorN out = new VectorN(pos);

		return out;

	}

	public VectorN get_planet_vel(body bodyEnum, Time t) throws IOException {

		update_posvel_and_frame(t);

		double[] vel = new double[3];
		int bodyNumber = bodyEnum.ordinal();
		vel[0] = posvel[bodyNumber].x[3];
		vel[1] = posvel[bodyNumber].x[4];
		vel[2] = posvel[bodyNumber].x[5];

		VectorN out = new VectorN(vel);

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

	private VectorN ICRF_to_ECI(VectorN in, Time mytime) throws IOException {

		double[] posvel = new double[6];
		int bodyNumber = DE405Body.body.EARTH.ordinal();
		posvel[0] = posvelICRF[bodyNumber].x[0];
		posvel[1] = posvelICRF[bodyNumber].x[1];
		posvel[2] = posvelICRF[bodyNumber].x[2];
		posvel[3] = posvelICRF[bodyNumber].x[3];
		posvel[4] = posvelICRF[bodyNumber].x[4];
		posvel[5] = posvelICRF[bodyNumber].x[5];

		VectorN earthPos = new VectorN(posvel);

		VectorN returnval = in.minus(earthPos);
		return returnval;
	}

}