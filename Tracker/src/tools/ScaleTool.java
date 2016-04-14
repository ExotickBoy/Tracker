package tools;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.KeyStroke;

import core.Driver;
import core.Edit;
import core.Tool;

public final class ScaleTool extends Tool {
	
	private static final String TOOL_NAME = "Scale";
	private static final KeyStroke HOT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
	
	public ScaleTool() {
		
		setKeyStroke(HOT_KEY);
		setName(TOOL_NAME);
		setGroup(ToolGroup.TRANSFORMATION);
		setMode(Driver.RAILER_MODE);
		
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public boolean isActivatable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onActivate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Edit onFinalise() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onAbort() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void drawOver(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawUnder(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeMessage(String input) {
		// TODO Auto-generated method stub
		
	}
	
}
