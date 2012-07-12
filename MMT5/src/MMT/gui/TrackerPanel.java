/* A MMT.gui.TrackerPanel is the part of the GUI
 * that deals with tracker-related things. It allows
 * the user to send various commands to the tracker
 * and retrieve observations from it.
 */

package MMT.gui;

import MMT.tracker.Tracker;
import MMT.gui.HistoryPane;
import MMT.gui.SimpleButton;
import MMT.gui.boxes.Column;
import MMT.gui.boxes.Row;

import smx.tracker.TrackerException;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/* A JPanel is just an invisible box in Swing that you can slap
 * other things onto. It helps organize stuff. We want all our
 * tracker-related stuff together, so we put it all in one panel.
 * It's also an ActionListener because we want to get notified
 * whenever a GUI-related event happens (e.g. a button is clicked). */
public class TrackerPanel extends JPanel implements ActionListener {
    // We have a HistoryPane to log various messages for the user:
    private final HistoryPane history;
    // and the internal Tracker object the user's controlling:
    private final Tracker tracker;
    
    // The buttons for sending commands to the tracker:
    private JButton connectButton;
    private JButton initializeButton;
    private JButton startupChecksButton;
    private JButton healthChecksButton;
    private JButton disconnectButton;
    private JButton abortButton;
    private JButton homeButton;
    private JButton moveButton;
    private JButton moveAbsoluteButton;

    // The box for selecting which measurement mode to set the tracker to:
    private JComboBox modeBox;
    private JButton setModeButton;
    
    // Fields for the user to enter coordinates:
    private JTextField radiusField;
    private JTextField thetaField;
    private JTextField phiField;
    
    public TrackerPanel() {
        super();
        this.tracker = new Tracker();
        
        // Build the GUI:
        
        this.radiusField = new JTextField(5);
        this.thetaField = new JTextField(5);
        this.phiField = new JTextField(5);
        
        this.connectButton = new SimpleButton("Connect", "connect", this);
        this.initializeButton = new SimpleButton("Initialize", "initialize", this);
        this.startupChecksButton = new SimpleButton("Startup Checks", "startupChecks", this);
        this.healthChecksButton = new SimpleButton("Health Checks", "healthChecks", this);
        this.disconnectButton = new SimpleButton("Disconnect", "disconnect", this);
        this.abortButton = new SimpleButton("Abort", "abort", this);
        this.homeButton = new SimpleButton("Home", "home", this);
        this.moveButton = new SimpleButton("Move", "move", this);
        this.moveAbsoluteButton = new SimpleButton("Move absolute", "moveAbsolute", this);
        this.setModeButton = new SimpleButton("Set Mode", "setMode", this);
        
        this.modeBox = new JComboBox(new String[] {"IFM", "ADM", "IFM set by ADM"});
        
        this.history = new HistoryPane();
        
        /* (I've formatted the calls to Row and Column so that the layout of code
         *  here is roughly the same as the layout of components in the GUI.) */
        this.add(new Column(
                            this.history,
                            new Row(this.connectButton, this.startupChecksButton, this.healthChecksButton),
                            new Row(this.initializeButton, this.homeButton, this.abortButton),
                            new Row(new Column(new Row(new JLabel("Radius (m): "), this.radiusField),
                                               new Row(new JLabel("Theta (rad): "), this.thetaField),
                                               new Row(new JLabel("Phi (rad): "), this.phiField)), new Column(this.moveButton,
                                                                                                              this.moveAbsoluteButton)),
                            new Row(this.modeBox, this.setModeButton)));
    }
    
    public void actionPerformed(ActionEvent e) {
        final String s = e.getActionCommand();
        
        if (s.equals("connect")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.connect();
                        return "Connected to tracker.";
                    } catch (TrackerException e) {
                        return "TrackerException when connecting to tracker.";
                    }
                }
            }).execute();
        }
        else if (s.equals("initialize")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.initialize();
                        return "Initialized tracker.";
                    } catch (TrackerException error) {
                        return "TrackerException when initializing tracker.";
                    }
                }
            }).execute();
        }
        else if (s.equals("healthChecks")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.healthChecks();
                        return "Started tracker health checks.";
                    } catch (TrackerException error) {
                        return "TrackerException when starting tracker health checks.";
                    }
                }
            }).execute();
        }
        else if (s.equals("startupChecks")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.startupChecks();
                        return "Started tracker startup checks.";
                    } catch (TrackerException error) {
                        return "TrackerException when starting tracker startup checks.";
                    }
                }
            }).execute();
        }
        else if (s.equals("abort")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.initialize();
                        return "Aborted tracker action.";
                    } catch (TrackerException error) {
                        return "TrackerException when aborting tracker action.";
                    }
                }
            }).execute();
        }
        else if (s.equals("home")) {
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.initialize();
                        return "Moved tracker home.";
                    } catch (TrackerException error) {
                        return "TrackerException when moving tracker home.";
                    }
                }
            }).execute();
        }
        else if (s.startsWith("move")) {
            final double radius = this.getRadius();
            final double theta = this.getTheta();
            final double phi = this.getPhi();
            if (Double.isNaN(radius) || Double.isNaN(theta) || Double.isNaN(phi)) return;
            
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        if (s.equals("move")) {
                            tracker.move(radius, theta, phi);
                            return "Moved tracker by r="+radius+", th="+theta+", ph="+phi;
                        } else {
                            tracker.moveAbsolute(radius, theta, phi);
                            return "Moved tracker by r="+radius+", th="+theta+", ph="+phi
                                        +" (absolute)";
                        }
                    } catch (TrackerException error) {
                        return "TrackerException when moving tracker.";
                    }
                }
            }).execute();
        }
        else if (s.equals("search")) {
            final double radius = this.getRadius();
            if (Double.isNaN(radius)) return;
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.search(radius);
                        return "Set tracker searching up to radius="+radius+".";
                    } catch (TrackerException error) {
                        return "TrackerException when having tracker search.";
                    }
                }
            }).execute();
        }
        else if (s.equals("setMode")) {
            final Tracker.MeasureMode mode;
            String modeStr = (String) this.modeBox.getSelectedItem();
            
            if (modeStr.equals("IFM")) mode = Tracker.MeasureMode.IFM;
            else if (modeStr.equals("ADM")) mode = Tracker.MeasureMode.ADM;
            else mode = Tracker.MeasureMode.IFM_SET_BY_ADM;
            
            (this.history.new Updater() {
                @Override
                public String getResult() {
                    try {
                        tracker.setMeasureMode(mode);
                        return "Set measurement mode to "+mode;}
                    catch (TrackerException error) {
                        return "TrackerException raised when setting mode to "+mode;
                    }
                }
            }).execute();
        }
    }
    
    public double getRadius() {
        try {
            return Double.parseDouble(this.radiusField.getText());
        } catch (java.lang.NumberFormatException e) {
            this.history.add("Could not parse radius field.");
            return Double.NaN;
        }
    }
    public double getTheta() {
        try {
            return Double.parseDouble(this.thetaField.getText());
        } catch (java.lang.NumberFormatException e) {
            this.history.add("Could not parse theta field.");
            return Double.NaN;
        }
    }
    public double getPhi() {
        try {
            return Double.parseDouble(this.phiField.getText());
        } catch (java.lang.NumberFormatException e) {
            this.history.add("Could not parse phi field.");
            return Double.NaN;
        }
    }
}
