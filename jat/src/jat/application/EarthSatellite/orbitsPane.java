package jat.application.EarthSatellite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class orbitsPane extends Composite
{
  private List list;
  public orbitsPane(Composite parent)
  {
    super(parent, SWT.NONE);
  	setLayout(new GridLayout());
  	final Label orbitsLabel = new Label(this, SWT.NONE);
  	orbitsLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
  	orbitsLabel.setText("Orbits");

  	list = new List(this, SWT.BORDER);
  	list.setItems(new String[] {"Orbit1", "Orbit2", "Transfer Orbit"});
  	list.setData("newKey", null);
  	list.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
  }
}
