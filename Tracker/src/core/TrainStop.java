package core;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import utils.Vector2;

public final class TrainStop implements Serializable, Drawable, OnRail, Selectable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_NAME = "Train Stop";
	
	private static final String TEXTURE_PATH = "src/train_stop.png";
	
	private static HashMap<TrainStop, String> trainStopToName = new HashMap<>();
	private static BufferedImage texture;
	
	RailLocation location;
	String name;
	
	static {
		
		try {
			
			texture = ImageIO.read(new File(TEXTURE_PATH));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public TrainStop(RailLocation location) {
		
		this.location = location;
		
	}
	
	public String getName() {
		
		if (name == null) {
			
			setName(DEFAULT_NAME);
			
		}
		
		return name;
		
	}
	
	public void setName(String name) {
		
		if (trainStopToName.entrySet().stream().filter((set) -> set.getKey() != this).map(Entry::getValue).anyMatch((value) -> name.equals(value))) {
			
			int count = 1;
			
			while (trainStopToName.containsValue(name + "." + String.format("%03d", count))) {
				
				count++;
				
			}
			
			if (count == 0) {
				
				trainStopToName.put(this, name);
				
			} else {
				
				trainStopToName.put(this, name + "." + String.format("%03d", count));
				
			}
			
		} else {
			
			trainStopToName.put(this, name);
			
		}
		
		this.name = trainStopToName.get(this);
		
	}
	
	@Override
	public String toString() {
		
		return getName();
		
	}
	
	public void draw(Graphics2D g) {
		
		Vector2 position = location.getVector();
		Vector2 direction = location.getDerivativeVector();
		double ang = atan2(direction.x, direction.y) - PI / 2;
		
		AffineTransform affineTransform = new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(texture, -RailConnection.TRACK_PEACE_SIZE / 2, RailConnection.TRACK_PEACE_SIZE / 2, RailConnection.TRACK_PEACE_SIZE, RailConnection.TRACK_PEACE_SIZE, null);
		
		g.setTransform(innitialTransform);
		
	}
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
	}
	
	@Override
	public RailLocation getRailLocation() {
		
		return location;
		
	}
	
}
