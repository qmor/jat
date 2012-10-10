/* JAT: Java Astrodynamics Toolkit
 * 
  Copyright 2012 Tobias Berthold

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package jat.core.util;

import java.applet.Applet;
import java.io.File;
import java.net.URL;

/**
 * @author Tobias Berthold File Utilities
 * 
 */
public class PathUtil {

	public String root_path;
	public String current_path;
	public String DE405Path;
	public String data_path;
	public String fs = File.separator;

	public PathUtil() {
		root_path = find_root();
		data_path = find_data_folder();
		DE405Path = root_path + "data/core/ephemeris/DE405data/";
		
	}

	public PathUtil(Applet myapplet) {

		current_path = find_current_path(myapplet);
		data_path = find_data_folder();
		root_path = find_root();
		DE405Path = root_path + "data/core/ephemeris/DE405data/";
		
		System.out.print("<PathUtil 1> ");
		System.out.println(current_path);
	}

	public String find_data_folder() {

		return find_root() + "data";
	}

	/**
	 * @return path to root Finds the path to the root of the project. Starts
	 *         with the path of the class from which it is called, strips
	 *         everything after it finds the string "jat" or "jatdevelop". Works
	 *         with an open folder structure or inside a jar file, on a local
	 *         hard disk as well as on the Internet.
	 */
	public String find_root() {

		String resource_path = PathUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		// System.out.print("<PathUtil 2> ");
		// System.out.println(resource_path);
		String[] numberSplit = resource_path.split("/");

		String root_path = "/";
		int i = 1;
		// go back in the directory tree until you find "jat" or others
		do {
			// System.out.println(numberSplit[i]);
			root_path = root_path + numberSplit[i] + "/";
			i++;
		} while (!(numberSplit[i].equals("jat")) && !(numberSplit[i].equals("jatdevelop"))
				&& !(numberSplit[i].equals("jatexperimental")));

		root_path = root_path + "jat" + "/";

		// System.out.println(root_path);

		return (root_path);
	}

	public String find_current_path(Applet a) {

		try {
			ResourceLoader c = new ResourceLoader();
			URL url = c.loadURL(a.getClass(), ".");
			// System.out.println(url.getPath());
			return url.getPath();

		} catch (Exception e) {
			System.err.println("Couldn't find current path in jat.core.util.PathUtil");
			System.exit(0);
			return "";
		}
	}

}
