package jat.matvec.io.gui;


import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameView extends JFrame {

  public FrameView(JPanel panel) {
    setContentPane(panel);
    pack();
    setVisible(true);
  }

  public FrameView(String title,JPanel panel) {
    super(title);
    setContentPane(panel);
    pack();
    setVisible(true);
  }

   public FrameView(JPanel[] panels) {
    JPanel panel = new JPanel();
    for (int i=0;i<panels.length;i++) {
      panel.add(panels[i]);
    }
    setContentPane(panel);
    pack();
    setVisible(true);
  }


}

