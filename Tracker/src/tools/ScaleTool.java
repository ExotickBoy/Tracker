package tools;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;

public final class ScaleTool extends Tool  {// TODO Auto-generated class stub
	
	private static final String TOOL_NAME = "Scale";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
	
	public ScaleTool() {
		
		setKeyStroke(HOT_KEY);
		setName(TOOL_NAME);
		setGroup(ToolGroup.TRANSFORMATION);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public boolean isActivatable() {
		
		return false;
		
	}
	
	@Override
	public void onActivate() {	}
	
	@Override
	public Edit onFinalise() {
		
		return null;
		
	}
	
	@Override
	public void onAbort() {}
	
	@Override
	public String getMessage() {
		
		return null;
		
	}
	
	@Override
	public void drawOver(Graphics2D g) {}
	
	@Override
	public void drawUnder(Graphics2D g) {}
	
	@Override
	public void takeMessage(String input) {}
	
}
