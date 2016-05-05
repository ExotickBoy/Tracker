package items;

import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;

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
		int index = connections.indexOf(getConnection()) + 1;
		
		if (trying < 0 || trying > getConnection().getLength()) {
			
			if (index < connections.size()) {
				
				RailConnection nextConnection = connections.get(index);
				boolean newDirection = nextConnection.isSameDirection(getConnection()) ? isForward() : !isForward();
				
				double left = -signum(along);
				double t;
				
				if (trying < 0) {
					
					left *= trying;
					t = nextConnection.isSameDirection(getConnection()) ? 1 : 0;
					
				} else {
					
					left *= getConnection().getLength() - trying;
					t = nextConnection.isSameDirection(getConnection()) ? 0 : 1;
					
				}
								
				return new RailLocation(t, nextConnection, newDirection).alongRail(left, connections);
				
			} else {
				
				return this;
				
			}
			
		} else {
			
			return new RailLocation(getConnection().getTAtDistanceFromStart(trying), getConnection(), isForward());
			
		}
		
	}
	
	public boolean isSameConnection(RailLocation location) {
		
		return getConnection() == location.getConnection() && isForward() == location.isForward();
		
	}
	
}
