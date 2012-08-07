package MMT.gui;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;

public class JListPane<E> extends JPanel {
    public Map<String, E> nameToItem;
    public DefaultListModel<String> nameListModel;
    public JList<String> nameJList;
    public JListPane() {
	super();
	this.nameToItem = new HashMap<String, E>();
	this.nameListModel = new DefaultListModel<String>();
	this.nameJList = new JList<String>(nameListModel);
	this.add(new JScrollPane(this.nameJList));
    }
    public JListPane(String[] names, E[] items) {
	this();
	if (names.length != items.length)
	    throw new RuntimeException("length of names and items unequal");
	for (int i=0; i<names.length; i++)
	    this.addItem(names[i], items[i]);
    }
    public void addItem(String name, E item) {
	if (!this.nameListModel.contains(name))
	    this.nameListModel.addElement(name);
	this.nameToItem.put(name, item);
    }
    public void removeSelected() {
	for (String s : this.nameJList.getSelectedValuesList()) {
	    this.nameListModel.removeElement(s);
	    this.nameToItem.remove(s);
	}
    }

    public E getSelectedValue() {
	return this.nameToItem.get(this.nameJList.getSelectedValue());
    }
    public List<E> getSelectedValuesList() {
	List<E> result = new ArrayList<E>();
	for (String s : this.nameJList.getSelectedValuesList())
	    result.add(this.nameToItem.get(s));
	return result;
    }
}