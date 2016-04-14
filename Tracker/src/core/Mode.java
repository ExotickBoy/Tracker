package core;

import java.awt.Graphics2D;

public abstract class Mode {
	
	private String name;
	
	public Mode() {
	
	}
	
	public abstract void onSwitchedTo();
	
	public abstract void onSwitchedAway();
	
	public abstract void draw(Graphics2D g);
	
	protected void setName(String name) {
		
		this.name = name;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	@Override
	public String toString() {
		
		return getName();
		
	}
	
}
