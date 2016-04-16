package items;

import java.io.Serializable;

import utils.BezierCurves;
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
	
	public void setForward(boolean forward) {
		
		this.forward = forward;
		
	}
	
	public boolean isForward() {
		
		return forward;
		
	}
	
	public Vector2 getVector() {
		
		return new Vector2(BezierCurves.cubicBezier(connection.p0.x, connection.p1.x, connection.p2.x, connection.p3.x, t), //
				BezierCurves.cubicBezier(connection.p0.y, connection.p1.y, connection.p2.y, connection.p3.y, t));
				
	}
	
	public Vector2 getDerivativeVector() {
		
		if (forward) {
			
			return new Vector2(BezierCurves.cubicBezierDerivative(connection.p0.x, connection.p1.x, connection.p2.x, connection.p3.x, t), //
					BezierCurves.cubicBezierDerivative(connection.p0.y, connection.p1.y, connection.p2.y, connection.p3.y, t));
					
		} else {
			
			return new Vector2(-BezierCurves.cubicBezierDerivative(connection.p0.x, connection.p1.x, connection.p2.x, connection.p3.x, t), //
					-BezierCurves.cubicBezierDerivative(connection.p0.y, connection.p1.y, connection.p2.y, connection.p3.y, t));
					
		}
		
	}
	
	@Override
	public String toString() {
		
		return "t=" + t + "\t@" + connection;
		
	}
	
}
