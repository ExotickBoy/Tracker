package tools;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import items.RailConnection;
import items.RailPoint;

public final class ConnectTool extends ActionTool {
	
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
		
		return scene.selected.size() == 2 && !scene.connections.stream().anyMatch((railConnection) -> {
			
			return railConnection.has(scene.selected.get(0)) && railConnection.has(scene.selected.get(1));
			
		});
		
	}
	
	@Override
	public Edit action() {
		
		RailPoint point1 = scene.selected.get(0);
		RailPoint point2 = scene.selected.get(1);
		
		connection = new RailConnection(point1, point2);
		
		scene.connections.add(connection);
		
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
	
}
