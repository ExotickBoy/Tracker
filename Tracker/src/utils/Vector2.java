package utils;

import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

import java.io.Serializable;

public final class Vector2 implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public double x;
	public double y;
	
	public Vector2(double x, double y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public Vector2(double a) {
		
		this(a, a);
		
	}
	
	public Vector2() {
		
		this(0);
		
	}
	
	public Vector2(Vector2 clone) {
		
		this.x = clone.x;
		this.y = clone.y;
		
	}
	
	public static Vector2 add(Vector2 a, Vector2 b) {
		
		return new Vector2(a.x + b.x, a.y + b.y);
		
	}
	
	public static Vector2 subtract(Vector2 a, Vector2 b) {
		
		return new Vector2(a.x - b.x, a.y - b.y);
		
	}
	
	public static Vector2 multiply(Vector2 a, Vector2 b) {
		
		return new Vector2(a.x * b.x, a.y * b.y);
		
	}
	
	public static Vector2 divide(Vector2 a, Vector2 b) {
		
		return new Vector2(a.x / b.x, a.y / b.y);
		
	}
	
	public static Vector2 add(Vector2 a, double b) {
		
		return new Vector2(a.x + b, a.y + b);
		
	}
	
	public static Vector2 subtract(Vector2 a, double b) {
		
		return new Vector2(a.x - b, a.y - b);
		
	}
	
	public static Vector2 multiply(Vector2 a, double b) {
		
		return new Vector2(a.x * b, a.y * b);
		
	}
	
	public static Vector2 divide(Vector2 a, double b) {
		
		return new Vector2(a.x / b, a.y / b);
		
	}
	
	public static Vector2 rotate(Vector2 a, double angle) { // angle in radians
		
		return new Vector2(cos(angle) * a.x - sin(angle) * a.y, sin(angle) * a.x + cos(angle) * a.y);
		
	}
	
	public static double project(Vector2 a, Vector2 b) {
		
		return ((a.x * b.x) + (a.y * b.y)) / magnitude(b);
		
	}
	
	public static double dot(Vector2 a, Vector2 b) {
		
		return a.x * b.x + a.y * b.y;
		
	}
	
	public static double magnitude(Vector2 a) {
		
		return hypot(a.x, a.y);
		
	}
	
	public static Vector2 normalise(Vector2 a) {
		
		return Vector2.divide(a, magnitude(a));
		
	}
	
	public static double distance(Vector2 a, Vector2 b) {
		
		return hypot(a.x - b.x, a.y - b.y);
		
	}
	
	public static double atan2(Vector2 a) {
		
		return Math.atan2(a.x, a.y);
		
	}
		
	@Override
	public String toString() {
		
		return x + "\t " + y;
		
	}
	
}
