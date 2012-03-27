/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 National Aeronautics and Space Administration and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
 *
 * This file is part of JAT. JAT is free software; you can
 * redistribute it and/or modify it under the terms of the
 * NASA Open Source Agreement
 * 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NASA Open Source Agreement for more details.
 *
 * You should have received a copy of the NASA Open Source Agreement
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package jat.vr;

import jat.core.cm.*;
import jat.core.eph.*;
import jat.core.matvec.data.*;
import jat.core.spacetime.*;
import jat.core.util.*;

import javax.media.j3d.*;
import javax.vecmath.Color3f;

/**
 * @author Tobias Berthold
 *
 */
public class Ephemeris3D extends Shape3D
{
	float size;
	DE405_Body body;
	int steps=100;
	//Shape3D s;
	public double[] coords;
	Color3f Color = Colors.gray;
	DE405 my_eph;
	double jd;
	Matrix MRot;

	public Ephemeris3D(DE405_Body bod, double jd_start, double jd_end)
	{
		//super(myapplet);
		this.body = bod;
		this.jd=jd_start;
		String fs = FileUtil.file_separator();
		my_eph = new DE405(FileUtil.getClassFilePath("jat.eph","DE405")+fs+"DE405data"+fs);
		MRot=new RotationMatrix(1,cm.Rad(Constants.eps));
		draw();
	}
	
	public Ephemeris3D(DE405_Body bod, double jd_start, int days)
	{
		//super(myapplet);
		this.body = bod;
		this.jd=jd_start;
		this.steps=days;
		String fs = FileUtil.file_separator();
		my_eph = new DE405(FileUtil.getClassFilePath("jat.eph","DE405")+fs+"DE405data"+fs);
		MRot=new RotationMatrix(1,cm.Rad(Constants.eps));
		draw();
	}
	
	private void draw()
	{
		//double rv[];
		VectorN rv;
		// Create coords array
		coords = new double[steps * 3];
		for (int k = 0; k < steps; k++)
		{
			double mjd_tt = TimeUtils.JDtoMJD(jd);
			rv = MRot.times(new VectorN(my_eph.get_planet_pos(body, mjd_tt+k)));
//			System.out.println("The position is");
//			System.out.println("x= " + rv[0] + " km");
//			System.out.println("y= " + rv[1] + " km");
//			System.out.println("z= " + rv[2] + " km");
//			coords[k * 3 + 0] = rv[0];
//			coords[k * 3 + 1] = rv[1];
//			coords[k * 3 + 2] = rv[1];
			coords[k * 3 + 0] = rv.get(0);
			coords[k * 3 + 1] = rv.get(1);
			coords[k * 3 + 2] = rv.get(2);
		}
		int num_vert = coords.length / 3;
		int[] stripLengths = { num_vert };

		LineStripArray myLines =
			new LineStripArray(num_vert, GeometryArray.COORDINATES | GeometryArray.COLOR_3, stripLengths);
		Color3f colors[] = new Color3f[num_vert];
		for (int i = 0; i < num_vert; i++)
			colors[i] = Color;
		myLines.setColors(0, colors);
		myLines.setCoordinates(0, coords);

		this.setGeometry(myLines);

	}
}
