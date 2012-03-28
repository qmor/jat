package jat.unittest.core.forces;

import java.io.IOException;

import jat.core.forces.CIRA_ExponentialDrag;
import jat.core.forces.HarrisPriester;
import jat.core.spacecraft.Spacecraft;

public class CIRAExponentialDragTest extends ForceModelTest {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(CIRAExponentialDragTest.class);
  }

  /*
   * Test method for 'jat.forces.HarrisPriester.acceleration(Time, BodyRef, Spacecraft)'
   */
  public void testAccelerationTimeBodyRefSpacecraft() throws IOException {
    Spacecraft sc = new Spacecraft();
    sc.set_area(20);
    sc.set_mass(1000);
    sc.set_cd(1.2);
    CIRA_ExponentialDrag force = new CIRA_ExponentialDrag(sc.cd(), sc.area(), sc.mass());
    
    testForceModelAcceleration(sc, force, "cira_drag.txt", 
        "CIRA exponential atmosphere drag");
  }
}
