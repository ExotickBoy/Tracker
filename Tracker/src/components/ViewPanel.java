package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import core.Driver;
import core.Tool;

public final class ViewPanel extends JPanel implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1685718003872213732L;
	
	private boolean willIgnore = false;
	
	public ViewPanel() {
		
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		registerKeyboardAction((e) -> {
			
			if (Driver.tool != null && Driver.tool.willAbortOnEscape()) {
				
				Driver.abortTool();
				
			}
			
		} , KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
		
		registerKeyboardAction((e) -> {
			
			if (Driver.tool != null && Driver.tool.willFinaliseOnEnter()) {
				
				Driver.finaliseTool();
				
			}
			
		} , KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
		
		registerKeyboardAction((e) -> {
			
			Driver.cycleMode();
			
		} , KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_FOCUSED);
		
		Driver.tools.stream().map(Tool::getGroup).filter(Objects::nonNull).distinct().forEach((group) -> {
			
			new ToolGroupPopup(Driver.tools, this, group);
			
		});
		
		Driver.tools.forEach((tool) -> {
			
			registerKeyboardAction((e) -> {
				
				if (!tool.isModeSpecific() || Driver.mode == tool.getMode()) {
					
					Driver.selectTool(tool);
					
					willIgnore = true;
					
				}
				
			} , tool.getKeyStroke(), JComponent.WHEN_FOCUSED);
			
		});
		
		setPreferredSize(new Dimension(Driver.WINDOW_WIDTH, Driver.WINDOW_HEIGHT));
		
	}
	
	@Override
	protected void paintComponent(Graphics g2) {
		
		super.paintComponent(g2);
		
		Graphics2D g = (Graphics2D) g2;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if (Driver.tool != null) {
			
			Driver.tool.drawUnder(g);
			
		}
		
		Driver.mode.draw(g);
		
		if (Driver.tool != null) {
			
			Driver.tool.drawOver(g);
			
			g.setColor(Color.BLACK);
			g.drawString("(" + Driver.tool.getName() + ")" + Driver.tool.getMessage(), Driver.TOOL_MESSAGE_MARGIN, getHeight() - Driver.TOOL_MESSAGE_MARGIN);
			
		} else if (!Driver.scene.selected.isEmpty()) {
			
			g.setColor(Color.BLACK);
			g.drawString("(" + Driver.scene.selected.size() + ") " + Driver.scene.selected.get(0), Driver.TOOL_MESSAGE_MARGIN, getHeight() - Driver.TOOL_MESSAGE_MARGIN);
			
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
		if (willIgnore) {
			
			willIgnore = false;
			return;
			
		}
		
		if (e.getKeyChar() == '\b') {
			
			if (Driver.input.length() > 0) {
				
				Driver.input = Driver.input.substring(0, Driver.input.length() - 1);
				
			}
			
			if (Driver.tool != null) {
				
				Driver.tool.takeMessage(Driver.input);
				Driver.frame.repaint();
				
			}
			
		} else {
			
			Driver.input += e.getKeyChar();
			
			if (Driver.tool != null) {
				Driver.tool.takeMessage(Driver.input);
				Driver.frame.repaint();
				
			}
			
		}
		
		if (Driver.tool != null) {
			
			Driver.tool.keyTyped(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.keyReleased(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.keyPressed(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseWheelMoved(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseReleased(e);
			Driver.frame.repaint();
			
			if (Driver.tool != null && e.getButton() == 1 && Driver.tool.willFinaliseOnRealeaseLeftMouse()) {
				
				Driver.finaliseTool();
				
			} else if (Driver.tool != null &&  e.getButton() == 3 && Driver.tool.willAbortOnRealeaseRightMouse()) {
				
				Driver.abortTool();
				
			}
			
		}
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		if (Driver.tool == null) {
			
			if (e.getButton() == 1) {
				
				if (e.isShiftDown()) {
					
					Driver.selectTool(Driver.PLACE_TOOL);
					
				} else {
					
					Driver.selectTool(Driver.LEFT_SELECT_TOOL);
					
				}
				
			} else if (e.getButton() == 2) {
				
				// selectTool(middleMouseTool);
				
			} else if (e.getButton() == 3) {
				
				Driver.selectTool(Driver.RIGHT_SELECT_TOOL);
				
			}
			
		}
		
		if (Driver.tool != null) {
			
			Driver.tool.mousePressed(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseExited(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseEntered(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseClicked(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseMoved(e);
			Driver.frame.repaint();
			
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (Driver.tool != null) {
			
			Driver.tool.mouseDragged(e);
			Driver.frame.repaint();
			
		}
		
	}
	
}