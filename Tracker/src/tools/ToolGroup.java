package tools;

import javax.swing.KeyStroke;

public enum ToolGroup {
	
	TRANSFORMATION("Transformations", KeyStroke.getKeyStroke('q'), true), SPECIALS("Specials", KeyStroke.getKeyStroke('w'), true), //
	DELETE("Are you sure ?", KeyStroke.getKeyStroke('x'), false), PLACE("Place", KeyStroke.getKeyStroke('a'), true);
	
	private String name;
	private KeyStroke hotKey;
	private boolean willShow;
	
	private ToolGroup(String name, KeyStroke hotKey, boolean willShow) {
		
		this.name = name;
		this.hotKey = hotKey;
		this.willShow = willShow;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public KeyStroke getKeyStroke() {
		
		return hotKey;
		
	}
	
	public boolean willShow() {
		
		return willShow;
		
	}
	
}
