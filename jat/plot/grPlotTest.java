package jat.plot;

/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2003 The JAT Project. All rights reserved.
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

import gov.usgs.gr.*;
import gov.usgs.gr.data.*;
import gov.usgs.gr.visual.*;
import gov.usgs.gr.visual.gl.*;

/**
* <P>
* The grPlotTest.java Class provides an example of using Gr for plotting.
*
* @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
* @version 1.0
*/

public class grPlotTest {

	public static void main(String[] args) {

		// create the data to be plotted
		float[] array1 = new float[200];
		float[] array2 = new float[200];
		double t = 0;
		int j = 0;
		while (j < 199) {
			array1[j] = (float) t;
			array1[j + 1] = (float) Math.sin(t);
			array2[j] = (float) t;
			array2[j + 1] = (float) Math.cos(t);
			//			System.out.println(array1[j]+" "+array1[j+1]+" "+array2[j]+" "+array2[j+1]);						
			j = j + 2;
			t = t + 0.06;
		}

		// create a gr
		Gr gr = new Gr("plot title");

		// put the data into XyDataSeries
		XyDataSeries data1 = new XyDataSeries(array1);
		XyDataSeries data2 = new XyDataSeries(array2);

		data1.setDimensionLocked(0, true);
		data1.setDimensionLocked(1, true);
		data2.setDimensionLocked(0, true);
		data2.setDimensionLocked(1, true);

		// create the curves		
		Curve curve1 = new GlCurve(data1);
		Curve curve2 = new GlCurve(data2);

		// create the graphs and add the curves		
		Graph graph1 = new GlGraph();
		Graph graph2 = new GlGraph();
		Graph graph3 = new GlGraph();
		graph1.add(curve1);
		graph1.add(curve2);
		graph2.add(curve2);
		graph3.add(curve1);

		graph1.setSelected(true);
		graph2.setSelected(true);
		graph3.setSelected(true);

		// create the PageOfGraphs and add the graphs
		PageOfGraphs page = new PageOfGlGraphs();
		page.add(graph1);
		page.add(graph2);
		page.add(graph3);

		// create the GrController
		GrController grcon = new GrController(page);
		grcon.setUsingOpenGL(true);

		// set it up and show it
		grcon.setupAndShow(gr);
		grcon.setUsingOpenGL(true);
		grcon.add(page);

		grcon.showMoreGraphs();

		grcon.zoomAll();

	}
}
