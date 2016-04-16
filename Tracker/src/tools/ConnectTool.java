package tools;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;
import items.RailConnection;
import items.RailPoint;

public final class ConnectTool extends Tool {
	
	private static final String TOOL_NAME = "Connect";
	private static final KeyStroke KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F, 0);
	
	RailConnection connection;
	
	public ConnectTool() {
		
		setName(TOOL_NAME);
		setKeyStroke(KEY_STROKE);
		setGroup(ToolGroup.SPECIALS);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return scene.selected.size() == 2;
		
	}
	
	@Override
	public void onActivate() {
		
		RailPoint point1 = scene.selected.get(0);
		RailPoint point2 = scene.selected.get(1);
		
		connection = new RailConnection(point1, point2);
		
		boolean exists = scene.connections.stream().anyMatch((railConnection) -> {
			
			return railConnection.has(point1) && railConnection.has(point2);
			
		});
		
		if (!exists) {
			
			scene.connections.add(connection);
			finalise();
			
		} else {
			
			abort();
			
		}
		
	}
	
	@Override
	public Edit onFinalise() {
		
		return new Edit() {
			
			RailConnection connection = ConnectTool.this.connection;
			
			@Override
			public void undo() {
				
				scene.connections.remove(connection);
				
			}
			
			@Override
			public void redo() {
				
				scene.connections.add(connection);
				
			}
			
		};
		
	}
	
	@Override
	public void onAbort() {}
	
	@Override
	public void drawOver(Graphics2D g) {}
	
	@Override
	public void drawUnder(Graphics2D g) {}
	
	@Override
	public String getMessage() {
		
		return null;
		
	}
	
	@Override
	public void takeMessage(String input) {}
	
}
