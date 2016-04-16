package tools;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import items.RailPoint;

public class SelectAllTool extends ActionTool {
	
	private static final String TOOL_NAME = "Select All";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
	
	private ArrayList<RailPoint> before;
	private ArrayList<RailPoint> after;
	
	public SelectAllTool() {
		
		setName(TOOL_NAME);
		setGroup(ToolGroup.SPECIALS);
		setKeyStroke(HOT_KEY);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public Edit action() {
		
		before = new ArrayList<>(scene.selected);
		
		if (scene.selected.size() == 0 && scene.railPoints.size() != 0) {
			
			scene.selected.clear();
			scene.selected.addAll(scene.railPoints);
			
		} else if (scene.selected.size() == scene.railPoints.size() && scene.selected.size() == scene.railPoints.size()) {
			
			scene.selected.clear();
			
		} else {
			
			scene.selected = Stream.concat(scene.selected.stream(), scene.railPoints.stream()).distinct().collect(Collectors.toCollection(ArrayList::new));
			
		}
		
		after = new ArrayList<>(scene.selected);
		
		return new Edit() {
			
			ArrayList<RailPoint> before = SelectAllTool.this.before;
			ArrayList<RailPoint> after = SelectAllTool.this.after;
			
			@Override
			public void redo() {
				
				scene.selected = after;
				
			}
			
			@Override
			public void undo() {
				
				scene.selected = before;
				
			}
			
		};
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return scene.railPoints.size() > 0;
		
	}
	
}
