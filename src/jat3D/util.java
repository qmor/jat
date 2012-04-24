package jat3D;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.GraphicsConfigTemplate3D;

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

	
}


