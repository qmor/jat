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

import java.awt.Font;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * @author Tobias Berthold
 * 
 */
public class RGBAxis3D extends Body3D {

	float axislength;
	Shape3D s;
	TransformGroup axislabelTrans;
	/**
	 * creates a 3D x-y-z axis object
	 */
	public RGBAxis3D() {
		super();
		axislength = 15000.0f;
		s = new Shape3D();
		s.setGeometry(createGeometry());
		addChild(s);
		addChild(generateAxesLabels());
	}

	/**
	 * creates a 3D x-y-z axis object
	 * 
	 * @param axislength
	 *            size of the x-y-z axes
	 * 
	 */
	public RGBAxis3D(float axislength) {
		this.axislength = axislength;
		s = new Shape3D();
		s.setGeometry(createGeometry());
		addChild(s);
	}

	public RGBAxis3D(double axislength) {
		this.axislength = (float) axislength;
		s = new Shape3D();
		s.setGeometry(createGeometry());
		addChild(s);
	}

	private Geometry createGeometry() {
		IndexedLineArray axisLines = new IndexedLineArray(18, GeometryArray.COORDINATES | GeometryArray.COLOR_3, 30);

		// create line for X axis
		axisLines.setCoordinate(0, new Point3f(-1.0f * axislength, 0.0f, 0.0f));
		axisLines.setCoordinate(1, new Point3f(1.0f * axislength, 0.0f, 0.0f));
		axisLines.setCoordinate(2, new Point3f(0.9f * axislength, 0.1f * axislength, 0.1f * axislength));
		axisLines.setCoordinate(3, new Point3f(0.9f * axislength, -0.1f * axislength, 0.1f * axislength));
		axisLines.setCoordinate(4, new Point3f(0.9f * axislength, 0.1f * axislength, -0.1f * axislength));
		axisLines.setCoordinate(5, new Point3f(0.9f * axislength, -0.1f * axislength, -0.1f * axislength));
		// create line for Y axis
		axisLines.setCoordinate(6, new Point3f(0.0f, -1.0f * axislength, 0.0f));
		axisLines.setCoordinate(7, new Point3f(0.0f, 1.0f * axislength, 0.0f));
		axisLines.setCoordinate(8, new Point3f(0.1f * axislength, 0.9f * axislength, 0.1f * axislength));
		axisLines.setCoordinate(9, new Point3f(-0.1f * axislength, 0.9f * axislength, 0.1f * axislength));
		axisLines.setCoordinate(10, new Point3f(0.1f * axislength, 0.9f * axislength, -0.1f * axislength));
		axisLines.setCoordinate(11, new Point3f(-0.1f * axislength, 0.9f * axislength, -0.1f * axislength));
		// create line for Z axis
		axisLines.setCoordinate(12, new Point3f(0.0f, 0.0f, -1.0f * axislength));
		axisLines.setCoordinate(13, new Point3f(0.0f, 0.0f, 1.0f * axislength));
		axisLines.setCoordinate(14, new Point3f(0.1f * axislength, 0.1f * axislength, 0.9f * axislength));
		axisLines.setCoordinate(15, new Point3f(-0.1f * axislength, 0.1f * axislength, 0.9f * axislength));
		axisLines.setCoordinate(16, new Point3f(0.1f * axislength, -0.1f * axislength, 0.9f * axislength));
		axisLines.setCoordinate(17, new Point3f(-0.1f * axislength, -0.1f * axislength, 0.9f * axislength));

		axisLines.setCoordinateIndex(0, 0);
		axisLines.setCoordinateIndex(1, 1);
		axisLines.setCoordinateIndex(2, 2);
		axisLines.setCoordinateIndex(3, 1);
		axisLines.setCoordinateIndex(4, 3);
		axisLines.setCoordinateIndex(5, 1);
		axisLines.setCoordinateIndex(6, 4);
		axisLines.setCoordinateIndex(7, 1);
		axisLines.setCoordinateIndex(8, 5);
		axisLines.setCoordinateIndex(9, 1);
		axisLines.setCoordinateIndex(10, 6);
		axisLines.setCoordinateIndex(11, 7);
		axisLines.setCoordinateIndex(12, 8);
		axisLines.setCoordinateIndex(13, 7);
		axisLines.setCoordinateIndex(14, 9);
		axisLines.setCoordinateIndex(15, 7);
		axisLines.setCoordinateIndex(16, 10);
		axisLines.setCoordinateIndex(17, 7);
		axisLines.setCoordinateIndex(18, 11);
		axisLines.setCoordinateIndex(19, 7);
		axisLines.setCoordinateIndex(20, 12);
		axisLines.setCoordinateIndex(21, 13);
		axisLines.setCoordinateIndex(22, 14);
		axisLines.setCoordinateIndex(23, 13);
		axisLines.setCoordinateIndex(24, 15);
		axisLines.setCoordinateIndex(25, 13);
		axisLines.setCoordinateIndex(26, 16);
		axisLines.setCoordinateIndex(27, 13);
		axisLines.setCoordinateIndex(28, 17);
		axisLines.setCoordinateIndex(29, 13);

		Color3f colors[] = new Color3f[18];
		for (int i = 0; i < 6; i++)
			colors[i] = Colors.red;
		for (int i = 6; i < 12; i++)
			colors[i] = Colors.green;
		for (int i = 12; i < 18; i++)
			colors[i] = Colors.blue;
		axisLines.setColors(0, colors);

		axisLines.setColorIndex(0, 0);
		axisLines.setColorIndex(1, 1);
		axisLines.setColorIndex(2, 2);
		axisLines.setColorIndex(3, 1);
		axisLines.setColorIndex(4, 3);
		axisLines.setColorIndex(5, 1);
		axisLines.setColorIndex(6, 4);
		axisLines.setColorIndex(7, 1);
		axisLines.setColorIndex(8, 5);
		axisLines.setColorIndex(9, 1);
		axisLines.setColorIndex(10, 6);
		axisLines.setColorIndex(11, 7);
		axisLines.setColorIndex(12, 8);
		axisLines.setColorIndex(13, 7);
		axisLines.setColorIndex(14, 9);
		axisLines.setColorIndex(15, 7);
		axisLines.setColorIndex(16, 10);
		axisLines.setColorIndex(17, 7);
		axisLines.setColorIndex(18, 11);
		axisLines.setColorIndex(19, 7);
		axisLines.setColorIndex(20, 12);
		axisLines.setColorIndex(21, 13);
		axisLines.setColorIndex(22, 14);
		axisLines.setColorIndex(23, 13);
		axisLines.setColorIndex(24, 15);
		axisLines.setColorIndex(25, 13);
		axisLines.setColorIndex(26, 16);
		axisLines.setColorIndex(27, 13);
		axisLines.setColorIndex(28, 17);
		axisLines.setColorIndex(29, 13);

		return axisLines;

	} // end of Axis createGeometry()

	public TransformGroup generateAxesLabels() {
		TransformGroup axislabelTrans = new TransformGroup();

		Font3D font3d = new Font3D(new Font("Display", Font.PLAIN, 1000000), new FontExtrusion());

		float axeslength=axislength;
		float i_xaxes=1000f;
		float i_yaxes=1000f;
		// X-Axis Label
//		Text3D xfont = new Text3D(font3d, new String("X"), new Point3f((i_xaxes + axeslength + 0.5f),
//				(i_yaxes - 0.25f), 0.0f));
//		Shape3D xshape = new Shape3D(xfont);
		Text3D xfont = new Text3D(font3d, new String("X"), new Point3f(axislength,axislength,0));
		Shape3D xshape = new Shape3D(xfont);

		// Y-Axis Label
		Text3D yfont = new Text3D(font3d, new String("Y"), new Point3f((i_xaxes - 0.25f),
				(i_yaxes + axeslength + 0.5f), 0.0f));
		Shape3D yshape = new Shape3D(yfont);

		// Z-Axis Label
		Text3D zfont = new Text3D(font3d, new String("Z"), new Point3f((i_xaxes - 0.5f), (i_yaxes - 0.5f),
				(axeslength + 0.5f)));
		Shape3D zshape = new Shape3D(zfont);

		axislabelTrans.addChild(xshape);
		axislabelTrans.addChild(yshape);
		axislabelTrans.addChild(zshape);

		return axislabelTrans;
	} // End generateAxesLabels

} // end of class Axis

