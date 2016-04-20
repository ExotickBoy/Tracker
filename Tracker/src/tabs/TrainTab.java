package tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;

import core.Driver;
import items.RailLocation;
import items.Train;
import items.TrainSection;
import items.TrainStop;
import utils.Units;

public class TrainTab extends ComboBoxTab<Train> implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String TRAIN_STATS_LABEL = "Train Stats";
	private static final String TRAIN_SECTIONS_LABEL = "Train Sections";
	private static final String TRAIN_STOPS_LABEL = "Train Stops";
	
	private static final String SPEED_LABEL = "Speed:";
	private static final String MASS_LABEL = "Mass:";
	private static final String ACCELERATING_FORCE_LABEL = "Acceleration:";
	private static final String BRAKING_FORCE_LABEL = "Deceleration:";
	
	private static final String UP_BUTTON_TEXT = "↑";
	private static final String DOWN_BUTTON_TEXT = "↓";
	
	private static final String TAB_TITLE = "Trains";
	private static final String NAME_PREFIX = "Train > ";
	
	private static final String ADD_BUTTON_TEXT = "Add";
	private static final String REMOVE_BUTTON_TEXT = "Remove";
	
	private static final String PAUSE_BUTTON_TEXT = "❚❚";
	private static final String RUN_BUTTON_TEXT = "►";
	
	private Train active;
	
	private JLabel speedDataLabel;
	private JLabel massDataLabel;
	private JLabel acceleratingForceDataLabel;
	private JLabel brakingForceDataLabel;
	
	private JList<TrainSection> trainSectionList;
	private DefaultListModel<TrainSection> sectionListModel;
	
	private JButton moveSectionUpButton;
	private JButton moveSectionDownButton;
	private JButton addSectionButton;
	private JButton removeSectionButton;
	private JComboBox<String> sectionSelecterComboBox;
	
	private JList<TrainStop> trainStopList;
	private DefaultListModel<TrainStop> stopListModel;
	
	private JButton moveStopUpButton;
	private JButton moveStopDownButton;
	private JButton addStopButton;
	private JButton removeStopButton;
	private JComboBox<TrainStop> stopSelecterComboBox;
	
	private JButton runTrainButton;
	
	public TrainTab() {
		
		super();
		
		setTitle(TAB_TITLE);
		Driver.viewPanel.addMouseListener(this);
		setComboBoxModel(new DefaultComboBoxModel<Train>(getScene().trains.stream().toArray(size -> new Train[size])));
		
	}
	
	@Override
	protected String getDefaultLabelText() {
		
		return NAME_PREFIX;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("comboBoxEdited")) {
			
			active.setName(getComboBox().getSelectedItem().toString());
			Driver.viewPanel.grabFocus();
			
		} else { // comboBoxChanged
			
			if (getComboBox().getSelectedIndex() != -1) {
				
				getComboBox().setEditable(true);
				active = (Train) getComboBox().getSelectedItem();
				
				updateLists();
				update();
				
				Driver.frame.repaint();
				
			}
			
		}
		
		update();
		
	}
	
	private void updateLists() {
		
		if (active != null) {
			
			sectionListModel.clear();
			active.getSections().forEach(sectionListModel::addElement);
			
			stopListModel.clear();
			active.getRout().forEach(stopListModel::addElement);
			
		}
		
	}
	
	@Override
	protected boolean isEditable() {
		
		return false;
		
	}
	
	@Override
	protected JPanel getPanel() {
		
		JPanel view = new JPanel();
		view.setLayout(new GridBagLayout());
		view.setBorder(BorderFactory.createEmptyBorder(0, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS));
		
		runTrainButton = new JButton();
		runTrainButton.setText(RUN_BUTTON_TEXT);
		runTrainButton.setEnabled(false);
		runTrainButton.addActionListener((e) -> {
			
			active.setRunning(!active.isRunning());
			
			runTrainButton.setText(active.isRunning() ? PAUSE_BUTTON_TEXT : RUN_BUTTON_TEXT);
			
		});
		
		speedDataLabel = new JLabel();
		speedDataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		speedDataLabel.setText(Units.METRE_PER_SECOND.formatShortUnit(0));
		
		massDataLabel = new JLabel();
		massDataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		massDataLabel.setText(Units.KILO_GRAM.formatShortUnit(0));
		
		acceleratingForceDataLabel = new JLabel();
		acceleratingForceDataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		acceleratingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(0));
		
		brakingForceDataLabel = new JLabel();
		brakingForceDataLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		brakingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(0));
		
		sectionListModel = new DefaultListModel<>();
		trainSectionList = new JList<>(sectionListModel);
		trainSectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		trainSectionList.addListSelectionListener((ListSelectionEvent e) -> {
			
			moveSectionUpButton.setEnabled(trainSectionList.getSelectedIndex() != 0 && trainSectionList.getModel().getSize() != 0);
			moveSectionDownButton.setEnabled(trainSectionList.getSelectedIndex() != sectionListModel.getSize() - 1 && trainSectionList.getModel().getSize() != 0);
			removeSectionButton.setEnabled(trainSectionList.getSelectedIndex() != -1);
			
		});
		
		moveSectionUpButton = new JButton();
		moveSectionUpButton.setText(UP_BUTTON_TEXT);
		moveSectionUpButton.setEnabled(false);
		moveSectionUpButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			TrainSection toMove = sectionListModel.getElementAt(index);
			sectionListModel.removeElementAt(index);
			sectionListModel.add(index - 1, toMove);
			
			active.getSections().remove(index);
			active.getSections().add(index - 1, toMove);
			
			trainSectionList.setSelectedIndex(index - 1);
			
			active.recalculateSections();
			Driver.frame.repaint();
			
		});
		
		moveSectionDownButton = new JButton();
		moveSectionDownButton.setText(DOWN_BUTTON_TEXT);
		moveSectionDownButton.setEnabled(false);
		moveSectionDownButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			TrainSection toMove = sectionListModel.getElementAt(index);
			sectionListModel.removeElementAt(index);
			sectionListModel.add(index + 1, toMove);
			
			active.getSections().remove(index);
			active.getSections().add(index + 1, toMove);
			
			trainSectionList.setSelectedIndex(index + 1);
			
			active.recalculateSections();
			Driver.frame.repaint();
			
		});
		
		HashMap<String, Function<RailLocation, TrainSection>> stringToSectionMap = new HashMap<>();
		stringToSectionMap.put(TrainSection.LOCOMOTIVE_NAME, TrainSection.Locomotive::new);
		stringToSectionMap.put(TrainSection.WAGON_NAME, TrainSection.Wagon::new);
		
		sectionSelecterComboBox = new JComboBox<>(stringToSectionMap.keySet().stream().toArray(size -> new String[size]));
		
		addSectionButton = new JButton();
		addSectionButton.setText(ADD_BUTTON_TEXT);
		addSectionButton.setEnabled(false);
		addSectionButton.addActionListener(e -> {
			
			active.addSection(stringToSectionMap.get(sectionSelecterComboBox.getSelectedItem()));
			updateLists();
			update();
			Driver.frame.repaint();
			
		});
		
		removeSectionButton = new JButton();
		removeSectionButton.setText(REMOVE_BUTTON_TEXT);
		removeSectionButton.setEnabled(false);
		removeSectionButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			active.removeSection(index);
			updateLists();
			update();
			trainSectionList.setSelectedIndex(index);
			Driver.frame.repaint();
			
		});
		
		// stops
		
		stopListModel = new DefaultListModel<>();
		trainStopList = new JList<>(stopListModel);
		trainStopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		trainStopList.addListSelectionListener((ListSelectionEvent e) -> {
			
			moveStopUpButton.setEnabled(trainStopList.getSelectedIndex() != 0 && trainStopList.getModel().getSize() != 0);
			moveStopDownButton.setEnabled(trainStopList.getSelectedIndex() != stopListModel.getSize() - 1 && trainStopList.getModel().getSize() != 0);
			removeStopButton.setEnabled(trainStopList.getSelectedIndex() != -1);
			
		});
		
		moveStopUpButton = new JButton();
		moveStopUpButton.setText(UP_BUTTON_TEXT);
		moveStopUpButton.setEnabled(false);
		moveStopUpButton.addActionListener(e -> {
			
			int index = trainStopList.getSelectedIndex();
			TrainStop toMove = stopListModel.getElementAt(index);
			stopListModel.removeElementAt(index);
			stopListModel.add(index - 1, toMove);
			
			active.removeDestination(index);
			active.addDestination(index - 1, toMove);
			
			trainStopList.setSelectedIndex(index - 1);
			
			Driver.frame.repaint();
			
		});
		
		moveStopDownButton = new JButton();
		moveStopDownButton.setText(DOWN_BUTTON_TEXT);
		moveStopDownButton.setEnabled(false);
		moveStopDownButton.addActionListener(e -> {
			
			int index = trainStopList.getSelectedIndex();
			TrainStop toMove = stopListModel.getElementAt(index);
			stopListModel.removeElementAt(index);
			stopListModel.add(index + 1, toMove);
			
			active.removeDestination(index);
			active.addDestination(index + 1, toMove);
			
			trainStopList.setSelectedIndex(index + 1);
			
			Driver.frame.repaint();
			
		});
		
		stopSelecterComboBox = new JComboBox<>(scene.trainStops.stream().toArray(size -> new TrainStop[size]));
		stopSelecterComboBox.setEditable(false);
		stopSelecterComboBox.addActionListener(new ActionListener() {
			
			TrainStop last;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (e.getActionCommand().equals("comboBoxEdited")) {
					
					last.setName(stopSelecterComboBox.getSelectedItem().toString());
					Driver.viewPanel.grabFocus();
					
				} else { // comboBoxChanged
					
					if (getComboBox().getSelectedIndex() != -1) {
						
						stopSelecterComboBox.setEditable(true);
						last = scene.trainStops.get(getComboBox().getSelectedIndex());
						update();
						
						Driver.frame.repaint();
						
					}
					
				}
				
			}
		});
		
		addStopButton = new JButton();
		addStopButton.setText(ADD_BUTTON_TEXT);
		addStopButton.setEnabled(false);
		addStopButton.addActionListener(e -> {
			
			active.addDestination(scene.trainStops.get(stopSelecterComboBox.getSelectedIndex()));
			updateLists();
			update();
			Driver.frame.repaint();
			
		});
		
		removeStopButton = new JButton();
		removeStopButton.setText(REMOVE_BUTTON_TEXT);
		removeStopButton.setEnabled(false);
		removeStopButton.addActionListener(e -> {
			
			int index = trainStopList.getSelectedIndex();
			active.removeDestination(index);
			updateLists();
			update();
			trainStopList.setSelectedIndex(index);
			Driver.frame.repaint();
			
		});
		//
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(runTrainButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(new JLabel(TRAIN_STATS_LABEL, SwingConstants.CENTER), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(new JLabel(SPEED_LABEL, SwingConstants.LEFT), c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(speedDataLabel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(new JLabel(MASS_LABEL, SwingConstants.LEFT), c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(massDataLabel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(new JLabel(ACCELERATING_FORCE_LABEL, SwingConstants.LEFT), c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(acceleratingForceDataLabel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(new JLabel(BRAKING_FORCE_LABEL, SwingConstants.LEFT), c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(brakingForceDataLabel, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, 0);
		view.add(new JLabel(TRAIN_SECTIONS_LABEL, SwingConstants.CENTER), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		view.add(new JScrollPane(trainSectionList), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, Driver.LAYOUT_MARGINS / 2);
		view.add(moveSectionUpButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(moveSectionDownButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, Driver.LAYOUT_MARGINS / 2);
		view.add(addSectionButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(removeSectionButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, 0);
		view.add(sectionSelecterComboBox, c);
		
		// stops
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, 0);
		view.add(new JLabel(TRAIN_STOPS_LABEL, SwingConstants.CENTER), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		view.add(new JScrollPane(trainStopList), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, Driver.LAYOUT_MARGINS / 2);
		view.add(moveStopUpButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(moveStopDownButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, Driver.LAYOUT_MARGINS / 2);
		view.add(addStopButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(removeStopButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, 0);
		view.add(stopSelecterComboBox, c);
		
		// sectionSelecterComboBox
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		
		JPanel filler = new JPanel();
		filler.setLayout(new GridBagLayout());
		filler.add(view, c);
		
		return filler;
		
	}
	
	@Override
	public void onSwitchedTo() {
		
		setComboBoxModel(new DefaultComboBoxModel<Train>(getScene().trains.stream().toArray(size -> new Train[size])));
		stopSelecterComboBox.setModel(new DefaultComboBoxModel<>(scene.trainStops.stream().toArray(size -> new TrainStop[size])));
		
		updateLists();
		update();
		
	}
	
	@Override
	public void onSwitchedAway() {}
	
	@Override
	public void onToolActivate() {}
	
	@Override
	public void onToolFinalise() {
		
		setComboBoxModel(new DefaultComboBoxModel<Train>(getScene().trains.stream().toArray(size -> new Train[size])));
		stopSelecterComboBox.setModel(new DefaultComboBoxModel<>(scene.trainStops.stream().toArray(size -> new TrainStop[size])));
		
	}
	
	@Override
	public void onToolAbort() {}
	
	@Override
	public void onUndo() {}
	
	@Override
	public void onRedo() {}
	
	public void update() {
		
		if (active != null) {
			
			getLabel().setText(NAME_PREFIX + active.getName());
			
			runTrainButton.setEnabled(true);
			runTrainButton.setText(active.isRunning() ? PAUSE_BUTTON_TEXT : RUN_BUTTON_TEXT);
			
			addSectionButton.setEnabled(true);
			addStopButton.setEnabled(true);
			sectionSelecterComboBox.setEnabled(true);
			stopSelecterComboBox.setEnabled(true);
			
			updateLabel(speedDataLabel, Units.METRE_PER_SECOND, active.getSpeed());
			updateLabel(massDataLabel, Units.KILO_GRAM, active.getMass());
			updateLabel(acceleratingForceDataLabel, Units.METRE_PER_SECOND_PER_SECOND, active.getMaxAcceleration());
			updateLabel(brakingForceDataLabel, Units.METRE_PER_SECOND_PER_SECOND, active.getMaxDeceleration());
			
		} else {
			
			getLabel().setText(NAME_PREFIX);
			
			sectionListModel.clear();
			stopListModel.clear();
			
			sectionSelecterComboBox.setEnabled(false);
			stopSelecterComboBox.setEnabled(false);
			
			updateLabel(speedDataLabel, Units.METRE_PER_SECOND, 0);
			updateLabel(massDataLabel, Units.KILO_GRAM, 0);
			updateLabel(acceleratingForceDataLabel, Units.METRE_PER_SECOND_PER_SECOND, 0);
			updateLabel(brakingForceDataLabel, Units.METRE_PER_SECOND_PER_SECOND, 0);
			
		}
		
	}
	
	private void updateLabel(JLabel label, Units unit, double amount) {
		
		label.setText(unit.formatShortUnit(amount));
		label.setToolTipText(unit.formatLongUnit(amount));
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		
		Driver.viewPanel.grabFocus();
		
	}
	
	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void mousePressed(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	@Override
	public void onModeSwitched() {}
	
}
