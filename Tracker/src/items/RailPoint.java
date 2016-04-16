package items;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import core.Driver;
import interfaces.Drawable;
import interfaces.Selectable;
import utils.Vector2;

public final class RailPoint implements Serializable, Drawable, Selectable {
	
	private static final String DEFAULT_NAME = "RailPoint";
	
	private static final long serialVersionUID = 1L;
	
	private static final double POINT_RADIUS = 12.5;
	private static final Color ACTIVE_POINT_COLOR = Color.RED;
	private static final Color SELECTED_POINT_COLOR = Color.ORANGE;
	private static final Color UNSELECTED_POINT_COLOR = Color.BLACK;
	
	private static HashMap<RailPoint, String> railPointToName = new HashMap<>();
	
	private Vector2 position = new Vector2();
	private double direction = 0;
	
	private String name;
	
	public RailPoint() {
	
	}
	
	public RailPoint(Vector2 position) {
		
		this.position = position;
		
	}
	
	public void draw(Graphics2D g) {
		
		if (Driver.scene.selected.contains(this)) {
			
			if (Driver.scene.selected.get(0) == this) {
				
				g.setColor(ACTIVE_POINT_COLOR);
				
			} else {
				
				g.setColor(SELECTED_POINT_COLOR);
				
			}
			
		} else {
			
			g.setColor(UNSELECTED_POINT_COLOR);
			
		}
		
		g.draw(new Line2D.Double(position.x + cos(direction - PI / 2) * POINT_RADIUS, position.y + sin(direction - PI / 2) * POINT_RADIUS, //
				position.x - cos(direction - PI / 2) * POINT_RADIUS, position.y - sin(direction - PI / 2) * POINT_RADIUS));
				
	}
	
	public void setDirection(double direction) {
		
		this.direction = direction;
		
	}
	
	public double getDirection() {
		
		return direction;
		
	}
	
	public Vector2 getPosition() {
		
		return position;
		
	}
	
	public void setPosition(Vector2 position) {
		
		this.position = position;
		
	}
	
	public String getName() {
		
		if (name == null) {
			
			setName(DEFAULT_NAME);
			
		}
		
		return name;
		
	}
	
	public void setName(String name) {
		
		if (railPointToName.entrySet().stream().filter((set) -> set.getKey() != this).map(Entry::getValue).anyMatch((value) -> name.equals(value))) {
			
			int count = 1;
			
			while (railPointToName.containsValue(name + "." + String.format("%03d", count))) {
				
				count++;
				
			}
			
			if (count == 0) {
				
				railPointToName.put(this, name);
				
			} else {
				
				railPointToName.put(this, name + "." + String.format("%03d", count));
				
			}
			
		} else {
			
			railPointToName.put(this, name);
			
		}
		
		this.name = railPointToName.get(this);
		
	}
	
	public void updateConnections() {
		
		Driver.scene.connections.stream().filter(connection -> connection.has(this)).forEach(RailConnection::update);;
		
	}
	
	@Override
	public String toString() {
		
		return getName();
		
	}
	
	public static int sortAlphabetically(RailPoint a, RailPoint b) {
		
		return a.name.compareTo(b.name);
		
	}
	
}
