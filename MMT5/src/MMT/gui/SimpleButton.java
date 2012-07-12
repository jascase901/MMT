/* The SimpleButton class is a super-basic subclass of the JButton class.
 * It just provides a more convenient constructor, which sets the
 *  action command and an event listener instead of requiring client code
 *  to make separate calls for that.
 */

package MMT.gui;

import javax.swing.JButton;
import java.awt.event.ActionListener;

public class SimpleButton extends JButton {
    public SimpleButton(String title, String command, ActionListener listener) {
        super(title);
        this.setActionCommand(command);
        this.addActionListener(listener);
    }
}
