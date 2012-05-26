package edu.ucsb.deepspace.business;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Button;

public class Command {
	
	private Button source;
	private String message;
	private Future<String> fut;
	
	public Command(Button source, Future<String> fut) {
		this.source = source;
		this.fut = fut;
	}

	public String getMessage() {return message;}

	public Button getSource() {return source;}
	
	public Future<String> getFut() {return fut;}
	
	public void calcFut() throws InterruptedException, ExecutionException {
		message = fut.get();
	}
	
}