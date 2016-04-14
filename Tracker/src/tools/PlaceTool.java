package tools;

import java.util.ArrayList;

import core.Driver;
import core.Edit;
import core.RailPoint;
import core.Tool;
import utils.Vector2;

public final class PlaceTool extends ActionTool {
	
	private static final String TOOL_NAME = "Plotter";
	
	private RailPoint added;
	private ArrayList<RailPoint> selectedBefore;
	
	public PlaceTool() {
		
		setName(TOOL_NAME);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public Tool getFollowingTool() {
		
		return Driver.GRAB_TOOL;
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return true;
		
	}
	
	@Override
	public Edit action() {
		
		selectedBefore = new ArrayList<>(scene.selected);
		
		added = new RailPoint(new Vector2(getMousePosition().getX(), getMousePosition().getY()));
		scene.railPoints.add(added);
		
		scene.selected.clear();
		scene.selected.add(scene.railPoints.get(scene.railPoints.size() - 1));
		
		return new Edit() {
			
			RailPoint added = PlaceTool.this.added;
			ArrayList<RailPoint> selectedBefore = PlaceTool.this.selectedBefore;
			
			@Override
			public void undo() {
				
				scene.railPoints.remove(added);
				scene.selected = selectedBefore;
				
			}
			
			@Override
			public void redo() {
				
				scene.railPoints.add(added);
				scene.selected.clear();
				scene.selected.add(added);
				
			}
			
		};
		
	}
	
}
