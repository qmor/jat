package jat.core.astronomy;

import jat.core.util.FileUtil;
import jat.core.util.FileUtil2;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class StarCatalog {
	
	public List manystardata;

	public StarCatalog() {
		manystardata = new ArrayList();

	}

	public void load() {
		FileUtil2 f = new FileUtil2();
		String fs = FileUtil.file_separator();
		String star_data_file = f.root_path + "data" + fs + "core" + fs + "astronomy" + fs + "hyg_100.csv";
		String[] nextLine;
		CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(star_data_file));
			reader.readNext(); // read over header line
			while ((nextLine = reader.readNext()) != null) {
				for (int i = 0; i < nextLine.length; i++)
					System.out.print(nextLine[i] + " | ");
				System.out.println();
				manystardata.add(new onestardata(nextLine[6], Double.parseDouble(nextLine[7]), Double
						.parseDouble(nextLine[8])));
			}
		} catch (IOException e) {
			System.out.println("Problem loading star database");
			e.printStackTrace();
		}

	}



	public int size() {
		return manystardata.size();
	}

	public static void main(String[] args) throws IOException {

		// manystardata.add(new onestardata("a", 1., 1.));
		// manystardata.add(new onestardata("b", 2., 3.));
		// onestardata t = (onestardata) manystardata.get(1);
		// System.out.println(t.ProperName);

		/*
		 * StarCatalog s = new StarCatalog();
		 * 
		 * onestardata t; for (int i = 0; i < manystardata.size(); i++) { t =
		 * (onestardata) manystardata.get(i);
		 * 
		 * System.out.println(t.ProperName + " " + t.RA + " " + t.dec); }
		 */
	}
}
