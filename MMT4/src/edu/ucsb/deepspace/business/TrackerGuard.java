package edu.ucsb.deepspace.business;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import smx.tracker.MeasurePointData;
import smx.tracker.TrackerException;
import edu.ucsb.deepspace.persistence.Writer;

public class TrackerGuard {
	
	private Tracker trk;
	private final ExecutorService exec = Executors.newFixedThreadPool(1);
	
	public TrackerGuard(String ipAddress, String userName, String password) {
		this.trk = new Tracker(ipAddress, userName, password);
	}
	
	public void kill() {
		exec.shutdown();
	}
	
	public Future<String> commandNoArg(final TrackerCommands c) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					switch (c) {
					case CONNECT:
						result = trk.connect(); break;
					case DISCONNECT:
						result = trk.disconnect(); break;
					case ABORT:
						result = trk.abort(); break;
					case WEATHER:
						result = trk.weather(); break;
					case HOME:
						result = trk.home(); break;
					case HEALTHCHECKS:
						result = trk.healthChecks(); break;
					case STARTUPCHECKS:
						result = trk.startupChecks(); break;
					case TARGETTYPE:
						result = trk.targetType(); break;
					default:
						System.out.println("TrackerGuard.commandNoArg no argument given.  Error.");
					}
					
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}
		};
		return exec.submit(call);
	}
	
//	public Future<String> connect(){
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.connect();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}
//		};
//		return exec.submit(call);
//	}
	
//	public Future<String> disconnect() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.disconnect();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
//	
//	public Future<String> abort() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.abort();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
//	
//	public Future<String> weather() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.weather();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
//	
//	public Future<String> home() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.home();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
	
	public Future<String> move(final double radius, final double theta, final double phi) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.move(radius, theta, phi, false);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
//	public Future<String> healthChecks() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.healthChecks();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
	
	public Future<String> initialize(final boolean minimum) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.initialize(minimum);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	public Future<String> search(final double radius) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String result = "";
				try {
					result = trk.search(radius);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	public Future<String> setMeasureMode(final TrackerMeasureMode mode) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() {
				String result = "";
				try {
					result = trk.setMeasureMode(mode);
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
//	public Future<String> startupChecks() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() {
//				String result = "";
//				try {
//					result = trk.startupChecks();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
	
//	public Future<String> targetType() {
//		Callable<String> call = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				String result = "";
//				try {
//					result = trk.targetType();
//				} catch (TrackerException e) {
//					result = "Message from exception: " + e.getMessage();
//					e.printStackTrace();
//				}
//				return result;
//			}	
//		};
//		return exec.submit(call);
//	}
	
	public Future<String> compensate(final String refName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() {
				String result = "";
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(refName);
				try {
					trk.move(t.getCoord());
					Coordinate before = new Coordinate(trk.measure(10));
					trk.move(0, -2*before.getTheta(), Math.PI, true);
					Coordinate after = new Coordinate(trk.measure(10));
					double radiusError = before.getRadius() - after.getRadius();
					double thetaError = before.getTheta() - (after.getTheta() - -2*before.getTheta());
					double phiError = before.getPhi() - (after.getPhi() - Math.PI);
					
					Calendar time = new GregorianCalendar(TimeZone.getDefault());
					int month = time.get(Calendar.MONTH) + 1;
					String timestamp = month + "," + time.get(Calendar.DAY_OF_MONTH) + "," + time.get(Calendar.YEAR) + "," + time.get(Calendar.HOUR_OF_DAY) + "," + time.get(Calendar.MINUTE) + "," + time.get(Calendar.SECOND);
					String dataOut = radiusError + "," + thetaError + "," + phiError;
					String totalOut = "\"" + t.getName() + "\"," + timestamp + "," + dataOut;
					String filename = "compensation/" + t.getName() + ".csv";
					Writer.append(filename, totalOut);
					result = "Compensation recorded.\n";
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				} catch (IOException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	public Future<String> saveRefl(final String name) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				try {
					Coordinate c = new Coordinate(trk.measure(1));
					Reflectable r = new Target(name, c);
					Map<String, Reflectable> reflectables = Bookkeeper.getInstance().getReflectables();
					reflectables.put(name, r);
					Bookkeeper.getInstance().setReflectables(reflectables);
				} catch (TrackerException e) {
					e.printStackTrace();
				}
				return "Reflectable saved.\n";
			}
		};
		return exec.submit(call);
	}
	
	public Future<String> goToRef(final String refName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(refName);
				String result = "";
				try {
					result = trk.move(t.getCoord());
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	public Future<String> updateReflPos(final String refName) {
		Callable<String> call = new Callable<String>() {
			@Override
			public String call() throws Exception {
				Target t = (Target) Bookkeeper.getInstance().getReflectables().get(refName);
				String result = "";
				try {
					Coordinate coord = new Coordinate(trk.measure(1));
					t.setCoord(coord);
					result = "Position updated.";
				} catch (TrackerException e) {
					result = "Message from exception: " + e.getMessage();
					e.printStackTrace();
				}
				return result;
			}	
		};
		return exec.submit(call);
	}
	
	public Coordinate measThenUpdate(String actName, int numPoints) {
		Target t = (Target) Bookkeeper.getInstance().getReflectables().get(actName);
		
		try {
			trk.move(t.getCoord());
			if(!trk.reflPresent()) {
				trk.move(t.getCoord());
			}
			Coordinate coord = new Coordinate(trk.measure(numPoints));
			t.setCoord(coord);
			return coord.toCartesian();
		} catch (TrackerException e) {
			e.printStackTrace();
			throw new Error("problem from measThenUpdate in TrackerGuard");
		}
	}
	
	public void actGoalPos() {
		Map<String, Actuator> acts = Bookkeeper.getInstance().getActuators();
		for (String s : acts.keySet()) {
			Coordinate c = measThenUpdate(s, 3);
			acts.get(s).setGoalDist(c.getRadius());
		}
	}
	
	public String testMeasure(MeasurementConfig mc) {
		try {
			String weather = trk.weatherCsv();
			Reflectable r = Bookkeeper.getInstance().getReflectables().get(mc.getName());
			trk.move(r.getCoord());
			trk.measure(mc);
			String out = "";
			out = r.getName() + "," + r.getCoord().toCsv() + "," + weather;
			return out;
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		throw new Error("error with testMeasure in TrackerGuard");
	}
	
	public String reflPresent() {
		String out = "error";
		try {
			boolean present = trk.reflPresent();
			if (present) out = "present";
			else out = "not present";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public String move2(final double radius, final double theta, final double phi) {
		String out = "error";
		try {
			trk.move(radius, theta, phi, false);
			return "moved";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public String search2(final double radius) {
		String out = "error";
		try {
			trk.search(radius);
			boolean present = trk.reflPresent();
			if (present) out = "present";
			else out = "not present";
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public String getCoordinates() {
		String out = "error";
		try {
			boolean present = trk.reflPresent();
			if (!present) {
				out = "no reflectable";
				return out;
			}
			MeasurePointData[] point = trk.measure(1);
			Coordinate c = new Coordinate(point);
			return c.toCsv();
		} catch (TrackerException e) {
			e.printStackTrace();
		}
		return out;
	}
	
}