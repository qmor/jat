package jat.forces.unittest;

import java.io.IOException;

import jat.forces.CIRA_ExponentialDrag;
import jat.forces.NRLMSISE_Drag;
import jat.spacecraft.Spacecraft;

public class NRLMSISEDragTest extends ForceModelTest {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(NRLMSISEDragTest.class);
  }

  /*
   * Test method for 'jat.forces.HarrisPriester.acceleration(Time, BodyRef, Spacecraft)'
   */
  public void testAccelerationTimeBodyRefSpacecraft() throws IOException {
    Spacecraft sc = new Spacecraft();
    sc.set_area(20);
    sc.set_mass(1000);
    sc.set_cd(1.2);
    NRLMSISE_Drag force = new NRLMSISE_Drag(sc.cd(), sc.area(), sc.mass());
    
    testForceModelAcceleration(sc, force, "nrlmsise_drag.txt", 
        "NRLMSISE atmosphere drag");
  }
}
