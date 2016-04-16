package tools;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;
import items.RailPoint;
import utils.Vector2;

public final class DuplicateTool extends ActionTool {
	
	private static final String TOOL_NAME = "Duplicate";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.SHIFT_MASK);
	
	private ArrayList<RailPoint> added = new ArrayList<>();
	private ArrayList<RailPoint> selectedBefore = new ArrayList<>();
	
	public DuplicateTool() {
		
		setName(TOOL_NAME);
		setKeyStroke(HOT_KEY);
		setGroup(ToolGroup.SPECIALS);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return !scene.selected.isEmpty();
		
	}
	
	@Override
	public Edit action() {
		
		selectedBefore = new ArrayList<>(scene.selected);
		added.clear();
		
		scene.selected.forEach((railPoint) -> {
			
			RailPoint add = new RailPoint();
			add.setPosition(new Vector2(railPoint.getPosition()));
			add.setDirection(railPoint.getDirection());
			/*
			scene.selected.stream().anyMatch((railPoint2)->{
				
				scene.connections.stream().anyMatch((connection)->{
					
					return connection.isConnectionBetween(railPoint, railPoint2)
					
				});
				
			});
			*/
			scene.railPoints.add(add);
			added.add(add);
			
		});
		
		scene.selected.clear();
		scene.selected.addAll(added);
		
		return new Edit() {
			
			ArrayList<RailPoint> added = new ArrayList<>(DuplicateTool.this.added);
			ArrayList<RailPoint> selectedBefore = new ArrayList<>(DuplicateTool.this.selectedBefore);
			
			@Override
			public void redo() {
				
				scene.railPoints.addAll(added);
				scene.selected.clear();
				scene.selected.addAll(added);
				
			}
			
			@Override
			public void undo() {
				
				scene.railPoints.removeAll(added);
				scene.selected = selectedBefore;
				
			}
			
		};
		
	}
	
	@Override
	public Tool getFollowingTool() {
		
		return Driver.GRAB_TOOL;
		
	}
	
}
