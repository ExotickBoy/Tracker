package interfaces;

import items.RailLocation;

public interface OnRail extends Collidable {
	
	public void setRailLocation(RailLocation location);
	
	public RailLocation getRailLocation();
	
}
