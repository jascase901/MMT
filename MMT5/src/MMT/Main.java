/* This class contains the main function that runs
 * the whole MMT shebang.
 */

package MMT;

import MMT.gui.boxes.Row;
import MMT.gui.TrackerPanel;
import MMT.gui.ActuatorPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Component;

public class Main {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("ButtonDemo");
        
        Row contentPane = new Row(new TrackerPanel(), new ActuatorPanel());
        contentPane.setOpaque(true);
        
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {createAndShowGUI();}
        });
    }
}
