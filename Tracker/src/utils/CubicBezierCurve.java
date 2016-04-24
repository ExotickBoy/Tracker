package utils;

import static java.lang.Math.hypot;
import static utils.BezierUtils.*;
import static java.lang.Math.*;

public class CubicBezierCurve {
	
	protected Vector2 p0;
	protected Vector2 p1;
	protected Vector2 p2;
	protected Vector2 p3;
	
	protected double length;
	protected double[] tToLength;
	
	protected int resolution;
	
	public CubicBezierCurve() {
	
	}
	
	public CubicBezierCurve(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, int resolution) {
		
		this.resolution = resolution;
		tToLength = new double[resolution + 1];
		
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
	}
	
	public Vector2 getP0() {
		
		return p0;
		
	}
	
	public Vector2 getP1() {
		
		return p1;
		
	}
	
	public Vector2 getP2() {
		
		return p2;
		
	}
	
	public Vector2 getP3() {
		
		return p3;
		
	}
	
	public void setP0(Vector2 p0) {
		
		this.p0 = p0;
		updateLength();
		
	}
	
	public void setP1(Vector2 p1) {
		
		this.p1 = p1;
		updateLength();
		
	}
	
	public void setP2(Vector2 p2) {
		
		this.p2 = p2;
		updateLength();
		
	}
	
	public void setP3(Vector2 p3) {
		
		this.p3 = p3;
		updateLength();
		
	}
	
	public void setResolution(int resolution) {
		
		this.resolution = resolution;
		tToLength = new double[resolution + 1];
		
	}
	
	public int getResolution() {
		
		return resolution;
		
	}
	
	public double getLength() {
		
		return length;
		
	}
	
	public Vector2 getPoint(double t) {
		
		return cubicBezier(p0, p1, p2, p3, t);
		
	}
	
	public Vector2 getDerivative(double t) {
		
		return cubicBezierDerivative(p0, p1, p2, p3, t);
		
	}
	
	public Vector2 getSecondDerivative(double t) {
		
		return cubicBezierSecondDerivative(p0, p1, p2, p3, t);
		
	}
	
	public double getDistanceFromBegining(double t) {
		
		int startAt = (int) (t * getResolution());
		double between = (t * getResolution() - startAt);
		
		return tToLength[startAt] * between + tToLength[startAt + 1] * (1 - between);
		
	}
	
	public double getDistanceFromEnd(double t) {
		
		return length - getDistanceFromBegining(t);
		
	}
	
	public double getTAtDistanceFromStart(double distance) {
		
		int between = 0;
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for (int i = 0; i < getResolution(); i++) {
			
			max = max(max, tToLength[i]);
			min = min(min, tToLength[i]);
			
			if (tToLength[i] >= distance) {
				
				between = i - 1;
				break;
				
			}
			
		}
		
		if (distance > max) {
			
			return 1;
			
		} else if (distance <= min) {
			
			return 0;
			
		} else {
			
			double beginning = tToLength[between];
			double ending = tToLength[between + 1];
			
			double gap 			=	ending - beginning;
			double difference	=	distance - beginning;
			
			return ((double) between / resolution) + difference / gap / resolution;
			
		}
	}
	
	public double getTAtDistanceFromEnd(double distance) {
		
		return getTAtDistanceFromStart(length - distance);
		
	}
	
	public double getClosestAproach(int resolution, Vector2 to) {
		
		Vector2 p0 = Vector2.subtract(this.p0, to);
		Vector2 p1 = Vector2.subtract(this.p1, to);
		Vector2 p2 = Vector2.subtract(this.p2, to);
		Vector2 p3 = Vector2.subtract(this.p3, to);
		
		double bestT = 0;
		double bestDistance = Double.MAX_VALUE;
		
		for (int i = 0; i <= resolution; i++) {
			
			double t = (double) i / resolution;
			double distance = hypot(cubicBezier(p0.x, p1.x, p2.x, p3.x, t), cubicBezier(p0.y, p1.y, p2.y, p3.y, t));
			
			if (distance < bestDistance) {
				
				bestT = t;
				bestDistance = distance;
				
			}
			
		}
		
		return bestT;
		
	}
	
	public double getClosestAproach(Vector2 to) {
		
		return getClosestAproach(resolution, to);
		
	}
	
	protected void updateLength() {
		
		length = 0;
		
		double lastX = p0.x;
		double lastY = p0.y;
		
		for (int i = 0; i < getResolution(); i++) {
			
			double t = (double) i / getResolution();
			
			double x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t);
			double y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t);
			
			length += hypot(lastX - x, lastY - y);
			tToLength[i] = length;
			
			lastX = x;
			lastY = y;
			
		}
		
		tToLength[getResolution()] = length;
		
	}
	
}
