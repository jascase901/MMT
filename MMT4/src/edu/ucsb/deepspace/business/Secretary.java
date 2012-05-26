package edu.ucsb.deepspace.business;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

import edu.ucsb.deepspace.gui.MMT4MainWindow;

public class Secretary extends Thread {
	private final ExecutorService exec = Executors.newFixedThreadPool(3);
	private final BlockingQueue<Command> queue = new ArrayBlockingQueue<Command>(50);
	
	void waitForResult(final Command command) {
		exec.submit(new Runnable() {
			public void run() {
				try {
					command.calcFut();
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("Hello from the catch block of Secretary's \"waitForResult\" method.");
				} catch (ExecutionException e) {
					System.out.println("ExecutionException in Secretary's waitForResult method.");
					e.printStackTrace();
				}
				queue.add(command);
			}
		});
	}
	
	@Override
	public void run() {
		Command temp;
		while (true) {
			try {
				temp = queue.take();
			} catch (InterruptedException e) {
				break;
			}
			final String message = temp.getMessage();
			final Button button = temp.getSource();
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					button.setEnabled(true);
					MMT4MainWindow.updateStatusArea(message);
					System.out.println(message);
					if (message.contains("ADM") || message.contains("IFM")) {
						MMT4MainWindow.updateMeasMode(message);
					}
				}
			});
		}
	}
	
	public void kill() {
		exec.shutdown();
	}
}