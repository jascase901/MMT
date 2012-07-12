/* A MMT.gui.HistoryPane is a scrollable column of text.
 * It functions as a logging tool -- client code can call
 * its 'add' method, and a new of text will be added to it.
 *
 * I don't entirely understand how to work with JScrollPanes,
 * so the innards of HistoryPane were just kinda hacked
 * together randomly until they worked. Sorry!
 */

package MMT.gui;

import MMT.gui.boxes.Column;

import javax.swing.SwingWorker;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;

public class HistoryPane extends javax.swing.JScrollPane {
    private Column column;
    
    public HistoryPane(int width, int height) {
        super();
        this.column = new Column();
        this.column.setSize(new Dimension(width, height));
        this.getViewport().add(column);
        this.getViewport().setPreferredSize(new Dimension(width, height));
        this.revalidate();
    }
    public HistoryPane() {
        this(300, 200);
    }
    
    public void add(String s) {
        // Add a new row to the column and redraw the box:
        this.column.add(new JLabel(s));
        this.column.revalidate();
        this.column.repaint();

        // And let's scroll down to the new bottom so that
        //  the user doesn't have to do it manually:
        this.getViewport().setViewPosition(new Point(0, this.column.getHeight()));
    }
    
    /* The Updater class is a specialized kind of SwingWorker. Updater
     * instances just perform some (potentially time-consuming) task in the
     * background, decide on a result string, and add that string to the
     * HistoryPane.
     * The simple way to do this would just be to create a plain-vanilla
     * Thread object that runs the task and updates the panel, but that
     * does not work -- Swing is not thread-safe, and the pane acts
     * wonky if you change it from any thread other than the "event
     * dispatch" thread. So we do the time-consuming part in the
     * 'doInBackground' method (which Swing executes in the background),
     * and the pane modification is done by the 'done' method (which is
     * performed by the event dispatch thread once the background part
     * finishes). */
    public abstract class Updater extends SwingWorker<Void,Void> {
        protected String result;
        public abstract String getResult();
        
        @Override
        public Void doInBackground() {
            this.result = this.getResult();
            return null;
        }
        @Override
        public void done() {
            add(this.result);
        }
    }
}
