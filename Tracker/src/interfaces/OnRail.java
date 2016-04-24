package interfaces;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;

import items.RailLocation;
import utils.Vector2;

public interface OnRail extends Collidable {
	
	public void setRailLocation(RailLocation location);
	
	public RailLocation getRailLocation();
	
	public default AffineTransform getRailPointTransform() {
		
		Vector2 position = getRailLocation().getPoint();
		Vector2 direction = getRailLocation().getDerivative();
		double ang = atan2(direction.x, direction.y);
		
		return new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		
	}
	
}
