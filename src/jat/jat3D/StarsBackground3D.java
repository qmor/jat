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

package jat.jat3D;

import java.io.IOException;

import jat.core.astronomy.StarCatalog;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * Planet class
 * 
 * @author Tobias Berthold
 */
public class StarsBackground3D extends BranchGroup {
	float radius;
	String Texturefilename;
	Appearance app;
	Color3f Starcolor; // Star color if texture not found

	public StarsBackground3D(float scale) {
		super();
		Starcolor = Colors.blue;

		addChild(createStarSphere());

	}

	private BranchGroup createStarSphere(){

		BranchGroup bg = new BranchGroup();

		StarCatalog s;
		try {
			s = new StarCatalog();
			System.out.println(s.size()+" stars loaded");
		} catch (IOException e) {
			System.out.println("Problem loading star database");
			e.printStackTrace();
		}


		
		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);

		return bg;

	}

	private BranchGroup createPointCloud() {

		BranchGroup bg = new BranchGroup();

		final java.util.Random rand = new java.util.Random();
		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);
		float[] point = new float[3];
		float[] brightness = new float[3];
		for (int i = 0; i < 20000; i++) {
			point[0] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[1] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[2] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[0] *= 1.e8;
			point[1] *= 1.e8;
			point[2] *= 1.e8;

			starfield.setCoordinate(i, point);
			final float mag = rand.nextFloat();
			brightness[0] = mag;
			brightness[1] = mag;
			brightness[2] = mag;
			starfield.setColor(i, brightness);
		}
		bg.addChild(new Shape3D(starfield));

		return bg;

	}

	private Background createBackGraph() {
		Background background = new Background();
		final BoundingSphere infiniteBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
		background.setApplicationBounds(infiniteBounds);
		BranchGroup bg = new BranchGroup();

		final java.util.Random rand = new java.util.Random();
		PointArray starfield = new PointArray(20000, PointArray.COORDINATES | PointArray.COLOR_3);
		float[] point = new float[3];
		float[] brightness = new float[3];
		for (int i = 0; i < 20000; i++) {
			point[0] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[1] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			point[2] = (rand.nextInt(2) == 0) ? rand.nextFloat() * -1.0f : rand.nextFloat();
			starfield.setCoordinate(i, point);
			final float mag = rand.nextFloat();
			brightness[0] = mag;
			brightness[1] = mag;
			brightness[2] = mag;
			starfield.setColor(i, brightness);
		}
		bg.addChild(new Shape3D(starfield));

		background.setGeometry(bg);
		return background;
	}

}
