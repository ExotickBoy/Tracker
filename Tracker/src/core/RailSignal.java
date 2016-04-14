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

import javax.imageio.ImageIO;

import utils.Vector2;

public class RailSignal implements Serializable, Drawable, OnRail,Selectable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String RED_SIGNAL_TEXTURE_PATH = "src/signal_red.png";
	private static final String AMBER_SIGNAL_TEXTURE_PATH = "src/signal_amber.png";
	private static final String GREEN_SIGNAL_TEXTURE_PATH = "src/signal_green.png";
	
	private static final int RED_COLOR = 0;
	private static final int AMBER_COLOR = 1;
	private static final int GREEN_COLOR = 2;
	
	private transient static BufferedImage redSignalTexture;
	private transient static BufferedImage amberSignalTexture;
	private transient static BufferedImage greenSignalTexture;
	
	private static HashMap<Integer, BufferedImage> colorToImage = new HashMap<>();
	
	RailLocation location;
	boolean decided;
	int color;
	
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
		
		Vector2 position = location.getVector();
		Vector2 direction = location.getDerivativeVector();
		double ang = atan2(direction.x, direction.y) - PI / 2;
		
		AffineTransform affineTransform = new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		g.drawImage(colorToImage.get(color), -RailConnection.TRACK_PEACE_SIZE / 2, RailConnection.TRACK_PEACE_SIZE / 2, RailConnection.TRACK_PEACE_SIZE / 2,
				RailConnection.TRACK_PEACE_SIZE, null);
				
		g.setTransform(innitialTransform);
		
	}
	
}
