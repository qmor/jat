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

import jat.core.math.matvec.data.VectorN;
import jat.core.util.FileUtil;
import jat.core.util.FileUtil2;

import java.applet.Applet;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Base class for 3D objects. Planet, moons, and spacecraft extend this class.
 * To be extended by specific object
 * 
 * @author Tobias Berthold
 */

// abstract public class body3D extends TransformGroup
public class Body3D extends TransformGroup {
	Vector3f Vf = new Vector3f();
	Vector3d Vd = new Vector3d();
	Vector3d VRot = new Vector3d();
	Transform3D Trans = new Transform3D();
	double scale = 1.0; // scale factor for 3D objects
	Applet myapplet;
	static String images_path, Lightwave_path, Wavefront_path, ThreeDStudio_path;

	public Body3D() {
		FileUtil2 f = new FileUtil2();
		String fs = FileUtil.file_separator();
		images_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "images_hires" + fs;
		//System.out.println(images_path);
		Wavefront_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "Wavefront" + fs;
		Lightwave_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "Lightwave" + fs;
		ThreeDStudio_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "3DStudio" + fs;
		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	}

//	public Body3D(Applet myapplet) {
//		this.myapplet = myapplet;
//
//		FileUtil2 f = new FileUtil2();
//		String fs = FileUtil.file_separator();
//		images_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "images_hires" + fs;
//		//System.out.println(images_path);
//		Wavefront_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "Wavefront" + fs;
//		Lightwave_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "Lightwave" + fs;
//		ThreeDStudio_path = f.root_path + "data" + fs + "core" + fs + "vr" + fs + "3DStudio" + fs;
//
//		setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//
//		// add();
//	}

	// Methods to be implemented in subclasses
	// abstract public void add();

	public void set_scale(double scale) {
		getTransform(Trans);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	/**
	 * Set the position of the body in km
	 * 
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param z
	 *            z position
	 */
	public void set_position(double x, double y, double z) {
		getTransform(Trans);
		Vd.x = x;
		Vd.y = y;
		Vd.z = z;
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	/**
	 * Set new body position.
	 * 
	 * @param rv
	 *            new position
	 */
	public void set_position(double[] rv) {
		getTransform(Trans);
		Vd.x = rv[0];
		Vd.y = rv[1];
		Vd.z = rv[2];
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	/**
	 * Set new body position.
	 * 
	 * @param r
	 *            new position
	 */
	public void set_position(VectorN r) {
		getTransform(Trans);
		Vd.x = r.x[0];
		Vd.y = r.x[1];
		Vd.z = r.x[2];
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	public void set_position(Point3d r) {
		getTransform(Trans);
		Vd.x = r.x;
		Vd.y = r.y;
		Vd.z = r.z;
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	public void set_position(Vector3d r) {
		getTransform(Trans);
		Vd.x = r.x;
		Vd.y = r.y;
		Vd.z = r.z;
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	/**
	 * Set_body attitude.using quaternion
	 * 
	 * @param quatObject
	 *            quaternion
	 */
	public void set_attitude(Transform3D quatObject) {
		setTransform(quatObject);
	}

	/**
	 * Set body attitude without changing position or scale using Euler angles
	 * 
	 * @param alpha
	 *            x angle
	 * @param beta
	 *            y angle
	 * @param gamma
	 *            z angle
	 */
	public void set_attitude(double alpha, double beta, double gamma) {
		getTransform(Trans);
		Trans.get(Vf);
		VRot.x = alpha;
		VRot.y = beta;
		VRot.z = gamma;
		Trans.setEuler(VRot);
		Trans.setTranslation(Vf);
		Trans.setScale(scale);
		setTransform(Trans);
	}

	/**
	 * Set body position and attitude
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param alpha
	 * @param beta
	 * @param gamma
	 */
	public void set_pos_attitude(double x, double y, double z, double alpha, double beta, double gamma) {
		getTransform(Trans);
		Trans.get(Vf);
		VRot.x = alpha;
		VRot.y = beta;
		VRot.z = gamma;
		Trans.setEuler(VRot);
		Vd.x = x;
		Vd.y = y;
		Vd.z = z;
		Trans.setTranslation(Vd);
		Trans.setScale(scale);
		setTransform(Trans);
	}
}

// Transform3D T_3D = new Transform3D();
// Transform3D RotX = new Transform3D();
// Transform3D RotY = new Transform3D();
// Transform3D RotZ = new Transform3D();
/*
 * public void set_attitude(double alpha, double beta, double gamma) {
 * getTransform(T_3D); T_3D.get(Vf); // translate.set(Vf);
 * 
 * RotX.setIdentity(); RotY.setIdentity(); RotZ.setIdentity(); RotX.rotX(alpha);
 * RotY.rotY(beta); RotZ.rotZ(gamma); RotX.mul(RotY); // RotX=RotX . RotY
 * RotX.mul(RotZ); // RotX=RotX . RotZ
 * 
 * RotX.setTranslation(Vf); setTransform(RotX); }
 * 
 * 
 * public void set_attitude(double alpha, double beta, double gamma) {
 * getTransform(RotX); RotX.get(Vf); // scale=RotX.getScale();
 * RotY.setIdentity(); RotZ.setIdentity(); RotX.rotX(alpha); RotY.rotY(beta);
 * RotZ.rotZ(gamma); RotX.mul(RotY); // RotX=RotX . RotY RotX.mul(RotZ); //
 * RotX=RotX . RotZ
 * 
 * RotX.setTranslation(Vf); RotX.setScale(scale); setTransform(RotX); }
 */

// public void set_earth_rotation(double angle)
// {
// earthRotate.setIdentity();
// earthRotate.rotZ(angle);
// }
//
// public void rotate_earth()
// {
// TG_earth.getTransform(T_3D);
// T_3D.mul(earthRotate, T_3D);
// TG_earth.setTransform(T_3D);
// }
