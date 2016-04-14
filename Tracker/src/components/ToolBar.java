package components;

import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import core.Driver;
import core.Mode;
import core.Tool;
import tools.ToolGroup;

public class ToolBar extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	
	private static final String TOOLS_LABEL = "Tools";
	
	public JComboBox<Mode> modeComboBox;
	
	public ToolBar() {
		
		JMenu toolsMenu = new JMenu();
		toolsMenu.setText(TOOLS_LABEL);
		Driver.tools.stream().map(Tool::getGroup).filter(Objects::nonNull).distinct().filter(ToolGroup::willShow).map((group) -> {
			
			JMenu groupMenu = new JMenu();
			
			groupMenu.add(new JMenuItem("Work in progress"));
			
			groupMenu.setText(group.getName());
			groupMenu.setMnemonic(group.getKeyStroke().getKeyChar());
			
			Driver.tools.stream().filter((tool) -> {
				
				return tool.getGroup() == group;
				
			}).map((tool) -> {
				
				JMenuItem toolItem = new JMenuItem();
				toolItem.setEnabled(false);
				toolItem.setText(tool.getName());
				toolItem.setAccelerator(tool.getKeyStroke());
				toolItem.setMnemonic(tool.getKeyStroke().getKeyChar());
				toolItem.addActionListener((e) -> {
					
					Driver.selectTool(tool);
					
				});
				
				return toolItem;
				
			}).forEach(groupMenu::add);
			
			return groupMenu;
			
		}).forEach(toolsMenu::add);
		add(toolsMenu);
		
		modeComboBox = new JComboBox<Mode>(Driver.modes.stream().toArray((size) -> new Mode[size]));
		modeComboBox.setPreferredSize(modeComboBox.getMinimumSize());
		modeComboBox.addActionListener((e) -> {
			
			Driver.selectMode((Mode) modeComboBox.getSelectedItem());
			
		});
		add(modeComboBox);
		
		/*
		 * fileMenu = new JMenu(); fileMenu.setText("File"); fileMenu.setMnemonic('f');
		 * 
		 * saveItem = new JMenuItem(); saveItem.setText("Save"); saveItem.setMnemonic('s'); saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); saveItem.setToolTipText("Saves the current scene to a file."); saveItem.addActionListener((e) -> {
		 * 
		 * Driver.save();
		 * 
		 * });
		 * 
		 */
		
	}
	
}
