package jat.matvec.io.data;

import jat.matvec.data.Matrix;
import jat.matvec.io.data.fileTools.MatrixString;
import jat.matvec.io.data.fileTools.CharFile;

import java.io.File;
//import java.io.IOException;

public class MatrixFile {

  private Matrix M;
  private File file;

  public MatrixFile(File f,Matrix m) {
    M = m;
    file = f;
    CharFile.toFile(file,MatrixString.printMatrix(M));
  }

  public MatrixFile(String fn,Matrix m) {
    M = m;
    file = new File(fn);
    CharFile.toFile(file,MatrixString.printMatrix(M));
  }

  public MatrixFile(File f) {
    file = f;
    if (file.exists()) {
      M = MatrixString.readMatrix(CharFile.fromFile(file));
    } else {
      M = new Matrix(0,0);
      throw new IllegalArgumentException("File does not exist.");
    }
  }

  public MatrixFile(String fn) {
    file = new File(fn);
    if (file.exists()) {
      M = MatrixString.readMatrix(CharFile.fromFile(file));
    } else {
      M = new Matrix(0,0);
      throw new IllegalArgumentException("File does not exist.");
    }
  }

  public Matrix getMatrix() {
    return M;
  }

  public File getFile() {
    return file;
  }

  public String getFileName() {
    return file.getName();
  }
}