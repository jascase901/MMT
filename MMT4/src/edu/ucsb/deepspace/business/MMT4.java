package edu.ucsb.deepspace.business;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ucsb.deepspace.gui.MMT4MainWindow;

public class MMT4 {
	
	private static Mediator mediator;
	private static MMT4MainWindow window;
	
	public static void main(String[] args) {
		mediator = Mediator.getInstance();
		Shell shell = new Shell(Display.getDefault(), SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		window = new MMT4MainWindow(shell, SWT.NULL, mediator);
		window.alive();
	}
	
}