package jat.unittest.core.forces;

import java.io.IOException;

import jat.core.forces.SolarRadiationPressure;
import jat.core.spacecraft.Spacecraft;

public class SolarRadiationPressureTest extends ForceModelTest {

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
    SolarRadiationPressure force = new SolarRadiationPressure(sc); 
    
    testForceModelAcceleration(sc, force, "solar_pressure.txt", 
        "solar pressure radiation");
  }
}
