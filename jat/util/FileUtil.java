/* JAT: Java Astrodynamics Toolkit
 *
 * Copyright (c) 2002 The JAT Project. All rights reserved.
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

package jat.util;

import java.net.*;

public class FileUtil {
	
	public static String file_separator() {
		return System.getProperty("file.separator");
	}

	public static String getClassFilePath(String package_name,
			String class_name) {
		String out = null;

		// check for Windoze
		String file_separator = System.getProperty("file.separator");
		boolean windoze = file_separator.contentEquals("\\");

		String arg = package_name + "." + class_name;
		URL url = null;

		try {
			Class c = Class.forName(arg);
			String classRes = "/" + arg.replace('.', '/') + ".class";
			url = c.getResource(classRes);
		} catch (Throwable t) {
			out = "Unable to locate class " + arg;
			System.out.println("Unable to locate class " + arg);
		}

		try {
			// get the path
			out = url.toURI().getPath();
//			System.out.println("out = "+out);
			
			// chop off the unneeded stuff
			if (windoze) {
				out = out.substring(1, out.length()
						- (class_name + ".class").length());				
			} else {
				out = out.substring(0, out.length()
						- (class_name + ".class").length());				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// if windoze, replace with backslashes
		if (windoze) {
			out = out.replace("/", "\\");
		}
		return out;
	}

}