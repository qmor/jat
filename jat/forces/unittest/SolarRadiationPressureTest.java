package jat.forces.unittest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jat.eph.DE405;
import jat.forces.SolarRadiationPressure;
import jat.matvec.data.VectorN;
import jat.spacecraft.Spacecraft;
import jat.spacetime.BodyCenteredInertialRef;
import jat.spacetime.EarthRef;
import jat.spacetime.Time;
import junit.framework.TestCase;

public class SolarRadiationPressureTest extends TestCase {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(SolarRadiationPressureTest.class);
  }

  /*
   * Test method for 'jat.forces.SolarRadiationPressure.acceleration(Time, BodyRef, Spacecraft)'
   */
  public void testAccelerationTimeBodyRefSpacecraft() throws IOException {
    Spacecraft sc = new Spacecraft();
    sc.set_area(20);
    sc.set_mass(1000);
    sc.set_cr(0.07);
    SolarRadiationPressure force = new SolarRadiationPressure();
    // We assume the positions are ECI
    EarthRef ref = new EarthRef(0, 0);
    
    InputStream rstrm = getClass().getClassLoader().
      getResourceAsStream("jat/forces/unittest/leo_orbit.txt");
    BufferedReader rrdr = new BufferedReader(new InputStreamReader(rstrm));
    rrdr.readLine(); // Ignore the first line.  It's column headers.
    String rLine = rrdr.readLine();
    
    String forceFile = "jat/forces/unittest/solar_pressure.txt";
    InputStream fstrm = 
      getClass().getClassLoader().getResourceAsStream(forceFile);
    BufferedReader frdr = null;
    BufferedWriter fwrtr = null;
    String fLine = null;
    if (fstrm == null) {
      // We cannot read the target file.  Create a target file instead.
      System.out.println("Could not open solar radiation pressure file " +
          forceFile);
      File outFile = new File(System.getProperty("java.io.tmpdir"),
          "solar_pressure.txt");
      fwrtr = new BufferedWriter(new FileWriter(outFile));
      System.out.println("Creating solar radiation pressure file " +
          outFile.getAbsolutePath());
      fwrtr.write("Solar Pressure  x\t\t\t\ty\t\t\t\tz");
      fwrtr.newLine();
    }
    else {
      frdr = new BufferedReader(new InputStreamReader(fstrm));
      frdr.readLine(); // Ignore the first line.  It's column headers.
      fLine = frdr.readLine();
    }
  
    int lineNum = 1;
    while (rLine != null) {
      VectorN position = new VectorN(3);
      Time t = parse(rLine, position);
      sc.updateMotion(position, new VectorN(3));
      VectorN computed = force.acceleration(t, ref, sc);
      if (fwrtr == null) {
        // Doing compare
        VectorN target = readTarget(fLine);
        assertEquals("Error computing solar radiation pressure at " +
            "time/position specified on line " + lineNum, target, computed);
        fLine = frdr.readLine();
      }
      else {
        // Doing generation of target file
        writeTarget(computed, fwrtr);
      }
      rLine = rrdr.readLine();
      ++lineNum;
    }
    
    if (fwrtr != null) {
      fwrtr.close();
      fail("Could not find target file to which to compare computed results.");
    }
  }
  
  private Time parse(String positionInput, VectorN position) {
    // First parse the line into four doubles.
    String[] numberStrs = positionInput.trim().split("\\s+");
    double t = Double.parseDouble(numberStrs[0]);
    Time time = new Time(t);
    position.set(0, Double.parseDouble(numberStrs[1])*1000);
    position.set(1, Double.parseDouble(numberStrs[2])*1000);
    position.set(2, Double.parseDouble(numberStrs[3])*1000);
    return time;
  }

  private VectorN readTarget(String positionInput) {
    // First parse the line into three doubles.
    String[] numberStrs = positionInput.trim().split("\\s+");
    VectorN force = new VectorN(3);
    force.set(0, Double.parseDouble(numberStrs[0]));
    force.set(1, Double.parseDouble(numberStrs[1]));
    force.set(2, Double.parseDouble(numberStrs[2]));
    return force;
  }

  private void writeTarget(VectorN force, BufferedWriter wrtr)
    throws IOException {
    wrtr.write(force.get(0) + "\t" + force.get(1) + "\t" + force.get(2));
    wrtr.newLine();
  }
}
