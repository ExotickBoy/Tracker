package core;

import javax.swing.JPanel;

public abstract class Tab extends JPanel {
	
	private static final long serialVersionUID = 474122277491505507L;
	
	protected static Scene scene;
	
	private String title;
	
	public Tab() {}
	
	public abstract void onSwitchedTo();
	
	public abstract void onSwitchedAway();
	
	public abstract void onToolActivate();
	
	public abstract void onToolFinalise();
	
	public abstract void onToolAbort();
	
	public abstract void onUndo();
	
	public abstract void onRedo();
	
	public String getTitle() {
		
		return title;
		
	}
	
	protected void setTitle(String title) {
		
		this.title = title;
		
	}
	
	protected static Scene getScene() {
		
		return scene;
		
	}
	
	public static void setScene(Scene scene) {
		
		Tab.scene = scene;
		
	}
	
}
