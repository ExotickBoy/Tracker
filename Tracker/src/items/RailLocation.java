package items;

import static java.lang.Math.cos;
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
	
	public double getDirection() {
		
		return Vector2.atan2(getDerivative());
		
	}
	
	public AffineTransform getRailPointTransform() {
		
		Vector2 position = getPoint();
		double ang = getDirection();
		
		return new AffineTransform(cos(ang), -sin(ang), sin(ang), cos(ang), position.x, position.y);
		
	}
	
	public RailLocation downRail(double length, ArrayList<RailConnection> connections) {
		
		if (isForward()) {
			
			if (getT() > 0 && getConnection().getDistanceFromStart(getT()) > length) {
				
				return new RailLocation(getConnection().getTAtDistanceFromStart(getConnection().getDistanceFromStart(getT()) - length), getConnection(), isForward());
				
			} else {
				
				double left = length - getConnection().getDistanceFromStart(getT());
				
				int index = connections.indexOf(this.getConnection());
				RailConnection newConnection = connections.get(index + 1);
				
				return new RailLocation(getConnection().isSameDirection(newConnection) ? newConnection.getTAtDistanceFromEnd(left) : newConnection.getTAtDistanceFromStart(left),
						newConnection, getConnection().isSameDirection(newConnection) ? isForward() : !isForward()).downRail(length, connections);
			}
			
		} else {
			
			if (getT() > 0 && getConnection().getDistanceFromEnd(getT()) > length) {
				
				return new RailLocation(getConnection().getTAtDistanceFromEnd(getConnection().getDistanceFromEnd(getT()) - length), getConnection(), isForward());
				
			} else {
				
				double left = length + getConnection().getDistanceFromEnd(getT());
				
				int index = connections.indexOf(this.getConnection());
				RailConnection newConnection = connections.get(index + 1);
				
				System.out.println();
				
				return new RailLocation(getConnection().isSameDirection(newConnection) ? 0 : 1, newConnection,
						getConnection().isSameDirection(newConnection) ? isForward() : !isForward()).downRail(length, connections);
			}
			
		}
		
		// return this;
		
	}
	
}
