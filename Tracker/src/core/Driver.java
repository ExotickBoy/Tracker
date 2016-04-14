package core;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import components.MenuBar;
import components.TabsPanel;
import components.ToolBar;
import components.ViewPanel;
import modes.RailerMode;
import modes.TrainMode;
import tools.ConnectTool;
import tools.DeleteTool;
import tools.DuplicateTool;
import tools.ExtrudeTool;
import tools.GrabTool;
import tools.LeftSelectTool;
import tools.PlaceTool;
import tools.RightSelectTool;
import tools.RotateTool;
import tools.ScaleTool;
import tools.SelectAllTool;
import tools.TrainPlaceTool;
import utils.Vector2;

public final class Driver {
	
	// strings
	
	private static final String SAVE_TITLE = "Save As...";
	private static final String SAVE_BUTTON = "Save As";
	private static final String LOAD_TITLE = "Load...";
	private static final String LOAD_BUTTON = "Open";
	
	// modes
	
	public static final TrainMode TRAIN_MODE = new TrainMode();
	public static final RailerMode RAILER_MODE = new RailerMode();
	
	// tools
	
	public static final Tool LEFT_SELECT_TOOL = new LeftSelectTool();
	public static final Tool PLACE_TOOL = new PlaceTool();
	public static final Tool RIGHT_SELECT_TOOL = new RightSelectTool();
	
	public static final Tool GRAB_TOOL = new GrabTool();
	public static final Tool SCALE_TOOL = new ScaleTool();
	public static final Tool ROTATE_TOOL = new RotateTool();
	
	public static final Tool DUPLICATE_TOOL = new DuplicateTool();
	public static final Tool CONNECT_TOOL = new ConnectTool();
	public static final Tool SELECT_ALL_TOOL = new SelectAllTool();
	public static final Tool EXTRUDE_TOOL = new ExtrudeTool();
	
	public static final Tool DELETE_TOOL = new DeleteTool();
	
	public static final Tool TRAIN_TOOL = new TrainPlaceTool();
	
	// other constants
	
	public static final int TOOL_MESSAGE_MARGIN = 10;
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 720;
	public static final int LAYOUT_MARGINS = 4;
	public static final int TAB_PANEL_WIDTH = 200;
	
	public static JFrame frame;
	public static ToolBar toolBar;
	public static TabsPanel tabsPanel;
	public static JPanel viewPanel;
	
	private static final String FRAME_TITLE = "Tracker";
	
	private static final String DEFAULT_SAVE_LOCATION = System.getProperty("user.home") + "/Desktop";
	private static final String FILE_EXTENSION = ".ktf";
	private static final String FILE_TYPE_DESCRIPTION = "Kacper Tracker File";
	
	public static File currentSave;
	public static Scene scene;
	
	public static ArrayList<Edit> edits = new ArrayList<>();
	public static ArrayList<Edit> undone = new ArrayList<>();
	
	public static ArrayList<Tool> tools = new ArrayList<>();
	public static Tool tool;
	
	public static ArrayList<Mode> modes = new ArrayList<>();
	public static Mode mode;
	
	public static String input = "";
	
	public static void main(String[] args) {
		
		// tools
		
		tools.add(LEFT_SELECT_TOOL);
		tools.add(PLACE_TOOL);
		tools.add(RIGHT_SELECT_TOOL);
		
		tools.add(GRAB_TOOL);
		tools.add(SCALE_TOOL);
		tools.add(ROTATE_TOOL);
		
		tools.add(DUPLICATE_TOOL);
		tools.add(CONNECT_TOOL);
		tools.add(SELECT_ALL_TOOL);
		tools.add(EXTRUDE_TOOL);
		
		tools.add(DELETE_TOOL);
		
		// modes
		
		modes.add(RAILER_MODE);
		modes.add(TRAIN_MODE);
		
		scene = new Scene();
		mode = RAILER_MODE;
		mode.onSwitchedTo();
		
		Tab.setScene(scene);
		Tool.setScene(scene);
		
		RailPoint rp1 = new RailPoint(new Vector2(100, 100));
		RailPoint rp2 = new RailPoint(new Vector2(300, 100));
		RailConnection rc = new RailConnection(rp1, rp2);
		
		scene.railPoints.add(rp1);
		scene.railPoints.add(rp2);
		scene.connections.add(rc);
		Train train = new Train(new RailLocation(.1, rc));
		train.addSection(TrainSection.Wagon::new);
		train.addSection(TrainSection.Wagon::new);
		scene.trains.add(train);
		scene.trainStops.add(new TrainStop(new RailLocation(.9, rc)));
		scene.railSignals.add(new RailSignal(new RailLocation(.5, rc)));
		
		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel left = new JPanel(new BorderLayout());
		left.add(toolBar = new ToolBar(), BorderLayout.SOUTH);
		left.add(viewPanel = new ViewPanel(), BorderLayout.CENTER);
		contentPane.add(tabsPanel = new TabsPanel(), BorderLayout.EAST);
		contentPane.add(left, BorderLayout.CENTER);
		
		frame = new JFrame(FRAME_TITLE);
		frame.setContentPane(contentPane);
		frame.setJMenuBar(new MenuBar());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	public static void selectMode(Mode mode) {
		
		abortTool();
		
		Driver.mode.onSwitchedAway();
		Driver.mode = mode;
		Driver.mode.onSwitchedTo();
		
		frame.repaint();
		
	}
	
	public static void cycleMode() {
		
		selectMode(modes.get((modes.indexOf(mode) + 1) % modes.size()));
		toolBar.modeComboBox.setSelectedItem(mode);
		
	}
	
	public static void selectTool(Tool tool) {
		
		if (tool.isActivatable() && tool != Driver.tool && (Driver.tool == null || Driver.tool.isOverwritable()) //
				&& (!tool.isModeSpecific() || tool.getMode() == mode)) {
				
			if (Driver.tool != null) {
				
				Driver.tool.onAbort();
				
			}
			
			Driver.tool = tool;
			input = "";
			
			Driver.tool.onActivate();
			tabsPanel.onToolActivate();
			
		}
		
		frame.repaint();
		
	}
	
	public static void abortTool() {
		
		if (tool != null) {
			
			tool.onAbort();
			tool = null;
			
			tabsPanel.onToolAbort();
			
			frame.repaint();
			
		}
		
	}
	
	public static void finaliseTool() {
		
		if (tool != null) {
			
			Edit edit = tool.onFinalise();
			
			if (edit != null) {
				
				addEdit(edit);
				
			}
			
			Tool last = tool;
			
			input = "";
			tool = null;
			
			if (last.getFollowingTool() != null) {
				
				selectTool(last.getFollowingTool());
				last.getFollowingTool().setFromFollow(true);
				
			}
			
			tabsPanel.onToolFinalise();
			
			frame.repaint();
			
		}
		
	}
	
	public static File choseFile(String title, String defaultName, String buttonText) {
		
		JFileChooser fc = new JFileChooser();
		if (!(defaultName == null || defaultName.equals(""))) {
			fc.setSelectedFile(new File(defaultName + FILE_EXTENSION));
		}
		fc.setCurrentDirectory(new File(DEFAULT_SAVE_LOCATION));
		fc.setDialogTitle(title);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setApproveButtonText(buttonText);
		fc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				
				return FILE_TYPE_DESCRIPTION + " (*" + FILE_EXTENSION + ")";
				
			}
			
			@Override
			public boolean accept(File f) {
				
				return f.isDirectory() || f.getName().endsWith(FILE_EXTENSION);
				
			}
			
		});
		
		int returnVal = fc.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			return fc.getSelectedFile();
			
		} else {
			
			return null;
			// the selection was stopped;
			
		}
		
	}
	
	private static void setCurrentSave(File currentSave) { // this also validates
		
		if (currentSave != null && !currentSave.getName().endsWith(FILE_EXTENSION)) {
			
			scene.name = currentSave.getName().replace(FILE_EXTENSION, "");
			Driver.currentSave = new File(currentSave.getAbsolutePath() + FILE_EXTENSION);
			
		} else if (currentSave != null) {
			
			Driver.currentSave = currentSave;
			
		}
		
	}
	
	private static void updateFrameTitle() {
		
		frame.setTitle(FRAME_TITLE + " - " + currentSave.getAbsolutePath());
		
	}
	
	public static void save() {
		
		if (currentSave == null) {
			
			setCurrentSave(choseFile(SAVE_TITLE, scene.getName(), SAVE_BUTTON));
			
		}
		
		if (currentSave != null) {
			
			scene.save(currentSave);
			updateFrameTitle();
			
		}
		
	}
	
	public static void saveAs() {
		
		File newSave = Driver.choseFile(SAVE_TITLE, scene.getName(), SAVE_BUTTON);
		
		if (newSave != null) {
			
			setCurrentSave(newSave);
			scene.save(Driver.currentSave);
			
			updateFrameTitle();
			
		}
		
	}
	
	public static void open() {
		
		File loadFrom = Driver.choseFile(LOAD_TITLE, "", LOAD_BUTTON);
		
		if (loadFrom != null) {
			
			currentSave = loadFrom;
			scene = Scene.load(Driver.currentSave);
			Tool.setScene(scene);
			Tab.setScene(scene);
			updateFrameTitle();
			frame.repaint();
			
		}
		
	}
	
	public static void addEdit(Edit edit) {
		
		edits.add(edit);
		undone.clear();
		MenuBar.undoItem.setEnabled(true);
		MenuBar.redoItem.setEnabled(false);
		
	}
	
	public static void undo() {
		
		Edit last = edits.get(edits.size() - 1);
		
		last.undo();
		
		edits.remove(last);
		undone.add(last);
		
		MenuBar.undoItem.setEnabled(!edits.isEmpty());
		MenuBar.redoItem.setEnabled(true);
		
		tabsPanel.onUndo();
		
		frame.repaint();
		
	}
	
	public static void redo() {
		
		Edit last = undone.get(undone.size() - 1);
		
		last.redo();
		
		undone.remove(last);
		edits.add(last);
		
		MenuBar.undoItem.setEnabled(true);
		MenuBar.redoItem.setEnabled(!undone.isEmpty());
		
		tabsPanel.onRedo();
		
		frame.repaint();
		
	}
	
}
