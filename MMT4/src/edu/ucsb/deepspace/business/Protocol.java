package edu.ucsb.deepspace.business;

import java.io.IOException;
import java.util.Map;

public class Protocol {
    private Mediator mediator;
    
    public Protocol(Mediator mediator) {
    	this.mediator = mediator;
    }

    public String processInput(String theInput) {
        String theOutput = null;
        String[] split = theInput.split("%");
        
        String command = split[0];
        String[] args = split[1].split(",");
        
        if (command.equals("move")) {
    		double radius = Double.parseDouble(args[0]);
        	double theta = Double.parseDouble(args[1]);
        	double phi = Double.parseDouble(args[2]);
        	theOutput = mediator.move(radius, theta, phi);
    	}
    	else if (command.equals("present")) {
    		theOutput = mediator.reflPresent();
    	}
    	else if (command.equals("search")) {
    		double radius = Double.parseDouble(args[0]);
    		theOutput = mediator.search(radius);
    	}
    	else if (command.equals("coordinates")) {
    		theOutput = mediator.getCoordinates();
    	}
    	else if (command.equals("moveAct")) {
    		theOutput = "enter paramaters as:  name,amount";
    		String name = args[0];
        	System.out.println(name);
        	long steps = Long.parseLong(args[1]);
        	System.out.println(name);
        	System.out.println(steps);
        	Map<String, Reflectable> acts = Bookkeeper.getInstance().getReflectables();
        	Actuator a = (Actuator) acts.get(name);
        	if (a == null) System.out.println("act is null");
        	try {
				a.sendCommand2("Move Relative", steps);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        return theOutput;
    }
}