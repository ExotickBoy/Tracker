package tools;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;
import items.RailConnection;
import items.RailPoint;
import utils.Vector2;

public final class DuplicateTool extends ActionTool {
	
	private static final String TOOL_NAME = "Duplicate";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.SHIFT_MASK);
	
	private ArrayList<RailPoint> added = new ArrayList<>();
	private ArrayList<RailConnection> addedConnections = new ArrayList<>();
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
		addedConnections.clear();
		
		HashMap<RailPoint, RailPoint> newToOriginal = new HashMap<>();
		
		scene.selected.forEach((railPoint) -> {
			
			RailPoint add = new RailPoint();
			add.setPosition(new Vector2(railPoint.getPosition()));
			add.setDirection(railPoint.getDirection());
			
			scene.railPoints.add(add);
			added.add(add);
			newToOriginal.put(add, railPoint);
			
		});
		
		added.stream().forEach((add) -> {
			
			added.stream().filter(another -> another != add).forEach((another) -> {
				
				scene.connections.stream().filter((connection) -> {
					
					return connection.point1 == newToOriginal.get(add) && connection.point2 == newToOriginal.get(another);
					
				}).map(connection -> {
					
					return new RailConnection(add, another);
					
				}).forEach((connection) -> {
					
					addedConnections.add(connection);
					
				});
				
			});
			
		});
		addedConnections.forEach(scene.connections::add);
		
		scene.selected.clear();
		scene.selected.addAll(added);
		
		return new Edit() {
			
			ArrayList<RailPoint> added = new ArrayList<>(DuplicateTool.this.added);
			ArrayList<RailConnection> addedConnections = new ArrayList<>(DuplicateTool.this.addedConnections);
			ArrayList<RailPoint> selectedBefore = new ArrayList<>(DuplicateTool.this.selectedBefore);
			
			@Override
			public void redo() {
				
				scene.railPoints.addAll(added);
				scene.connections.addAll(addedConnections);
				scene.selected.clear();
				scene.selected.addAll(added);
				
			}
			
			@Override
			public void undo() {
				
				scene.railPoints.removeAll(added);
				scene.connections.removeAll(addedConnections);
				scene.selected = selectedBefore;
				
			}
			
		};
		
	}
	
	@Override
	public Tool getFollowingTool() {
		
		return Driver.GRAB_TOOL;
		
	}
	
}
