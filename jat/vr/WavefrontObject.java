/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project and the Center for Space Research (CSR),
 * The University of Texas at Austin. All rights reserved.
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

package jat.vr;

import java.io.*;
import java.applet.Applet;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;


/** WavefrontObject class
 * @author Tobias Berthold
 */
public class WavefrontObject extends ABody3D
{

	public WavefrontObject(Applet myapplet, String filename, float scale)
	{
		super(myapplet);
		this.scale = scale;
		String fullname;
		Scene s = null;

//		int flags = ObjectFile.RESIZE;
//		ObjectFile f =	new ObjectFile(flags);
		ObjectFile f = new ObjectFile();

		filename = Wavefront_path + filename;
		try
		{
			s = f.load(filename);
		} catch (FileNotFoundException e)
		{
			System.err.println(e);
			System.err.println("Exception loading file: " + e);
			//System.exit(1);
		}
		addChild(s.getSceneGroup());
	}
}
