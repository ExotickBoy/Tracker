package interfaces;

import utils.Collider;

public interface Collidable extends Drawable {
	
	public Collider getCollider();
	
	public default boolean collidesWith(Collidable other) {
		
		return this.getCollider().collidesWith(other.getCollider());
		
	}
	
	public boolean isByRail();
	
	public boolean willDrawCollider();
	
	public void setDrawCollider(boolean willDrawCollider);
	
}
