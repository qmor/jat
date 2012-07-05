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

import java.io.File;
import java.net.URL;

/**
 * @author Tobias Berthold 
 * File Utilities
 * 
 */
public class FileUtil2 {

	public String root_path;
	//public String current_path;
	public String fs = File.separator;

	public FileUtil2() {
		//current_path = find_current_path2();
		root_path = find_root();
	}

	public String find_data_folder() {

		return find_root() + "data/";
	}

	/**
	 * @return path to root Finds the path to the root of the project. Starts
	 *         with the path of the class from which it is called, strips
	 *         everything after it finds the string "jat" or "jatdevelop". Works
	 *         with an open folder structure or inside a jar file, on a local
	 *         hard disk as well as on the Internet.
	 */
	public String find_root() {

		String resource_path = FileUtil2.class.getProtectionDomain().getCodeSource().getLocation().getPath();

		String[] numberSplit = resource_path.split("/");

		String root_path = "/";
		int i = 1;
		do {
			// System.out.println(numberSplit[i]);
			root_path = root_path + numberSplit[i] + "/";
			i++;
		} while (!(numberSplit[i].equals("jat")) && !(numberSplit[i].equals("jatdevelop"))
				&& !(numberSplit[i].equals("jatexperimental")));

		root_path = root_path + "jat" + "/";

		System.out.println(root_path);

		return (root_path);
	}

//	public String find_current_path2() {
//
//		try {
//
//
//			//System.out.println("path: "+path);
//			System.out.println("root path: "+root_path);
//
//			// URL helpURL2 = new URL(fileName);
//			ResourceLoader c = new ResourceLoader();
//			URL url = c.loadURL(this.getClass(), ".");
//			System.out.println(url.getPath());
//
//			// displayURL(helpURL2, editorPane, relative_path);
//
//		} catch (Exception e) {
//			System.err.println("Couldn't find current path in jat.core.util.FileUtil2");
//			System.exit(0);
//		}
//		return "";
//	}
/*
	public String find_current_path() {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource("");
		if (url == null) {
			System.out.println("Failed to get current path in FileUtil2");
			System.exit(1);
		} else {
			System.out.println(url.getPath());
			String current_path = url.getPath();
		}
		return current_path;
	}

*/

}


//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//URL url = classLoader.getResource("");
//String current_path = url.getPath();
// System.out.println(url.getPath());
