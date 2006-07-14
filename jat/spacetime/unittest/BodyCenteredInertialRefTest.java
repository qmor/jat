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
 * File created by Rob Antonucci 
 **/
package jat.spacetime.unittest;

import jat.eph.DE405;
import jat.matvec.data.VectorN;
import jat.spacetime.BodyCenteredInertialRef;
import jat.spacetime.EarthRef;
import jat.spacetime.LunaFixedRef;
import jat.spacetime.LunaRef;
import jat.spacetime.ReferenceFrame;
import jat.spacetime.ReferenceFrameTranslater;
import jat.spacetime.Time;
import jat.spacetime.TimeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.TestCase;

public class BodyCenteredInertialRefTest extends TestCase {

  /** A simple structure holding the contents of one line of */
  public static class Entry
  {
    public double epochSecs;
    public VectorN lciPos;
    public VectorN lcfPos;
  }
    
  public static void main(String[] args) {
    junit.textui.TestRunner.run(BodyCenteredInertialRefTest.class);
  }

  /*
   * Test method for 'jat.spacetime.BodyCenteredInertialRef.getTranslater(ReferenceFrame, Time)'
   */
  public void testECItoLCI() {
    
    // Some time
    Time t = new Time(TimeUtils.JDtoMJD(2.458232499999999e+006));
    EarthRef eciRef = new EarthRef(t);
    LunaRef lciRef = new LunaRef();
    ReferenceFrameTranslater x = new ReferenceFrameTranslater(eciRef, lciRef, t);
    
    DE405 ephemeris_file = new DE405();
    VectorN moonEciPos = ephemeris_file.get_Geocentric_Moon_pos(t.jd_tdb()).times(1000);
    VectorN moonLciPos = x.translatePoint(moonEciPos);
    // Passed if within a meter.
    assertTrue("Moon Geocentric position " + moonEciPos + " did not translate to Moon origin when " +
            "translating from ECI to LCI.  Translated to " + moonLciPos, moonLciPos.mag()<1);

    // At the time of this test, get_Geocentric_Moon_vel was broken.
    // So compute geocentric velocity from two positions at two points in time
    VectorN geocentricPos1 = ephemeris_file.get_Geocentric_Moon_pos(t.jd_tdb());
    Time t2 = new Time(t.mjd_utc());
    t2.update(1);
    VectorN geocentricPos2 = ephemeris_file.get_Geocentric_Moon_pos(t2.jd_tdb());
    VectorN moonEciVel = geocentricPos2.minus(geocentricPos1).times(1000);
    VectorN moonLciVel = x.translateVelocity(moonEciVel, moonEciPos);
    // Passed if within a cm/sec.
    assertTrue("Moon Geocentric velocity " + moonEciVel + " did not translate to stand still when " +
            "translating from ECI to LCI.  Translated to " + moonLciVel, moonLciVel.mag()<0.01);
  } 
}
