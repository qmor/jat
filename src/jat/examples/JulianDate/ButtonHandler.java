package jat.examples.JulianDate;

import jat.core.cm.cm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonHandler implements ActionListener {
	DateConverter d;
	Integer year;
	
	public ButtonHandler(DateConverter d) {
		this.d=d;
	}

	public void actionPerformed(ActionEvent ev) {

		// Read in values
		if (ev.getSource() == d.btn_Jul_to_Cal) {
			System.out.println("button 1 pressed");
			Integer year = (Integer) d.yearfield.getValue();
			System.out.println(year);

			System.out.println(cm.juliandate(year, 1, 1, 12, 0, 0));

		}// End of if(ev.getSource() == startButton)
		if (ev.getSource() == d.btn_Cal_to_Jul) {
			System.out.println("button 2 pressed");
			year=(Integer) d.yearfield.getValue();
			d.JDfield.setValue((Double) cm.juliandate(year, 1, 1, 12, 0, 0));

		}
	}// End of ActionPerformed
}// End of Button Handler
