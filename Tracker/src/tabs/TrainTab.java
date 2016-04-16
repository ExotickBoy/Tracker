package tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
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
import utils.Units;

public class TrainTab extends ComboBoxTab<Train> implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final String TRAIN_STATS_LABEL = "Train Stats";
	private static final String TRAIN_SECTIONS_LABEL = "Train Sections";
	
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
	
	private Train active;
	
	private JList<TrainSection> trainSectionList;
	DefaultListModel<TrainSection> sectionListModel;
	
	private JLabel speedDataLabel;
	private JLabel massDataLabel;
	private JLabel acceleratingForceDataLabel;
	private JLabel brakingForceDataLabel;
	
	private JButton moveUpButton;
	private JButton moveDownButton;
	private JButton addButton;
	private JButton removeButton;
	private JComboBox<String> sectionSelecterComboBox;
	
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
				updatePanel();
				
				Driver.frame.repaint();
				
			}
			
		}
		
		updatePanel();
		
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
			
			moveUpButton.setEnabled(trainSectionList.getSelectedIndex() != 0 && trainSectionList.getModel().getSize() != 0);
			moveDownButton.setEnabled(trainSectionList.getSelectedIndex() != sectionListModel.getSize() - 1 && trainSectionList.getModel().getSize() != 0);
			removeButton.setEnabled(trainSectionList.getSelectedIndex() != -1);
			
		});
		
		moveUpButton = new JButton();
		moveUpButton.setText(UP_BUTTON_TEXT);
		moveUpButton.setEnabled(false);
		moveUpButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			TrainSection toMove = sectionListModel.getElementAt(index);
			sectionListModel.removeElementAt(index);
			sectionListModel.add(index - 1, toMove);
			
			active.sections.remove(index);
			active.sections.add(index - 1, toMove);
			
			trainSectionList.setSelectedIndex(index - 1);
			
			active.recalculateSections();
			Driver.frame.repaint();
			
		});
		
		moveDownButton = new JButton();
		moveDownButton.setText(DOWN_BUTTON_TEXT);
		moveDownButton.setEnabled(false);
		moveDownButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			TrainSection toMove = sectionListModel.getElementAt(index);
			sectionListModel.removeElementAt(index);
			sectionListModel.add(index + 1, toMove);
			
			active.sections.remove(index);
			active.sections.add(index + 1, toMove);
			
			trainSectionList.setSelectedIndex(index + 1);
			
			active.recalculateSections();
			Driver.frame.repaint();
			
		});
		
		HashMap<String, Function<RailLocation, TrainSection>> stringToSectionMap = new HashMap<>();
		stringToSectionMap.put(TrainSection.LOCOMOTIVE_NAME, TrainSection.Locomotive::new);
		stringToSectionMap.put(TrainSection.WAGON_NAME, TrainSection.Wagon::new);
		
		sectionSelecterComboBox = new JComboBox<>(stringToSectionMap.keySet().stream().toArray(size -> new String[size]));
		
		addButton = new JButton();
		addButton.setText(ADD_BUTTON_TEXT);
		addButton.setEnabled(false);
		addButton.addActionListener(e -> {
			
			active.addSection(stringToSectionMap.get(sectionSelecterComboBox.getSelectedItem()));
			updatePanel();
			Driver.frame.repaint();
			
		});
		
		removeButton = new JButton();
		removeButton.setText(REMOVE_BUTTON_TEXT);
		removeButton.setEnabled(false);
		removeButton.addActionListener(e -> {
			
			int index = trainSectionList.getSelectedIndex();
			active.removeSection(index);
			updatePanel();
			trainSectionList.setSelectedIndex(index);
			Driver.frame.repaint();
			
		});
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
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
		view.add(moveUpButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(moveDownButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, Driver.LAYOUT_MARGINS / 2);
		view.add(addButton, c);
		
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS / 2, 0, 0);
		view.add(removeButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(Driver.LAYOUT_MARGINS, 0, 0, 0);
		view.add(sectionSelecterComboBox, c);
		
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
		updatePanel();
		
	}
	
	@Override
	public void onSwitchedAway() {}
	
	@Override
	public void onToolActivate() {}
	
	@Override
	public void onToolFinalise() {}
	
	@Override
	public void onToolAbort() {}
	
	@Override
	public void onUndo() {}
	
	@Override
	public void onRedo() {}
	
	private void updatePanel() {
		
		if (active != null) {
			
			getLabel().setText(NAME_PREFIX + active.getName());
			
			sectionListModel.clear();
			active.sections.forEach(sectionListModel::addElement);
			
			addButton.setEnabled(true);
			sectionSelecterComboBox.setEnabled(true);
			
			speedDataLabel.setText(Units.METRE_PER_SECOND.formatShortUnit(active.getSpeed()));
			speedDataLabel.setToolTipText(Units.METRE_PER_SECOND.formatLongUnit(active.getSpeed()));
			
			massDataLabel.setText(Units.KILO_GRAM.formatShortUnit(active.getMass()));
			massDataLabel.setToolTipText(Units.KILO_GRAM.formatLongUnit(active.getMass()));
			
			acceleratingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(active.getMaxAcceleration()));
			acceleratingForceDataLabel.setToolTipText(Units.METRE_PER_SECOND_PER_SECOND.formatLongUnit(active.getMaxAcceleration()));
			
			brakingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(active.getMaxDeceleration()));
			brakingForceDataLabel.setToolTipText(Units.METRE_PER_SECOND_PER_SECOND.formatLongUnit(active.getMaxDeceleration()));
			
		} else {
			
			getLabel().setText(NAME_PREFIX);
			
			sectionListModel.clear();
			sectionSelecterComboBox.setEnabled(false);
			
			speedDataLabel.setText(Units.METRE_PER_SECOND.formatShortUnit(0));
			speedDataLabel.setToolTipText("");
			
			massDataLabel.setText(Units.KILO_GRAM.formatShortUnit(0));
			massDataLabel.setToolTipText("");
			
			acceleratingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(0));
			acceleratingForceDataLabel.setToolTipText("");
			
			brakingForceDataLabel.setText(Units.METRE_PER_SECOND_PER_SECOND.formatShortUnit(0));
			brakingForceDataLabel.setToolTipText("");
			
		}
		
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
	
}
