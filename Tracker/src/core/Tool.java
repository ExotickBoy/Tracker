package core;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.KeyStroke;

import tools.ToolGroup;

public abstract class Tool implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
	
	private static final String DEFAULT_NAME = "Unnamed Tool";
	
	private KeyStroke keyStroke;
	private String name;
	private ToolGroup group;
	private Mode mode;
	
	private boolean isFromFollow; // TODO add this to edits
	
	protected static Scene scene;
	
	public Tool() {
		
		setName(DEFAULT_NAME);
		
	}
	
	public abstract boolean isActivatable();
	
	public abstract void onActivate();
	
	public abstract Edit onFinalise();
	
	public abstract void onAbort();
	
	public abstract String getMessage();
	
	public abstract void drawOver(Graphics2D g);
	
	public abstract void drawUnder(Graphics2D g);
	
	public abstract void takeMessage(String input);
	
	public final boolean hasFollowingTool() {
		
		return getFollowingTool() != null;
		
	}
	
	public Tool getFollowingTool() {
		
		return null;
		
	}
	
	public void setFromFollow(boolean isFromFollow) {
		
		this.isFromFollow = isFromFollow;
		
	}
	
	public boolean isFromFollow() {
		
		return isFromFollow;
		
	}
	
	public boolean willFinaliseOnEnter() {
		
		return true;
		
	}
	
	public boolean willAbortOnEscape() {
		
		return true;
		
	}
	
	public boolean isOverwritable() {
		
		return true;
		
	}
	
	public void finalise() {
		
		Driver.finaliseTool();
		
	}
	
	public void abort() {
		
		Driver.abortTool();
		
	}
	
	protected void setMode(Mode mode) {
		
		this.mode = mode;
		
	}
	
	public Mode getMode() {
		
		return mode;
		
	}
	
	public boolean isModeSpecific() {
		
		return mode != null;
		
	}
	
	protected void setKeyStroke(KeyStroke keyStroke) {
		
		this.keyStroke = keyStroke;
		
	}
	
	public KeyStroke getKeyStroke() {
		
		return keyStroke;
		
	}
	
	protected void setGroup(ToolGroup group) {
		
		this.group = group;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	protected void setName(String name) {
		
		this.name = name;
		
	}
	
	public ToolGroup getGroup() {
		
		return group;
		
	}
	
	public Point getMousePosition() {
		
		return Driver.viewPanel.getMousePosition();
		
	}
	
	public static void setScene(Scene scene) {
		
		Tool.scene = scene;
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
}
