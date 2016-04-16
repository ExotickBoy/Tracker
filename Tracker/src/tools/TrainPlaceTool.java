package tools;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import core.Driver;
import core.Edit;
import core.Tool;
import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import items.RailConnection;
import items.RailLocation;
import items.RailSignal;
import items.Train;
import items.TrainStop;
import utils.BezierCurves;
import utils.Snap;
import utils.Vector2;

import static java.lang.Math.*;

public class TrainPlaceTool extends Tool {
	
	private class ClosestApproach extends Snap<RailLocation> {
		
		// Vector2 mouse;
		// Vector2 location;
		
		public ClosestApproach(Vector2 mouse, RailConnection railConnection) {
			
			double t = BezierCurves.cubicClosesAproach(railConnection.p0, railConnection.p1, railConnection.p2, railConnection.p3, mouse);
			boolean direction = Vector2.project(Vector2.subtract(BezierCurves.cubicBezier(railConnection.p0, railConnection.p1, railConnection.p2, railConnection.p3, t), mouse),
					Vector2.rotate(BezierCurves.cubicBezierDerivative(railConnection.p0, railConnection.p1, railConnection.p2, railConnection.p3, t), PI / 2)) < 0;
					
			setDestincation(new RailLocation(t, railConnection, direction));
			setDistance(Vector2.magnitude(Vector2.subtract(mouse, BezierCurves.cubicBezier(railConnection.p0, railConnection.p1, railConnection.p2, railConnection.p3, t))));
			
			// location = getDestincation().getVector();
			// this.mouse = mouse;
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			// g.draw(new Line2D.Double(mouse.x, mouse.y, location.x, location.y));
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private static final String TOOL_NAME = "Place Train";
	
	private static final double SNAP_DISTANCE = 30;
	
	ArrayList<Function<RailLocation, OnRail>> getPlacing = new ArrayList<>();
	ArrayList<Consumer<OnRail>> addPlacing = new ArrayList<>();
	int index;
	
	OnRail placing;
	Snap<RailLocation> snap;
	
	public TrainPlaceTool() {
		
		setName(TOOL_NAME);
		setGroup(ToolGroup.PLACE);
		
		getPlacing.add(Train::new);
		addPlacing.add((a) -> Driver.scene.trains.add((Train) a));
		getPlacing.add(RailSignal::new);
		addPlacing.add((a) -> Driver.scene.railSignals.add((RailSignal) a));
		getPlacing.add(TrainStop::new);
		addPlacing.add((a) -> Driver.scene.trainStops.add((TrainStop) a));
		
		Train p = new Train(null);
		placing = p;
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return true;
		
	}
	
	@Override
	public void onActivate() {
	
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		finalise();
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		mouseMoved(e);
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		Vector2 mouse = new Vector2(e.getX(), e.getY());
		
		snap = getSnap(mouse);
		
		if (snap != null) {
			
			if (placing == null) {
				
				placing = getPlacing.get(index).apply(snap.getDestincation());
				
			} else {
				
				placing.setRailLocation(snap.getDestincation());
				
			}
			
		} else if (placing != null) {
			
			placing.setRailLocation(null);
			
		}
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		index += e.getWheelRotation();
		index %= getPlacing.size();
		
		if (index == -1) {
			
			index = getPlacing.size() + index;
			
		}
		
		if (snap != null) {
			
			placing = getPlacing.get(index).apply(snap.getDestincation());
			
		} else {
			
			placing = null;
			
		}
		
	}
	
	@Override
	public Edit onFinalise() {
		
		if (snap != null && placing instanceof Selectable) {
			
			addPlacing.get(index).accept(placing);
			placing = null;
			
		}
		
		return null;
		
	}
	
	@Override
	public void onAbort() {
	
	}
	
	@Override
	public String getMessage() {
		
		return "[Scroll to place other]";
		
	}
	
	@Override
	public boolean willAbortOnEscape() {
		
		return false;
		
	}
	
	@Override
	public Tool getFollowingTool() {
		
		return Driver.TRAIN_TOOL;
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {
		
		if (placing != null && placing instanceof Drawable && placing.getRailLocation() != null) {
			
			Driver.draw(placing, g);
			
		}
		
	}
	
	@Override
	public void drawUnder(Graphics2D g) {
		
		if (snap != null) {
			
			Driver.draw(snap, g);
		}
		
	}
	
	@Override
	public void takeMessage(String input) {}
	
	public static boolean withinDistance(Snap<RailLocation> snap) {
		
		return snap.getDistance() < SNAP_DISTANCE;
		
	}
	
	private Snap<RailLocation> getSnap(Vector2 mouse) {
		
		Builder<Snap<RailLocation>> snapBuilder = Stream.builder();
		
		scene.connections.stream().map((railConnection) -> {
			
			return new ClosestApproach(mouse, railConnection);
			
		}).forEach(snapBuilder::accept);
		
		return snapBuilder.build().filter(TrainPlaceTool::withinDistance).min(Snap::sortByDistance).orElse(null);
		
	}
	
}
