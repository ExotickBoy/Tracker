package items;

import java.awt.Graphics2D;
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
	
	public static final double MAX_SPEED = 10; // m/s
	
	private HashMap<Train, String> trainToName = new HashMap<>();
	
	private boolean willDrawCollider;
	
	String name;
	
	private RailLocation location;
	private transient Path path;
	private double speed = 0;
	private boolean running;
	
	private ArrayList<TrainSection> sections = new ArrayList<>();
	
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
	
	public void draw(Graphics2D g) {
		
		getSections().forEach((trainSection) -> {
			
			trainSection.draw(g);
			
		});
		
	}
	
	@Override
	public void setRailLocation(RailLocation location) {
		
		this.location = location;
		
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
					
					getSections().get(i).location = getLocationBehind(getSections().get(i - 1).location);
					
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
				
		return downRail(another, TrainSection.TRAIN_LENGTH);
		
	}
	
	private RailLocation downRail(RailLocation another, double howFar) {
		
		double newT = another.t + (another.forward ? 1 : -1) * another.connection.getLength() / howFar;
		
		// if (newT > 0 || newT < 1 && been.indexOf(another.getConnection()) < been.size() - 2) {
		//
		// RailConnection newConnection = been.get(been.indexOf(another.getConnection()) + 1);
		// RailLocation newLocation = new RailLocation(another.connection.isSameDirection(newConnection) ? 0 : 1, newConnection,
		// another.connection.isSameDirection(newConnection) ? another.forward : !another.forward);
		//
		// double distanceLeft = howFar - (another.forward ? another.t : 1 - another.t) * another.connection.getLength();
		//
		// return downRail(newLocation, distanceLeft);
		//
		// } else {
		//
		return new RailLocation(newT, another.connection, another.forward);
		//
		// }
		//
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
