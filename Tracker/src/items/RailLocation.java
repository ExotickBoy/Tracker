package items;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

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
	
	public RailLocation(RailConnection connection, boolean forward) {
		
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
	
	public double getDirection() {
		
		return Vector2.atan2(getDerivative());
		
	}
	
	public AffineTransform getRailPointTransform() {
		
		Vector2 position = getPoint();
		double ang = getDirection();
		
		return new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		
	}
		
	public RailLocation alongRail(double along, ArrayList<RailConnection> connections) {
		
		double trying = getConnection().getDistanceFromStart(getT()) + (isForward() ? -along : along);
		
		if (trying < 0) {
			
			double left = trying;
			Optional<RailConnection> nextConnection = connections.stream().filter((connection) -> {
				return (isForward() ? connection.has(getConnection().point2) : connection.has(getConnection().point1)) && connection != getConnection();
			}).findFirst();
			
			if (nextConnection.isPresent()) {
				
				boolean newDirection = nextConnection.get().isSameDirection(getConnection()) ? isForward() : !isForward();
				return new RailLocation(nextConnection.get().isSameDirection(getConnection()) ? 1 : 0, nextConnection.get(), newDirection).alongRail(left, connections);
				
			} else {
				
				return this;
				
			}
			
		} else if (trying > getConnection().getLength()) {
			
			double left = getConnection().getLength() - trying;
			Optional<RailConnection> nextConnection = connections.stream().filter((connection) -> {
				return (isForward() ? connection.has(getConnection().point2) : connection.has(getConnection().point1)) && connection != getConnection();
			}).findFirst();
			
			if (nextConnection.isPresent()) {
				
				boolean newDirection = nextConnection.get().isSameDirection(getConnection()) ? isForward() : !isForward();
				return new RailLocation(nextConnection.get().isSameDirection(getConnection()) ? 0 : 1, nextConnection.get(), newDirection).alongRail(left, connections);
				
			} else {
				
				return this;
				
			}
			
		} else {
			
			return new RailLocation(getConnection().getTAtDistanceFromStart(trying), getConnection(), isForward());
			
		}
		
	}
	
}
