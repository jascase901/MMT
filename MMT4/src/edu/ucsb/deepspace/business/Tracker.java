package edu.ucsb.deepspace.business;

import edu.ucsb.deepspace.business.Bookkeeper;
import edu.ucsb.deepspace.business.Coordinate;
import edu.ucsb.deepspace.business.MeasurementConfig;
import smx.tracker.ADMOnly;
import smx.tracker.AverageFilter;
import smx.tracker.ContinueTrigger;
import smx.tracker.Filter;
import smx.tracker.InterferometerOnly;
import smx.tracker.InterferometerSetByADM;
import smx.tracker.IntervalTrigger;
import smx.tracker.MeasureCfg;
import smx.tracker.MeasurePointData;
import smx.tracker.NullStartTrigger;
import smx.tracker.SMRTargetType;
import smx.tracker.StartTrigger;
import smx.tracker.TrackerException;
import smx.tracker.WeatherInformation;
/**
 * Wraps access to the smx.tracker.Tracker object.  This class provides more functionality than the default methods
 * contained in smx.tracker.Tracker.  
 * @author Reed Sanpore
 *
 */
public class Tracker {
	private smx.tracker.Tracker trk;
	private final String ipAddress, name, password;
	private static final String NOT_CONNECTED = "Not connected to the tracker.\n";
	private static final String TRACKER_TYPE = "TrackerKeystone";
	private static final boolean BLOCKING = true;
	private Filter filter;
	private StartTrigger startTrigger;
	private ContinueTrigger conTrigger;
	private MeasureCfg cfg;
	
	
	/**
	 * Constructor for the tracker wrapper.
	 * @param ipAddress IP address of the tracker, "192.168.1.4"
	 * @param userName "user"
	 * @param password ""
	 */
	Tracker (String ipAddress, String userName, String password) {
		this.ipAddress = ipAddress;
		this.name = userName;
		this.password = password;
		try {
			trk = new smx.tracker.Tracker(TRACKER_TYPE);
			trk.setBlocking(BLOCKING);
		} catch (TrackerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks to make sure we're already connected to the tracker before attempting further tracker calls.
	 * @throws TrackerException
	 */
	void isConnected() throws TrackerException {
		if (!trk.connected()) {
			throw new TrackerException(NOT_CONNECTED);
		}
	}
	
	/**
	 * Connects to the tracker.  Also checks to see if the tracker is ready to be initialized and checks the
	 * measurement mode of the tracker.
	 * @return ready to be initialized Y/N, measurement mode
	 * @throws TrackerException thrown if already connected, or if the connect attempt failed
	 */
	String connect() throws TrackerException {
		String info;
		
		if (trk.connected()) {
			throw new TrackerException("Already connected to the tracker.\n");
		}
		
		trk.connect(ipAddress, name, password);
		if (trk.connected()) {
			Bookkeeper.getInstance().toggleTrkCon();
			info = "Connected to tracker on IP address: " + ipAddress+ "\n";
			if (trk.initialized(false)) {
				info += "Tracker initialized.\n";
			}
			else {
				if (trk.readyToInitialize(false)) {
					info += "Tracker ready to be initialized.\n";
				}
				else {
					info += "Tracker not ready to be initialized.\n";
				}
			}
			String s = trk.distanceMeasureMode().toString();
			
			if (s.contains("ADM") && s.contains("Only")) {
				info += "Distance Measure Mode is ADM only.\n";
				Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.ADM);
			}
			else if (s.contains("Interferometer")) {
				if (s.contains("ADM")) {
					info += "Distance Measure Mode is IFM set by ADM.\n";
					Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.IFMBYADM);
				}
				else {
					info += "Distance Measure Mode is IFM.\n";
					Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.IFM);
				}
			}
			return info;
		}
		else {
			throw new TrackerException("Connection to the tracker failed.\n");
		}
	}
	
	/**
	 * Attempts to disconnect from the tracker.
	 * @return "Tracker disconnected.\n" if the disconnect was successful
	 * @throws TrackerException thrown if not connected, or if the disconnect attempt failed
	 */
	String disconnect() throws TrackerException {
		isConnected();
		trk.disconnect();
		Bookkeeper.getInstance().toggleTrkCon();
		if (trk.connected()) {
			throw new TrackerException("Failed to disconnect from the tracker.\n");
		}
		return "Tracker disconnected.\n";
	}
	
	/**
	 * Used to send the abort command to the tracker.<P>
	 * This causes the tracker to stop what it is currently doing.
	 * @return "Tracker aborted." if the abort was successful
	 * @throws TrackerException thrown if not connected
	 */
	String abort() throws TrackerException {
		isConnected();
		trk.abort();
		return "Tracker aborted.\n";
	}
	
	/**
	 * Queries the tracker's weather station.
	 * @return string containing the relevant weather data
	 * @throws TrackerException thrown if not connected
	 */
	String weather() throws TrackerException {
		isConnected();
		WeatherInformation weather = trk.getWeatherInfo();
		String weatherReturn = "";
		weatherReturn = weatherReturn + "Pressure" + weather.getAirPressure() + "\n";
		weatherReturn = weatherReturn + "Temperature" + weather.getAirTemperature() + "\n";
		weatherReturn = weatherReturn + "Humidity" + weather.getHumidity() + "\n";
		return weatherReturn;
	}
	
	String weatherCsv() throws TrackerException {
		isConnected();
		WeatherInformation weather = trk.getWeatherInfo();
		return weather.getAirTemperature() + "," + weather.getAirPressure() + "," + weather.getHumidity();
	}
	
	/**
	 * Sends the tracker to home position.
	 * @return "Tracker moved to home position.\n"
	 * @throws TrackerException thrown if not connected
	 */
	String home() throws TrackerException {
		isConnected();
		trk.home(false);
		return "Tracker moved to home position.\n";
	}
	
	/**
	 * Points the tracker to specified spherical coordinates.
	 * @param radius
	 * @param theta
	 * @param phi
	 * @return "Tracker moved.\n"
	 * @throws TrackerException thrown if not connected
	 */
	String move(double radius, double theta, double phi, boolean relative) throws TrackerException {
		isConnected();
		trk.move(phi, theta, radius, relative);
		return "Tracker moved.\n";
	}
	
	String move(Coordinate coord) throws TrackerException {
		isConnected();
		trk.move(coord.getPhi(), coord.getTheta(), coord.getRadius(), false);
		return "Tracker moved.\n";
	}
	
	/**
	 * Causes the tracker to perform its startup checks.<P>
	 * These should be performed whenever the tracker is turned on.
	 * @return "HealthChecks started.\n"
	 * @throws TrackerException thrown if not connected
	 */
	String healthChecks() throws TrackerException {
		isConnected();
		trk.startApplicationFrame("HealthChecks", "");
		return "HealthChecks started.\n";
	}
	
	/**
	 * Initializes the tracker.
	 * @param minimum true for minimum initialization, false for full initialization
	 * @return a message that depends on the success of the initialization 
	 * @throws TrackerException thrown if not connected
	 */
	String initialize(boolean minimum) throws TrackerException {
		isConnected();
		trk.initialize();
		if (trk.initialized(false)) {
			return "Tracker initialized.\n";
		}
		return "Tracker failed to initialize.\n";
	}
	
	/**
	 * Searches for a reflectable within a circle of specified radius.
	 * @param radius of the search area, in meters
	 * @return message depending on whether a reflectable was found or not
	 * @throws TrackerException thrown if not connected, or if no reflectable was found
	 */
	String search(double radius) throws TrackerException {
		isConnected();
		trk.search(radius);
		if (trk.targetPresent()) {
			return "Target found.\n";
		}
		else {
			throw new TrackerException("Target not found.\n");
		}
	}
	
	/**
	 * Set the tracker's measurement mode.
	 * @param mode must be one of: "ADM", "IFM", "IFM set by ADM"
	 * @return confirmation that the mode was changed
	 * @throws TrackerException thrown if not connected
	 */
	String setMeasureMode(TrackerMeasureMode mode) throws TrackerException {
		if (mode == null) throw new NullPointerException("New measurement mode is null.");
		isConnected();
		
		switch (mode) {
			case IFM:
				trk.changeDistanceMeasureMode(new InterferometerOnly());
				Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.IFM);
				break;
			case ADM:
				trk.changeDistanceMeasureMode(new ADMOnly());
				Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.ADM);
				break;
			case IFMBYADM:
				trk.changeDistanceMeasureMode(new InterferometerSetByADM());
				Bookkeeper.getInstance().setTrkMeasmode(TrackerMeasureMode.IFMBYADM);
				break;
			default:
				assert false; //There are only three measurement modes.
		}
		
		return "Measure mode set to: " + mode + ".\n";
	}
	
	/**
	 * Runs the tracker's startup checks.
	 * @return "StartupChecks started.\n"
	 * @throws TrackerException thrown if not connected
	 */
	String startupChecks() throws TrackerException {
		isConnected();
		trk.startApplicationFrame("StartupChecks", "");
		return "StartupChecks started.\n";
	}
	
	/**
	 * Changes the target type to SMR with diameter of 0.022225 meters.
	 * @return "Target type changed.\n"
	 * @throws TrackerException thrown if not connected
	 */
	String targetType() throws TrackerException {
		isConnected();
		trk.changeTargetType(new SMRTargetType(0.022225));
		return "Target type changed.\n";
	}
	
	/**
	 * Returns true if the tracker is pointing at a reflectable, false if not.
	 */
	boolean reflPresent() throws TrackerException {
		isConnected();
		return trk.targetPresent();
	}
	
	//used when TrackerGuard needs to perform a one time measurement for some purpose
	MeasurePointData[] measure(int numPoints) throws TrackerException {	
		return asdf(1, 1024, numPoints);
	}
	
	//for more complex measuring
	MeasurePointData[] measure(MeasurementConfig mc) throws TrackerException {
		return asdf(mc.getObservationRate(), mc.getSamplesPerObservation(), mc.getNumObservations());
	}
	
	private MeasurePointData[] asdf(double observationRate, int samplesPerObservation, int numObservations) throws TrackerException {
		filter = new AverageFilter();
		startTrigger = new NullStartTrigger();
		conTrigger = new IntervalTrigger(observationRate);
		cfg = new MeasureCfg(samplesPerObservation, filter, startTrigger, conTrigger);
		
		trk.startMeasurePoint(cfg);
		MeasurePointData[] data = trk.readMeasurePointData(numObservations);
		trk.stopMeasurePoint();
		
		return data;
	}
	
}