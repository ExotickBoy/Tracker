package core;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import interfaces.Drawable;
import items.RailConnection;
import items.RailLocation;
import items.Train;
import utils.RootFinder;

public class Path implements Drawable {
	
	private static final double METERS_PER_PIXEL = 15;
	
	private static final String RED_ARROW_TEXTURE_PATH = "src/path_red.png";
	private static final String AMBER_ARROW_TEXTURE_PATH = "src/path_amber.png";
	private static final String GREEN_ARROW_TEXTURE_PATH = "src/path_green.png";
	
	private static final int RED_COLOR = 0;
	private static final int AMBER_COLOR = 1;
	private static final int GREEN_COLOR = 2;
	
	private static final int ARROW_WIDTH = 30;
	private static final int ARROW_LENGTH = 15;
	private static final double SPACE_BETWEEN_ARROWS = 30;
	
	private transient static BufferedImage redArrowTexture;
	private transient static BufferedImage amberArrowTexture;
	private transient static BufferedImage greenArrowTexture;
	
	private static HashMap<Integer, BufferedImage> colorToImage = new HashMap<>();
	
	ArrayList<RailLocation> connections;
	Train train;
	
	boolean isFinished;
	
	double lenghtInPixels;
	double length;
	
	long startsTime;
	
	double accelerateTo;
	double cruisTo;
	double brakeTo;
	
	private double until;
	
	private double traveledInCruis;
	private double traveledInDeceleration;
	private double traveledInAcceleration;
	
	private RailLocation from;
	private RailLocation to;
	
	static {
		
		try {
			
			redArrowTexture = ImageIO.read(new File(RED_ARROW_TEXTURE_PATH));
			amberArrowTexture = ImageIO.read(new File(AMBER_ARROW_TEXTURE_PATH));
			greenArrowTexture = ImageIO.read(new File(GREEN_ARROW_TEXTURE_PATH));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		colorToImage.put(RED_COLOR, redArrowTexture);
		colorToImage.put(AMBER_COLOR, amberArrowTexture);
		colorToImage.put(GREEN_COLOR, greenArrowTexture);
		
	}
	
	private Path(ArrayList<RailLocation> connections) {
		
		this.connections = connections;
		
		from = connections.get(0);
		to = connections.get(connections.size() - 1);
		
		lenghtInPixels = connections.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum();
		lenghtInPixels -= (from.isForward() ? 1 - from.getT() : from.getT()) * from.getConnection().getLength();
		lenghtInPixels -= (to.isForward() ? to.getT() : 1 - to.getT()) * to.getConnection().getLength();
		// System.out.println((to.isForward() ? 1 - to.getT() : to.getT()) * to.getConnection().getLength());
		// System.out.println(to.getT() + " " + to.getConnection().length);
		length = lenghtInPixels / METERS_PER_PIXEL;
		
	}
	
	public void setStartTime(long startTime) {
		
		this.startsTime = startTime;
		
		System.out.println("starting");
		
	}
	
	public long getStartTime() {
		
		return startsTime;
		
	}
	
	public void setTrain(Train train) {
		
		this.train = train;
		
		double accelerationTime = (Train.MAX_SPEED / train.getMaxAcceleration());
		double decelerationTime = (Train.MAX_SPEED / train.getMaxDeceleration());
		
		accelerateTo = accelerationTime;
		
		traveledInAcceleration = .5 * train.getMaxAcceleration() * pow(accelerationTime, 2);
		traveledInDeceleration = .5 * train.getMaxDeceleration() * pow(decelerationTime, 2);
		traveledInCruis = length - (traveledInAcceleration + traveledInDeceleration);
		
		cruisTo = (traveledInCruis / Train.MAX_SPEED) + accelerateTo;
		brakeTo = cruisTo + decelerationTime;
		
		if (traveledInCruis < 0) {
			
			// double a = train.getMaxAcceleration() + train.getMaxDeceleration();
			// double b = 0;
			// double c = -2 * length;
			
			double d = train.getMaxDeceleration();
			double a = train.getMaxAcceleration();
//			
//			if (a == d) {
//				
//				brakeTo = sqrt(2) * sqrt(length);
//				
//			} else {
				
				double ac = ((a * d * d + d * d * d) / (a * a + 2 * a * d + d * d)) - ((2 * d * d) / (a + d)) + d;
				double bc = 0;
				double cc = -2 * length;
				
				brakeTo = abs(RootFinder.quadraticRoots(ac, bc, cc)[0]);
				
//			}
			
			accelerateTo = (d * brakeTo) / (a + d);
			cruisTo = accelerateTo;
			
		}
		
	}
	
	public void updateTrain(long time) {
		
		double timePassed = (time - startsTime) / 1000.; // s
		
		double speed = 0; // m/s
		double traveled = 0; // metres
		
		if (timePassed < accelerateTo) {
			
			speed = timePassed * train.getMaxAcceleration();
			traveled = .5 * train.getMaxAcceleration() * pow(timePassed, 2);
			
		} else if (timePassed < cruisTo) {
			
			speed = Train.MAX_SPEED;
			traveled = .5 * train.getMaxAcceleration() * pow(accelerateTo, 2) + Train.MAX_SPEED * (timePassed - accelerateTo);
			
		} else if (timePassed < brakeTo) {
			
			speed = (brakeTo - timePassed) * train.getMaxDeceleration();
			traveled = length - .5 * train.getMaxDeceleration() * pow(timePassed - brakeTo, 2);
			
		} else {
			
			traveled = length;
			isFinished = true;
			
		}
		
		train.setSpeed(speed);
		
		until = traveled * METERS_PER_PIXEL + ((from.isForward() ? 1 - from.getT() : from.getT()) * from.getConnection().getLength());
		connections.stream().anyMatch((connection) -> {
			
			until -= connection.getConnection().getLength();
			
			if (until < 0) {
				
				train.setRailLocation(
						new RailLocation(connection.isForward() ? (-until / connection.getConnection().getLength()) : 1 - (-until / connection.getConnection().getLength()),
								connection.getConnection(), connection.isForward()));
				train.recalculateSections();
				return true;
				
			} else {
				
				return false;
				
			}
			
		});
		
	}
	
	public static Path pathFind(RailLocation from, RailLocation to) {
		
		ArrayList<RailLocation> connections = trace(new ArrayList<>(), from, to);
		
		if (connections == null || (from.isForward() == to.isForward() && from.getT() == to.getT() && from.getConnection() == to.getConnection())) {
			
			System.out.println("no path");
			return null;
			
		} else {
			
			return new Path(connections);
			
		}
		
	}
	
	public static ArrayList<RailLocation> trace(ArrayList<RailLocation> been, RailLocation from, RailLocation to) {
		
		if (from.getConnection() == to.getConnection() && from.isForward() == to.isForward() && //
				((to.isForward() && to.getT() < from.getT()) || (!to.isForward() && to.getT() > from.getT()))) {
				
			been.add(to);
			return been;
			
		} else {
			
			been.add(from);
			return from.getConnection().getConnections().stream().filter((RailConnection connection) -> {
				
				return connection.has(from.isForward() ? from.getConnection().point1 : from.getConnection().point2);
				
			}).filter((connection) -> {
				
				return been.stream().noneMatch((fromPath) -> {
					
					boolean direction = connection.point1 == (from.isForward() ? from.getConnection().point1 : from.getConnection().point2);
					
					return fromPath.getConnection() == connection && direction == from.isForward();
					
				});
				
			}).filter((connection) -> {
				
				return from.getConnection().canPass(connection);
				
			}).map((connection) -> {
				
				boolean forward = connection.isSameDirection(from.getConnection()) ? from.isForward() : !from.isForward();
				
				RailLocation from2 = new RailLocation(forward ? 1 : 0, connection, forward);
				
				return trace(new ArrayList<>(been), from2, to);
				
			}).filter(Objects::nonNull).min((a, b) -> {
				
				return Double.compare(a.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum(),
						b.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum());
						
			}).orElse(null);
			
		}
		
	}
	
	public boolean isFinished() {
		
		return isFinished;
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		int amount = (int) (length * METERS_PER_PIXEL / SPACE_BETWEEN_ARROWS);
		
		int index = 1;
		RailLocation at = from;
		for (int i = 0; i < amount; i++) {
			
			AffineTransform before = g.getTransform();
			AffineTransform transfrom = at.getRailPointTransform();
			g.transform(transfrom);
			
			g.drawImage(colorToImage.get(1), -ARROW_WIDTH / 2, 0, ARROW_WIDTH, ARROW_LENGTH, null);
			
			g.setTransform(before);
			
			double left = length * METERS_PER_PIXEL / amount;
			
			if (at.isForward()) {
				
				if (at.getConnection().getDistanceFromStart(at.getT()) > left) {
					
					at = new RailLocation(at.getConnection().getTAtDistanceFromStart(at.getConnection().getDistanceFromStart(at.getT()) - left), at.getConnection(),
							at.isForward());
							
				} else {
					
					left -= at.getConnection().getDistanceFromStart(at.getT());
					
					RailConnection newConnection;
					
					if (index == connections.size()) {
						
						break;
						
					} else {
						
						newConnection = connections.get(index++).getConnection();
						at = new RailLocation(
								at.getConnection().isSameDirection(newConnection) ? newConnection.getTAtDistanceFromEnd(left) : newConnection.getTAtDistanceFromStart(left),
								newConnection, at.getConnection().isSameDirection(newConnection) ? at.isForward() : !at.isForward());
					}
					
				}
				
			} else {
				
				if (at.getConnection().getDistanceFromEnd(at.getT()) > left) {
					
					at = new RailLocation(at.getConnection().getTAtDistanceFromStart(at.getConnection().getDistanceFromStart(at.getT()) + left), at.getConnection(),
							at.isForward());
							
				} else {
					
					left -= at.getConnection().getDistanceFromEnd(at.getT());
					
					RailConnection newConnection;
					
					if (index == connections.size()) {
						
						break;
						
					} else {
						
						newConnection = connections.get(index++).getConnection();
						at = new RailLocation(
								at.getConnection().isSameDirection(newConnection) ? newConnection.getTAtDistanceFromStart(left) : newConnection.getTAtDistanceFromEnd(left),
								newConnection, at.getConnection().isSameDirection(newConnection) ? at.isForward() : !at.isForward());
					}
					
				}
				
			}
			
		}
		
	}
	
}
