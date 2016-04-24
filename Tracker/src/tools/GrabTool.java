package tools;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
import items.RailConnection;
import items.RailPoint;
import utils.Snap;
import utils.Vector2;

public final class GrabTool extends Tool {
	
	private class AlignSnap extends Snap<Vector2> {
		
		boolean isHorizontal;
		
		public AlignSnap(boolean isHorizontal, RailPoint railPoint, RailPoint active) {
			
			if (isHorizontal) {
				
				setDestincation(new Vector2(railPoint.getPosition().x, active.getPosition().y));
				setDistance(abs(railPoint.getPosition().x - (before.get(active).x + change.x)));
			} else {
				
				setDestincation(new Vector2(active.getPosition().x, railPoint.getPosition().y));
				setDistance(abs(railPoint.getPosition().y - (before.get(active).y + change.y)));
				
			}
			
			this.isHorizontal = isHorizontal;
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			g.setColor(ALIGN_SNAP_LINE_COLOR);
			
			if (isHorizontal) {
				
				g.draw(new Line2D.Double(getDestincation().x, 0, getDestincation().x, Driver.frame.getHeight()));
				
			} else {
				
				g.draw(new Line2D.Double(0, getDestincation().y, Driver.frame.getWidth(), getDestincation().y));
				
			}
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class BetweenSnap extends Snap<Vector2> {
		
		boolean isHorizontal;
		
		RailPoint railPoint1;
		RailPoint railPoint2;
		
		private BetweenSnap(boolean isHorizontal, RailPoint railPoint1, RailPoint railPoint2, RailPoint active) {
			
			if (isHorizontal) {
				
				setDestincation(new Vector2((railPoint1.getPosition().x + railPoint2.getPosition().x) / 2, active.getPosition().y));
				setDistance(abs(before.get(active).x + change.x - (railPoint1.getPosition().x + railPoint2.getPosition().x) / 2));
				
			} else {
				
				setDestincation(new Vector2(active.getPosition().x, (railPoint1.getPosition().y + railPoint2.getPosition().y) / 2));
				setDistance(abs(before.get(active).y + change.y - (railPoint1.getPosition().y + railPoint2.getPosition().y) / 2));
				
			}
			
			this.isHorizontal = isHorizontal;
			
			if (isHorizontal ? (railPoint1.getPosition().x < railPoint2.getPosition().x) : (railPoint1.getPosition().y < railPoint2.getPosition().y)) {
				
				this.railPoint1 = railPoint1;
				this.railPoint2 = railPoint2;
				
			} else {
				
				this.railPoint1 = railPoint2;
				this.railPoint2 = railPoint1;
				
			}
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			g.setColor(BETWEEN_SNAP_LINE_COLOR);
			
			if (isHorizontal) {
				
				g.draw(new Line2D.Double(railPoint1.getPosition().x, getDestincation().y, getDestincation().x - BETWEEN_SNAP_MARGIN, getDestincation().y));
				
				g.draw(new Line2D.Double(railPoint1.getPosition().x, getDestincation().y, railPoint1.getPosition().x + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(railPoint1.getPosition().x, getDestincation().y, railPoint1.getPosition().x + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_ARROW_SIZE));
						
				g.draw(new Line2D.Double(railPoint1.getPosition().x, min(getDestincation().y - BETWEEN_SNAP_ARROW_SIZE, railPoint1.getPosition().y), railPoint1.getPosition().x,
						max(getDestincation().y + BETWEEN_SNAP_ARROW_SIZE, railPoint1.getPosition().y)));
						
				g.draw(new Line2D.Double(getDestincation().x - BETWEEN_SNAP_MARGIN, getDestincation().y, getDestincation().x - BETWEEN_SNAP_MARGIN - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x - BETWEEN_SNAP_MARGIN, getDestincation().y, getDestincation().x - BETWEEN_SNAP_MARGIN - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_ARROW_SIZE));
						
				// right
				
				g.draw(new Line2D.Double(railPoint2.getPosition().x, getDestincation().y, getDestincation().x + BETWEEN_SNAP_MARGIN, getDestincation().y));
				
				g.draw(new Line2D.Double(railPoint2.getPosition().x, getDestincation().y, railPoint2.getPosition().x - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(railPoint2.getPosition().x, getDestincation().y, railPoint2.getPosition().x - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_ARROW_SIZE));
						
				g.draw(new Line2D.Double(railPoint2.getPosition().x, min(getDestincation().y - BETWEEN_SNAP_ARROW_SIZE, railPoint2.getPosition().y), railPoint2.getPosition().x,
						max(getDestincation().y + BETWEEN_SNAP_ARROW_SIZE, railPoint2.getPosition().y)));
						
				g.draw(new Line2D.Double(getDestincation().x + BETWEEN_SNAP_MARGIN, getDestincation().y, getDestincation().x + BETWEEN_SNAP_MARGIN + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x + BETWEEN_SNAP_MARGIN, getDestincation().y, getDestincation().x + BETWEEN_SNAP_MARGIN + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_ARROW_SIZE));
						
			} else {
				
				g.draw(new Line2D.Double(getDestincation().x, railPoint1.getPosition().y, getDestincation().x, getDestincation().y - BETWEEN_SNAP_MARGIN));
				
				g.draw(new Line2D.Double(getDestincation().x, railPoint1.getPosition().y, getDestincation().x - BETWEEN_SNAP_ARROW_SIZE,
						railPoint1.getPosition().y + BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x, railPoint1.getPosition().y, getDestincation().x + BETWEEN_SNAP_ARROW_SIZE,
						railPoint1.getPosition().y + BETWEEN_SNAP_ARROW_SIZE));
						
				g.draw(new Line2D.Double(min(getDestincation().x - BETWEEN_SNAP_ARROW_SIZE, railPoint1.getPosition().x), railPoint1.getPosition().y,
						max(getDestincation().x + BETWEEN_SNAP_ARROW_SIZE, railPoint1.getPosition().x), railPoint1.getPosition().y));
						
				g.draw(new Line2D.Double(getDestincation().x, getDestincation().y - BETWEEN_SNAP_MARGIN, getDestincation().x - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_MARGIN - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x, getDestincation().y - BETWEEN_SNAP_MARGIN, getDestincation().x + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y - BETWEEN_SNAP_MARGIN - BETWEEN_SNAP_ARROW_SIZE));
						
				// right
				
				g.draw(new Line2D.Double(getDestincation().x, railPoint2.getPosition().y, getDestincation().x, getDestincation().y + BETWEEN_SNAP_MARGIN));
				
				g.draw(new Line2D.Double(getDestincation().x, railPoint2.getPosition().y, getDestincation().x - BETWEEN_SNAP_ARROW_SIZE,
						railPoint2.getPosition().y - BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x, railPoint2.getPosition().y, getDestincation().x + BETWEEN_SNAP_ARROW_SIZE,
						railPoint2.getPosition().y - BETWEEN_SNAP_ARROW_SIZE));
						
				g.draw(new Line2D.Double(min(getDestincation().x - BETWEEN_SNAP_ARROW_SIZE, railPoint2.getPosition().x), railPoint2.getPosition().y,
						max(getDestincation().x + BETWEEN_SNAP_ARROW_SIZE, railPoint2.getPosition().x), railPoint2.getPosition().y));
						
				g.draw(new Line2D.Double(getDestincation().x, getDestincation().y + BETWEEN_SNAP_MARGIN, getDestincation().x - BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_MARGIN + BETWEEN_SNAP_ARROW_SIZE));
				g.draw(new Line2D.Double(getDestincation().x, getDestincation().y + BETWEEN_SNAP_MARGIN, getDestincation().x + BETWEEN_SNAP_ARROW_SIZE,
						getDestincation().y + BETWEEN_SNAP_MARGIN + BETWEEN_SNAP_ARROW_SIZE));
						
			}
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return false;
			
		}
		
	}
	
	private class OriginSnap extends utils.Snap<Vector2> {
		
		boolean isHorizontal;
		Vector2 centre;
		
		public OriginSnap(boolean isHorizontal, Vector2 centre) {
			
			// super(isHorizontal ? new Vector2(0, centre.y) : new Vector2(centre.x, 0), 0d);
			
			this.isHorizontal = isHorizontal;
			this.centre = centre;
			
			if (isHorizontal) {
				
				setDestincation(new Vector2(0, centre.y));
				
			} else {
				
				setDestincation(new Vector2(centre.x, 0));
				
			}
			setDistance(0);
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			if (isHorizontal) {
				
				g.setColor(ORIGIN_SNAP_X_AXIS_COLOR);
				g.draw(new Line2D.Double(0, centre.y, Driver.frame.getWidth(), centre.y));
				
			} else {
				
				g.setColor(ORIGIN_SNAP_Y_AXIS_COLOR);
				g.draw(new Line2D.Double(centre.x, 0, centre.x, Driver.frame.getHeight()));
				
			}
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return true;
			
		}
		
	}
	
	private class MessageSnap extends Snap<Vector2> {
		
		// boolean isHorizontal;
		
		public MessageSnap(boolean isHorizontal, String input) {
			
			if (isHorizontal) {
				
				setDestincation(new Vector2(before.get(active).x + getValue(input), 0));
				
			} else {
				
				setDestincation(new Vector2(0, before.get(active).y + getValue(input)));
				
			}
			
			// this.isHorizontal = isHorizontal;
			setDistance(0);
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return true;
			
		}
		
	}
	
	private static final Color ALIGN_SNAP_LINE_COLOR = Color.CYAN;
	private static final Color BETWEEN_SNAP_LINE_COLOR = Color.MAGENTA;
	private static final Color ORIGIN_SNAP_X_AXIS_COLOR = Color.RED;
	private static final Color ORIGIN_SNAP_Y_AXIS_COLOR = Color.GREEN;
	
	private static final double BETWEEN_SNAP_MARGIN = 7;
	private static final double BETWEEN_SNAP_ARROW_SIZE = 7;
	
	private static final int SNAP_DISTANCE = 10;
	
	private static final String TOOL_NAME = "Grab";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_G, 0);
	
	private Vector2 from = new Vector2();
	private Vector2 change = new Vector2();
	private HashMap<RailPoint, Vector2> before = new HashMap<>();
	
	String input;
	
	boolean snapping;
	Snap<Vector2> horizontalSnap;
	Snap<Vector2> verticalSnap;
	RailPoint active;
	
	public GrabTool() {
		
		setKeyStroke(HOT_KEY);
		setName(TOOL_NAME);
		setGroup(ToolGroup.TRANSFORMATION);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if ((MouseEvent.BUTTON2_DOWN_MASK & e.getModifiersEx()) == MouseEvent.BUTTON2_DOWN_MASK) {
			
			middleMouse(e);
			
		}
		
		mouseMoved(e);
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		Vector2 now = new Vector2(e.getX(), e.getY());
		change = Vector2.subtract(now, from);
		active = scene.selected.get(0);
		
		snapping = e.isShiftDown();
		
		if (snapping) {
			
			if (horizontalSnap == null || !horizontalSnap.isPermanent()) {
				
				horizontalSnap = getSnap(true, active);
				
			}
			
			if (verticalSnap == null || !verticalSnap.isPermanent()) {
				
				verticalSnap = getSnap(false, active);
				
			}
			
		} else {
			
			if (horizontalSnap != null && !horizontalSnap.isPermanent()) {
				
				horizontalSnap = null;
				
			}
			
			if (verticalSnap != null && !verticalSnap.isPermanent()) {
				
				verticalSnap = null;
				
			}
			
		}
		
		if (horizontalSnap != null) {
			
			change.x = horizontalSnap.getDestincation().x - before.get(active).x;
			
		}
		
		if (verticalSnap != null) {
			
			change.y = verticalSnap.getDestincation().y - before.get(active).y;
			
		}
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.getPosition().x = change.x + before.get(railPoint).x;
			railPoint.getPosition().y = change.y + before.get(railPoint).y;
			
		});
		
		scene.connections.stream().filter((connection) -> {
			
			return scene.railPoints.stream().anyMatch((railPoint) -> {
				
				return connection.has(railPoint);
				
			});
			
		}).forEach(RailConnection::update);
		;
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (e.getButton() == 2) {
			
			middleMouse(e);
			
		}
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return !scene.selected.isEmpty() && getMousePosition() != null;
		
	}
	
	@Override
	public void onActivate() {
		
		from = new Vector2(getMousePosition().x, getMousePosition().y);
		change = new Vector2();
		
		before.clear();
		scene.selected.forEach((railPoint) -> {
			
			before.put(railPoint, new Vector2(railPoint.getPosition()));
			
		});
		
		horizontalSnap = null;
		verticalSnap = null;
		
		input = "";
		
	}
	
	@Override
	public Edit onFinalise() {
		
		if (Vector2.magnitude(change) == 0) {
			
			return null;
			
		} else {
			
			return new Edit() {
				
				HashMap<RailPoint, Vector2> before = new HashMap<>(GrabTool.this.before);
				HashMap<RailPoint, Vector2> after = new HashMap<>();
				
				{
					
					scene.selected.forEach((railPoint) -> {
						
						after.put(railPoint, railPoint.getPosition());
						
					});
					
				}
				
				@Override
				public void redo() {
					
					after.entrySet().forEach((set) -> {
						
						set.getKey().setPosition(new Vector2(set.getValue()));
						
					});
					
					scene.connections.stream().filter((connection) -> {
						
						return after.keySet().stream().anyMatch(connection::has);
						
					}).forEach(RailConnection::update);
					
				}
				
				@Override
				public void undo() {
					
					before.entrySet().forEach((set) -> {
						
						set.getKey().setPosition(set.getValue());
						
					});
					
					scene.connections.stream().filter((connection) -> {
						
						return before.keySet().stream().anyMatch(connection::has);
						
					}).forEach(RailConnection::update);
					
				}
				
			};
			
		}
		
	}
	
	@Override
	public void onAbort() {
		
		before.entrySet().forEach((entry) -> {
			
			entry.getKey().setPosition(entry.getValue());
			
		});
		
		scene.connections.stream().filter((connection) -> {
			
			return before.keySet().stream().anyMatch(connection::has);
			
		}).forEach(RailConnection::update);
		
	}
	
	@Override
	public String getMessage() {
		
		StringBuilder message = new StringBuilder();
		if (!input.equals("")) {
			
			if (verticalSnap instanceof OriginSnap) {
				message.append(" input-dx:");
				message.append(input);
			} else {
				message.append(" dx:");
				message.append(String.format("%5.0f", change.x));
			}
			
			if (horizontalSnap instanceof OriginSnap) {
				message.append(" input-dy:");
				message.append(input);
			} else {
				message.append(", dy:");
				message.append(String.format("%5.0f", change.y));
			}
			
		} else {
			message.append(" dx:");
			message.append(String.format("%5.0f", change.x));
			message.append(", dy:");
			message.append(String.format("%5.0f", change.y));
		}
		
		message.append(" (");
		message.append(String.format("%5.2f", hypot(change.x, change.y)));
		message.append(")");
		
		if (snapping) {
			
			message.append(" [Snapping]\t");
			
		} else {
			
			message.append(" [Shift for snapping]\t");
			
		}
		
		if ((verticalSnap instanceof OriginSnap) || (horizontalSnap instanceof OriginSnap)) {
			
			if (verticalSnap instanceof OriginSnap) {
				
				message.append(" [Snapped to x-axis]");
				
			}
			
			if (horizontalSnap instanceof OriginSnap) {
				
				message.append(" [Snapped to y-axis]");
				
			}
			
		} else {
			
			message.append(" [Middle Mouse for axis snap]");
			
		}
		
		return message.toString();
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {}
	
	@Override
	public void drawUnder(Graphics2D g) {
		
		if (horizontalSnap != null) {
			
			Driver.draw(horizontalSnap, g);
			
		}
		
		if (verticalSnap != null) {
			
			Driver.draw(verticalSnap, g);
			
		}
		
	}
	
	private void middleMouse(MouseEvent e) {
		
		double angle = atan2(from.x - getMousePosition().x, from.y - getMousePosition().y);
		double modded = abs(abs(angle / PI) - .5);
		
		if (modded > .25) { // vertical
			
			if (verticalSnap instanceof OriginSnap) {
				
				verticalSnap = null;
				input = "";
				
			}
			
			horizontalSnap = new OriginSnap(false, before.get(scene.selected.get(0)));
			
		} else { // horizontal
			
			if (horizontalSnap instanceof OriginSnap) {
				
				horizontalSnap = null;
				input = "";
				
			}
			
			verticalSnap = new OriginSnap(true, before.get(scene.selected.get(0)));
			
		}
		
	}
	
	private Snap<Vector2> getSnap(boolean isHorizontal, RailPoint active) {
		
		Builder<Snap<Vector2>> snapBuilder = Stream.builder();
		
		scene.railPoints.stream().filter((railPoint) -> railPoint != active && !scene.selected.contains(railPoint)).map((railPoint) -> {
			
			return new AlignSnap(isHorizontal, railPoint, active);
			
		}).forEach(snapBuilder::accept);
		
		scene.railPoints.stream().filter((railPoint) -> railPoint != active && !scene.selected.contains(railPoint)).map((railPoint1) -> {
			
			return scene.railPoints.stream().filter((railPoint) -> railPoint != active && !scene.selected.contains(railPoint)).map((railPoint2) -> {
				
				return new BetweenSnap(isHorizontal, railPoint1, railPoint2, active);
				
			}).min(Snap::sortByDistance).orElse(null);
			
		}).filter(Objects::nonNull).forEach(snapBuilder::accept);
		
		return snapBuilder.build().filter(GrabTool::withinDistance).filter(Snap::isValid).min(Snap::sortByDistance).orElse(null);
		
	}
	
	public static boolean withinDistance(Snap<Vector2> snap) {
		
		return snap.getDistance() < SNAP_DISTANCE;
		
	}
	
	@Override
	public void takeMessage(String input) {
		
		this.input = input;
		
		if (verticalSnap instanceof OriginSnap) {
			
			if (input.equals("")) {
				
				horizontalSnap = null;
				
			} else {
				
				horizontalSnap = new MessageSnap(true, input);
				change.x = horizontalSnap.getDestincation().x - before.get(active).x;
				
			}
			
		}
		
		if (horizontalSnap instanceof OriginSnap) {
			
			if (input.equals("")) {
				
				verticalSnap = null;
				
			} else {
				
				verticalSnap = new MessageSnap(false, input);
				change.y = verticalSnap.getDestincation().y - before.get(active).y;
				
			}
			
		}
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.getPosition().x = change.x + before.get(railPoint).x;
			railPoint.getPosition().y = change.y + before.get(railPoint).y;
			
		});
		
	}
	
	private static double getValue(String input) {
		
		try {
			
			return Double.valueOf(input);
			
		} catch (Exception e) {
			
			return 0;
			
		}
		
	}
	
}
