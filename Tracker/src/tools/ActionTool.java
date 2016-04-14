package tools;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import core.Edit;
import core.Tool;

public abstract class ActionTool extends Tool {
	
	public ActionTool() {
	
	}
	
	public abstract Edit action();
	
	@Override
	public final void onActivate() {
		
		finalise();
		
	}
	
	@Override
	public final Edit onFinalise() {
		
		return action();
		
	}
	
	@Override
	public final void onAbort() {}
	
	@Override
	public final void drawOver(Graphics2D g) {}
	
	@Override
	public final void drawUnder(Graphics2D g) {}
	
	@Override
	public final String getMessage() {
		
		return null;
		
	};
	
	@Override
	public final boolean isOverwritable() {
		
		return false;
		
	}
	
	@Override
	public final boolean willAbortOnEscape() {
		
		return false;
		
	}
	
	@Override
	public final boolean willFinaliseOnEnter() {
		
		return false;
		
	}
	
	@Override
	public final void takeMessage(String input) {}
	
	@Override
	public final void mouseDragged(MouseEvent e) {}
	
	@Override
	public final void mouseMoved(MouseEvent e) {}
	
	@Override
	public final void mouseClicked(MouseEvent e) {}
	
	@Override
	public final void mouseEntered(MouseEvent e) {}
	
	@Override
	public final void mouseExited(MouseEvent e) {}
	
	@Override
	public final void mousePressed(MouseEvent e) {}
	
	@Override
	public final void mouseReleased(MouseEvent e) {}
	
	@Override
	public final void mouseWheelMoved(MouseWheelEvent e) {}
	
	@Override
	public final void keyPressed(KeyEvent e) {}
	
	@Override
	public final void keyReleased(KeyEvent e) {}
	
	@Override
	public final void keyTyped(KeyEvent e) {}
	
}
