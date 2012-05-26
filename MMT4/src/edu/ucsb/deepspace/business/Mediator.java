package edu.ucsb.deepspace.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Button;

import edu.ucsb.deepspace.persistence.Writer;

public class Mediator {
	
	private static final Mediator INSTANCE = new Mediator();
	private TrackerGuard trkGuard;
	private ActuatorGuard actGuard;
	private String ipAddress = "192.168.1.4";
	private String userName = "user";
	private String password = "";
	private Secretary secretary;
	private final ExecutorService exec = Executors.newFixedThreadPool(1);
	//private final MMTSocketServer sockserv;
	
	private Mediator() {
		this.trkGuard = new TrackerGuard(ipAddress, userName, password);
		this.actGuard = new ActuatorGuard();
		this.secretary = new Secretary();
		secretary.start();
		secretary.setName("Secretary");
		ReflectableIO.readRef("reflectables.csv");
		
		//FTDIact.getInstance();
		FTDI.getInstance();
		//sockserv = new MMTSocketServer(this);
		//sockserv.start();
		//sockserv.setName("Socket Server");
	}
	
	public void kill() {
		ReflectableIO.writeRef("reflectables.csv");
		exec.shutdown();
		actGuard.kill();
		trkGuard.kill();
		secretary.kill();
		secretary.interrupt();
		//sockserv.flagToggle();
		//sockserv.interrupt();
	}
	
	public static Mediator getInstance() {return INSTANCE;}	
	
	public void trackerCommand(Button button, TrackerCommands tc) {
		Command c = new Command(button, trkGuard.commandNoArg(tc));
		secretary.waitForResult(c);
	}
	
	public void move(final double radius, final double theta, final double phi, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.move(radius, theta, phi)));
	}
	
	public void initialize(final boolean minimum, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.initialize(minimum)));
	}
	
	public void search(final double radius, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.search(radius)));
	}
	
	public void setMeasureMode(final TrackerMeasureMode mode, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.setMeasureMode(mode)));
	}
	
	public void compensation(String refName, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.compensate(refName)));
	}
	
	public void updateReflPos(String refName, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.updateReflPos(refName)));
	}
	
	public void goToRef(String refName, Button button) {
		secretary.waitForResult(new Command(button, trkGuard.goToRef(refName)));
	}
	
	/**
	 * 
	 * @param actName actuator name
	 * @param command command
	 * @param steps number of steps to move, 0 if N/A for the command
	 */
	public void sendCommand(String actName, ActuatorCommand command, String steps, Button button) {
		System.out.println("Mediator.sendCommand");
		secretary.waitForResult(new Command(button, actGuard.sendCommand(actName, command, steps)));
	}
	
	public void silenceActuators(Button button) {
		secretary.waitForResult(new Command(button, actGuard.silenceActs()));
	}
	
	public void saveReflectable(Button button, String name) {
		secretary.waitForResult(new Command(button, trkGuard.saveRefl(name)));
	}
	
	public void exportRefl(Button button, final String filename) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				ReflectableIO.writeRef(filename);
				return "Reflectables exported.\n";
			}
		};
		Future<String> fut = exec.submit(call);
		secretary.waitForResult(new Command(button, fut));
	}
	
	public void importRefl(Button button, final String filename) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				ReflectableIO.readRef(filename);
				return "Reflectables imported.\n";
			}
		};
		Future<String> fut = exec.submit(call);
		secretary.waitForResult(new Command(button, fut));
	}
	
	//for finding the pointing vector of an actuator
	public void calibrate(final String actName, Button button) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				List<Coordinate> position = new ArrayList<Coordinate>();
				for (int i = 0; i < 10; i++) {
					position.add(trkGuard.measThenUpdate(actName, 10));
					actGuard.sendCommand(actName, ActuatorCommand.MOVERELATIVE, "-40");
				}
				position.add(trkGuard.measThenUpdate(actName, 10));
				String out = "";
				for (Coordinate c : position) {
					out += c.toCsv() + "\n";
				}
				FileUtils.writeStringToFile(new File("tester.csv"), out);
				return "Calibration done.";
			}
		};
		Future<String> fut = exec.submit(call);
		secretary.waitForResult(new Command(button, fut));
	}
	
	public void performTest(Button button) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				trkGuard.actGoalPos();
				Calendar begin = new GregorianCalendar(TimeZone.getDefault());
				String filename = Test.makeDir(begin, "test");
				McMaker mcMaker = new McMaker(begin, 1d);
				MeasurementConfig mc;
				boolean flag = true;
				String out = "";
				while (flag) {
					double time = new GregorianCalendar(TimeZone.getDefault()).getTimeInMillis();
					mc = mcMaker.nextMc();
					flag = mc.getFlag();
					out = trkGuard.testMeasure(mc);
					out = out + "," + time;
					if(mc.getServo()) {
						String steps = String.valueOf(actGuard.error(mc.getName()));
						actGuard.sendCommand(mc.getName(), ActuatorCommand.MOVERELATIVE, steps);
					}
					filename = filename + "/data.csv";
					Writer.append(filename, out);
				}
				return null;
			}
		};
		Future<String> fut = exec.submit(call);
		secretary.waitForResult(new Command(button, fut));
	}
	
	public String reflPresent() {
		return trkGuard.reflPresent();
	}
	
	public String move(final double radius, final double theta, final double phi) {
		return trkGuard.move2(radius, theta, phi);
	}
	
	public String search(double radius) {
		return trkGuard.search2(radius);
	}
	
	public String getCoordinates() {
		return trkGuard.getCoordinates();
	}
	
	public void music() {
		try {
			actGuard.music();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void test(String actName) {
		actGuard.test(actName);
	}
	
}