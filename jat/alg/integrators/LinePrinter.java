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

package jat.alg.integrators;
import java.io.*;

/** <P>
 * The LinePrinter Class provides a way to print out integrator output data
 * to a tab delimited ASCII file. Note: remember to close the LinePrinter
 * when you are done using it.
 *
 * @author <a href="mailto:dgaylor@users.sourceforge.net">Dave Gaylor
 * @version 1.0
 */
public class LinePrinter implements Printable {

	private int[] indices;
	private PrintWriter pw;
	private FileOutputStream outfile;
	private boolean printall;
	private String directory = "C:\\Temp\\";
	private String filename = "output.txt";
	private double multiple = 1;
	private int counter = 0;
	private boolean isadaptive = false;

	/** Default constructor. Prints the entire state array to System.out.
	 */
	public LinePrinter() {
		this.printall = true;
		this.pw = new PrintWriter(System.out, true);
	}

	/** Prints a subset of the state array to System.out.
	 * @param i Integer array containing the indices of the elements of the state array to be printed.
	 */
	public LinePrinter(int[] i) {
		this.printall = false;
		this.indices = new int[i.length];
		for (int j = 0; j < i.length; j++) {
			this.indices[j] = i[j];
		}
		this.pw = new PrintWriter(System.out, true);
	}

	/** Prints the entire state array to the user supplied directory and file.
	 * @param dir String containing the directory.
	 * @param fname String containing the filename.
	 * @throws IOException thrown if it can't open the file.
	 */
	public LinePrinter(String dir, String fname) {
		this.printall = true;
		this.directory = new String(dir);
		this.filename = new String(fname);
		openFile();
	}

	/** Prints the entire state array to the user supplied directory and file.
	 * @param dir String containing the directory.
	 * @param fname String containing the filename.
	 * @throws IOException thrown if it can't open the file.
	 */
	public LinePrinter(String fname) {
		this.printall = true;
		openFile(fname);
	}

	/** Prints the entire state array to the user supplied directory and file.
	 * @param dir String containing the directory.
	 * @param fname String containing the filename.
	 * @param i Integer array containing the indices of the elements of the state array to be printed.
	 * @throws IOException thrown if it can't open the file.
	 */
	public LinePrinter(String dir, String fname, int[] i) {
		this.printall = false;
		this.directory = new String(dir);
		this.filename = new String(fname);
		this.indices = new int[i.length];
		for (int j = 0; j < i.length; j++) {
			this.indices[j] = i[j];
		}
		openFile();
	}
	/** Opens the file
	 */
	private void openFile() {
		try {
			this.outfile = new FileOutputStream(directory + filename);
			this.pw = new PrintWriter(outfile);
		} catch (IOException e) {
			System.err.println("LinePrinter error opening file: " + e);
		}
		return;
	}

	/** Opens the file
	 * @param fname String containing directory and filename
	 */
	private void openFile(String fname) {
		try {
			this.outfile = new FileOutputStream(fname);
			this.pw = new PrintWriter(outfile);
		} catch (IOException e) {
			System.err.println("LinePrinter error opening file: " + e);
		}
		return;
	}

	/** Closes the LinePrinter, the PrintWriter and the output file. Always remember to call this method when you are done printing!
	 */
	public void close() {
		// close the PrintWriter
		pw.close();

		// if necessary, close the output file
		if (this.outfile != null) {
			try {
				outfile.close();
			} catch (IOException e) {
				System.err.println("LinePrinter error closing file: " + e);
			}
		}
		return;
	}
	
	/** Set whether the print calls are fixed step (false) or adaptive (true).
	 * If true, the print call will disregard thinning procedures.
	 * @param a boolean parameter
	 */
	public void setIsAdaptive(boolean a){
	    this.isadaptive = a;
	}
	
	/** Print out data only every n seconds
	 * @param n  how often to print
	 */
	
	public void setThinning(double n) {
		multiple = n;
	}
	
	/**
	 * Return the value of the thinning.
	 * @see setThinning(double)
	 * @return value of thinning
	 */
	public double getThinning(){
	    return multiple;
	}

	/** Implements the Printable interface. This method is called once per integration step by the integrator.
	 * @param t time or independent variable.
	 * @param y state or dependent variable array.
	 */
	public void print(double t, double[] y) {	
		double tprint = counter * multiple;
		if (t == tprint || isadaptive) {
			// print the time variable
			pw.print(t + "\t");

			// print the state array
			if (printall) {
				// print all of the y array
				for (int j = 0; j < y.length; j++) {
					pw.print(y[j] + "\t");
				}
			} else {
				// check to make sure there is enough to print
				if (indices.length > y.length) {
					System.out
							.println("LinePrinter: too many elements to print");
					return;
				}
				// print the requested parts of the y array
				for (int j = 0; j < indices.length; j++) {
					pw.print(y[indices[j]] + "\t");
				}
			}

			// add the linefeed for the next line
			pw.println();
			counter = counter + 1;
		}		
	}

	/** Implements the Printable interface. This method is called once per integration step by the integrator.
	 * @param y double array.
	 */
	public void print(double[] y) {

		// print the state array
		if (printall) {
			// print all of the y array
			for (int j = 0; j < y.length; j++) {
				pw.print(y[j] + "\t");
			}
		} else {
			// check to make sure there is enough to print
			if (indices.length > y.length) {
				System.out.println("LinePrinter: too many elements to print");
				return;
			}
			// print the requested parts of the y array
			for (int j = 0; j < indices.length; j++) {
				pw.print(y[indices[j]] + "\t");
			}
		}

		// add the linefeed for the next line
		pw.println();
	}

	/** Print a string to a line
	 * @param str string to be printed
	 */
	public void println(String str) {
		pw.println(str);
	}

	/** Print a string array to a line
	 * @param str string array to be printed
	 */
	public void println(String[] str) {
		for (int i = 0; i < str.length; i++) {
			pw.print(str[i] + "\t");
		}
		pw.println();
	}

	/** Print a string array to a line
	 * @param title string containing a title
	 * @param str string array to be printed
	 */
	public void println(String title, String[] str) {
		pw.print(title + "\t");
		for (int i = 0; i < str.length; i++) {
			pw.print(str[i] + "\t");
		}
		pw.println();
	}

}
