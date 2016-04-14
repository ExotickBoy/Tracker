package tabs;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import core.Tab;

public class WorldTab extends Tab {
	
	private static final long serialVersionUID = 1L;
	
	private static final String TAB_TITLE = "World";
	
	public WorldTab() {
		
		setTitle(TAB_TITLE);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		add(new JLabel("Work in progress"));
		add(new JLabel("• camera location"));
		add(new JLabel("• angles in deg/rad"));
		add(new JLabel("• snap types  "));
		add(new JLabel("• centres, eg centre of rotation"));
		
	}
	
	@Override
	public void onToolActivate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onToolFinalise() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onToolAbort() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUndo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onRedo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSwitchedTo() {
		
	}
	
	@Override
	public void onSwitchedAway() {
		// TODO Auto-generated method stub
		
	}
	
}
