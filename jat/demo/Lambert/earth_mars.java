/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002, 2003 The JAT Project. All rights reserved.
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

package jat.demo.Lambert;
import jat.cm.*;
import jat.plot.*;
import jat.alg.integrators.*;
import jat.matvec.data.*;

/**
 * @author Tobias Berthold
 *
 * Lambert Targeting example
 * 
 */


public class earth_mars implements Printable
{

    SinglePlot traj_plot = new SinglePlot();
    private int plotnum = 0;

    /** Creates a new instance of TwoBodyExample */
    public earth_mars()
    {
        // set up the trajectory plot
        traj_plot.setTitle("Lambert Targeting");
        traj_plot.plot.setXLabel("x (km)");
        traj_plot.plot.setYLabel("y (km)");
    }

    /** Implements the Printable interface to get the data out of the propagator and pass it to the plot.
     *  This method is executed by the propagator at each integration step.
     * @param t Time.
     * @param y Data array.
     */
    public void print(double t, double [] y)
    {

        // handle the first variable for plotting - this is a little mystery but it works
        boolean first = true;
        if (t == 0.0) first = false;

        // add data point to the plot
        traj_plot.plot.addPoint(plotnum, y[0], y[1], first);

        // also print to the screen for warm fuzzy feeling
		//if(!first)	System.out.println("t         x         y");		
        //System.out.println(t+" "+y[0]+" "+y[1]);
    }


	public static void main(String[] args)
	{	
			earth_mars x = new earth_mars();

        // create a TwoBody orbit using orbit elements
        TwoBody elem0 = new TwoBody(cm.earth_moon_elements,Constants.GM_Sun/1000000000);
        TwoBody elemf = new TwoBody(cm.mars_elements,Constants.GM_Sun/1000000000);


        // propagate the orbits for plotting
//        elem0.propagate(0., elem0.period()/3, x, true);
//        x.plotnum++;
//        elemf.propagate(0., elemf.period()/3, x, true);
//        x.plotnum++;

		
//		elem0.propagate(0., (7. * 31 * 24 * 60 * 60), x, true);
//        x.plotnum++;
//		elemf.propagate(0., (7. * 31 * 24 * 60 * 60), x, true);
//        x.plotnum++;
		elem0.propagate(0., ( 100*24 * 60 * 60), x, true);
        x.plotnum++;
		elemf.propagate(0., ( 100* 24 * 60 * 60), x, true);
        x.plotnum++;
		
        VectorN r0 = elem0.getR();
        VectorN v0 = elem0.getV();
        VectorN rf = elemf.getR();
        VectorN vf = elemf.getV();
		
//		r0.print("r0");
//		v0.print("v0");
//		rf.print("rf");
//		vf.print("vf");
		System.out.println("Distance of earth from sun: "+r0.mag() +" km");
		System.out.println("Velocity of earth in sun reference frame: "+v0.mag() +" km/s");
		System.out.println("Distance of Mars from sun: "+rf.mag() +" km");
		System.out.println("Velocity of Mars in sun reference frame: "+vf.mag() +" km/s");

        Lambert lambert = new Lambert(Constants.GM_Sun);
        double totaldv = lambert.compute(r0, v0, rf, vf, 200* 24 * 60 * 60  );
        
        // apply the first delta-v        
        VectorN dv0 = lambert.deltav0;
        v0 = v0.plus(dv0);
        System.out.println("tof = "+lambert.tof);
        
        TwoBody chaser = new TwoBody(r0, v0);
        chaser.print("chaser orbit");
        chaser.propagate(0.0, lambert.tof, x, true);
        
        // add plot legends
        x.traj_plot.plot.addLegend(0, "initial orbit");
        x.traj_plot.plot.addLegend(1, "target orbit");
        x.traj_plot.plot.addLegend(2, "chaser");

        // make the plot visible and square
        x.traj_plot.setVisible(true);
        int size=(int)cm.mars_elements.a;
        x.traj_plot.plot.setXRange(-size,size);
        x.traj_plot.plot.setYRange(-size,size);		

	}
}
