package jat.application.orbitviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class orbitviewerEvents implements ActionListener, ItemListener {
	orbitviewerGUI d;

	public orbitviewerEvents(orbitviewerGUI d) {
		this.d = d;
	}

	public void actionPerformed(ActionEvent ev) {

		// Read in values
		if (ev.getSource() == d.setButton) {
			//System.out.println("button 1 pressed");

			d.o.a = (Double) d.semimajorfield.getValue();
			d.o.e = (Double) d.EccentricityField.getValue();
			d.o.i = (Double) d.InclinationField.getValue();
			d.o.raan = (Double) d.RightAscensionField.getValue();
			d.o.w = (Double) d.ArgumentofPerigeeField.getValue();
			d.o.ta = (Double) d.TrueAnomalyField.getValue();
			//System.out.println("a "+ d.o.a);

			d.o.plot.removeAllPlots();
			d.o.add_scene();

		}
	}// End of ActionPerformed

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		Object source = e.getItemSelectable();

		if (source == d.rotationCheckBox) {
			 System.out.println("rot");
			 if(d.rotationCheckBox.isSelected())
					d.o.plot.plotCanvas.timer.start();
				else
					d.o.plot.plotCanvas.timer.stop();
				
		}		
	}

}
