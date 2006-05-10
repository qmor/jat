package jat.application.EarthSatellite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.custom.SashForm;

// This program uses the Lightweight OpenGL library LWJGL
// Under Windows, the path to the DLL's has to be given
// In Eclipse, go to the run configurations, add the following line
// to the VM arguments:
// -Djava.library.path=E:\workspace\Jat\external_dll\lwjgl
// and substitute the path to where you have the dll's stored on your hard disk 



public class MainProgram
{
	static Display display;

	public static void main(String[] args)
	{
		display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		SashForm form = new SashForm(shell, SWT.HORIZONTAL);
		form.setLayout(new FillLayout());
		
		Composite animationpane = new AnimationPane(display, form);
		animationpane.setLayout(new FillLayout());

		Composite orbits = new orbitsPane(form);
		//orbits.setLayout(new FillLayout());
		
		shell.setText("JAT Earth Satellite");
		shell.setSize(629, 470);

		final Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		final MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("File");

		final Menu menu_1 = new Menu(fileMenuItem);
		fileMenuItem.setMenu(menu_1);

		final MenuItem viewMenuItem = new MenuItem(menu, SWT.CASCADE);
		viewMenuItem.setText("View");

		final Menu menu_2 = new Menu(viewMenuItem);
		viewMenuItem.setMenu(menu_2);

		final MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("Help");

		final Menu menu_3 = new Menu(helpMenuItem);
		helpMenuItem.setMenu(menu_3);
		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		display.dispose();
	}

}
