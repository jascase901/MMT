/* A MMT.tracker.Tracker instance represents a laser tracker
 * (there is probably only one, but whatever). It allows client
 * code to send various commands to the tracker and receive
 * measurements from it.
 
 * This Tracker class is largely just a wrapper around
 *  smx.tracker.Tracker (the interface to the FARO laser
 *  tracker provided by the folks who sell it). It simplifies
 *  the interface somewhat and makes sure everything is thread-
 *  safe: while one thread is calling one of a Tracker
 *  instance's methods, other threads' attempts to access that
 *  Tracker's methods will block.
 */

package MMT.tracker;

import MMT.tracker.UnconnectedException;

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
import smx.tracker.StartTrigger;
import smx.tracker.TrackerException;

import java.util.concurrent.locks.ReentrantLock;

public class Tracker {
    
    // PUBLIC STUFF

    // A Tracker can be measuring distance in any one of three modes.
    // The MeasureMode enum enumerates the possibilities.
    public static enum MeasureMode {IFM, ADM, IFM_SET_BY_ADM};

    public Tracker(String ipAddress, String userName, String password) {
        // We communicate with the tracker over ethernet, and have to provide
        // authentication (name/password) to play with it:
        this.ipAddress = ipAddress;
        this.name = userName;
        this.password = password;
        
        // The lock we use to ensure thread-safety:
        this.lock = new ReentrantLock();
        
        try {
            this.tracker = new smx.tracker.Tracker(TRACKER_TYPE);
            // We want Tracker-related calls to block, because let's be honest,
            //  we hate threading and want to do it as little as possible.
            this.tracker.setBlocking(BLOCKING);
        } catch (TrackerException e) {
            e.printStackTrace();
        }
    }
    public Tracker() {
        this(DEFAULT_IP, DEFAULT_USER, DEFAULT_PASSWORD);
    }
    
    /* measure(...) takes some measurements from the tracker. At the time
     *  of writing, I don't really know how it works, so sorry for the
     *  crummy documentation on it. */
    public MeasurePointData[] measure(double observationRate, int samplesPerObservation, int numObservations) throws TrackerException {
        MeasureCfg cfg = new MeasureCfg(samplesPerObservation, new AverageFilter(),
                                        new NullStartTrigger(), new IntervalTrigger(observationRate));
        MeasurePointData[] data;
        this.lock.lock();
        try {
            this.tracker.startMeasurePoint(cfg);
            data = this.tracker.readMeasurePointData(numObservations);
            this.tracker.stopMeasurePoint();
        } finally {
            this.lock.unlock();
        }
        
        return data;
    }
    public MeasurePointData[] measure(int numPoints) throws TrackerException {
        return measure(1, 1024, numPoints);
    }
    
    /**
     * Connects to the tracker.
     * @return whether the connection was successful
     * @throws TrackerException thrown if something went horribly wrong with the connect attempt
     */
    public boolean connect() throws TrackerException {
        this.lock.lock();
        try {
            if (this.tracker.connected()) return true;
            this.tracker.connect(ipAddress, name, password);
            return this.tracker.connected();
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Initializes the tracker.
     * @throws TrackerException thrown if not connected
     */
    public boolean initialize() throws TrackerException {
        this.lock.lock();
        try {
            this.checkConnection();
            if (this.tracker.initialized(false)) return true;
            else if (this.tracker.readyToInitialize(false)) {
                this.tracker.initialize();
                return this.tracker.initialized(false);
            } else throw new TrackerException("Tracker is not ready to be initialized.");
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Runs the tracker's startup checks.
     * @throws TrackerException thrown if not connected
     */
    public void startupChecks() throws TrackerException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.startApplicationFrame("StartupChecks", "");
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Causes the tracker to perform its startup checks.<P>
     * These should be performed whenever the tracker is turned on.
     * @throws TrackerException thrown if not connected
     */
    public void healthChecks() throws TrackerException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.startApplicationFrame("HealthChecks", "");
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Attempts to disconnect from the tracker.
     * @return whether the disconnect was successful
     * @throws TrackerException thrown if something went horribly wrong with the disconnect attempt
     */
    public boolean disconnect() throws TrackerException {
        this.lock.lock();
        
        try {
            if (!this.tracker.connected()) return true;
            this.tracker.disconnect();
            return (!this.tracker.connected());
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Used to send the abort command to the tracker.
     * This causes the tracker to stop what it is currently doing.
     * @throws TrackerException thrown if not connected
     */
    public void abort() throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.abort();
        } finally {
            this.lock.unlock();
        }
    }
    
    
    /**
     * Sends the tracker to home position.
     * @throws TrackerException thrown if not connected
     */
    public void home() throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.home(false);
        } finally {
            this.lock.unlock();
        }
    }
    
    /**
     * Points the tracker to specified spherical coordinates.
     * @param radius
     * @param theta
     * @param phi
     * @throws TrackerException thrown if not connected
     */
    public void move(double radius, double theta, double phi) throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.move(phi, theta, radius, true);
        } finally {
            this.lock.unlock();
        }
    }
    public void moveAbsolute(double radius, double theta, double phi) throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.move(phi, theta, radius, false);
        } finally {
            this.lock.unlock();
        }
    }
    
    /**
     * Searches for a reflectable within a circle of specified radius.
     * @param radius of the search area, in meters
     * @return whether a target was found
     * @throws TrackerException thrown if not connected
     */
    public boolean search(double radius) throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            this.tracker.search(radius);
            return this.tracker.targetPresent();
        } finally {
            this.lock.unlock();
        }
    }
    /**
     * Returns whether the tracker is pointing at a target.
     */
    public boolean targetPresent() throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            return this.tracker.targetPresent();
        } finally {
            this.lock.unlock();
        }
    }
    
    public void setMeasureMode(MeasureMode mode) throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            switch (mode) {
                case IFM:
                    this.tracker.changeDistanceMeasureMode(new InterferometerOnly());
                    break;
                case ADM:
                    this.tracker.changeDistanceMeasureMode(new ADMOnly());
                    break;
                case IFM_SET_BY_ADM:
                    this.tracker.changeDistanceMeasureMode(new InterferometerSetByADM());
                    break;
            }
        } finally {
            this.lock.unlock();
        }
    }
    public MeasureMode getMeasureMode() throws TrackerException, UnconnectedException {
        this.lock.lock();
        try {
            this.checkConnection();
            String response = this.tracker.distanceMeasureMode().toString();
            if (response.contains("ADM") && response.contains("Interferometer"))
                return MeasureMode.IFM_SET_BY_ADM;
            else if (response.contains("ADM"))
                return MeasureMode.ADM;
            else return MeasureMode.IFM;
        } finally {
            this.lock.unlock();
        }
    }
    
    
    
    // PRIVATE STUFF
    
    private static final String TRACKER_TYPE = "TrackerKeystone";
    private static final boolean BLOCKING = true;
    
    // Default information for connecting to the tracker -- it's not
    //  likely to change anytime soon.
    private static final String DEFAULT_IP = "192.168.1.4";
    private static final String DEFAULT_USER = "user";
    private static final String DEFAULT_PASSWORD = "";
    
    private smx.tracker.Tracker tracker;
    private final String ipAddress, name, password;
    private ReentrantLock lock;
    
    // This function throws an UnconnectedException if we're not connected
    //  to the tracker.
    private void checkConnection() throws TrackerException {
        this.lock.lock();
        boolean connected = this.tracker.connected();
        this.lock.unlock();
        if (!connected) {
            throw new UnconnectedException();
        }
    }
}
