package items;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;

import core.Path;
import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import utils.Collider;

public final class Train implements Serializable, Drawable, OnRail, Selectable {
	
	private static final long serialVersionUID = 0L;
	
	private static final String DEFAULT_TRAIN = "Train";
	private static final Color NAME_LABLE_COLOR = Color.RED;
	public static final double MAX_SPEED = 10; // m/s
	
	private HashMap<Train, String> trainToName = new HashMap<>();
	
	private boolean willDrawCollider;
	private boolean willDrawName = true;
	private boolean willDrawPath = true;
	
	String name;
	
	private RailLocation location;
	private transient Path path;
	private double speed = 0;
	private boolean running;
	
	private ArrayList<TrainSection> sections = new ArrayList<>();
	private ArrayList<RailConnection> been = new ArrayList<>();
	
	private ArrayList<TrainStop> rout = new ArrayList<>();
	private RailLocation going;
	private int goingIndex;
	
	public Train() {
		
		addSection(TrainSection.Locomotive::new);
		
	}
	
	public Train(RailLocation location) {
		
		setRailLocation(location);
		addSection(TrainSection.Locomotive::new);
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		if (path != null && willDrawPath) {
			
			path.draw(g);
			
		}
		
		for (int i = getSections().size() - 1; i >= 0; i--) {
			
			getSections().get(i).draw(g);
			
		}
		
		Font fontBefore = g.getFont();
		AffineTransform affineTransform = getRailLocation().getRailPointTransform();
		affineTransform.rotate(PI / 2);
		AffineTransform innitialTransform = g.getTransform();
		g.transform(affineTransform);
		
		if (willDrawName) {
			
			g.setColor(NAME_LABLE_COLOR);
			if (getRailLocation().getDirection() < 0) {
				
				g.setColor(NAME_LABLE_COLOR);
				g.setFont(fontBefore.deriveFont((float) -fontBefore.getSize()));
				g.drawString(getName(), TrainSection.TRAIN_LENGTH / 2, TrainSection.TRAIN_WIDTH / 2);
				g.setFont(fontBefore);
				
			} else {
				
				g.drawString(getName(), -TrainSection.TRAIN_LENGTH / 2, -TrainSection.TRAIN_WIDTH / 2);
				
			}
			
		}
		
		g.setTransform(innitialTransform);
		
	}
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
		if (location != null) {
			
			been.remove(location.getConnection());
			been.add(0, location.getConnection());
			
		}
		
		recalculateSections();
		
	}
	
	@Override
	public RailLocation getRailLocation() {
		
		return getLocation();
		
	}
	
	@Override
	public Collider getCollider() {
		
		return getSections().stream().map(TrainSection::getCollider).reduce(Collider::combineCollider).get();
		
	}
	
	public String getName() {
		
		if (name == null) {
			
			setName(DEFAULT_TRAIN);
			
		}
		
		return name;
		
	}
	
	public void setName(String name) {
		
		if (trainToName.entrySet().stream().filter((set) -> set.getKey() != this).map(Entry::getValue).anyMatch((value) -> name.equals(value))) {
			
			int count = 1;
			
			while (trainToName.containsValue(name + "." + String.format("%03d", count))) {
				
				count++;
				
			}
			
			if (count == 0) {
				
				trainToName.put(this, name);
				
			} else {
				
				trainToName.put(this, name + "." + String.format("%03d", count));
				
			}
			
		} else {
			
			trainToName.put(this, name);
			
		}
		
		this.name = trainToName.get(this);
		
	}
	
	public double getSpeed() {
		
		return speed;
		
	}
	
	public double getMass() {
		
		return getSections().stream().mapToDouble(TrainSection::getMass).sum();
		
	}
	
	public double getMaxAcceleratingForce() {
		
		return getSections().stream().mapToDouble(TrainSection::getMaxAcceleratingForce).sum();
		
	}
	
	public double getMaxBrakingForce() {
		
		return getSections().stream().mapToDouble(TrainSection::getMaxBrakingForce).sum();
		
	}
	
	public double getMaxAcceleration() {
		
		return getMaxAcceleratingForce() / getMass();
		
	}
	
	public double getMaxDeceleration() {
		
		return getMaxBrakingForce() / getMass();
		
	}
	
	@Override
	public String toString() {
		
		return getName();
		
	}
	
	public void addSection(Function<RailLocation, TrainSection> getter) {
		
		RailLocation newLocation;
		
		if (getSections().size() == 0) {
			
			newLocation = getLocation();
			
		} else {
			
			RailLocation lastLocation = getSections().get(getSections().size() - 1).location;
			
			newLocation = getLocationBehind(lastLocation);
			
		}
		
		TrainSection section = getter.apply(newLocation);
		section.train = this;
		getSections().add(section);
		
	}
	
	public void removeSection(int sectionIndex) {
		
		getSections().remove(sectionIndex);
		recalculateSections();
	}
	
	public void removeSection(TrainSection section) {
		
		getSections().remove(getSections());
		recalculateSections();
		
	}
	
	public void recalculateSections() {
		
		if (getSections().size() > 0) {
			
			getSections().get(0).location = getLocation();
			
			if (getSections().size() > 1) {
				
				for (int i = 1; i < getSections().size(); i++) {
					
					getSections().get(i).setRailLocation(getLocationBehind(getSections().get(i - 1).location));
					
				}
				
			}
			
		}
		
	}
	
	public void updateLocation(long time) {
		
		if (running && path != null && path.getStartTime() == 0) {
			
			path.setStartTime(time);
			path.setTrain(this);
			
		} else if (running && path != null && path.getStartTime() != 0) {
			
			path.updateTrain(time);
			
			if (path.isFinished()) {
				
				going = next();
				path = Path.pathFind(location, going);
				
			}
			
		}
		
	}
	
	private RailLocation next() {
		
		goingIndex++;
		goingIndex %= rout.size();
		
		return rout.get(goingIndex).location;
		
	}
	
	public void addDestination(TrainStop trainStop) {
		
		rout.add(trainStop);
		
		if (going == null) {
			
			going = trainStop.getRailLocation();
			goingIndex = 0;
			path = Path.pathFind(location, going);
			
		}
		
	}
	
	public void addDestination(int index, TrainStop trainStop) {
		
		rout.add(index, trainStop);
		
		if (going == null) {
			
			going = trainStop.getRailLocation();
			path = Path.pathFind(location, going);
			path.setTrain(this);
			
		}
		
	}
	
	public void removeDestination(int index) {
		
		rout.remove(index);
		
		if (rout.size() == 0) {
			
			going = null;
			path = null;
			
		}
		
	}
	
	public ArrayList<TrainStop> getRout() {
		
		return rout;
		
	}
	
	private RailLocation getLocationBehind(RailLocation another) {
		
		return another.alongRail(-TrainSection.TRAIN_LENGTH * TrainSection.ALONG_TRACK_LENGTH_MULTIPLYER, been);
		
	}
	
	@Override
	public boolean willDrawCollider() {
		
		return willDrawCollider;
		
	}
	
	@Override
	public void setDrawCollider(boolean willDrawCollider) {
		
		this.willDrawCollider = willDrawCollider;
		
	}
	
	@Override
	public boolean isByRail() {
		
		return false;
		
	}
	
	public boolean isRunning() {
		
		return running;
		
	}
	
	public void setRunning(boolean running) {
		
		this.running = running;
		
	}
	
	public ArrayList<TrainSection> getSections() {
		
		return sections;
		
	}
	
	public void setSections(ArrayList<TrainSection> sections) {
		
		this.sections = sections;
		
	}
	
	public RailLocation getLocation() {
		
		return location;
		
	}
	
	public void setSpeed(double speed) {
		
		this.speed = speed;
		
	}
	
}
