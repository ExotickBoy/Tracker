package tools;

import java.awt.Graphics2D;
import java.util.ArrayList;

import core.Driver;
import core.Edit;
import core.RailConnection;
import core.RailPoint;
import core.Tool;

public final class DeleteTool extends Tool {
	
	private static final String TOOL_NAME = "Delete";
	
	private ArrayList<RailConnection> brokenConnections;
	
	public DeleteTool() {
		
		setName(TOOL_NAME);
		setGroup(ToolGroup.DELETE);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return !scene.selected.isEmpty();
		
	}
	
	@Override
	public void onActivate() {
		
		brokenConnections = new ArrayList<>();
		
		scene.railPoints.removeAll(scene.selected);
		scene.connections.removeIf((railConnection) -> {
			
			boolean will = scene.selected.contains(railConnection.point1) || scene.selected.contains(railConnection.point2);
			
			if (will) {
				
				brokenConnections.add(railConnection);
				
			}
			
			return will;
			
		});
		
		finalise();
		Driver.selectTool(Driver.GRAB_TOOL);
		
	}
	
	@Override
	public Edit onFinalise() {
		
		return new Edit() {
			
			ArrayList<RailPoint> deleted = new ArrayList<>(scene.selected);
			ArrayList<RailConnection> brokenConnections = DeleteTool.this.brokenConnections;
			
			{
				scene.selected.clear();
				
			}
			
			@Override
			public void redo() {
				
				scene.selected.clear();
				scene.railPoints.removeAll(deleted);
				scene.connections.removeAll(brokenConnections);
				
			}
			
			@Override
			public void undo() {
				
				scene.selected.clear();
				scene.selected.addAll(deleted);
				scene.railPoints.addAll(deleted);
				scene.connections.addAll(brokenConnections);
				
			}
			
		};
		
	}
	
	@Override
	public void onAbort() {}
	
	@Override
	public String getMessage() {
		
		return "";
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {}
	
	@Override
	public void drawUnder(Graphics2D g) {}
	
	@Override
	public void takeMessage(String input) {}
	
}
