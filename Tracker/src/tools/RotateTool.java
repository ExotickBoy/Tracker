package tools;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;
import items.RailPoint;
import utils.Snap;
import utils.Vector2;

public final class RotateTool extends Tool {
	
	private class AlignSnap extends Snap<Double> {
		
		Vector2 centre;
		Vector2 other;
		
		public AlignSnap(RailPoint centre, RailPoint selected, RailPoint other) {
			
			setDestincation(atan2(centre.getPosition().x - beforePosition.get(selected).x, centre.getPosition().y - beforePosition.get(selected).y)
					- atan2(centre.getPosition().x - other.getPosition().x, centre.getPosition().y - other.getPosition().y));
					
			setDistance(distanceBetweenTwoAngles(relativeAngle, getDestincation()));
			
			this.centre = centre.getPosition();
			this.other = other.getPosition();
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			g.setColor(ALIGN_SNAP_COLOR);
			
			g.draw(new Line2D.Double(centre.x, centre.y, other.x, other.y));
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class DirectionSnap extends Snap<Double> {
		
		RailPoint active;
		RailPoint railPoint;
		
		public DirectionSnap(RailPoint active, RailPoint railPoint) {
			
			setDestincation(-beforeRotation.get(active) - Vector2.atan2(Vector2.subtract(railPoint.getPosition(), active.getPosition())) - PI / 2);
			//setDestincation(0d);
			
			setDistance(distanceBetweenTwoAngles(relativeAngle, getDestincation()));
			
			
			this.active = active;
			this.railPoint = railPoint;
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			g.setColor(DIRECTION_SNAP_COLOR);
			
			g.draw(new Line2D.Double(active.getPosition().x, active.getPosition().y, railPoint.getPosition().x, railPoint.getPosition().y));
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class MessageSnap extends Snap<Double> {
		
		public MessageSnap(String message) {
			
			setDestincation(getValue(message));
			setDistance(0);
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return true;
			
		}
		
	}
	
	private static final Color ALIGN_SNAP_COLOR = Color.MAGENTA;
	private static final Color DIRECTION_SNAP_COLOR = Color.MAGENTA;
	private static final Color MOUSE_LINE_COLOR = Color.BLACK;
	
	private static final double SNAP_DISTANCE = toRadians(4);
	
	private static final String TOOL_NAME = "Rotate";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
	
	Vector2 mouseStart;
	Vector2 mouse;
	
	HashMap<RailPoint, Vector2> beforePosition = new HashMap<>();
	HashMap<RailPoint, Double> beforeRotation = new HashMap<>();
	RailPoint active;
	
	double angleAtStart;
	double relativeAngle;
	
	Snap<Double> snap;
	boolean snapping;
	
	String input;
	
	public RotateTool() {
		
		setKeyStroke(HOT_KEY);
		setName(TOOL_NAME);
		setGroup(ToolGroup.TRANSFORMATION);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		mouseMoved(e);
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		mouse.x = e.getX();
		mouse.y = e.getY();
		
		relativeAngle = angleAtStart - atan2(mouse.x - active.getPosition().x, mouse.y - active.getPosition().y);
		
		snapping = e.isShiftDown();
		
		if (snapping && (snap == null || (snap != null && !snap.isPermanent()))) {
			
			snap = getSnap();
			
			if (snap != null) {
				
				relativeAngle = snap.getDestincation();
				
			}
			
		} else {
			
			snap = null;
			
		}
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.setPosition(Vector2.add(Vector2.rotate(new Vector2(Vector2.subtract(beforePosition.get(railPoint), //
					active.getPosition())), relativeAngle), active.getPosition()));
			railPoint.setDirection((beforeRotation.get(railPoint) + relativeAngle + (PI * 2)) % (PI * 2));
			
		});
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (e.getButton() == 3) {
			
			abort();
			
		} else {
			
			finalise();
			
		}
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return scene.selected.size() > 0;
		
	}
	
	@Override
	public void onActivate() {
		
		mouseStart = new Vector2(getMousePosition().getX(), getMousePosition().getY());
		mouse = new Vector2(mouseStart);
		
		beforePosition.clear();
		beforeRotation.clear();
		
		scene.selected.forEach((railPoint) -> {
			
			beforePosition.put(railPoint, new Vector2(railPoint.getPosition()));
			beforeRotation.put(railPoint, railPoint.getDirection());
			
		});
		
		active = scene.selected.get(0);
		
		snap = null;
		input = "";
		
		angleAtStart = atan2(mouseStart.x - active.getPosition().x, mouseStart.y - active.getPosition().y);
		
	}
	
	@Override
	public Edit onFinalise() {
		
		return new Edit() {
			
			HashMap<RailPoint, Vector2> beforePosition = new HashMap<>(RotateTool.this.beforePosition);
			HashMap<RailPoint, Vector2> afterPosition = new HashMap<>();;
			HashMap<RailPoint, Double> beforeRotation = new HashMap<>(RotateTool.this.beforeRotation);
			HashMap<RailPoint, Double> afterRotation = new HashMap<>();;
			
			{
				
				scene.railPoints.forEach((railPoint) -> {
					
					afterPosition.put(railPoint, new Vector2(railPoint.getPosition()));
					afterRotation.put(railPoint, railPoint.getDirection());
					
				});
				
			}
			
			@Override
			public void undo() {
				
				beforePosition.forEach((railPoint, before) -> {
					
					railPoint.setPosition(before);
					
				});
				beforeRotation.forEach((railPoint, before) -> {
					
					railPoint.setDirection(before);
					
				});
				
			}
			
			@Override
			public void redo() {
				
				afterPosition.forEach((railPoint, after) -> {
					
					railPoint.setPosition(after);
					
				});
				afterRotation.forEach((railPoint, after) -> {
					
					railPoint.setDirection(after);
					
				});
				
			}
			
		};
		
	}
	
	@Override
	public void onAbort() {
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.setPosition(beforePosition.get(railPoint));
			railPoint.setDirection(beforeRotation.get(railPoint));
			
		});
		
	}
	
	@Override
	public String getMessage() {
		
		StringBuilder message = new StringBuilder();
		if (input.equals("")) {
			
			message.append("dr:");
			message.append(String.format("%3.2f", toDegrees(relativeAngle)));
			
		} else {
			
			message.append("input:");
			message.append(input);
			
		}
		
		if (snapping) {
			
			message.append(" [Snapping]\t");
			
		} else {
			
			message.append(" [Shift for snapping]\t");
			
		}
		
		return message.toString();
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {
		
		g.setColor(MOUSE_LINE_COLOR);
		
		if (!(snap instanceof MessageSnap)) {
			
			g.draw(new Line2D.Double(active.getPosition().x, active.getPosition().y, mouse.x, mouse.y));
			
		} else {
		
		}
		
	}
	
	@Override
	public void drawUnder(Graphics2D g) {
		
		if (snap != null) {
			
			Driver.draw(snap, g);
			
		}
		
	}
	
	@Override
	public void takeMessage(String input) {
		
		this.input = input;
		if (input.equals("")) {
			
			snap = null;
			
		} else {
			
			snap = new MessageSnap(input);
			
			relativeAngle = snap.getDestincation();
			scene.selected.forEach((railPoint) -> {
				
				railPoint.setPosition(Vector2.add(Vector2.rotate(new Vector2(Vector2.subtract(beforePosition.get(railPoint), //
						active.getPosition())), relativeAngle), active.getPosition()));
				railPoint.setDirection(beforeRotation.get(railPoint) + relativeAngle);
				
			});
			
		}
		
	}
	
	private Snap<Double> getSnap() {
		
		Builder<Snap<Double>> snapBuilder = Stream.builder();
		
		scene.selected.stream().filter((selectedRailPoint) -> {
			
			return selectedRailPoint != active;
			
		}).map((selected) -> {
			
			return scene.railPoints.stream().filter((other) -> {
				
				return !scene.selected.contains(other);
				
			}).map((other) -> {
				
				return new AlignSnap(active, selected, other);
				
			}).min(Snap::sortByDistance).orElse(null);
			
		}).filter(Objects::nonNull).forEach(snapBuilder::accept);
		
		scene.railPoints.stream().filter((railPoint) -> {
			
			return !scene.selected.contains(railPoint);
			
		}).map((railPoint) -> {
			
			return new DirectionSnap(active, railPoint);
			
		}).forEach(snapBuilder::accept);
		
		return snapBuilder.build().filter(RotateTool::withinDistance).min(Snap::sortByDistance).orElse(null);
		
	}
	
	private double distanceBetweenTwoAngles(double a, double b) {
		
		return abs(PI - abs(abs(a - b) - PI));
		
	}
	
	private static boolean withinDistance(Snap<Double> snap) {
		
		return snap.getDistance() < SNAP_DISTANCE;
		
	}
	
	public static double getValue(String message) {
		
		try {
			
			return toRadians(Double.valueOf(message));
			
		} catch (Exception e) {
			
			return 0;
			
		}
		
	}
	
}
