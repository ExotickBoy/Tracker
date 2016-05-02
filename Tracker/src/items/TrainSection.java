package items;

import static java.lang.Math.atan2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import utils.Collider;
import utils.Vector2;
import utils.Collider.Triangle;

public abstract class TrainSection implements Serializable, Drawable, OnRail, Selectable {
	
	private static final long serialVersionUID = 1L;
	
	public static final class Locomotive extends TrainSection {
		
		private static final long serialVersionUID = 0L;
		
		public Locomotive(RailLocation location) {
			
			super(location);
			
			setTexture(LOCOMOTIVE_TEXTURE_ID);
			setName(LOCOMOTIVE_NAME);
			
		}
		
	}
	
	public static final class Wagon extends TrainSection {
		
		private static final long serialVersionUID = 0L;
		
		public Wagon(RailLocation location) {
			
			super(location);
			
			setTexture(WAGON_TEXTURE_ID);
			setName(WAGON_NAME);
			setMaxAcceleratingForce(WAGON_MAX_ACCELERATING_FORCE);
			
		}
		
	}
	
	public static final double ALONG_TRACK_LENGTH_MULTIPLYER = .98;
	public static final int TRAIN_LENGTH = 60;
	public static final int TRAIN_WIDTH = 30;
	
	public static final String LOCOMOTIVE_NAME = "Locomotive";
	public static final BufferedImage LOCOMOTIVE_TEXTURE = getTexture("src/locomotive.png");
	public static final int LOCOMOTIVE_TEXTURE_ID = 0;
	
	public static final String WAGON_NAME = "Wagon";
	public static final double WAGON_MAX_ACCELERATING_FORCE = 0;
	public static final BufferedImage WAGON_TEXTURE = getTexture("src/wagon.png");
	public static final int WAGON_TEXTURE_ID = 1;
	
	public static final double DEFAULT_MASS = 40000;
	public static final double DEFAULT_MAX_ACCELERATING_FORCE = 240000;
	public static final double DEFAULT_MAX_BRAKINGFORCE = 80000;
	
	public static final BufferedImage[] TEXTURES = new BufferedImage[] { LOCOMOTIVE_TEXTURE, WAGON_TEXTURE };
	
	double mass = DEFAULT_MASS; // kg
	double maxAcceleratingForce = DEFAULT_MAX_ACCELERATING_FORCE; // N
	double maxBrakingForce = DEFAULT_MAX_BRAKINGFORCE; // N
	
	int texture;
	RailLocation location;
	String name;
	
	Train train;
	
	private TrainSection(RailLocation location) {
		
		setRailLocation(location);
		
	}
	
	protected void setMass(double mass) {
		
		this.mass = mass;
		
	}
	
	protected void setTexture(int texture) {
		
		this.texture = texture;
		
	}
	
	public double getMass() {
		
		return mass;
		
	}
	
	public void setMaxBrakingForce(double maxBrakingForce) {
		
		this.maxBrakingForce = maxBrakingForce;
		
	}
	
	public double getMaxBrakingForce() {
		
		return maxBrakingForce;
		
	}
	
	public void setMaxAcceleratingForce(double maxAcceleratingForce) {
		
		this.maxAcceleratingForce = maxAcceleratingForce;
		
	}
	
	public double getMaxAcceleratingForce() {
		
		return maxAcceleratingForce;
		
	}
	
	public int getTexture() {
		
		return texture;
		
	}
	
	public void setName(String name) {
		
		this.name = name;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public void draw(Graphics2D g) {
		
		AffineTransform affineTransform = getRailLocation().getRailPointTransform();
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(TEXTURES[texture], -TRAIN_WIDTH / 2, -TRAIN_LENGTH / 2, TRAIN_WIDTH, TRAIN_LENGTH, null);
		
		g.setTransform(innitialTransform);
		
	}
	
	@Override
	public RailLocation getRailLocation() {
		
		return location;
		
	}
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
	}
	
	@Override
	public String toString() {
		
		return getName();
		
	}
	
	@Override
	public Collider getCollider() {
		
		Vector2 position = getRailLocation().getPoint();
		Vector2 direction = getRailLocation().getDerivative();
		double ang = -atan2(direction.x, direction.y);
		
		Vector2 point1 = new Vector2(-TRAIN_WIDTH / 2, -TRAIN_LENGTH / 2);
		Vector2 point2 = new Vector2(TRAIN_WIDTH / 2, -TRAIN_LENGTH / 2);
		Vector2 point3 = new Vector2(TRAIN_WIDTH / 2, TRAIN_LENGTH / 2);
		Vector2 point4 = new Vector2(-TRAIN_WIDTH / 2, TRAIN_LENGTH / 2);
		
		point1 = Vector2.add(Vector2.rotate(point1, ang), position);
		point2 = Vector2.add(Vector2.rotate(point2, ang), position);
		point3 = Vector2.add(Vector2.rotate(point3, ang), position);
		point4 = Vector2.add(Vector2.rotate(point4, ang), position);
		
		ArrayList<Triangle> triangles = new ArrayList<>();
		triangles.add(new Triangle(point1, point2, point3));
		triangles.add(new Triangle(point3, point4, point1));
		
		return new Collider(triangles);
		
	}
	
	private static BufferedImage getTexture(String path) {
		
		try {
			
			return ImageIO.read(new File(path));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		}
		
	}
	
	@Override
	public boolean willDrawCollider() {
		
		return train.willDrawCollider();
		
	}
	
	@Override
	public void setDrawCollider(boolean willDrawCollider) {
		
		train.setDrawCollider(willDrawCollider);
		
	}
	
	@Override
	public boolean isByRail() {
		
		return false;
		
	}
	
}
