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

import jat.alg.integrators.LinePrinter;
import jat.eph.DE405;
import jat.matvec.data.VectorN;
import jat.spacetime.BodyCenteredInertialRef;
import jat.spacetime.LunaFixedRef;
import jat.spacetime.ReferenceFrameTranslater;
import jat.spacetime.Time;
import jat.spacetime.TimeUtils;
import jat.traj.RelativeTraj;
import jat.traj.Trajectory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import junit.framework.TestCase;

public class LunaFixedRefTest extends TestCase {

  /** A simple structure holding the contents of one line of */
  public static class Entry
  {
    public double epochSecs;
    public VectorN lciPos;
    public VectorN lcfPos;
  }
    
  public static void main(String[] args) {
    junit.textui.TestRunner.run(LunaFixedRefTest.class);
  }

  /*
   * Test method for 'jat.spacetime.LunaFixedRef.getTranslater(ReferenceFrame, Time)'
   */
  public void testLCItoLCF() throws IOException {
    final String COMMENT = "#";
    final String DECLARATION = "%";
    final String NUM_LINES_VAR = "num";
    final String EPOCH_VAR = "jd0";
    
    InputStream strm = getClass().getClassLoader().getResourceAsStream(
    "jat/spacetime/unittest/llo_ascii.txt");
    BufferedReader rdr = new BufferedReader(new InputStreamReader(strm));
    
    // The first lines will specify the number of data lines
    // and the start of the epoch.
    int numLines = 0;
    double epoch = 0;
    int lineCtr=0;
    while ((numLines == 0) || (epoch == 0)) {
      ++lineCtr;
      String nextLine = rdr.readLine();
      if (nextLine.startsWith(COMMENT)) {
        // It's a comment.  Ignore.
      }
      else if (nextLine.startsWith(DECLARATION)) {
        // Variable definition.  Parse.
        int equalIndex = nextLine.indexOf("=");
        assertTrue("Declaraion on line " + lineCtr + "not of the form: " +
            "% var = value", equalIndex > 2);
        String varName = nextLine.substring(1, equalIndex).trim();
        String value = nextLine.substring(equalIndex+1).trim();
        if (varName.equals(NUM_LINES_VAR)) {
          numLines = Integer.parseInt(value);
        }
        else if (varName.equals(EPOCH_VAR)) {
          epoch = Double.parseDouble(value);
        }
      }
      else if (!nextLine.trim().equals("")) {
        fail("Found data line before " + NUM_LINES_VAR +
            " and " + EPOCH_VAR + " were defined.  Error at line " + lineCtr);
      }
    }
    
    compareData(rdr, numLines, epoch, lineCtr);
  }
  
  private void compareData(BufferedReader rdr, int numLines, double epoch,
      int startLine) throws IOException
  {
    BodyCenteredInertialRef inertial = new BodyCenteredInertialRef(DE405.MOON);
    LunaFixedRef fixed = new LunaFixedRef();
    int numEntriesRead = 0;
    int lineCtr = startLine;
    boolean done = false;
    Trajectory lpos = new Trajectory();
    Trajectory comp = new Trajectory();
    RelativeTraj rel;
    Time t = new Time(TimeUtils.JDtoMJD(epoch));
    while ((numEntriesRead < numLines) && !done) {
      Entry nextEntry = new Entry();
      ++lineCtr;
      String nextLine = rdr.readLine();      
      done = (nextLine == null);
      if ((nextLine != null) && !nextLine.trim().equals(""))
      {
        parseNext(nextLine, nextEntry, lineCtr);
        //Time t = new Time(epoch.mjd_utc());
        t.update(nextEntry.epochSecs);
        ReferenceFrameTranslater xlater = 
          new ReferenceFrameTranslater(inertial, fixed, t);
        VectorN computedF = xlater.translatePoint(nextEntry.lciPos);
        //assertEquals("Computed incorrect LCF value on line "+lineCtr,
        //    nextEntry.lcfPos, computedF);
        lpos.add(t.get_sim_time()/3600.0,nextEntry.lcfPos.times(1000).x,new double[3]);
        comp.add(t.get_sim_time()/3600.0,computedF.times(1000).x,new double[3]);
        ReferenceFrameTranslater backXlater = 
          new ReferenceFrameTranslater(fixed, inertial, t);
        VectorN computedI = backXlater.translatePoint(nextEntry.lcfPos);
        //assertEquals("Computed incorrect LCI value on line "+lineCtr,
        //    nextEntry.lciPos, computedI);
        VectorN recomputedF = backXlater.translatePointBack(nextEntry.lciPos);
        //assertEquals("Reverse translation failed on line "+lineCtr,
        //    nextEntry.lcfPos, recomputedF);
        ++numEntriesRead;
      }
    }
    rel = new RelativeTraj(lpos,comp,new LinePrinter());
    rel.setVerbose(false);
    rel.process(1e-6);
    //assertEquals("Encountered " + numEntriesRead + " lines of data when " +
    //    numLines + " were expected.", numLines, numEntriesRead);
  }
  
  private void parseNext(String input, Entry entry, int lineNum)
  {
    try {
      // The line should have 13 doubles separated by spaces.
      String[] doubles = input.trim().split("\\s+");
      assertEquals("Line " + String.valueOf(lineNum) + " needs 13 number separated " +
          "by spaces.", 13, doubles.length);
      NumberFormat parser = new DecimalFormat("0.0000000000000000E000");
      entry.epochSecs = Double.parseDouble(doubles[0]);
      entry.lcfPos = new VectorN(3);
      entry.lcfPos.set(0, Double.parseDouble(doubles[1]));
      entry.lcfPos.set(1, Double.parseDouble(doubles[2]));
      entry.lcfPos.set(2, Double.parseDouble(doubles[3]));
      entry.lciPos = new VectorN(3);
      entry.lciPos.set(0, Double.parseDouble(doubles[7]));
      entry.lciPos.set(1, Double.parseDouble(doubles[8]));
      entry.lciPos.set(2, Double.parseDouble(doubles[9]));
    }
    catch (NullPointerException e) {
      fail("Failure to parse doubles on line " + lineNum + ". " +
          e.getMessage());
    }
  }

}
