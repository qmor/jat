package jat.application.orbitviewer;

import org.math.plot.FrameView;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;

public class plotsphere {

	public static void main(String[] args) {

		Plot3DPanel p = new Plot3DPanel();

		double radius=1;
	
		p.addSpherePlot("toto", radius);
		p.setFixedBounds(0, -1, 1);
		p.setFixedBounds(1, -1, 1);
		p.setFixedBounds(2, -1, 1);
		p.setLegendOrientation(PlotPanel.SOUTH);
		new FrameView(p);
	}
}
