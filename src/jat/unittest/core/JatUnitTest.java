package jat.unittest.core;

import junit.framework.Test;
import junit.framework.TestSuite;


public final class JatUnitTest {

  ///////////////////////////////////////////////////////////////////////
  // Main operation

  /**
     * This is the method called to start the test application.
     * @param args
     */
  public static void main(final String[] args) {
   //     junit.textui.TestRunner.run(JatUnitTest.class);
    }

  ///////////////////////////////////////////////////////////////////////
  // Test Operations

  /**
   * Launch all the tests of the suite.
   * @return Test
   */
    public static Test suite() {
      TestSuite suite = new TestSuite("JAT Unit Test Suite");
      suite.addTestSuite(jat.unittest.core.forces.CIRAExponentialDragTest.class);
      suite.addTestSuite(jat.unittest.core.forces.GravityModelTest.class);
      suite.addTestSuite(jat.unittest.core.forces.HarrisPriesterTest.class);
      suite.addTestSuite(jat.unittest.core.forces.NRLMSISEDragTest.class);
      suite.addTestSuite(jat.unittest.core.forces.SolarRadiationPressureTest.class);
      suite.addTestSuite(jat.unittest.core.spacetime.BodyCenteredInertialRefTest.class);
      suite.addTestSuite(jat.unittest.core.spacetime.LunaFixedRefTest.class);
      return suite;
    }

}
