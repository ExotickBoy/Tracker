package tabs;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;

import core.Driver;
import core.Edit;
import items.RailPoint;

public final class RailPointTab extends ComboBoxTab<RailPoint> implements MouseListener {
	
	private static final String TAB_TITLE = "Points";
	
	private static final String NAME_PREFIX = "RailPoint > ";
	
	private static final String DIRECTION_LABEL = "dir:";
	private static final String Y_LABEL = "y:";
	private static final String X_LABEL = "x:";
	
	private static final long serialVersionUID = 4188568533187466892L;
	
	RailPoint active;
	
	JSpinner xSpinner;
	JSpinner ySpinner;
	JSpinner directionSpinner;
	
	boolean user = true;
	
	public RailPointTab() {
		
		setTitle(TAB_TITLE);
		
		Driver.viewPanel.addMouseListener(this);
		setComboBoxModel(new DefaultComboBoxModel<>(scene.railPoints.stream().toArray(size -> new RailPoint[size])));
		
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(2, 1));
		top.setBorder(BorderFactory.createEmptyBorder(Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS));
		
	}
	
	@Override
	public void onToolActivate() {
		
		disableComponenets();
		
	}
	
	@Override
	public void onToolFinalise() {
		
		update();
		
	}
	
	@Override
	public void onToolAbort() {
		
		update();
		
	}
	
	private void update() {
		
		getComboBox().setModel(new DefaultComboBoxModel<>(getScene().railPoints.stream().toArray(size -> new RailPoint[size])));
		
		if (getScene().selected.size() > 0) {
			
			active = getScene().selected.get(0);
			
		} else {
			
			active = null;
			
		}
		
		if (active != null) {
			
			user = false;
			getComboBox().setSelectedItem(active);
			
			getLabel().setText(NAME_PREFIX + active.getName());
			
			xSpinner.setValue(active.getPosition().x);
			ySpinner.setValue(active.getPosition().y);
			directionSpinner.setValue(toDegrees(active.getDirection()));
			user = true;
			
			enableComponents();
			
		} else {
			
			user = false;
			getComboBox().setSelectedIndex(-1);
			
			getComboBox().setSelectedItem("");
			getLabel().setText(NAME_PREFIX);
			
			xSpinner.setValue(0);
			ySpinner.setValue(0);
			directionSpinner.setValue(0);
			user = true;
			
			disableComponenets();
			
		}
		
	}
	
	private void enableComponents() {
		
		getComboBox().setEditable(true);
		
		xSpinner.setEnabled(true);
		ySpinner.setEnabled(true);
		directionSpinner.setEnabled(true);
		
	}
	
	private void disableComponenets() {
		
		getComboBox().setEditable(false);
		xSpinner.setEnabled(false);
		ySpinner.setEnabled(false);
		directionSpinner.setEnabled(false);
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		
		Driver.viewPanel.grabFocus();
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void onUndo() {
		
		update();
		
	}
	
	@Override
	public void onRedo() {
		
		update();
		
	}
	
	@Override
	public void onSwitchedTo() {
		
		update();
		
	}
	
	@Override
	public void onSwitchedAway() {}
	
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
				
				active = (RailPoint) getComboBox().getSelectedItem();
				
				if (user) {
					
					if (getScene().selected.contains(active)) {
						
						getScene().selected.set(0, active);
						
					} else {
						
						getScene().selected.clear();
						getScene().selected.add(active);
						
					}
					
				}
				
				update();
				
				Driver.frame.repaint();
				
			}
			
		}
		
	}
	
	@Override
	protected boolean isEditable() {
		
		return true;
		
	}
	
	@Override
	protected JPanel getPanel() {
		
		JPanel view = new JPanel();
		view.setLayout(new GridBagLayout());
		
		view.setBorder(BorderFactory.createEmptyBorder(0, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS));
		GridBagConstraints c = new GridBagConstraints();
		
		xSpinner = new JSpinner();
		xSpinner.setModel(new SpinnerNumberModel());
		xSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (active != null && user) {
					
					double to = Double.valueOf(xSpinner.getModel().getValue().toString());
					Driver.addEdit(new Edit() {
						
						double after = to;
						double before = active.getPosition().x;
						
						RailPoint associated = active;
						
						@Override
						public void undo() {
							
							associated.getPosition().x = before;
							associated.updateConnections();
							
						}
						
						@Override
						public void redo() {
							
							associated.getPosition().x = after;
							associated.updateConnections();
							
						}
						
					});
					
					active.getPosition().x = to;
					Driver.frame.repaint();
					
				}
				
			}
		});
		
		JLabel xLabel = new JLabel(X_LABEL, SwingConstants.RIGHT);
		
		ySpinner = new JSpinner();
		ySpinner.setModel(new SpinnerNumberModel());
		ySpinner.addChangeListener((e) -> {
			
			if (active != null && user) {
				
				double to = Double.valueOf(ySpinner.getModel().getValue().toString());
				Driver.addEdit(new Edit() {
					
					double after = to;
					double before = active.getPosition().y;
					
					RailPoint associated = active;
					
					@Override
					public void undo() {
						
						associated.getPosition().y = before;
						associated.updateConnections();
						
					}
					
					@Override
					public void redo() {
						
						associated.getPosition().y = after;
						associated.updateConnections();
						
					}
					
				});
				
				active.getPosition().y = to;
				Driver.frame.repaint();
				
			}
			
		});
		
		JLabel yLabel = new JLabel(Y_LABEL, SwingConstants.RIGHT);
		
		directionSpinner = new JSpinner();
		directionSpinner.setModel(new SpinnerNumberModel() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object getNextValue() {
				
				return (Double.valueOf(super.getNextValue().toString()) + 360) % 360;
				
			}
			
			public Object getPreviousValue() {
				
				return (Double.valueOf(super.getPreviousValue().toString()) + 360) % 360;
				
			};
			
		});
		JFormattedTextField txt = ((JSpinner.NumberEditor) directionSpinner.getEditor()).getTextField();
		AbstractFormatter defaultFormat = new AbstractFormatter() {
			
			private static final long serialVersionUID = 1L;
			
			AbstractFormatter defaultFormatter;
			
			{
				
				defaultFormatter = txt.getFormatter();
				
			}
			
			@Override
			public String valueToString(Object value) throws ParseException {
				
				return defaultFormatter.valueToString((Double.valueOf(value.toString()) + 360) % 360) + "º";
				
			}
			
			@Override
			public Object stringToValue(String text) throws ParseException {
				
				return defaultFormatter.stringToValue(text);
				
			}
			
		};
		txt.setFormatterFactory(new DefaultFormatterFactory(defaultFormat));
		directionSpinner.addChangeListener((e) -> {
			
			if (active != null && user) {
				
				double to = toRadians(Double.valueOf(directionSpinner.getValue().toString()));
				Driver.addEdit(new Edit() {
					
					double after = to;
					double before = active.getPosition().y;
					
					RailPoint associated = active;
					
					@Override
					public void undo() {
						
						associated.getPosition().y = before;
						associated.updateConnections();
						
					}
					
					@Override
					public void redo() {
						
						associated.getPosition().y = after;
						associated.updateConnections();
						
					}
					
				});
				
				active.setDirection(to);
				Driver.frame.repaint();
				
			}
			
		});
		
		JLabel directionLabel = new JLabel(DIRECTION_LABEL, SwingConstants.RIGHT);
		
		// c.anchor = GridBagConstraints.NORTHWEST;
		// c.weighty = 1;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS);
		view.add(xLabel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(xSpinner, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, Driver.LAYOUT_MARGINS);
		view.add(yLabel, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, Driver.LAYOUT_MARGINS, 0);
		view.add(ySpinner, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, Driver.LAYOUT_MARGINS);
		view.add(directionLabel, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		view.add(directionSpinner, c);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		
		// xLabel.setBorder(BorderFactory.createEtchedBorder());
		// yLabel.setBorder(BorderFactory.createEtchedBorder());
		// directionLabel.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel filler = new JPanel();
		filler.setLayout(new GridBagLayout());
		filler.add(view, c);
		
		return filler;
		
	}
	
	@Override
	public void onModeSwitched() {
		// TODO Auto-generated method stub
		
	}
	
}
