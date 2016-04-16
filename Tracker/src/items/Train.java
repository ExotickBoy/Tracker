package items;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;

import interfaces.Drawable;
import interfaces.OnRail;
import interfaces.Selectable;
import utils.Collider;

public final class Train implements Serializable, Drawable, OnRail, Selectable {
	
	private static final long serialVersionUID = 0L;
	
	private static final String DEFAULT_TRAIN = "Train";
	
	private HashMap<Train, String> trainToName = new HashMap<>();
	
	String name;
	
	RailLocation location;
	RailLocation going;
	double speed = 0;
	
	public ArrayList<TrainSection> sections = new ArrayList<>();
	
	public Train(RailLocation location) {
		
		this.location = location;
		addSection(TrainSection.Locomotive::new);
		
	}
	
	public void draw(Graphics2D g) {
		
		sections.forEach((trainSection) -> {
			
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
		
		return location;
		
	}
	
	@Override
	public Collider getCollider() {
		
		return sections.stream().map(TrainSection::getCollider).reduce(Collider::combineCollider).get();
		
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
		
		return sections.stream().mapToDouble(TrainSection::getMass).sum();
		
	}
	
	public double getMaxAcceleratingForce() {
		
		return sections.stream().mapToDouble(TrainSection::getMaxAcceleratingForce).sum();
		
	}
	
	public double getMaxBrakingForce() {
		
		return sections.stream().mapToDouble(TrainSection::getMaxBrakingForce).sum();
		
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
		
		if (sections.size() == 0) {
			
			newLocation = location;
			
		} else {
			
			RailLocation lastLocation = sections.get(sections.size() - 1).location;
			
			newLocation = getLocationBehind(lastLocation);
			
		}
		
		TrainSection section = getter.apply(newLocation);
		sections.add(section);
		
	}
	
	public void removeSection(int sectionIndex) {
		
		sections.remove(sectionIndex);
		recalculateSections();
	}
	
	public void removeSection(TrainSection section) {
		
		sections.remove(sections);
		recalculateSections();
		
	}
	
	public void recalculateSections() {
		
		if (sections.size() > 0) {
			
			sections.get(0).location = location;
			
			if (sections.size() > 1) {
				
				for (int i = 1; i < sections.size(); i++) {
					
					sections.get(i).location = getLocationBehind(sections.get(i - 1).location);
					
				}
				
			}
			
		}
		
	}
	
	private RailLocation getLocationBehind(RailLocation another) {
		
		another.connection.updateLength();
		
		double t = another.t + (another.forward ? 1 : -1) * TrainSection.TRAIN_LENGTH / another.connection.length;
		
		return new RailLocation(t, another.connection);
		
	}
	
	@Override
	public boolean willDrawCollider() {
		
		return true;
		
	}
	
}
