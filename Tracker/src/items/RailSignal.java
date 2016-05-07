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

import javax.imageio.ImageIO;

import core.Driver;
import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import utils.Collider;
import utils.Vector2;
import utils.Collider.Triangle;

public class RailSignal implements Serializable, Drawable, OnRail, Selectable {
	
	private static final int LENGTH_OF_COLOR_CYCLE = 1500; // ms
	
	private static final long serialVersionUID = 1L;
	
	private static final String RED_SIGNAL_TEXTURE_PATH = "src/signal_red.png";
	private static final String AMBER_SIGNAL_TEXTURE_PATH = "src/signal_amber.png";
	private static final String GREEN_SIGNAL_TEXTURE_PATH = "src/signal_green.png";
	
	private static final int RED_COLOR = 0;
	private static final int AMBER_COLOR = 1;
	private static final int GREEN_COLOR = 2;
	
	private static final double SIGNAL_LENGTH = 25;
	private static final double SIGNAL_WIDTH = 12.5;
	private static final double SIGNAL_OFFSET = 13;
	
	private transient static BufferedImage redSignalTexture;
	private transient static BufferedImage amberSignalTexture;
	private transient static BufferedImage greenSignalTexture;
	
	private static HashMap<Integer, BufferedImage> colorToImage = new HashMap<>();
	
	private RailLocation location;
	private boolean willDrawCollider;
	private boolean decided;
	private int color;
	
	private static long startTime;
	
	static {
		
		try {
			
			redSignalTexture = ImageIO.read(new File(RED_SIGNAL_TEXTURE_PATH));
			amberSignalTexture = ImageIO.read(new File(AMBER_SIGNAL_TEXTURE_PATH));
			greenSignalTexture = ImageIO.read(new File(GREEN_SIGNAL_TEXTURE_PATH));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		colorToImage.put(RED_COLOR, redSignalTexture);
		colorToImage.put(AMBER_COLOR, amberSignalTexture);
		colorToImage.put(GREEN_COLOR, greenSignalTexture);
		
	}
	
	public RailSignal(RailLocation location) {
		
		this.location = location;
		decided = false;
		color = RED_COLOR;
		
		if (startTime != 0) {
			
			startTime = System.currentTimeMillis();
			
		}
		
	}
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
	}
	
	@Override
	public RailLocation getRailLocation() {
		
		return location;
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		if (!decided && Driver.mode == Driver.RUNNING_MODE) {
			
			color = (int) (3 * ((System.currentTimeMillis() - startTime) % LENGTH_OF_COLOR_CYCLE) / LENGTH_OF_COLOR_CYCLE);
			
		}
		
		AffineTransform affineTransform = getRailLocation().getRailPointTransform();
		affineTransform.rotate(PI / 2);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(colorToImage.get(color), (int) (-SIGNAL_WIDTH / 2), (int) (SIGNAL_OFFSET), (int) SIGNAL_WIDTH, (int) SIGNAL_LENGTH, null);
		
		g.setTransform(innitialTransform);
		
	}
	
	@Override
	public Collider getCollider() {
		
		Vector2 position = getRailLocation().getPoint();
		Vector2 direction = getRailLocation().getDerivative();
		double ang = PI / 2 - atan2(direction.x, direction.y);
		
		Vector2 point1 = new Vector2(-SIGNAL_WIDTH / 2, SIGNAL_OFFSET);
		Vector2 point2 = new Vector2(SIGNAL_WIDTH / 2, SIGNAL_OFFSET);
		Vector2 point3 = new Vector2(SIGNAL_WIDTH / 2, SIGNAL_OFFSET + SIGNAL_LENGTH);
		Vector2 point4 = new Vector2(-SIGNAL_WIDTH / 2, SIGNAL_OFFSET + SIGNAL_LENGTH);
		
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
		
		return willDrawCollider;
		
	}
	
	@Override
	public void setDrawCollider(boolean willDrawCollider) {
		
		this.willDrawCollider = willDrawCollider;
		
	}
	
	@Override
	public boolean isByRail() {
		
		return true;
		
	}
	
}
