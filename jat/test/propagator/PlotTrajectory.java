/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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

package jat.test.propagator;


import jat.traj.*;
import jat.alg.integrators.LinePrinter;
/**
 * @author Richard C. Page III
 *
 */
public class PlotTrajectory {

    /**
     * 
     */
    public PlotTrajectory() {

    }
    
	public static void main(String[] args) throws java.io.IOException {
	    Trajectory jat = new Trajectory();
	    Trajectory stk = new Trajectory();
	    String test = "GEO30.txt";
	    String file = "C:/Code/Jat/jat/test/propagator/output/"+test;
	    jat.readFromFile(file);
	    String stkfile = "C:/STK_Test_Files/GEO30.txt";
	    stk.readFromFile(stkfile);
	    LinePrinter lp = new LinePrinter();
	    RelativeTraj comp = new RelativeTraj(jat,stk,lp);
	    comp.process();
	    
	}
}
