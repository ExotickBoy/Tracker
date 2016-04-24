package tools;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
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

public final class ScaleTool extends Tool {// TODO Auto-generated class stub
	
	private class MessageSnap extends Snap<Double> {
		
		public MessageSnap(boolean isHorizontal, String input) {
			
			setDestincation(getValue(input));
			setDistance(0);
			
		}
		
		@Override
		public void draw(Graphics2D g) {}
		
		@Override
		public boolean isPermanent() {
			
			return true;
			
		}
		
	}
	
	private class OriginSnap extends utils.Snap<Double> {
		
		boolean isHorizontal;
		
		public OriginSnap(boolean isHorizontal) {
			
			// super(isHorizontal ? new Vector2(0, centre.y) : new Vector2(centre.x, 0), 0d);
			
			this.isHorizontal = isHorizontal;
			
			setDestincation(1d);
			setDistance(0);
			
		}
		
		@Override
		public void draw(Graphics2D g) {
			
			if (isHorizontal) {
				
				g.setColor(ORIGIN_SNAP_X_AXIS_COLOR);
				g.draw(new Line2D.Double(0, activeStart.y, Driver.frame.getWidth(), activeStart.y));
				
			} else {
				
				g.setColor(ORIGIN_SNAP_Y_AXIS_COLOR);
				g.draw(new Line2D.Double(activeStart.x, 0, activeStart.x, Driver.frame.getHeight()));
				
			}
			
		}
		
		@Override
		public boolean isPermanent() {
			
			return true;
			
		}
		
	}
	
	private static final Color ORIGIN_SNAP_X_AXIS_COLOR = Color.RED;
	private static final Color ORIGIN_SNAP_Y_AXIS_COLOR = Color.GREEN;
	
	private static final String TOOL_NAME = "Scale";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
	private static final double SNAP_DISTANCE = 10;
	
	private HashMap<RailPoint, Vector2> locationsBefore = new HashMap<>();
	
	private Vector2 mouseStart;
	private Vector2 mouseNow;
	private Vector2 activeStart;
	
	private String input = "";
	private Vector2 scale;
	
	private boolean snapping;
	
	private Snap<Double> verticalSnap;
	private Snap<Double> horizontalSnap;
	
	public ScaleTool() {
		
		setKeyStroke(HOT_KEY);
		setName(TOOL_NAME);
		setGroup(ToolGroup.TRANSFORMATION);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (e.getButton() == 2) {
			
			double angle = atan2(mouseStart.x - getMousePosition().x, mouseStart.y - getMousePosition().y);
			double modded = abs(abs(angle / PI) - .5);
			
			if (modded < .25) { // vertical
				
				if (verticalSnap instanceof OriginSnap) {
					
					verticalSnap = null;
					input = "";
					
				}
				
				horizontalSnap = new OriginSnap(false);
				
			} else { // horizontal
				
				if (horizontalSnap instanceof OriginSnap) {
					
					horizontalSnap = null;
					input = "";
					
				}
				
				verticalSnap = new OriginSnap(true);
				
			}
			
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		mouseNow = new Vector2(e.getX(), e.getY());
		
		scale = new Vector2(Vector2.magnitude(Vector2.subtract(mouseNow, activeStart)) / Vector2.magnitude(Vector2.subtract(mouseStart, activeStart)));
		
		if (distanceBetweenTwoAngles(Vector2.atan2(Vector2.subtract(mouseNow, activeStart)), Vector2.atan2(Vector2.subtract(mouseNow, mouseNow))) > PI / 2) {
			
			scale = Vector2.multiply(scale, -1);
			
		}
		
		snapping = e.isShiftDown();
		
		if (snapping) {
			
			if (horizontalSnap == null || !horizontalSnap.isPermanent()) {
				
				horizontalSnap = getSnap(true);
				
			}
			
			if (verticalSnap == null || !verticalSnap.isPermanent()) {
				
				verticalSnap = getSnap(false);
				
			}
			
		}
		
		if (horizontalSnap != null) {
			
			scale.x = horizontalSnap.getDestincation();
			
		}
		
		if (verticalSnap != null) {
			
			scale.y = verticalSnap.getDestincation();
			
		}
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.setPosition(Vector2.add(Vector2.multiply(Vector2.subtract(locationsBefore.get(railPoint), activeStart), scale), activeStart));
			
		});
		
		updateConnections();
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		mouseMoved(e);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return scene.selected.size() > 1 && getMousePosition() != null;
		
	}
	
	@Override
	public void onActivate() {
		
		horizontalSnap = null;
		verticalSnap = null;
		
		mouseStart = new Vector2(getMousePosition().x, getMousePosition().y);
		scale = new Vector2(1);
		activeStart = scene.selected.get(0).getPosition();
		
		locationsBefore.clear();
		scene.selected.forEach((railPoint) -> {
			
			locationsBefore.put(railPoint, new Vector2(railPoint.getPosition()));
			
		});
		
	}
	
	@Override
	public Edit onFinalise() {
		
		return null;
		
	}
	
	@Override
	public void onAbort() {}
	
	@Override
	public String getMessage() {
		
		StringBuilder message = new StringBuilder();
		if (!input.equals("")) {
			
			if (verticalSnap instanceof OriginSnap) {
				message.append(" input-sx:");
				message.append(input);
			} else {
				message.append(" sx:");
				message.append(String.format("%2.3f", scale.x));
			}
			
			if (horizontalSnap instanceof OriginSnap) {
				message.append(" input-sy:");
				message.append(input);
			} else {
				message.append(", sy:");
				message.append(String.format("%2.3f", scale.y));
			}
			
		} else if (scale.x == scale.y) {
			
			message.append(" s:");
			message.append(String.format("%2.3f", scale.x));
			
		} else {
			
			message.append(" sx:");
			message.append(String.format("%2.3f", scale.x));
			message.append(" sy:");
			message.append(String.format("%2.3f", scale.y));
			
		}
		
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
	public void drawOver(Graphics2D g) {
		
		if (mouseNow != null && mouseStart != null) {
			
			g.draw(new Line2D.Double(activeStart.x, activeStart.y, mouseNow.x, mouseNow.y));
			
		}
		
	}
	
	@Override
	public void drawUnder(Graphics2D g) {
		
		if (horizontalSnap != null) {
			
			horizontalSnap.draw(g);
			
		}
		
		if (verticalSnap != null) {
			
			verticalSnap.draw(g);
			
		}
		
	}
	
	@Override
	public void takeMessage(String input) {
		
		this.input = input;
		
		if (verticalSnap instanceof OriginSnap) {
			
			if (input.equals("")) {
				
				horizontalSnap = null;
				
			} else {
				
				horizontalSnap = new MessageSnap(true, input);
				scale.x = horizontalSnap.getDestincation();
				
			}
			
		}
		
		if (horizontalSnap instanceof OriginSnap) {
			
			if (input.equals("")) {
				
				verticalSnap = null;
				
			} else {
				
				verticalSnap = new MessageSnap(false, input);
				scale.y = verticalSnap.getDestincation();
				
			}
			
		}
		
		scene.selected.forEach((railPoint) -> {
			
			railPoint.setPosition(Vector2.add(Vector2.multiply(Vector2.subtract(locationsBefore.get(railPoint), activeStart), scale), activeStart));
			
		});
		
		updateConnections();
		
	}
	
	private Snap<Double> getSnap(boolean isHorizontal) {
		
		Builder<Snap<Double>> builder = Stream.builder();
		
		return builder.build().filter(ScaleTool::withinDistance).filter(Snap::isValid).min(Snap::sortByDistance).orElse(null);
		
	}
	
	private void updateConnections() {
		
		scene.connections.stream().filter((connection) -> {
			
			return scene.railPoints.stream().anyMatch((railPoint) -> {
				
				return connection.has(railPoint);
				
			});
			
		}).forEach(RailConnection::update);
		
	}
	
	private double distanceBetweenTwoAngles(double a, double b) {
		
		return abs(PI - abs(abs(a - b) - PI));
		
	}
	
	private static boolean withinDistance(Snap<Double> snap) {
		
		return snap.getDistance() < SNAP_DISTANCE;
		
	}
	
	private static double getValue(String input) {
		
		try {
			
			return Double.valueOf(input);
			
		} catch (Exception e) {
			
			return 0;
			
		}
		
	}
	
}
