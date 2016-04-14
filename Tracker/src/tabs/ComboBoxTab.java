package tabs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Driver;
import core.Tab;

public abstract class ComboBoxTab<T> extends Tab implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	JComboBox<T> comboBox;
	DefaultComboBoxModel<T> comboBoxModel;
	JLabel label;
	
	public ComboBoxTab() {
		
		setLayout(new BorderLayout());
		
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(2, 1));
		top.setBorder(BorderFactory.createEmptyBorder(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS));
		
		comboBoxModel = new DefaultComboBoxModel<>();
		comboBox = new JComboBox<>(comboBoxModel);
		comboBox.addActionListener(this);
		comboBox.setEditable(isEditable());
		
		label = new JLabel(getDefaultLabelText());
		
		top.add(comboBox);
		top.add(label);
		
		JPanel bottom = getPanel();
		
		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.CENTER);
		
	}
	
	protected final JLabel getLabel() {
		
		return label;
		
	}
	
	protected JComboBox<T> getComboBox() {
		
		return comboBox;
		
	}
	
	protected void setComboBoxModel(ComboBoxModel<T> model) {
		
		int selectedBefore = comboBox.getSelectedIndex();
		comboBox.setModel(model);
		comboBox.setSelectedIndex(selectedBefore);
		comboBox.setEditable(isEditable());
		
	}
	
	protected abstract String getDefaultLabelText();
	
	protected abstract boolean isEditable();
	
	protected abstract JPanel getPanel();
	
}
