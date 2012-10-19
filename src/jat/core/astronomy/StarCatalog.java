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

package jat.core.astronomy;

import jat.core.util.PathUtil;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class StarCatalog {
	public List<Stardata> manystardata;
	PathUtil p;
	public int a;

	public StarCatalog(PathUtil p) {
		this.p = p;
	}

	public void load() {
		manystardata = new ArrayList<Stardata>();

		// PathUtil f = new PathUtil();
		// String fs = f.fs;
		 String star_data_file = p.data_path+"astronomy/hyg_100.csv";
		String[] nextLine;
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(star_data_file));
			reader.readNext(); // read over header line
			while ((nextLine = reader.readNext()) != null) {
				// for (int i = 0; i < nextLine.length; i++)
				// System.out.print(nextLine[i] + " | ");
				// System.out.println();
				manystardata.add(new Stardata(nextLine[6], Double.parseDouble(nextLine[7]), Double
						.parseDouble(nextLine[8])));
			}
		} catch (IOException e) {
			System.out.println("Problem loading star database");
			e.printStackTrace();
		}

	}

}
