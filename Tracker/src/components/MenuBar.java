package components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import core.Driver;

public final class MenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	
	public static JMenu fileMenu;
	public static JMenu editMenu;
	
	public static JMenuItem newItem;
	public static JMenuItem saveItem;
	public static JMenuItem saveAsItem;
	public static JMenuItem openItem;
	
	public static JMenuItem undoItem;
	public static JMenuItem redoItem;
	
	public MenuBar() {
		
		fileMenu = new JMenu();
		fileMenu.setText("File");
		fileMenu.setMnemonic('f');
		
		newItem = new JMenuItem();
		newItem.setText("New");
		newItem.setMnemonic('n');
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newItem.setToolTipText("Makes a new scene");
		newItem.addActionListener((e) -> {
			
			Driver.newScene();
			
		});
		
		saveItem = new JMenuItem();
		saveItem.setText("Save");
		saveItem.setMnemonic('s');
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.setToolTipText("Saves the current scene to a file.");
		saveItem.addActionListener((e) -> {
			
			Driver.save();
			
		});
		
		saveAsItem = new JMenuItem();
		saveAsItem.setText("Save As");
		saveAsItem.setMnemonic('a');
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
		saveAsItem.setToolTipText("Saves the current scene to a file.");
		saveAsItem.addActionListener((e) -> {
			
			Driver.saveAs();
			
		});
		
		openItem = new JMenuItem();
		openItem.setText("Open");
		openItem.setMnemonic('o');
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openItem.setToolTipText("Loads a scene from a file.");
		openItem.addActionListener((e) -> {
			
			Driver.open();
			
		});
		
		fileMenu.add(newItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.add(openItem);
		
		editMenu = new JMenu();
		editMenu.setText("Edit");
		editMenu.setMnemonic('e');
		
		undoItem = new JMenuItem();
		undoItem.setEnabled(false);
		undoItem.setText("Undo");
		undoItem.setMnemonic('u');
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undoItem.setToolTipText("Undoes the last edit");
		undoItem.addActionListener((e) -> {
			
			Driver.undo();
			
		});
		
		redoItem = new JMenuItem();
		redoItem.setEnabled(false);
		redoItem.setText("Redo");
		redoItem.setMnemonic('r');
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		redoItem.setToolTipText("Redoes an undid event");
		redoItem.addActionListener((e) -> {
			
			Driver.redo();
			
		});
		
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		
		add(fileMenu);
		add(editMenu);
		
	}
	
}
