package core;

public abstract class Snap<T> implements Drawable {
	
	private T destincation;
	private double distance;
	
	public Snap() {
	
	}
	
	public T getDestincation() {
		
		return destincation;
		
	}
	
	public double getDistance() {
		
		return distance;
		
	}
	
	protected void setDestincation(T destincation) {
		
		this.destincation = destincation;
		
	}
	
	protected void setDistance(double distance) {
		
		this.distance = distance;
		
	}
	
	public static int sortByDistance(Snap<?> a, Snap<?> b) {
		
		return Double.compare(a.getDistance(), b.getDistance());
		
	}
	
	public abstract boolean isPermanent();
	
	public final boolean isValid() {
		
		return true;
		
	}
	
}
