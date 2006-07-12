/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2006 The JAT Project. All rights reserved.
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
 * Emergent Space Technologies
 * File created by Richard C. Page III 
 **/
package jat.spacetime;

import jat.eph.DE405;
import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;

/**
 * Represents the Earth-Centered Body-Fixed Reference Frame.
 * 
 * @author Rob Antonucci
 */
public class EarthFixedRef implements ReferenceFrame {
  
    /**
     * Construct a ECF reference frame.
     */
    public EarthFixedRef()
    {
      // Does nothing.
    }
    
    /**
     * Compute the ECI to ECF transformation matrix.
     */
    private Matrix computeECI2ECF(Time t) {
      // EarthRef already does this.
      EarthRef ref = new EarthRef(t);
      return ref.ECI2ECEF();
    }


    /**
     * Returns a translater to translate into other reference frames.
     * @param other another reference frame
     * @param t time at which translation will be done
     * @return translater object or null if does not know how
     * to translate
     */
    public ReferenceFrameTranslater getTranslater(ReferenceFrame other, Time t)
    {
      ReferenceFrameTranslater xlater = null;
      if (other instanceof EarthFixedRef) {
        // Same reference frame.  No translation needed.
        xlater = new ReferenceFrameTranslater();
      }
      else if (other instanceof BodyCenteredInertialRef) {
        xlater = getTranslater((BodyCenteredInertialRef)other, t);
      }
      return xlater;
    }
    
    /**
     * Returns a translater to translate to ECI or LCI or any
     * other something-CI.
     * @param inertialRef an inertial reference frame
     * @param t time at which translation will be done
     * @return translater object
     */
    private ReferenceFrameTranslater 
      getTranslater(BodyCenteredInertialRef inertialRef, Time t)
    {
      // We determine the transformation matrix from ECF to ECI.
      // This can be used for transformation to any body-centered
      // inertial frame.
      Matrix eci2ecf = computeECI2ECF(t);
      Matrix xform = eci2ecf.transpose();
      
      // Determine the position of the other body relative to the Earth.
      // Then transform it to the ECF reference frame.
      DE405 jpl_ephem = new DE405();
      VectorN origin1 = jpl_ephem.get_pos(DE405.EARTH, t.jd_tdb());
      VectorN origin2 = 
        (inertialRef.getBody() == BodyCenteredInertialRef.SOLAR_SYSTEM ?
            new VectorN(3) : jpl_ephem.get_pos(inertialRef.getBody(), t.jd_tdb()));
      // We difference and convert to meters (JPL reports kilometers)
      VectorN diff = origin2.minus(origin1).times(1000);
      VectorN bodyPos = eci2ecf.times(diff);
      ReferenceFrameTranslater xlater =
        new ReferenceFrameTranslater(xform, bodyPos);
      
      return xlater;
    }
}
