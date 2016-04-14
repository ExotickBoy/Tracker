package components;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import core.Driver;
import core.Tool;
import tools.ToolGroup;

public final class ToolGroupPopup extends JPopupMenu {
	
	private static final long serialVersionUID = 1L;
	
	JMenuItem last;
	
	public ToolGroupPopup(ArrayList<Tool> tools, JPanel mainPanel, ToolGroup group) {
		
		JMenuItem title = new JMenuItem();
		title.setEnabled(false);
		title.setText(group.getName());
		add(title);
		addSeparator();
		
		last = title;
		
		mainPanel.registerKeyboardAction((e) -> {
			
			if (mainPanel.getMousePosition() != null && (Driver.tool == null || Driver.tool.isOverwritable())) {
				
				show(mainPanel, (int) (mainPanel.getMousePosition().x - last.getX() - getPreferredSize().getWidth() / 2),
						mainPanel.getMousePosition().y - last.getY() - last.getPreferredSize().height / 2);
						
			}
			
		} , group.getKeyStroke(), JComponent.WHEN_FOCUSED);
		
		tools.stream().filter((tool) -> {
			
			return tool.getGroup() == group;
			
		}).map((tool) -> {
			
			JMenuItem item = new JMenuItem();
			item.setText(tool.getName());
			item.setAccelerator(tool.getKeyStroke());
			item.addActionListener((e) -> {
				
				last = item;
				Driver.selectTool(tool);
				
			});
			
			return item;
			
		}).forEach(this::add);
		
	}
	
}
