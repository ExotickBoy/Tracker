package modes;

import java.awt.Graphics2D;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import core.Drawable;
import core.Driver;
import core.Mode;

public class RailerMode extends Mode {
	
	private static final String MODE_NAME = "Railer Mode";
	
	public RailerMode() {
		
		setName(MODE_NAME);
		
	}
	
	@Override
	public void onSwitchedTo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSwitchedAway() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		Builder<Drawable> toDraw = Stream.builder();
		
		Driver.scene.railPoints.forEach(toDraw::accept);
		Driver.scene.connections.forEach(toDraw::accept);
		
		toDraw.build().forEach((drawable) -> {
			
			drawable.draw(g);
			
		});
		
	}
	
}
