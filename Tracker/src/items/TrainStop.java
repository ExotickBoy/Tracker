package items;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import utils.Collider;
import utils.Vector2;
import utils.Collider.Triangle;

public final class TrainStop implements Serializable, Drawable, OnRail, Selectable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_NAME = "Train Stop";
	
	private static final double STOP_LENGTH = 25;
	private static final double STOP_WIDTH = 25;
	private static final double STOP_OFFSET = 13;
	
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
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
	}
	
	@Override
	public RailLocation getRailLocation() {
		
		return location;
		
	}
	
	public void draw(Graphics2D g) {
		
		AffineTransform affineTransform = getRailPointTransform();
		affineTransform.rotate(PI / 2);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(texture, (int) (-STOP_WIDTH / 2), (int) (STOP_OFFSET), (int) STOP_WIDTH, (int) STOP_LENGTH, null);
		
		g.setTransform(innitialTransform);
		
	}
	
	@Override
	public Collider getCollider() {
		
		Vector2 position = getRailLocation().getVector();
		Vector2 direction = getRailLocation().getDerivativeVector();
		double ang = PI / 2 - atan2(direction.x, direction.y);
		
		Vector2 point1 = new Vector2(-STOP_WIDTH / 2, STOP_OFFSET);
		Vector2 point2 = new Vector2(STOP_WIDTH / 2, STOP_OFFSET);
		Vector2 point3 = new Vector2(STOP_WIDTH / 2, STOP_OFFSET + STOP_LENGTH);
		Vector2 point4 = new Vector2(-STOP_WIDTH / 2, STOP_OFFSET + STOP_LENGTH);
		
		point1 = Vector2.add(Vector2.rotate(point1, ang), position);
		point2 = Vector2.add(Vector2.rotate(point2, ang), position);
		point3 = Vector2.add(Vector2.rotate(point3, ang), position);
		point4 = Vector2.add(Vector2.rotate(point4, ang), position);
		
		ArrayList<Triangle> triangles = new ArrayList<>();
		triangles.add(new Triangle(point1, point2, point3));
		triangles.add(new Triangle(point3, point4, point1));
		
		return new Collider(triangles);
		
	}
	
	@Override
	public boolean willDrawCollider() {
		
		return true;
		
	}
	
}
