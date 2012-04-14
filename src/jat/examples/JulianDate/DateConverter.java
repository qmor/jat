package jat.examples.JulianDate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class DateConverter extends JApplet {
	static int appletwidth = 400; // Width of Applet
	static int appletheight = 300;
	public JButton btn_Jul_to_Cal;
	JButton btn_Cal_to_Jul;
	JFormattedTextField yearfield;
	JFormattedTextField monthfield;
	JFormattedTextField JDfield;
	ButtonHandler myb;

	/**
	 * Create the applet.
	 */
	public DateConverter() {
		myb=new ButtonHandler(this);
		getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel level1_Pane = new JPanel();
		JPanel level2_Pane_Mid = new JPanel();
		JPanel level2_Pane_Jul = new JPanel();
		JPanel level2_Pane_Cal = new JPanel();
		level2_Pane_Jul.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblJulianDate = new JLabel("Days");
		level2_Pane_Jul.add(lblJulianDate);
		level2_Pane_Jul.setBorder(BorderFactory
				.createTitledBorder("Julian Date"));

		getContentPane().add(level1_Pane);
		level1_Pane.setLayout(new BoxLayout(level1_Pane, BoxLayout.X_AXIS));
		level1_Pane.add(level2_Pane_Jul);
		
		JDfield = new JFormattedTextField();
		JDfield.setColumns(12);
		level2_Pane_Jul.add(JDfield);
		level1_Pane.add(level2_Pane_Mid);
		level1_Pane.add(level2_Pane_Cal);
		level2_Pane_Mid.setLayout(new BoxLayout(level2_Pane_Mid,
				BoxLayout.Y_AXIS));
		btn_Jul_to_Cal = new JButton(">>");
		//btn_Jul_to_Cal.addActionListener(new ButtonHandler());
		btn_Jul_to_Cal.addActionListener(myb);

		level2_Pane_Mid.add(btn_Jul_to_Cal);

		btn_Cal_to_Jul = new JButton("<<");
		btn_Cal_to_Jul.addActionListener(myb);
		level2_Pane_Mid.add(btn_Cal_to_Jul);
		level2_Pane_Cal
				.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("102px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("114px:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("32px"),},
			new RowSpec[] {
				RowSpec.decode("25px"),
				RowSpec.decode("19px"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("19px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblCalendarDate = new JLabel("Year");
		level2_Pane_Cal.add(lblCalendarDate, "2, 1, left, center");

		yearfield = new JFormattedTextField();
		int year = 2000;
		yearfield.setValue(new Integer(year));
		yearfield.setColumns(10);
		level2_Pane_Cal.add(yearfield, "4, 1, left, top");

		JLabel lblYear = new JLabel("Month");
		level2_Pane_Cal.add(lblYear, "2, 2, left, center");
		level2_Pane_Cal.setBorder(BorderFactory
				.createTitledBorder("Calendar Date"));

		monthfield = new JFormattedTextField();
		monthfield.setColumns(10);
		level2_Pane_Cal.add(monthfield, "4, 2, center, top");

		JLabel lblNewLabel = new JLabel("Day");
		level2_Pane_Cal.add(lblNewLabel, "2, 4, right, default");
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		level2_Pane_Cal.add(formattedTextField, "4, 4, fill, default");

		JLabel lblHour = new JLabel("Hour");
		level2_Pane_Cal.add(lblHour, "2, 6");

		JLabel lblMinute = new JLabel("Minute");
		level2_Pane_Cal.add(lblMinute, "2, 8");

		JLabel lblSecond = new JLabel("Second");
		level2_Pane_Cal.add(lblSecond, "2, 10");
	}


}
