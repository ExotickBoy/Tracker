package modes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.swing.SwingUtilities;

import components.TabsPanel;
import core.Driver;
import core.Mode;
import interfaces.Drawable;

public class RunningMode extends Mode {
	
	private static final String MODE_NAME = "Running Mode";
	
	long sinceFpsUpdate;
	int count;
	int fps;
	
	public RunningMode() {
		
		setName(MODE_NAME);
		
	}
	
	@Override
	public void onSwitchedTo() {
		
		sinceFpsUpdate = System.currentTimeMillis();
		count = 0;
		
	}
	
	@Override
	public void onSwitchedAway() {
	
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
			
			Driver.draw(drawable, g);
			
		});
		
		g.setColor(Color.RED);
		g.drawString("FPS:" + fps, 0, 11);
				
		count++;
		long now = System.currentTimeMillis();
		
		if (sinceFpsUpdate + 1000 < now) {
			
			sinceFpsUpdate += 1000;
			fps = count;
			count = 0;
			
		}
		
		Driver.scene.trains.forEach(train->{
			
			train.updateLocation(now);
			TabsPanel.TRAIN_TAB.update();
			
		});
		
		SwingUtilities.invokeLater(() -> {
			
			Driver.viewPanel.repaint();
			
		});
		
	}
	
}
