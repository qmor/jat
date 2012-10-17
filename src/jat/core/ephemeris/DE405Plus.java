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

import jat.coreNOSA.cm.Constants;
import jat.coreNOSA.cm.cm;
import jat.coreNOSA.math.MatrixVector.data.VectorN;
import jat.coreNOSA.spacetime.Time;
import jat.coreNOSA.spacetime.TimeUtils;

import java.applet.Applet;
import java.io.IOException;
import java.util.EnumSet;

/**
 * @author Tobias Berthold
 * 
 *         The DE405 Ephemeris data are given in the ICRF frame. This class
 *         allows to choose the frame in which position and velocity are given
 * 
 * 
 *         ECEF
 * 
 *         Earth-Centered Earth-Fixed
 * 
 *         ECI
 * 
 *         Earth-Centered Inertial
 * 
 *         ICRF
 * 
 *         International Celestial Reference Frame
 * 
 *         HEEQ - Heliocentric Earth equatorial
 * 
 *         This system has its Z axis parallel to the Sun's rotation axis
 *         (positive to the North) and its X axis towards the intersection of
 *         the solar equator and the solar central meridian as seen from the
 *         Earth. This system is sometimes known as heliocentric solar (HS).
 * 
 *         HEE - Heliocentric Earth ecliptic
 * 
 *         This system has its X axis towards the Earth and its Z axis
 *         perpendicular to the plane of the Earth's orbit around the Sun
 *         (positive North). This system is fixed with respect to the Earth-Sun
 *         line.
 * 
 *         HAE - Heliocentric Aries ecliptic
 * 
 *         This system has its Z axis perpendicular to the plane of the Earth's
 *         orbit around the Sun (positive North) and its X axis towards the
 *         First Point of Aries (the direction in space defined by the
 *         intersection between the Earth's equatorial plane and the plane of
 *         its orbit around the Sun (the plane of the ecliptic). This system is
 *         (to first order) fixed with respect to the distant stars. It is
 *         subject to slow change owing to the various slow motions of the
 *         Earth's rotation axis with respect to the fixed stars.
 * 
 */
public class DE405Plus extends DE405APL {

	DE405Plus.frame ephFrame;

	public DE405Plus() {
		super();
		ephFrame = frame.ICRF;
	}

	public DE405Plus(Applet myApplet) {
		super(myApplet);
		ephFrame = frame.ICRF;
	}

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

	public void setFrame(DE405Plus.frame ephFrame) {

		this.ephFrame = ephFrame;

	}

	public VectorN get_planet_pos(body bodyEnum, Time t) throws IOException {
		get_planet_posvel(bodyEnum, t.jd_tt());
		double[] pos = new double[3];
		double jultime = TimeUtils.MJDtoJD(TimeUtils.TTtoTDB(t.mjd_tt()));

		int bodyNumber = bodyEnum.ordinal();
		planetary_ephemeris(jultime);
		pos[0] = planet_r[bodyNumber][1];
		pos[1] = planet_r[bodyNumber][2];
		pos[2] = planet_r[bodyNumber][3];

		VectorN out;
		switch (ephFrame) {
		case ICRF:
			out = new VectorN(pos);
			break;
		case HEE:
			out = new VectorN(ecliptic_obliquity_rotate(new VectorN(pos)));
			break;
		default:
			out = new VectorN(pos);
			break;
		}

		return out;
	}

	VectorN ecliptic_obliquity_rotate(VectorN r) {
		VectorN returnval = new VectorN(3);
		double x, y, z, eps, c, s;
		x = r.get(0);
		y = r.get(1);
		z = r.get(2);
		eps = cm.Rad(Constants.eps);
		c = Math.cos(eps);
		s = Math.sin(eps);
		returnval.x[0] = x;
		returnval.x[1] = c * y + s * z;
		returnval.x[2] = -s * y + c * z;
		return returnval;
	}

}
