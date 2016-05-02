package items;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Graphics2D;
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
import utils.CubicBezierCurve;
import utils.Vector2;

public final class RailConnection extends CubicBezierCurve implements Serializable, Drawable, Selectable {
	
	private static final long serialVersionUID = -3448982061099628662L;
	
	private static final double TRACK_PEACE_SIZE = 25;
	private static final double BEZIER_ANCHOR_WEIGHT = .8;
	
	private static final double RESOLUTION_MULTIPLYER = .4;
	
	private static BufferedImage railImage;
	
	public RailPoint point1;
	public RailPoint point2;
	
	boolean p1Positive = false;
	boolean p2Positive = false;
	
	Vector2[] positions;
	double[] directions;
	
	static {
		
		try {
			
			railImage = ImageIO.read(new File("src/track.png"));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public RailConnection(RailPoint point1, RailPoint point2) {
		
		super();
		
		this.point1 = point1;
		this.point2 = point2;
		
		p0 = point1.getPosition();
		p3 = point2.getPosition();
		
		update();
		
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
			
			for (int i = 0; i < directions.length; i++) {
				
				Vector2 p = positions[i];
				double ang = directions[i];
				
				AffineTransform affineTransform = new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), p.x, p.y);
				AffineTransform innitialTransform = g.getTransform();
				g.transform(affineTransform);
				g.drawImage(railImage, (int) (-TRACK_PEACE_SIZE / 2), (int) (-TRACK_PEACE_SIZE / 2), (int) (TRACK_PEACE_SIZE), (int) (TRACK_PEACE_SIZE), null);
				g.setColor(Color.RED);
				// g.drawString("" + i, 15, 0);
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
		
		int segments = (int) (length / TRACK_PEACE_SIZE) + 1;
		double eachSegment = length / segments;
		
		directions = new double[segments + 1];
		positions = new Vector2[segments + 1];
		
		for (int i = 0; i < segments + 1; i++) {
			
			double t = getTAtDistanceFromStart(eachSegment * i);
			
			Vector2 dp = getDerivative(t);
			
			positions[i] = getPoint(t);
			directions[i] = atan2(dp.x, dp.y);
			
		}
		
	}
	
	public void updateTrains() {
		
		Driver.scene.trains.stream().filter((train) -> {
			
			return train.getLocation().connection == this;
			
		}).forEach(Train::recalculateSections);
		
	}
	
	public void updateInterpoints() {
		
		p0 = point1.getPosition();
		p3 = point2.getPosition();
		
		updateP1();
		updateP2();
		
	}
	
	private Vector2 updateP1() {
		
		Vector2 centre = Vector2.divide(Vector2.add(p0, p3), 2);
		
		Vector2 p1a = Vector2.add(getP0(), Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(getP3(), centre)), 0), point1.getDirection()));
		Vector2 p1b = Vector2.add(getP0(), Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(getP3(), centre)) * BEZIER_ANCHOR_WEIGHT, 0), point1.getDirection() + PI));
		
		p1Positive = Vector2.distance(p1a, centre) > Vector2.distance(p1b, centre);
		
		p1 = p1Positive ? p1b : p1a;
		
		return p1;
		
	}
	
	private Vector2 updateP2() {
		
		Vector2 centre = Vector2.divide(Vector2.add(p0, p3), 2);
		
		Vector2 p2a = Vector2.add(getP3(), Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(getP0(), centre)), 0), point2.getDirection()));
		Vector2 p2b = Vector2.add(getP3(), Vector2.rotate(new Vector2(Vector2.magnitude(Vector2.subtract(getP0(), centre)) * BEZIER_ANCHOR_WEIGHT, 0), point2.getDirection() + PI));
		
		p2Positive = Vector2.distance(p2a, centre) > Vector2.distance(p2b, centre);
		
		p2 = p2Positive ? p2b : p2a;
		
		return p2;
		
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
	
	@Override
	protected void updateLength() {
		
		setResolution((int) (hypot(p0.x - p3.x, p0.y - p3.y) * RESOLUTION_MULTIPLYER));
		
		super.updateLength();
		
	}
	
}
