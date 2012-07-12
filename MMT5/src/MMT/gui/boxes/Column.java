/* A MMT.gui.boxes.Column is just a panel that contains
 * a verticle row of components. The Row and Column
 * classes are handy for making somewhat-grid-like
 * layouts with rather more flexibility than a
 * GridLayout layout manager provides (and without
 * GridBagLayout's nightmarish complexity).
 *
 * The basic constructor for Column takes an array of
 * Components, but to keep client code from being
 * cluttered up with "new Component[] {...}"s,
 * additional constructors for up to ten-component
 * rows are provided.
 */

package MMT.gui.boxes;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Component;

public class Column extends JPanel {
    public Column() {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }
    
    public Column(Component[] items) {
        this();
        for (Component c : items) this.add(c);
    }
    
    public Column(Component c) {
        this(new Component[] {c});
    }
    public Column(Component c1, Component c2) {
        this(new Component[] {c1, c2});
    }
    public Column(Component c1, Component c2, Component c3) {
        this(new Component[] {c1, c2, c3});
    }
    public Column(Component c1, Component c2, Component c3, Component c4) {
        this(new Component[] {c1, c2, c3, c4});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5) {
        this(new Component[] {c1, c2, c3, c4, c5});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6) {
        this(new Component[] {c1, c2, c3, c4, c5, c6});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8, Component c9) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8, c9});
    }
    public Column(Component c1, Component c2, Component c3, Component c4, Component c5,
               Component c6, Component c7, Component c8, Component c9, Component c10) {
        this(new Component[] {c1, c2, c3, c4, c5, c6, c7, c8, c9, c10});
    }
}
