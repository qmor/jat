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

import jat.core.util.PathUtil;
import jat.core.util.jatMessages;
import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.Time;

import java.applet.Applet;
import java.io.IOException;
import java.util.EnumSet;

/**
 * The DE405 Ephemeris data files are given in the ICRF frame. This class allows
 * to choose the frame in which position and velocity are given
 * 
 * ECEF
 * 
 * Earth-Centered Earth-Fixed
 * 
 * ECI
 * 
 * Earth-Centered Inertial
 * 
 * ICRF
 * 
 * International Celestial Reference Frame
 * 
 * HEEQ - Heliocentric Earth equatorial
 * 
 * This system has its Z axis parallel to the Sun's rotation axis (positive to
 * the North) and its X axis towards the intersection of the solar equator and
 * the solar central meridian as seen from the Earth. This system is sometimes
 * known as heliocentric solar (HS).
 * 
 * HEE - Heliocentric Earth ecliptic
 * 
 * This system has its X axis towards the Earth and its Z axis perpendicular to
 * the plane of the Earth's orbit around the Sun (positive North). This system
 * is fixed with respect to the Earth-Sun line.
 * 
 * HAE - Heliocentric Aries ecliptic
 * 
 * This system has its Z axis perpendicular to the plane of the Earth's orbit
 * around the Sun (positive North) and its X axis towards the First Point of
 * Aries (the direction in space defined by the intersection between the Earth's
 * equatorial plane and the plane of its orbit around the Sun (the plane of the
 * ecliptic). This system is (to first order) fixed with respect to the distant
 * stars. It is subject to slow change owing to the various slow motions of the
 * Earth's rotation axis with respect to the fixed stars.
 */
public class DE405Plus extends DE405APL {

	public enum frame {
		ICRF, ECEF, ECI, HEEQ, HEE, HAE;
		private static final int amount = EnumSet.allOf(frame.class).size();
		private static frame[] val = new frame[amount];
		static {
			for (frame q : EnumSet.allOf(frame.class)) {
				val[q.ordinal()] = q;
			}
		}

	}

	DE405Plus.frame ephFrame;
	jatMessages messages;

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

	public DE405Plus(PathUtil p) {
		this.path = p;
		DE405_path = p.DE405Path;
		ephFrame = frame.ICRF;
	}

	public DE405Plus(Applet myApplet) {
		super(myApplet);
		ephFrame = frame.ICRF;
	}

	public void setFrame(DE405Plus.frame ephFrame) {

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