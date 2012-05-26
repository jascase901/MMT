package edu.ucsb.deepspace.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MMTSocketServer extends Thread {
	
    private Mediator mediator;
    private boolean flag = true;
    public MMTSocketServer(Mediator mediator) {
    	this.mediator = mediator;
    }
    
    @Override
    public void run() {
    	Protocol prot = new Protocol(mediator);
    	ServerSocket serverSocket = null;
    	
    	try {
    		serverSocket = new ServerSocket(4444);
			serverSocket.setSoTimeout(5000);
		
	    	PrintWriter out;
	    	BufferedReader in;
	    	String inputLine, outputLine;
	    	Socket clientSocket = null;
    	
	        while (flag) {
	        	try {
	        		clientSocket = serverSocket.accept();
	        	} catch (SocketTimeoutException e) {
	        		continue; //ignore, when this is thrown it means there was no connection within the last 5 seconds, so the loop restarts
	            }
    			
    			out = new PrintWriter(clientSocket.getOutputStream(), true);
    			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    			
    			inputLine = in.readLine();
    			outputLine = prot.processInput(inputLine);
    			out.print(outputLine);
    			
    			out.close();
    			in.close();
    			clientSocket.close();
	    	}
	        serverSocket.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    void flagToggle() {flag = false;}
    
}