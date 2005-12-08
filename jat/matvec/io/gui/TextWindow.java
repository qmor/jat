package jat.matvec.io.gui;

import jat.matvec.data.Text;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class TextWindow extends JPanel {

  private Text text;

  public TextWindow(Text t) {
    text  = t;
    toWindow();
  }

  public TextWindow(String s) {
    text = new Text(s);
    toWindow();
  }

  private void toWindow() {
    JTextArea textArea = new JTextArea(text.getString());
    JScrollPane scrollPane = new JScrollPane(textArea);
    add(scrollPane);
  }

}