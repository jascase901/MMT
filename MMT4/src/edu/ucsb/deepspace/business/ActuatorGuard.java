package edu.ucsb.deepspace.business;

import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.ucsb.deepspace.business.Reflectable;


public class ActuatorGuard {
	
	Bookkeeper bk = Bookkeeper.getInstance();
	private final ExecutorService exec = Executors.newFixedThreadPool(1);
	
	public ActuatorGuard() {
		
	}
	
	/**
	 * 
	 * @param actName actuator name
	 * @param command what command given to actuator
	 * @param steps number of steps to move, send 0 unless you are moving the actuator
	 * @return
	 */
	public Future<String> sendCommand(String actName, final ActuatorCommand command, final String steps) {
		System.out.println("ActuatorGuard.sendCommand");
		final Actuator act = (Actuator) bk.getReflectables().get(actName);
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				if (act == null) {
					return "Please select an actuator.\n";
				}
//				if (!act.alive()) {
//					return "Actuator is not working.\n";
//				}
				String result = "";
				String[] split;
				double[] temp2 = new double[8];
				
				switch (command) {
					case MOVERELATIVE:
						result = act.sendCommand2("Move Relative", Long.parseLong(steps));    break;
					case MOTORON:
						result = act.motorControl((byte) 0xFF);                               break;
					case MOTORREALLYOFF:
						result = act.motorControl((byte) 0xFF);                               break;
					case MOTOROFF:
						result = act.motorControl((byte) 0x00);                               break;
					case GETSTATUS:
						result = act.sendCommand(command);                               break;
					case INTERNALTEMP:
						result = act.sendCommand(command);
						split = result.split(" ");
						System.out.println(result);
						for (int i = 0; i < 8; i++) {
							temp2[i] = .0625*Double.parseDouble(split[i+2]);
						}
						act.setIntTemps(temp2);                                               break;
					case EXTERNALTEMP:
						result = act.sendCommand(command);
						System.out.println(result);
						split = result.split(" ");
						for (int i = 0; i < 8; i++) {
							temp2[i] = .0625*Double.parseDouble(split[i+2]);
						}
						act.setExtTemps(temp2);                                               break;
					case REBOOT:
						result = act.reboot();                                                break;
					case READEEPROM:
						//result = act.writeEeprom();                                           break;
					case WRITEEPROM:
						result = act.writeThenReadEEPROM();                                   break;
					case LED:
						result = act.sendCommand2("LED", Long.parseLong(steps));              break;
					default:
						result = "Wrong command string used.\n";                              break;
				}
				
//				if (command.equals("Move Relative")) {
//					System.out.println("asdf");
//					result = act.sendCommand2("Move Relative", Long.parseLong(steps));
//				}
//				else if (command.equals("Motor On")) {
//					result = act.motorControl((byte) 0xFF);
//				}
//				else if (command.equals("Motor Really Off")) {
//					result = act.motorReallyOff();
//				}
//				else if (command.equals("Motor Off")) {
//					result = act.motorControl((byte) 0x00);
//				}
//				else if (command.equals("Get Status")) {
//					result = act.sendCommand("Get Status");
//				}
//				else if (command.equals("Internal Temp")) {
//					result = act.sendCommand(command);
//					split = result.split(" ");
//					System.out.println(result);
//					for (int i = 0; i < 8; i++) {
//						temp2[i] = .0625*Double.parseDouble(split[i+2]);
//					}
//					act.setIntTemps(temp2);
//				}
//				else if (command.equals("External Temp")) {
//					result = act.sendCommand(command);
//					System.out.println(result);
//					split = result.split(" ");
//					for (int i = 0; i < 8; i++) {
//						temp2[i] = .0625*Double.parseDouble(split[i+2]);
//					}
//					act.setExtTemps(temp2);
//				}
//				else if (command.equals("Reboot")) {
//					result = act.reboot();
//				}
//				else if (command.equals("Read EEPROM")) {
//					result = act.writeThenReadEEPROM();
//					//result = act.readEeprom();
//				}
//				else if (command.equals("LED")) {
//					result = act.sendCommand2("LED", Long.parseLong(steps));
//				}
//				else {
//					result = "Wrong command string used.\n";
//				}
				return result + "\n";
			}
		};
		return exec.submit(call);
	}
	
	public Future<String> silenceActs() {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Map<String, Reflectable> reflectables = bk.getReflectables();
				for (String i : new TreeSet<String>(reflectables.keySet())) {
					Reflectable r = reflectables.get(i);
					if (r.getType().equals("Actuator")) {
						if (r.getName().equals("1")) continue;
						Actuator act = (Actuator) r;
						System.out.println(i + act.getType());
						Thread.sleep(100);
						act.sendCommand2("Move Relative", 4L);
					}
				}
				return "Actuators silenced.\n";
			}
			
		};
		return exec.submit(call);
	}
	
	public Future<String> calibrate(String actName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Map<String, Reflectable> reflectables = bk.getReflectables();
				for (String i : new TreeSet<String>(reflectables.keySet())) {
					Reflectable r = reflectables.get(i);
					if (r.getType().equals("Actuator")) {
						Actuator act = (Actuator) r;
						System.out.println(i + act.getType());
						Thread.sleep(1000);
						act.sendCommand2("Move Relative", 4L);
					}
				}
				return "Actuators silenced.\n";
			}
			
		};
		return exec.submit(call);
	}
	
	public void kill() {
		exec.shutdown();
		//FTDIact.getInstance().close();
		FTDI.getInstance().close();
		//FTD2XXJ.getInstance().kill();
	}
	
	public long error(String actName) {
		Actuator act = (Actuator) bk.getReflectables().get(actName);
		double error = act.getCoord().getRadius() - act.getGoalDist();
		long steps = (long) (error/0.000001);
		return steps;
	}
	
	public void music() throws IOException, InterruptedException {
		Map<String, Reflectable> refls = bk.getReflectables();
		//Actuator act1 = (Actuator) refls.get("1");
		//Actuator act2 = (Actuator) refls.get("2");
//		Actuator act3 = (Actuator) refls.get("3");
//		Actuator act4 = (Actuator) refls.get("4");
		Actuator act5 = (Actuator) refls.get("5");
//		Actuator act6 = (Actuator) refls.get("6");
//		Actuator act7 = (Actuator) refls.get("7");
		
		act5.sendCommand2("Move Relative", 3);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(2000);
		
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(2000);
		
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(2000);
		
		act5.sendCommand2("Move Relative", 3);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 6);
		Thread.sleep(1000);
		
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 8);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 9);
		Thread.sleep(1000);
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(1000);
		
		act5.sendCommand2("Move Relative", 7);
		Thread.sleep(2000);
		
		act5.sendCommand2("Move Relative", 7);
		
	}
	
	public void test(String actName) {
		Map<String, Reflectable> refls = bk.getReflectables();
		Actuator act = (Actuator) refls.get(actName);
		try {
			act.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}