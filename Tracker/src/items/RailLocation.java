package items;

import java.io.Serializable;

import utils.Vector2;

public final class RailLocation implements Serializable {
	
	private static final long serialVersionUID = 0L;
	
	double t;
	RailConnection connection;
	boolean forward;
	
	public RailLocation(double t, RailConnection connection, boolean forward) {
		
		this.t = t;
		this.connection = connection;
		this.forward = forward;
		
	}
	
	public RailLocation(double t, RailConnection connection) {
		
		this(t, connection, true);
		
	}
	
	public RailLocation(boolean forward, RailConnection connection) {
		
		this(0, connection, forward);
		
	}
	
	public RailLocation(RailLocation clone) {
		
		this.t = clone.t;
		this.connection = clone.connection;
		this.forward = clone.forward;
		
	}
	
	public void setForward(boolean forward) {
		
		this.forward = forward;
		
	}
	
	public boolean isForward() {
		
		return forward;
		
	}
	
	public void toggleForward() {
		
		this.forward = !forward;
		
	}
	
	public double getT() {
		
		return t;
		
	}
	
	public RailConnection getConnection() {
		
		return connection;
		
	}
	
	@Override
	public String toString() {
		
		return "t=" + t + "\t@" + connection;
		
	}
	
	public Vector2 getPoint() {
		
		return connection.getPoint(t);
		
	}
	
	public Vector2 getDerivative() {
		
		return Vector2.multiply(connection.getDerivative(t), isForward() ? 1 : -1);
		
	}
	
}
