package items;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static utils.BezierCurves.cubicBezier;
import static utils.BezierCurves.cubicBezierArcLength;
import static utils.BezierCurves.cubicBezierDerivative;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import core.Driver;
import interfaces.Drawable;
import interfaces.Selectable;
import utils.Vector2;

public final class RailConnection implements Serializable, Drawable, Selectable {
	
	private static final long serialVersionUID = -3448982061099628662L;
	
	private static final double TRACK_PEACE_SIZE = 25;
	
	private static final int ARC_LENGTH_CALCULATION_RESOLUTION = 50;
	private static final double BEZIER_ANCHOR_WEIGHT = .8;
	private static final double RAIL_AMOUNT_MULTIPLYER = 1.55;
	
	private static BufferedImage railImage;
	
	public RailPoint point1;
	public RailPoint point2;
	
	boolean p1Positive = false;
	boolean p2Positive = false;
	
	public Vector2 p0;
	public Vector2 p1;
	public Vector2 p2;
	public Vector2 p3;
	
	public double length;
	
	public int dank;
	
	static {
		
		try {
			
			railImage = ImageIO.read(new File("src/track.png"));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public RailConnection(RailPoint point1, RailPoint point2) {
		
		this.point1 = point1;
		this.point2 = point2;
		
		p0 = point1.getPosition();
		p3 = point2.getPosition();
		
		updateInterpoints();
		
	}
	
	public RailPoint getOther(RailPoint point) {
		
		if (point != point1 && point != point2) {
			
			return null;
			
		} else if (point == point1) {
			
			return point2;
			
		} else {
			
			return point1;
			
		}
		
	}
	
	public boolean has(RailPoint point) {
		
		return point1 == point || point2 == point;
		
	}
	
	public boolean isConnectionBetween(RailPoint a, RailPoint b) {
		
		return (a == point1 && b == point2) || (b == point1 && a == point2);
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		if (railImage != null) {
			
			updateInterpoints();
			
			int segments = (int) (length * RAIL_AMOUNT_MULTIPLYER / TRACK_PEACE_SIZE) + 1;
			
			for (int i = 0; i <= segments; i++) {
				
				double t = i / (double) segments;
				// t = sin(t * (PI ));
				
				double dx = cubicBezierDerivative(p0.x, p1.x, p2.x, p3.x, t);
				double dy = cubicBezierDerivative(p0.y, p1.y, p2.y, p3.y, t);
				
				double x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t);
				double y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t);
				
				double ang = atan2(dx, dy);
				
				AffineTransform affineTransform = new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), x, y);
				AffineTransform innitialTransform = g.getTransform();
				g.transform(affineTransform);
				
				g.drawImage(railImage, (int) (-TRACK_PEACE_SIZE / 2), (int) (-TRACK_PEACE_SIZE / 2), (int) (TRACK_PEACE_SIZE), (int) (TRACK_PEACE_SIZE), null);
				
				if (dank != 0) {
					
					if (dank == 3) {
						
						g.setColor(Color.GREEN);
						
					} else if (dank == 1) {
						
						g.setColor(Color.RED);
						
					} else if (dank == 2) {
						
						g.setColor(Color.YELLOW);
						
					}
					g.fill(new Rectangle(-10, -10, 20, 20));
					
				}
				
				g.setTransform(innitialTransform);
				
			}
			
		} else {
			
			g.draw(new Line2D.Double(point1.getPosition().x, point1.getPosition().y, point2.getPosition().x, point2.getPosition().y));
			
		}
		
	}
	
	public void update() {
		
		updateInterpoints();
		updateLength();
		updateTrains();
		
	}
	
	public void updateTrains() {
		
		Driver.scene.trains.stream().filter((train) -> {
			
			return train.getLocation().connection == this;
			
		}).forEach(Train::recalculateSections);
		
	}
	
	public void updateLength() {
		
		length = cubicBezierArcLength(p0, p1, p2, p3, ARC_LENGTH_CALCULATION_RESOLUTION);
		
	}
	
	public double getLength() {
		
		return length;
		
	}
	
	public void updateInterpoints() {
		
		p0 = point1.getPosition();
		p3 = point2.getPosition();
		
		Vector2 centre = Vector2.divide(Vector2.add(p0, p3), 2);
		
		Vector2 p1a = Vector2.add(p0, Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(p3, centre)), 0), point1.getDirection()));
		Vector2 p2a = Vector2.add(p3, Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(p0, centre)), 0), point2.getDirection()));
		
		Vector2 p1b = Vector2.add(p0, Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(p3, centre)) * BEZIER_ANCHOR_WEIGHT, 0), point1.getDirection() + PI));
		Vector2 p2b = Vector2.add(p3, Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(p0, centre)) * BEZIER_ANCHOR_WEIGHT, 0), point2.getDirection() + PI));
		
		p1Positive = Vector2.distance(p1a, centre) > Vector2.distance(p1b, centre);
		p2Positive = Vector2.distance(p2a, centre) > Vector2.distance(p2b, centre);
		
		p1 = p1Positive ? p1b : p1a;
		p2 = p2Positive ? p2b : p2a;
		
		updateLength();
		
	}
	
	public ArrayList<RailConnection> getConnections() {
		
		return Driver.scene.connections.stream().filter((connection) -> connection.has(point1) ^ connection.has(point2)).collect(Collectors.toCollection(ArrayList::new));
		
	}
	
	public boolean isSameDirection(RailConnection other) {
		
		return point1 != other.point1 && point2 != other.point2;
		
	}
	
	public boolean canPass(RailConnection other) {
		
		RailPoint shared = other.has(point1) ? point1 : point2;
		
		return (shared == point1 ? p1Positive : p2Positive) != (shared == other.point1 ? other.p1Positive : other.p2Positive);
		
	}
	
}
