/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can 
 * redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package jat.spacetime;

import jat.eph.DE405;
import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;

/**
 * Used to represent the inertial reference frame centered at any one
 * of the bodies tracked by the JPL ephemeris files.
 * 
 * @author Rob Antonucci
 *
 */
public class BodyCenteredInertialRef implements ReferenceFrame {
  
    /** Constant to indicate the reference is centered at
     * the barycenter of the solar system.
     */
    public static final int SOLAR_SYSTEM = 0;
    
    /** A code indentifying a body in the JPL ephemeris files.
     * (e.g. Mercury=1, Earth=3, Sun=10, Moon=11).
     * SOLAR_SYSTEM is a special case indicating the origin of
     * all the JPL ephemeris readings. 
     */
    private final int body;
    
    /**
     * Creates a reference frame centered on a central body and
     * using the inertial orientation
     * @param bodyNum a code indentifying a body in the JPL ephemeris
     * files (e.g. Mercury=1, Earth=3, Sun=10, Moon=11).
     * A special case, 0, indicates to use the Barycenter of the solar
     * system (the point from which all JPL ephemeris vectors 
     * originate) but it still uses the orientation used by ECI
     * and LCI.
     */
    public BodyCenteredInertialRef(int bodyNum)
    {
      body = bodyNum;
    }
    
    public int getBody() {
      return body;
    }
    
    /**
     * Creates a translate that can translate between two reference
     * frames at a given time
     * @param other another reference frame
     * @param t the time at which translation will be done
     * @return a translating object or null if the reference frame
     * does not know how to translate to the other reference frame.
     */
    public ReferenceFrameTranslater getTranslater(ReferenceFrame other, Time t)
    {
      ReferenceFrameTranslater xlater = null;
      if (other instanceof BodyCenteredInertialRef) {
        xlater = getTranslater((BodyCenteredInertialRef)other, t);
      }
      else if (other instanceof EarthRef) {
        // EarthRef is just a BodyCenteredInertialRef centered on Earth
        xlater = getTranslater(new BodyCenteredInertialRef(DE405.EARTH), t);
      }
      return xlater;
    }
    
    /**
     * Creates a translate that can translate between two inertial reference
     * frames at a given time
     * @param other another inertial reference frame
     * @param t the time at which translation will be done
     * @return a translating object
     */
    private ReferenceFrameTranslater getTranslater(BodyCenteredInertialRef other,
        Time t)
    {
      ReferenceFrameTranslater xlater = null;

      if (this.body == other.body)
      {
        xlater = new ReferenceFrameTranslater();
      }
      else
      {
        // Inertial reference frames have the same orientation, so
        // no transformation matrix is needed no matter where the
        // reference frame is centered.
        Matrix xform = Matrix.identity(3, 3);
        
        // To determine the distance between origins, we check the JPL
        // ephemeris of the two bodies and difference their positions.
        DE405 jpl_ephemeris = new DE405();
        VectorN origin1 = (this.body == SOLAR_SYSTEM ? 
            new VectorN(3) : jpl_ephemeris.get_pos(this.body, t.jd_tdb()));
        VectorN origin2 = (other.body == SOLAR_SYSTEM ?
            new VectorN(3) : jpl_ephemeris.get_pos(other.body, t.jd_tdb()));
        // We difference and convert to meters (JPL reports kilometers)
        VectorN diff = origin2.minus(origin1).times(1000);
        xlater = new ReferenceFrameTranslater(xform, diff);
      }
      return xlater;
    }
}
