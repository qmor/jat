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

import java.net.URL;

public class FileUtil2 {

	public String root_path;
	public String current_path ;

	public FileUtil2() {
		current_path=find_current_path();
		root_path=find_root();
	}


	public String find_data_folder() {

		return find_root() + "data/";
	}

	public String find_root() {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource("");
		// System.out.println(url.getPath());
		String current_path = url.getPath();
		// System.out.println(current_path);

		String[] numberSplit = current_path.split("/");

		String root_path = "/";
		int i = 1;
		do {
			// System.out.println(numberSplit[i]);
			root_path = root_path + numberSplit[i] + "/";
			i++;
		} while (!(numberSplit[i].equals("jat")));

		root_path = root_path + "jat" + "/";

		//System.out.println(root_path);

		return (root_path);
	}

	public String find_current_path() {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource("");
		// System.out.println(url.getPath());
		String current_path = url.getPath();
		return current_path;
	}
}
