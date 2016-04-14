package tools;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import core.Driver;
import core.Edit;
import core.RailPoint;
import core.Tool;
import utils.Vector2;

public final class RightSelectTool extends Tool {
	
	private static final String TOOL_NAME = "Select";

	private static final int SELECT_RADIOUS = 5;
	
	ArrayList<RailPoint> before;
	
	public RightSelectTool() {
		
		setName(TOOL_NAME);
		setMode(Driver.RAILER_MODE);
		
	}
		
	@Override
	public void mousePressed(MouseEvent e) {
		
		Vector2 mouse = new Vector2(e.getX(), e.getY());
		
		scene.railPoints.stream().filter((railPoint) -> {
			
			return Vector2.magnitude(Vector2.subtract(railPoint.getPosition(), mouse)) < SELECT_RADIOUS;
			
		}).min((rp1, rp2) -> {
			
			return Double.compare(Vector2.magnitude(Vector2.subtract(rp1.getPosition(), mouse)), Vector2.magnitude(Vector2.subtract(rp2.getPosition(), mouse)));
			
		}).ifPresent((railPoint) -> {
			
			if (e.isShiftDown()) {
				
				if (scene.selected.contains(railPoint)) {
					
					scene.selected.remove(railPoint);
					scene.selected.add(0, railPoint);
					
				} else {
					
					scene.selected.add(0, railPoint);
					
				}
				
			} else {
				
				scene.selected.clear();
				scene.selected.add(railPoint);
				
			}
			
		});
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		finalise();
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return true;
		
	}
	
	@Override
	public void onActivate() {
		
		before = new ArrayList<>(scene.selected);
		
	}
	
	@Override
	public Edit onFinalise() {
		
		return new Edit() {
			
			ArrayList<RailPoint> before = RightSelectTool.this.before;
			ArrayList<RailPoint> after = new ArrayList<>(scene.selected);
			
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
	public void takeMessage(String input) {
		
	}
	
}
