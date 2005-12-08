/* JAT: Java Astrodynamics Toolkit
*
* Copyright (c) 2005 Emergent Space Technologies Inc. All rights reserved.
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
package jat.forces;

import jat.math.MathUtils;
import jat.matvec.data.Matrix;
import jat.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * This class alows access to the gravity model data files (e.g. JGM2, JGM3, etc)
 * 
 * @author David Gaylor
 *
 */
public class GravityModel extends SphericalHarmonicGravity {

	private String file_separator = System.getProperty("file.separator");

	private String filePath = null;
	
	private String path = null;

	private GravityModelType type;

	private int size = 0;
	
	private double gm = 0.0;
	
	private double rref = 0.0;
	
	private int nmax = 0;
	
	private int mmax = 0;

	private boolean foundSize = false;
	
	private boolean normalized;
	
    /** Construct a gravity model from a STK gravity file included in JAT
     * @param n Desired degree.
     * @param m Desired order.
     * @param typ GravityModelType
     */
	public GravityModel(int n, int m, GravityModelType typ) {
		super(n, m);		
		type = typ;
		try{
		    path = FileUtil.getClassFilePath("jat.forces", "GravityModel")
		    		+ "earthGravity";
		}catch(NullPointerException e){
		    path = "C:/Code/Jat/jat/forces/earthGravity/";
		}
		filePath = path + file_separator + type.toString() + ".grv";
		System.out.println(filePath);
		initialize();
	}
	
    /** Construct a gravity model from any STK gravity file
     * @param n Desired degree.
     * @param m Desired order.
     * @param filepath path and filename of gravity file
     */
	public GravityModel(int n, int m, String filepath) {
		super(n, m);
		filePath = filepath;
		System.out.println(filePath);
		initialize();
	}	

	public void initialize() {
		// process the gravity file
		Matrix cs = readGrvFile(filePath);
		// check desired gravity model size vs what we have
		if ((this.n_desired > nmax)||(this.m_desired > mmax)) {
			System.err.println("GravityModel.initialize: desired size is greater than max size from gravity file");
			System.exit(-99);
		}
		// initialize
        this.initializeGM(gm);
        this.initializeR_ref(rref);
        this.initializeCS(nmax, mmax, cs.A);
	}

	private Matrix readGrvFile(String file) {
		
		File outfile = new File(path + file_separator+"test.out");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outfile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader in = new BufferedReader(fr);
		String line = null;
		String begin = "BEGIN";
		boolean beginFound = false;
		String end = "END";
		boolean endFound = false;

		while (!beginFound) {
			try {
				line = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			}
			// break line into space-delimited tokens
			String[] tokens = line.split("\\s");
			
			String first = tokens[0];			

			// look for size of gravity field
			if (first.matches("Degree")) {
				nmax = Integer.parseInt(tokens[tokens.length-1]);
				size = nmax + 1;
				foundSize = true;
				System.out.println("GravityModel: Max Degree = " + nmax);
			}

			if (first.matches("Order")) {
				mmax = Integer.parseInt(tokens[tokens.length-1]);
				System.out.println("GravityModel: Max Order = " + mmax);
			}

			if (first.matches("Gm")) {
				gm = Double.parseDouble(tokens[tokens.length-1]) + 1;
				System.out.println("GravityModel: GM = " + gm);
			}

			if (first.matches("RefDistance")) {
				rref = Double.parseDouble(tokens[tokens.length-1]) + 1;
				System.out.println("GravityModel: Rref = " + rref);
			}

			if (first.matches("Normalized")) {
				if (tokens[tokens.length-1].matches("Yes")) {
					normalized = true;
				} else {
					normalized = false;
				}
				System.out.println("GravityModel: normalized = " + normalized);
			}

			
			if (first.matches("BEGIN")) {
				beginFound = true;
				System.out.println("found BEGIN");
				if (!foundSize) {
					System.err
							.println("GravityModel: Size (Degree) of Gravity Field Not Found\n");
					System.exit(-99);
				}
			}
		}

		Matrix out = new Matrix(size, size);
		out.set(0,0,1.0);
		int nlines = 0;
		
		while (!endFound) {
			try {
				line = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			}
			// break line into tokens
			StringTokenizer tokens = new StringTokenizer(line, " ");
			int ntokens = tokens.countTokens();
			nlines = nlines + 1;
//			System.out.println("number of tokens = "+ntokens);
			
			String first = " ";			
			if (ntokens > 0) {
				first = tokens.nextToken();
			} else {
				first = line.trim();
			}
			
			// look for the end
			if (first.matches("END")) {
				endFound = true;
				System.out.println("found END");
				out.print(pw);
				pw.close();
				System.out.println("Lines Processed: "+nlines);
				return out;
			}
			
			// parse the data records
			if (ntokens == 4) {
				int n = Integer.parseInt(first);
				int m = Integer.parseInt(tokens.nextToken());
				
				// compute unnormalization factor
				double nf = 1.0;				
				if (normalized) nf = normFactor(n, m);
				
				// obtain the c and s values
				double c = nf * Double.parseDouble(tokens.nextToken());
				double s = nf * Double.parseDouble(tokens.nextToken());
//				System.out.println(ntokens+":"+n+":"+m+":"+c+":"+s);
				
				// put c and s into the correct places in the cs matrix
				out.set(n, m, c);
				if (m > 0) out.set(m-1, n, s);				
			}
		}
		
		out.print(pw);
		return out;
	}
	
	private double normFactor (int n, int m) {
		double nn = new Integer(n).doubleValue();
		double mm = new Integer(m).doubleValue();
		double nmmfact = MathUtils.factorial(nn - mm);
		double npmfact = MathUtils.factorial(nn + mm);
		double  delta = 0.0;
		if (m == 0) delta = 1.0;
		double num = nmmfact*(2.0*nn+1.0)*(2.0-delta);
		double out = 0.0;
		if (npmfact == 0.0) {
			System.out.println("GravityModel.normFactor: denominator = 0, n= "+n+" m = "+m+" npmfact = "+npmfact);
		} else {
			out = Math.sqrt(num/npmfact);
		}
		return out;
	}

	public static void main(String args[]) {
		GravityModel x = new GravityModel(20, 20, GravityModelType.JGM3);
		x.printParameters();
	}



}