package edu.ucsb.deepspace.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Display;

import edu.ucsb.deepspace.gui.MMT4MainWindow;

public class Bookkeeper {
	
	private Map<String, Reflectable> reflectables;
	private Map<String, Actuator> actuators = new HashMap<String, Actuator>();
	private Map<String, Target> targets = new HashMap<String, Target>();
	private boolean servoOnFlag = false;
	private boolean trkCon = false; // connection status of tracker
	private boolean trkIni = false;
	private boolean trkChecks = false;
	private TrackerMeasureMode trkMeasmode = TrackerMeasureMode.ADM;
	
	private Bookkeeper() {}
	private static final Bookkeeper INSTANCE = new Bookkeeper();
	public static Bookkeeper getInstance() {return INSTANCE;}
	
	/**
	 * Used to get the map of reflectables.
	 * @return the map of reflectables
	 */
	public Map<String, Reflectable> getReflectables() {
		return this.reflectables;
	}
	
	public Map<String, Actuator> getActuators() {return actuators;}
	public Map<String, Target> getTargets() {return targets;}
	
	/**
	 * Update bookkeeper's Map<String, Reflectable> with the new mapping
	 * @param reflectables
	 */
	public void setReflectables(Map<String, Reflectable> reflectables) {
		this.reflectables = reflectables;
		final List<String> targetList = new ArrayList<String>();
		final List<String> actuatorList = new ArrayList<String>();
		for (String s : new TreeSet<String>(reflectables.keySet())) {
			if (reflectables.get(s).getType().equals("Actuator")) {
				actuatorList.add(s);
				actuators.clear();
				actuators.put(s, (Actuator) reflectables.get(s));
			}
			else if (reflectables.get(s).getType().equals("Target")) {
				targetList.add(s);
				targets.clear();
				targets.put(s, (Target) reflectables.get(s));
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MMT4MainWindow.updateActuatorList(actuatorList);
				MMT4MainWindow.updateTargetList(targetList);
			}
		});
		
	}
	
	/**
	 * @return the value of <code>servoOnFlag</code>.
	 */
	public boolean getServoOnFlag() {return this.servoOnFlag;}
	
	/**
	 * Toggles the value of <code>servoOnFlag</code>.
	 */
	public void toggleServoOnFlag() {this.servoOnFlag = !this.servoOnFlag;}
	
	public boolean getTrkCon() {return trkCon;}
	public void toggleTrkCon() {trkCon = !trkCon;}
	
	public boolean getTrkIni() {return trkIni;}
	public void toggleTrkIni() {trkIni = !trkIni;}
	
	public boolean getTrkChecks() {return trkChecks;}
	public void toggleTrkChecks() {trkChecks = !trkChecks;}
	
	/**
	 * Delete a reflectable from the mapping.
	 * @param name of the reflectable to be deleted
	 */
	public void deleteRefl(String name) {
		reflectables.remove(name);
		setReflectables(reflectables);
	}

	public TrackerMeasureMode getTrkMeasmode() {
		return trkMeasmode;
	}

	public void setTrkMeasmode(TrackerMeasureMode trkMeasmode) {
		this.trkMeasmode = trkMeasmode;
	}
	
}