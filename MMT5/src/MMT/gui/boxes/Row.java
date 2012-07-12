/* A MMT.gui.boxes.Row is just a panel that contains
 * a horizontal row of components. The Row and Column
 * classes are handy for making somewhat-grid-like
 * layouts with rather more flexibility than a
 * GridLayout layout manager provides (and without
 * GridBagLayout's nightmarish complexity).
 *
 * The basic constructor for Row takes an array of
 * Components, but to keep client code from being
 * cluttered up with "new Component[] {...}"s,
 * additional constructors for up to ten-component
 * rows are provided.
 */

package MMT.gui.boxes;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.Component;

public class Row extends JPanel {
    public Row() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    }
    
    public Row(Component[] items) {
        this();
        for (Component c : items) this.add(c);
    }
    
    public Row(Component c) {
        this(new Component[] {c});
    }
    public Row(Component c1, Component c2) {
        this(new Component[] {c1, c2});
    }
    public Row(Component c1, Component c2, Component c3) {
        this(new Component[] {c1, c2, c3});
    }
    public Row(Component c1, Component c2, Component c3, Component c4) {
        this(new Component[] {c1, c2, c3, c4});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5) {
        this(new Component[] {c1, c2, c3, c4, c5});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6) {
        this(new Component[] {c1, c2, c3, c4, c5, c6});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8, Component c9) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8, c9});
    }
    public Row(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8, Component c9, Component c10) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8, c9, c10});
    }
}
