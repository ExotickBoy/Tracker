package tools;

import static java.lang.Math.PI;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import core.Driver;
import core.Edit;
import core.Tool;
import interfaces.Collidable;
import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import items.RailConnection;
import items.RailLocation;
import items.RailPoint;
import items.RailSignal;
import items.Train;
import items.TrainStop;
import utils.Snap;
import utils.Vector2;

public class TrainPlaceTool extends Tool {
	
	private class ClosestApproach extends Snap<RailLocation> {
		
		// Vector2 mouse;
		// Vector2 location;
		
		public ClosestApproach(Vector2 mouse, RailConnection railConnection) {
			
			double t = railConnection.getClosestAproach(mouse);
			boolean direction = Vector2.project(Vector2.subtract(railConnection.getPoint(t), mouse), Vector2.rotate(railConnection.getDerivative(t), PI / 2)) < 0;
			
			setDestincation(new RailLocation(t, railConnection, direction));
			setDistance(Vector2.magnitude(Vector2.subtract(mouse, getDestincation().getPoint())));
			
			// location = getDestincation().getVector();
			// this.mouse = mouse;
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class OppositeSnap extends Snap<RailLocation> {
		
		public OppositeSnap(Vector2 mouse, RailLocation to) {
			
			setDestincation(new RailLocation(to));
			getDestincation().toggleForward();
			setDistance(Vector2.distance(getDestincation().getPoint(), mouse) - OPPOSITE_SNAP_DISTANCE);
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class OnPointSnap extends Snap<RailLocation> {
		
		public OnPointSnap(Vector2 mouse, RailPoint point) {
			
			RailConnection to = scene.connections.stream().filter((connection) -> {
				
				return connection.has(point);
				
			}).findFirst().orElse(null);
			
			if (to != null) {
				
				setDestincation(new RailLocation(to.point1 == point ? 1 : 0, to));
				setDistance(Vector2.distance(getDestincation().getPoint(), mouse) - OPPOSITE_SNAP_DISTANCE);
				
				getDestincation().setForward(
						Vector2.project(Vector2.subtract(to.getPoint(getDestincation().getT()), mouse), Vector2.rotate(to.getDerivative(getDestincation().getT()), PI / 2)) < 0);
						
			}
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private static final String TOOL_NAME = "Place Train";
	
	private static final double SNAP_DISTANCE = 30;
	private static final double OPPOSITE_SNAP_DISTANCE = 12;
	
	ArrayList<Function<RailLocation, OnRail>> getPlacing = new ArrayList<>();
	ArrayList<Consumer<OnRail>> addPlacing = new ArrayList<>();
	int index;
	
	OnRail placing;
	Snap<RailLocation> snap;
	boolean canPlace = false;
	
	public TrainPlaceTool() {
		
		setName(TOOL_NAME);
		setGroup(ToolGroup.PLACE);
		
		getPlacing.add(Train::new);
		addPlacing.add((a) -> Driver.scene.trains.add((Train) a));
		getPlacing.add(RailSignal::new);
		addPlacing.add((a) -> Driver.scene.railSignals.add((RailSignal) a));
		getPlacing.add(TrainStop::new);
		addPlacing.add((a) -> Driver.scene.trainStops.add((TrainStop) a));
		
		Train p = new Train();
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
		
		if (placing != null && placing.getRailLocation() != null) {
			
			ArrayList<Collidable> collidesWith = getCollidables().stream().filter(other -> {
				
				return other.isByRail() == placing.isByRail() && other.collidesWith(placing);
				
			}).collect(Collectors.toCollection(ArrayList::new));
			
			canPlace = collidesWith.size() == 0;
			
			placing.setDrawCollider(collidesWith.size() > 0);
			
			getCollidables().forEach((collidable) -> {
				
				collidable.setDrawCollider(collidesWith.contains(collidable) && collidesWith.size() > 0);
				
			});
			
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
		
		if (canPlace & snap != null && placing instanceof Selectable) {
			
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
		
		getOnRails().stream().filter(item -> {
			
			return placing != null && placing.isByRail() && item.isByRail() && getOnRails().stream().map(OnRail::getRailLocation).noneMatch((location) -> {
				
				return location.getConnection() == item.getRailLocation().getConnection() && location.getT() == item.getRailLocation().getT()
						&& location.isForward() != item.getRailLocation().isForward();
						
			});
			
		}).map((onRail) -> {
			
			return new OppositeSnap(mouse, onRail.getRailLocation());
			
		}).forEach(snapBuilder::accept);
		
		scene.railPoints.stream().map((railPoint) -> {
			
			return new OnPointSnap(mouse, railPoint);
			
		}).forEach(snapBuilder::accept);
		
		return snapBuilder.build()
				// .filter(TrainPlaceTool::withinDistance)
				.filter(Snap::isValid).min(Snap::sortByDistance).orElse(null);
				
	}
	
	private ArrayList<Collidable> getCollidables() {
		
		return Stream.concat(Stream.concat(scene.railSignals.stream(), scene.trainStops.stream()), scene.trains.stream()).collect(Collectors.toCollection(ArrayList::new));
		
	}
	
	private ArrayList<OnRail> getOnRails() {
		
		return Stream.concat(Stream.concat(scene.railSignals.stream(), scene.trainStops.stream()), scene.trains.stream()).collect(Collectors.toCollection(ArrayList::new));
		
	}
	
	@Override
	public boolean willFinaliseOnRealeaseLeftMouse() {
		
		return true;
		
	}
	
	@Override
	public boolean willAbortOnRealeaseRightMouse() {
		
		return false;
		
	}
	
}
