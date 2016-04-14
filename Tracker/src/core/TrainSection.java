package core;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import utils.Vector2;

public abstract class TrainSection implements Serializable, Drawable, OnRail,Selectable {
	
	private static final long serialVersionUID = 0L;
	
	public static final class Locomotive extends TrainSection {
		
		private static final long serialVersionUID = 0L;
		
		public Locomotive(RailLocation location) {
			
			super(location);
			
			setTexture(LOCOMOTIVE_TEXTURE);
			setName(LOCOMOTIVE_NAME);
			
		}
		
	}
	
	public static final class Wagon extends TrainSection {
		
		private static final long serialVersionUID = 0L;
		
		public Wagon(RailLocation location) {
			
			super(location);
			
			setTexture(WAGON_TEXTURE);
			setName(WAGON_NAME);
			setMaxAcceleratingForce(WAGON_MAX_ACCELERATING_FORCE);
			
		}
		
	}
	
	public static final int TRAIN_LENGTH = 60;
	public static final int TRAIN_WIDTH = 30;
	
	public static final String LOCOMOTIVE_NAME = "Locomotive";
	public static final BufferedImage LOCOMOTIVE_TEXTURE = getTexture("src/locomotive.png");
	
	public static final String WAGON_NAME = "Wagon";
	public static final double WAGON_MAX_ACCELERATING_FORCE = 0;
	public static final BufferedImage WAGON_TEXTURE = getTexture("src/wagon.png");
	
	public static final double DEFAULT_MASS = 40000;
	public static final double DEFAULT_MAX_ACCELERATING_FORCE = 240000;
	public static final double DEFAULT_MAX_BRAKINGFORCE = 80000;
	
	double mass = DEFAULT_MASS; // kg
	double maxAcceleratingForce = DEFAULT_MAX_ACCELERATING_FORCE; // N
	double maxBrakingForce = DEFAULT_MAX_BRAKINGFORCE; // N
	
	transient BufferedImage texture;
	RailLocation location;
	String name;
	
	Train train;
	
	private TrainSection(RailLocation location) {
		
		this.location = location;
		
	}
	
	protected void setMass(double mass) {
		
		this.mass = mass;
		
	}
	
	protected void setTexture(String path) {
		
		setTexture(getTexture(path));
		
	}

	protected void setTexture(BufferedImage texture) {
		
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
	
	public BufferedImage getTexture() {
		
		return texture;
		
	}
	
	public void setName(String name) {
		
		this.name = name;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public void draw(Graphics2D g) {
		
		Vector2 position = location.getVector();
		Vector2 direction = location.getDerivativeVector();
		double ang = atan2(direction.x, direction.y);
		
		AffineTransform affineTransform = new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(getTexture(), -TRAIN_WIDTH / 2, -TRAIN_LENGTH / 2, TRAIN_WIDTH, TRAIN_LENGTH, null);
		
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
	
	private static BufferedImage getTexture(String path) {
		
		try {
			
			return ImageIO.read(new File(path));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
			
		}
		
	}
	
}
