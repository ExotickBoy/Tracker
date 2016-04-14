package modes;

import java.awt.Graphics2D;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import core.Drawable;
import core.Driver;
import core.Mode;
import core.Train;

public class TrainMode extends Mode {
	
	private static final String MODE_NAME = "Train Mode";
	
	public TrainMode() {
		
		setName(MODE_NAME);
		
	}
	
	@Override
	public void onSwitchedTo() {
		
		Driver.selectTool(Driver.TRAIN_TOOL);
		Driver.scene.trains.forEach(Train::recalculateSections);
		
	}
	
	@Override
	public void onSwitchedAway() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		Builder<Drawable> toDraw = Stream.builder();
		
		Driver.scene.railPoints.stream().filter((railPoint) -> {
			
			return !Driver.scene.connections.stream().anyMatch((connection) -> {
				
				return connection.has(railPoint);
				
			});
			
		}).forEach(toDraw::accept);
		Driver.scene.connections.forEach(toDraw::accept);
		Driver.scene.trains.forEach(toDraw::accept);
		Driver.scene.trainStops.forEach(toDraw::accept);
		Driver.scene.railSignals.forEach(toDraw::accept);
		
		toDraw.build().forEach((drawable) -> {
			
			drawable.draw(g);
			
		});
		
	}
}
