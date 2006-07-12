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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import jat.eph.DE405;
import jat.matvec.data.Matrix;
import jat.matvec.data.VectorN;
import jat.traj.Trajectory;

/**
 * Represents the Lunar Centered Fixed or Selenographic Reference Frame.
 * 
 * @author Rob Antonucci
 */
public class LunaFixedRef implements ReferenceFrame {
  
    /**
     * Construct a LCF reference frame.
     */
    public LunaFixedRef()
    {
      // Does nothing.
    }
    
    /**
     * Compute the LCI to LCF transformation matrix.
     */
    private Matrix computeLCI2LCF(Time t) {
      
      // First compute the Euler angles
      DE405 jpl_ephem = new DE405();
      double[] angles = jpl_ephem.get_Moon_Libration(t.jd_tdb());

      // Then compute the transformation matrix
      double alpha = angles[0] - Math.PI/2;
      double sina = Math.sin(alpha);
      double cosa = Math.cos(alpha);
      double delta = Math.PI/2 - angles[1];
      double sind = Math.sin(delta);
      double cosd = Math.cos(delta);
      double lambda = angles[2];
      double sinl = Math.sin(lambda);
      double cosl = Math.cos(lambda);
      Matrix xform = new Matrix(3, 3);
      xform.set(0, 0, -cosl*sina - sinl*sind*cosa);
      xform.set(0, 1, cosl*cosa - sinl*sind*sina);
      xform.set(0, 2, sinl*cosd);
      xform.set(1, 0, sinl*sina-cosl*sind*cosa);
      xform.set(1, 1, -sinl*cosa - cosl*sind*sina);
      xform.set(1, 2, cosl*cosd);
      xform.set(2, 0, cosd*cosa);
      xform.set(2, 1, cosd*sina);
      xform.set(2, 2, sind);
      
      return xform;
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
      if (other instanceof LunaFixedRef) {
        // Same reference frame.  No translation needed.
        xlater = new ReferenceFrameTranslater();
      }
      else if (other instanceof BodyCenteredInertialRef) {
        xlater = getTranslater((BodyCenteredInertialRef)other, t);
      }
      else if (other instanceof EarthRef) {
        // EarthRef is just a BodyCenteredInertialRef centered on Earth
        xlater = getTranslater(new BodyCenteredInertialRef(DE405.EARTH), t);
      }
      return xlater;
    }
    
    /**
     * Returns a translater to translate to LCI or ECI or any
     * other something-CI.
     * @param inertialRef an inertial reference frame
     * @param t time at which translation will be done
     * @return translater object
     */
    private ReferenceFrameTranslater 
      getTranslater(BodyCenteredInertialRef inertialRef, Time t)
    {
      // We determine the transformation matrix from LCF to LCI.
      // This can be used for transformation to any body-centered
      // inertial frame.
      Matrix lci2lcf = computeLCI2LCF(t);
      Matrix xform = lci2lcf.transpose();
      
      // Determine the position of the other body relative to the Moon.
      // Then transform it to the LCF reference frame.
      DE405 jpl_ephem = new DE405();
      VectorN origin1 = jpl_ephem.get_pos(DE405.MOON, t.jd_tdb());
      VectorN origin2 = 
        (inertialRef.getBody() == BodyCenteredInertialRef.SOLAR_SYSTEM ?
            new VectorN(3) : jpl_ephem.get_pos(inertialRef.getBody(), t.jd_tdb()));
      // We difference and convert to meters (JPL reports kilometers)
      VectorN diff = origin2.minus(origin1).times(1000);
      VectorN bodyPos = lci2lcf.times(diff);
      ReferenceFrameTranslater xlater =
        new ReferenceFrameTranslater(xform, bodyPos);
      
      return xlater;
    }
    
    public static void main(String args[]) throws IOException{
    	LunaFixedRef luna = new LunaFixedRef();
    	LunaRef lunaRef = new LunaRef();
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("C:/Code/Jat/jat/spacetime/unittest/llo_ascii.txt")));
			String line = reader.readLine();
			StringTokenizer tok = new StringTokenizer(line," ");
			while(!tok.nextToken().equals("="));
			String sjd = tok.nextToken();
			double jd0 = Double.parseDouble(sjd);
			double mjd0 = TimeUtils.JDtoMJD(jd0);
			Time time = new Time(mjd0);
			line = reader.readLine();
			double sec;
			Trajectory vlcf = new Trajectory();
			Trajectory jlcf = new Trajectory();
			for(int i=0; i<300; i++){
				line = reader.readLine();
				tok = new StringTokenizer(line," ");
				sec = Double.parseDouble(tok.nextToken());
				time.update(sec);
				VectorN xlcf = new VectorN(3);
				VectorN xlci = new VectorN(3);
				for(int j=0; j<6; j++)
					xlcf.x[j] = Double.parseDouble(tok.nextToken());
				for(int j=0; j<6; j++)
					xlci.x[j] = Double.parseDouble(tok.nextToken());
				//TODO: Finish test
                //luna.getTranslater(lunaRef,time);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
			e.printStackTrace();
		}
    }
}
