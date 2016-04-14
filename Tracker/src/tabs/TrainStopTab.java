package tabs;

import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import core.Driver;
import core.TrainStop;

public class TrainStopTab extends ComboBoxTab<TrainStop> {
	
	private static final long serialVersionUID = 0L;
	
	private static final String TAB_TITLE = "Train Stops";
	private static final String LABEL_PREFIX = "TrainStop > ";
	
	private TrainStop active;
	
	public TrainStopTab() {
		
		setTitle(TAB_TITLE);
		setComboBoxModel(new DefaultComboBoxModel<>(getScene().trainStops.stream().toArray(size -> new TrainStop[size])));
		
	}
	
	@Override
	public void onSwitchedTo() {}
	
	@Override
	public void onSwitchedAway() {}
	
	@Override
	public void onToolActivate() {}
	
	@Override
	public void onToolFinalise() {}
	
	@Override
	public void onToolAbort() {}
	
	@Override
	public void onUndo() {}
	
	@Override
	public void onRedo() {}
	
	@Override
	protected String getDefaultLabelText() {
		
		return LABEL_PREFIX;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("comboBoxEdited")) {
			
			active.setName(getComboBox().getSelectedItem().toString());
			Driver.viewPanel.grabFocus();
			
		} else { // comboBoxChanged
			
			if (getComboBox().getSelectedIndex() != -1) {
				
				getComboBox().setEditable(true);
				active = (TrainStop) getComboBox().getSelectedItem();
				updatePanel();
				
				Driver.frame.repaint();
				
			}
			
		}
		
	}
	
	@Override
	protected boolean isEditable() {
		
		return false;
		
	}
	
	@Override
	protected JPanel getPanel() {
		
		JPanel panel = new JPanel();
		
		return panel;
		
	}
	
	private void updatePanel() {
		
		if (active != null) {
			
			getLabel().setText(LABEL_PREFIX + active.toString());
			
		} else {
			
			getLabel().setText(LABEL_PREFIX);
			
		}
		
	}
	
}
