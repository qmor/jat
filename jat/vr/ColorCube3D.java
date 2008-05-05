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

import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.*;

/** Sphere class
 * @author Tobias Berthold
 */
public class ColorCube3D extends Body3D
{
	//float size=100.f;

	/**
	 * Constructor Sphere3D.
	 * @param size size
	 */
	public ColorCube3D(float size)
	{
//		this.size=size;
		addChild(new ColorCube(size));
	}

	public ColorCube3D(double size)
	{
//		this.size=size;
		addChild(new ColorCube(size));
	}

	public ColorCube3D(float size, Color3f color, float x, float y, float z)
	{
		addChild(new ColorCube(size));
		set_position(x, y, z);
	}
}
