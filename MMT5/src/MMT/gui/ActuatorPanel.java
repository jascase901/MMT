/* The MMT.gui.ActuatorPanel class is the part of the GUI
 * that deals with actuator-related things. It allows the
 * user to send commands to actuators and it has a
 * MMT.gui.HistoryPane in it that logs actuator-related
 * events.
 */

package MMT.gui;

import MMT.actuators.Actuator;
import MMT.gui.HistoryPane;
import MMT.gui.SimpleButton;
import MMT.gui.boxes.Column;
import MMT.gui.boxes.Row;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

/* A JPanel is just an invisible box in Swing that you can slap
 * other things onto. It helps organize stuff. We want all our
 * actuator-related stuff together, so we put it all in one panel.
 * It's also an ActionListener because we want to get notified
 * whenever a GUI-related event happens (e.g. a button is clicked). */
public class ActuatorPanel extends JPanel implements ActionListener {
    /* We maintain a list of all known actuators, so that the user can
     * send commands to them. We keep the list of actuators as a List
     * because Lists are nice to deal with, but we also need to store
     * it in a JList for GUI-related purposes. */
    public ArrayList<Actuator> actuatorList;
    private JList<Actuator> actuatorJList;
    
    // And we have a HistoryPane for logging all our messages:
    private final HistoryPane history;

    // This text field is where the user enters a number of microns
    //  he wants to move an actuator:
    private JTextField micronField;
    
    // And buttons for sending commands to the actuators:
    private JButton moveButton;
    private JButton moveAbsoluteButton;
    private JButton getStatusButton;
    
    public ActuatorPanel() {
        super();
        
        this.micronField = new JTextField(5);
        
        // Build our list of all actuators as well as the JList to
        //  display it to the user:
        this.actuatorList = this.findAllActuators();
        this.actuatorJList = new JList<Actuator>(this.actuatorList.toArray(new Actuator[] {}));
        
        // A SimpleButton is just a subclass of JButton with a nicer constructor:
        //  give it a name, a command message, and a listener for when it's clicked.
        this.moveButton = new SimpleButton("Move", "move", this);
        this.moveAbsoluteButton = new SimpleButton("Move (absolute)", "moveAbsolute", this);
        this.getStatusButton = new SimpleButton("Get status", "getStatus", this);
        
        this.history = new HistoryPane();
        
        /* Now we place the components in the frame.
         * (I've formatted the calls to Row and Column so that the layout of code
         *  here is roughly the same as the layout of components in the GUI.) */
        this.add(new Column(
            new Row(new JScrollPane(this.actuatorJList), this.history),
            new Row(this.micronField, new JLabel(" microns"), new Column(this.moveButton,
                                                                         this.moveAbsoluteButton)),
            this.getStatusButton));
    }
    
    public void actionPerformed(ActionEvent e) {
        final String s = e.getActionCommand();
        
        if (s.startsWith("move")) {
            /* s is either "move" or "moveAbsolute". Either way, we want to
             *  parse the 'microns' entry field and tell all the selected
             *  actuators to move or moveAbsolute by that amount. */
            final double distance = this.getMicrons();
            if (Double.isNaN(distance)) return; // Couldn't parse the field.
            
            for (final Actuator a : this.actuatorJList.getSelectedValuesList()) {
                /* A HistoryPanel.Updater will run its getResult method, then
                 * update the HistoryPanel with the string getResult returns. */
                (this.history.new Updater() {
                    @Override
                    public String getResult() {
                        try {
                            if (s.equals("move")) {
				String tmp = a.move(distance);
				System.out.println(tmp);
				return tmp;
				//a.move(distance);
				//return "Moved "+a.toString()+" "+distance+" microns.";
                            } else {
				return a.moveAbsolute(distance);
                                //a.moveAbsolute(distance);
                                //return "Moved "+a.toString()+" "+distance+" microns (absolute).";
                            }
                        } catch (java.io.IOException error) {
			    error.printStackTrace();
                            return "IOException thrown when moving "+a.toString();
                        } catch (java.lang.InterruptedException error) {
			    return "Thread interrupted during write/read to "+a.toString();
			}
                    }
                }).execute();
            }
        }
        else if (s.equals("getStatus")) {
            // We just want to update the HistoryPane with the status
            // of each selected actuator.
            for (final Actuator a : this.actuatorJList.getSelectedValuesList()) {
                (this.history.new Updater() {
                    @Override
                    public String getResult() {
                        try {
                            return a.toString() + " gives status "
                                     + a.getStatus().toString();
                        } catch (java.io.IOException error) {
			    error.printStackTrace();
                            return "IOException thrown when getting status of "
                                     +a.toString();
                        } catch (java.lang.InterruptedException error) {
                            return "InterruptedException thrown when getting status of "
                                     +a.toString();
                        }
                    }
                }).execute();
            }
        }
        else this.history.add("Unknown command received: "+s);
    }
    
    // getMicrons will try to parse the 'micron' text field and return it.
    // If it fails, it logs the failure on the HistoryPane and returns NaN.
    private double getMicrons() {
        try {
            return Double.parseDouble(this.micronField.getText());
        } catch (java.lang.NumberFormatException e) {
            this.history.add("Could not parse micron field.");
            return Double.NaN;
        }
    }
    
    // findAllActuators returns a list of all the actuators we know about.
    // We need to maintain such a list so that the user can send commands
    //  to any actuator he wants.
    private ArrayList<Actuator> findAllActuators() {
        ArrayList<Actuator> result = new ArrayList<Actuator>();
        for (int i=192; i<=204; i+=2)
            result.add(new Actuator(i));
        return result;
    }
}
