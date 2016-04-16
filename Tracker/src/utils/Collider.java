package utils;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import interfaces.Drawable;

public class Collider implements Drawable {
	
	public static final class Triangle {
		
		Vector2 point1;
		Vector2 point2;
		Vector2 point3;
		
		public Triangle(Vector2 point1, Vector2 point2, Vector2 point3) {
			
			this.point1 = point1;
			this.point2 = point2;
			this.point3 = point3;
			
		}
		
		private Stream<Vector2> getPoints() {
			
			Builder<Vector2> points = Stream.builder();
			
			points.add(point1);
			points.add(point2);
			points.add(point3);
			
			return points.build();
			
		}
		
		private Stream<Connection> getConnections() {
			
			Builder<Connection> connections = Stream.builder();
			
			connections.add(new Connection(point1, point2));
			connections.add(new Connection(point2, point3));
			connections.add(new Connection(point3, point1));
			
			return connections.build();
			
		}
		
		private boolean isInside(Vector2 otherPoint) {
			
			return pointPastLine(point1, point2, otherPoint) < 0 && pointPastLine(point2, point2, otherPoint) < 0 && pointPastLine(point2, point1, otherPoint) < 0;
			
		}
		
		private double pointPastLine(Vector2 line1, Vector2 line2, Vector2 point) {
			
			Vector2 along = Vector2.rotate(Vector2.subtract(line2, line1), PI / 2);
			Vector2 to = Vector2.subtract(point, line1);
			
			return Vector2.project(along, to);
			
		}
		
		private static class Connection implements Drawable {
			
			private static final Color COLLIDER_COLOR = Color.RED;
			
			Vector2 point1;
			Vector2 point2;
			
			public Connection(Vector2 point1, Vector2 point2) {
				
				this.point1 = point1;
				this.point2 = point2;
				
			}
			
			@Override
			public boolean equals(Object obj) {
				
				if (obj instanceof Connection) {
					
					Connection other = (Connection) obj;
					return (point1 == other.point1 && point2 == other.point2) || (point1 == other.point2 && point2 == other.point1);
					
				} else {
					
					return super.equals(obj);
					
				}
				
			}
			
			@Override
			public void draw(Graphics2D g) {
				
				g.setColor(COLLIDER_COLOR);
				g.draw(new Line2D.Double(point1.x, point1.y, point2.x, point2.y));
				
			}
			
		}
		
	}
	
	private ArrayList<Triangle> triangles;
	
	public Collider() {
		
		this(new ArrayList<Triangle>());
		
	}
	
	public Collider(ArrayList<Triangle> triangles) {
		
		this.triangles = triangles;
		
	}
	
	public void addTriangle(Triangle triangle) {
		
		this.triangles.add(triangle);
		
	}
	
	public Stream<Triangle> getTraingles() {
		
		return triangles.stream();
		
	}
	
	public boolean collidesWith(Collider other) {
		
		return other.triangles.stream().flatMap(Triangle::getPoints).distinct().anyMatch((otherPoint) -> {
			
			return triangles.stream().anyMatch((triangle) -> {
				
				return triangle.isInside(otherPoint);
				
			});
			
		}) || triangles.stream().flatMap(Triangle::getPoints).distinct().anyMatch((point) -> {
			
			return other.triangles.stream().anyMatch((otherTriangle) -> {
				
				return otherTriangle.isInside(point);
				
			});
			
		});
		
	}
	
	public static Collider combineCollider(Collider a, Collider b) {
		
		return new Collider(Stream.concat(a.getTraingles(), b.getTraingles()).collect(Collectors.toCollection(ArrayList::new)));
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		triangles.stream().flatMap(Triangle::getConnections)
				.filter(i -> Collections.frequency(triangles.stream().flatMap(Triangle::getConnections).collect(Collectors.toCollection(ArrayList::new)), i) == 1)
				.forEach((connection) -> {
					
					connection.draw(g);
					
				});
				
	}
	
}
