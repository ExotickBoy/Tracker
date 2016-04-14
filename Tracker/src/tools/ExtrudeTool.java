package tools;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.RailConnection;
import core.RailPoint;
import core.Tool;
import utils.Vector2;

public class ExtrudeTool extends ActionTool {
	
	private static final String TOOL_NAME = "Extrude";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0);
	
	private ArrayList<RailPoint> selectedBefore = new ArrayList<>();
	private ArrayList<RailPoint> added = new ArrayList<>();
	private ArrayList<RailConnection> connectionsAdded = new ArrayList<>();
	
	public ExtrudeTool() {
		
		setName(TOOL_NAME);
		setKeyStroke(HOT_KEY);
		setGroup(ToolGroup.SPECIALS);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public Edit action() {
		
		selectedBefore = new ArrayList<>(scene.selected);
		added.clear();
		
		scene.selected.forEach((railPoint) -> {
			
			RailPoint add = new RailPoint();
			add.setPosition(new Vector2(railPoint.getPosition()));
			add.setDirection(railPoint.getDirection());
			
			scene.railPoints.add(add);
			RailConnection connection = new RailConnection(railPoint, add);
			scene.connections.add(connection);
			connectionsAdded.add(connection);
			added.add(add);
			
		});
		
		scene.selected.clear();
		scene.selected.addAll(added);
		
		return new Edit() {
			
			ArrayList<RailPoint> added = new ArrayList<>(ExtrudeTool.this.added);
			ArrayList<RailPoint> selectedBefore = new ArrayList<>(ExtrudeTool.this.selectedBefore);
			ArrayList<RailConnection> connectionsAdded = new ArrayList<>(ExtrudeTool.this.connectionsAdded);
			
			@Override
			public void redo() {
				
				scene.railPoints.addAll(added);
				scene.selected.clear();
				scene.selected.addAll(added);
				scene.connections.addAll(connectionsAdded);
				
			}
			
			@Override
			public void undo() {
				
				scene.railPoints.removeAll(added);
				scene.selected = selectedBefore;
				scene.connections.removeAll(connectionsAdded);
				
			}
			
		};
		
	}
	
	@Override
	public Tool getFollowingTool() {
		
		return Driver.GRAB_TOOL;
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return !scene.selected.isEmpty();
		
	}
	
}
