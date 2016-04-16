package tools;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import core.Driver;
import core.Edit;
import core.Tool;
import items.RailPoint;
import utils.Vector2;

public final class LeftSelectTool extends Tool {
	
	private static final String TOOL_NAME = "Select";
	
	private static final double DEFAULT_RADIUS = 30;
	private static final double RADIUS_SCALE_FACCTOR = 1.4;
	private static final int MAX_RADIUS = 200;
	private static final int MIN_RADIUS = 2;
	
	private static final int TRANSPARENCY_AMOUNT = 20;
	private static final Color CIRLCE_COLOR = Color.ORANGE;
	private static final Color TRANSPARENT_CIRLCE_COLOR = new Color(CIRLCE_COLOR.getRed(), CIRLCE_COLOR.getGreen(), CIRLCE_COLOR.getBlue(), TRANSPARENCY_AMOUNT);
	
	private double radius = DEFAULT_RADIUS;
	
	private Vector2 mouseAt = new Vector2();
	
	private ArrayList<RailPoint> selectedBefore;
	
	public LeftSelectTool() {
		
		setName(TOOL_NAME);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		select(e);
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		mouseAt.x = e.getX();
		mouseAt.y = e.getY();
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		scene.selected.clear();
		
		select(e);
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		finalise();
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		if (e.getPreciseWheelRotation() > 0) {
			
			radius = min(radius * RADIUS_SCALE_FACCTOR, MAX_RADIUS);
			
		} else {
			
			radius = max(radius / RADIUS_SCALE_FACCTOR, MIN_RADIUS);
			
		}
		
		select(e);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return true;
		
	}
	
	@Override
	public boolean isOverwritable() {
		
		return false;
		
	}
	
	@Override
	public void onActivate() {
				
		selectedBefore = new ArrayList<>(scene.selected);
		
	}
	
	@Override
	public Edit onFinalise() {
		
		Set<Object> set1 = new HashSet<Object>();
		set1.addAll(selectedBefore);
		Set<Object> set2 = new HashSet<Object>();
		set2.addAll(scene.selected);
		
		if (set1.equals(set2) && (set1.size() == 0 || selectedBefore.get(0) == scene.selected.get(0))) {
			
			return null;
			
		} else {
			
			return new Edit() {
				
				ArrayList<RailPoint> selectedBefore = new ArrayList<RailPoint>(LeftSelectTool.this.selectedBefore);
				ArrayList<RailPoint> selectedAfter = new ArrayList<RailPoint>(scene.selected);
				
				@Override
				public void undo() {
					
					scene.selected = selectedBefore;
					
				}
				
				@Override
				public void redo() {
					
					scene.selected = selectedAfter;
					
				}
				
			};
			
		}
		
	}
	
	@Override
	public void onAbort() {
		
		scene.selected = selectedBefore;
		
	}
	
	@Override
	public String getMessage() {
		
		return scene.selected.size() + "/" + scene.railPoints.size() + " selected";
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {
		
		g.setColor(TRANSPARENT_CIRLCE_COLOR);
		g.fill(new Ellipse2D.Double(mouseAt.x - radius, mouseAt.y - radius, radius * 2, radius * 2));
		
		g.setColor(CIRLCE_COLOR);
		g.draw(new Ellipse2D.Double(mouseAt.x - radius, mouseAt.y - radius, radius * 2, radius * 2));
		
	}
	
	@Override
	public void drawUnder(Graphics2D g) {}
	
	private void select(MouseEvent e) {
		
		mouseAt.x = e.getX();
		mouseAt.y = e.getY();
		
		scene.railPoints.stream().filter((railPoint) -> {
			
			return !scene.selected.contains(railPoint);
			
		}).filter((railPoint) -> {
			
			return Vector2.magnitude(Vector2.subtract(railPoint.getPosition(), mouseAt)) < radius;
			
		}).sorted((a, b) -> {
			
			return Double.compare(Vector2.magnitude(Vector2.subtract(a.getPosition(), mouseAt)), Vector2.magnitude(Vector2.subtract(b.getPosition(), mouseAt)));
			
		}).forEach(scene.selected::add);
		
	}
	
	@Override
	public void takeMessage(String input) {}
	
}
