package edpas.componentes.vista.componentes;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class JListSingle extends JPanel {

    private JList lista;
    private DefaultListModel listModel;

    
    public JListSingle() {
	super();
	this.setOpaque(false);
	this.setLayout(new BorderLayout());
	this.initGUI();
    }
    
    public JListSingle(List items) {
	this();
	this.setItems(items);
    }
    
    private void initGUI() {
	this.listModel = new DefaultListModel();
	this.lista = new JList(listModel);
	this.lista.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	JScrollPane scrollPane = new JScrollPane(this.lista);
	this.add(scrollPane, BorderLayout.CENTER);	
    }
    
    public void addItem(Object item) {	
	if (item instanceof Comparable) {
	    int i=0;
	    for (; i<this.listModel.size(); i++) {
		Object objeto = this.listModel.elementAt(i);
		if (((Comparable)objeto).compareTo(item) > 0) {
		    break;
		}
	    }
	    this.listModel.add(i, item);
	}
	else {
	    this.listModel.addElement(item);
	}
    }
    
    public void removeItem(Object item) {
	this.listModel.removeElement(item);
    }
    
    public void setItems(List items) {
	this.listModel.clear();
	for (Object item: items) {
	    this.addItem(item);
	}
    }
    
    public List getItems() {
	List items = new ArrayList();
	for (int i=0; i<this.listModel.size(); i++) {
	    items.add(this.listModel.elementAt(i));
	}
	return items;
    }

    public Object getSelectedItem() {
	return this.lista.getSelectedValue();
    }
    
    public List getSelectedItems() {
	return this.lista.getSelectedValuesList();
    }
    
    public void setCellRenderer(ListCellRenderer listCellRenderer) {
	this.lista.setCellRenderer(listCellRenderer);
    }        
}
