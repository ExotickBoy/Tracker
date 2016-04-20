package components;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core.Driver;
import core.Tab;
import tabs.RailPointTab;
import tabs.TrainTab;
import tabs.WorldTab;

public final class TabsPanel extends JTabbedPane {
	
	public static final RailPointTab RAIL_POINT_TAB = new RailPointTab();
	public static final TrainTab TRAIN_TAB = new TrainTab();
	public static final WorldTab WORLD_TAB = new WorldTab();
	
	private static final long serialVersionUID = 218536682475195089L;
	private ArrayList<Tab> tabs;
	
	public TabsPanel() {
		
		setPreferredSize(new Dimension(Driver.TAB_PANEL_WIDTH, getPreferredSize().height));
			
		addChangeListener(new ChangeListener() {
			
			int lastSelected = -1;
			
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				
				if (lastSelected != -1) {
					
					int index = getSelectedIndex();
					
					tabs.get(lastSelected).onSwitchedAway();
					tabs.get(index).onSwitchedTo();
					
					lastSelected = getSelectedIndex();
					
				} else {
					
					int index = getSelectedIndex();
					
					tabs.get(index).onSwitchedTo();
					
					lastSelected = getSelectedIndex();
					
				}
				
			}
		});
		
		tabs = new ArrayList<Tab>();
		
		tabs.add(WORLD_TAB);
		tabs.add(RAIL_POINT_TAB);
		tabs.add(TRAIN_TAB);
		
		tabs.forEach((tab) -> {
			
			addTab(tab.getTitle(), tab);
			
		});
		setSelectedIndex(0);
		
	}
	
	public void onSwitchedTo() {
		
		tabs.get(getSelectedIndex()).onSwitchedTo();
		
	}
	
	public void onSwitchedAway() {
		
		tabs.get(getSelectedIndex()).onSwitchedAway();
		
	}
	
	public void onModeSwitched(){
		
		tabs.get(getSelectedIndex()).onModeSwitched();
		
	}
	
	public void onToolActivate() {
		
		tabs.get(getSelectedIndex()).onToolActivate();
		
	}
	
	public void onToolFinalise() {
		
		tabs.get(getSelectedIndex()).onToolFinalise();
		
	}
	
	public void onToolAbort() {
		
		tabs.get(getSelectedIndex()).onToolAbort();
		
	}
	
	public void onUndo() {
		
		tabs.get(getSelectedIndex()).onUndo();
		
	}
	
	public void onRedo() {
		
		tabs.get(getSelectedIndex()).onRedo();
		
	}
	
}
