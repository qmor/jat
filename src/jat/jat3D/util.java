package jat.jat3D;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4f;

public class util {

	public boolean check_for_Java3D_a() {

		try {
			Class.forName("javax.media.j3d.J3DBuffer");
		} catch (final ClassNotFoundException e) {
			// Your logic here
			return false;
		}
		return true;
	}

	public boolean check_for_Java3D_b() {

		try
		{
		   GraphicsConfigTemplate3D gconfigTemplate = new GraphicsConfigTemplate3D();
		   GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gconfigTemplate);
		}
		catch (Error e) // You shouldn't normally catch java.lang.Error... this is an exception
		{
		   System.out.println("Java3D binaries not installed");
			return false;
		}
		return true;
	}

	public static void print(String title, Matrix4f mat) {

		System.out.println(title);
		System.out.println(mat.m00 + " " + mat.m01 + " " + mat.m02 + " " + mat.m03);
		System.out.println(mat.m10 + " " + mat.m11 + " " + mat.m12 + " " + mat.m13);
		System.out.println(mat.m20 + " " + mat.m21 + " " + mat.m22 + " " + mat.m23);
		System.out.println(mat.m30 + " " + mat.m31 + " " + mat.m32 + " " + mat.m33);
		System.out.println();
	}

	public static void print_matrix_of_transform3D(String title, Transform3D t3) {
		Matrix4f mat = new Matrix4f();
		t3.get(mat);
		System.out.println(title);
		System.out.println(mat.m00 + " " + mat.m01 + " " + mat.m02 + " " + mat.m03);
		System.out.println(mat.m10 + " " + mat.m11 + " " + mat.m12 + " " + mat.m13);
		System.out.println(mat.m20 + " " + mat.m21 + " " + mat.m22 + " " + mat.m23);
		System.out.println(mat.m30 + " " + mat.m31 + " " + mat.m32 + " " + mat.m33);
		System.out.println();
	}	
}


