package utils;

import static java.lang.Math.hypot;
import static java.lang.Math.pow;

class BezierUtils {
	
	static double quadraticBezier(double p0, double p1, double p2, double t) {
		
		return pow(1 - t, 2) * p0 + 2 * (1 - t) * t * p1 + pow(t, 2) * p2;
		
	}
	
	static Vector2 quadraticBezier(Vector2 p0, Vector2 p1, Vector2 p2, double t) {
		
		return new Vector2(quadraticBezier(p0.x, p1.x, p2.x, t), quadraticBezier(p0.y, p1.y, p2.y, t));
		
	}
	
	static double quadraticBezierDerivative(double p0, double p1, double p2, double t) {
		
		return 2 * (1 - t) * (p1 - p0) + 2 * t * (p2 - p1);
		
	}
	
	static Vector2 quadraticBezierDerivative(Vector2 p0, Vector2 p1, Vector2 p2, double t) {
		
		return new Vector2(quadraticBezierDerivative(p0.x, p1.x, p2.x, t), quadraticBezierDerivative(p0.y, p1.y, p2.y, t));
		
	}
	
	static double quadraticBezierSecondDerivative(double p0, double p1, double p2, double t) {
		
		return 2 * (p2 - 2 * p1 + p0);
		
	}
	
	static Vector2 quadraticBezierSecondDerivative(Vector2 p0, Vector2 p1, Vector2 p2, double t) {
		
		return new Vector2(quadraticBezierSecondDerivative(p0.x, p1.x, p2.x, t), quadraticBezierSecondDerivative(p0.y, p1.y, p2.y, t));
		
	}
	
	static double quadraticBezierArcLength(Vector2 p0, Vector2 p1, Vector2 p2, int resolution) {
		
		double length = 0;
		
		double lastX = p0.x;
		double lastY = p0.y;
		
		for (int i = 1; i < resolution; i++) {
			
			double t = (double) i / resolution;
			
			double x = quadraticBezier(p0.x, p1.x, p2.x, t);
			double y = quadraticBezier(p0.y, p1.y, p2.y, t);
			
			length += hypot(lastX - x, lastY - y);
			
			lastX = x;
			lastY = y;
			
		}
		
		return length;
		
	}
	
	static double cubicBezier(double p0, double p1, double p2, double p3, double t) {
		
		return pow(1 - t, 3) * p0 + 3 * pow(1 - t, 2) * t * p1 + 3 * (1 - t) * pow(t, 2) * p2 + pow(t, 3) * p3;
		
	}
	
	static Vector2 cubicBezier(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double t) {
		
		return new Vector2(cubicBezier(p0.x, p1.x, p2.x, p3.x, t), cubicBezier(p0.y, p1.y, p2.y, p3.y, t));
		
	}
	
	static double cubicBezierDerivative(double p0, double p1, double p2, double p3, double t) {
		
		return 3 * pow(1 - t, 2) * (p1 - p0) + 6 * (1 - t) * t * (p2 - p1) + 3 * pow(t, 2) * (p3 - p2);
		
	}
	
	static Vector2 cubicBezierDerivative(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double t) {
		
		return new Vector2(cubicBezierDerivative(p0.x, p1.x, p2.x, p3.x, t), cubicBezierDerivative(p0.y, p1.y, p2.y, p3.y, t));
		
	}
	
	static double cubicBezierSecondDerivative(double p0, double p1, double p2, double p3, double t) {
		
		return 6 * (1 - t) * (p2 - 2 * p1 - p0) + 6 * t * (p3 - 2 * p2 + p1);
		
	}
	
	static Vector2 cubicBezierSecondDerivative(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, double t) {
		
		return new Vector2(cubicBezierSecondDerivative(p0.x, p1.x, p2.x, p3.x, t), cubicBezierSecondDerivative(p0.y, p1.y, p2.y, p3.y, t));
		
	}
	
	static double[] cubicBezierTToDistance(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, int resolution) {
		
		double[] data = new double[resolution];
		
		double length = 0;
		double lastX = p0.x;
		double lastY = p0.y;
		
		for (int i = 0; i < resolution; i++) {
			
			double t = (double) i / resolution;
			
			double x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t);
			double y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t);
			
			length += hypot(lastX - x, lastY - y);
			data[i] = length;
			
			lastX = x;
			lastY = y;
			
		}
		
		return data;
		
	}
	
	static double cubicBezierArcLength(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, int resolution) {
		
		double length = 0;
		
		double lastX = p0.x;
		double lastY = p0.y;
		
		for (int i = 1; i < resolution; i++) {
			
			double t = (double) i / resolution;
			
			double x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t);
			double y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t);
			
			length += hypot(lastX - x, lastY - y);
			
			lastX = x;
			lastY = y;
			
		}
		
		return length;
		
	}
	
	static double cubicClosesAproach(int resolution, Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, Vector2 to) {
		
		p0 = Vector2.subtract(p0, to);
		p1 = Vector2.subtract(p1, to);
		p2 = Vector2.subtract(p2, to);
		p3 = Vector2.subtract(p3, to);
		
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
	
	static double cubicClosesAproach(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, Vector2 to) {
		
		return cubicClosesAproach((int) cubicBezierArcLength(p0, p1, p2, p3, 100), p0, p1, p2, p3, to);
		
	}
	
}
