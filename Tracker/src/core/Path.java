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
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import interfaces.Drawable;
import items.RailConnection;
import items.RailLocation;
import items.Train;
import utils.RootFinder;

public class Path implements Drawable {
	
	private static final double PIXEL_PER_METRES = 15;
	
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
	
	private ArrayList<RailLocation> connections;
	private Train train;
	
	private boolean isFinished = false;
	
	private double lengthInPixels;
	private double length;
	
	private double traveled;
	private double speed;
	
	private double acc;
	private double dcc;
	
	private long startsTime;
	
	private double accelerateTo;
	private double cruisTo;
	private double brakeTo;
	
	private double traveledInCruis;
	private double traveledInDeceleration;
	private double traveledInAcceleration;
	
	private RailLocation from;
	private RailLocation to;
	
	private HashMap<Double, RailLocation> arrows = new HashMap<>();
	
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
	
	private Path(ArrayList<RailLocation> connections, RailLocation from, RailLocation to) {
		
		this.connections = connections;
		
		this.from = from;
		this.to = to;
		
		lengthInPixels = connections.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum();
		lengthInPixels -= from.isForward() ? from.getConnection().getDistanceFromEnd(from.getT()) : from.getConnection().getDistanceFromStart(from.getT());
		lengthInPixels -= to.isForward() ? to.getConnection().getDistanceFromStart(to.getT()) : to.getConnection().getDistanceFromEnd(to.getT());
		length = lengthInPixels / PIXEL_PER_METRES;
		
		// arrow
		
		int amount = (int) ((length - traveled) * PIXEL_PER_METRES / SPACE_BETWEEN_ARROWS);
		for (int i = 0; i <= amount; i++) {
			
			double along = (traveled - length) * i / amount;
			RailLocation at = to.alongRail(along * PIXEL_PER_METRES,
					connections.stream().map(RailLocation::getConnection).collect(ArrayList::new, (list, e) -> list.add(0, e), (list1, list2) -> list1.addAll(0, list2)));
			arrows.put(along, at);
			
		}
		
	}
	
	public void setStartTime(long startTime) {
		
		this.startsTime = startTime;
		
		traveled = 0;
		updatePlot(train);
		
		System.out.println("starting");
		
	}
	
	public long getStartTime() {
		
		return startsTime;
		
	}
	
	public void setTrain(Train train) {
		
		this.train = train;
		
	}
	
	public void updatePlot(Train train) {
		
		acc = train.getMaxAcceleration();
		dcc = train.getMaxDeceleration();
		
		double accelerationTime = (Train.MAX_SPEED / acc);
		double decelerationTime = (Train.MAX_SPEED / dcc);
		
		accelerateTo = accelerationTime;
		
		traveledInAcceleration = .5 * acc * pow(accelerationTime, 2);
		traveledInDeceleration = .5 * dcc * pow(decelerationTime, 2);
		traveledInCruis = length - (traveledInAcceleration + traveledInDeceleration);
		
		cruisTo = (traveledInCruis / Train.MAX_SPEED) + accelerateTo;
		brakeTo = cruisTo + decelerationTime;
		
		if (traveledInCruis < 0) {
			
			double d = dcc;
			double a = acc;
			
			double ac = ((a * d * d + d * d * d) / (a * a + 2 * a * d + d * d)) - ((2 * d * d) / (a + d)) + d;
			double bc = 0;
			double cc = -2 * length;
			
			brakeTo = abs(RootFinder.quadraticRoots(ac, bc, cc)[0]);
			
			accelerateTo = (d * brakeTo) / (a + d);
			cruisTo = accelerateTo;
			
		}
		
	}
	
	public void updateTrain(long time) {
		
		double timePassed = (time - startsTime) / 1000.; // s
		
		speed = timePassedToSpeed(timePassed);
		traveled = timePassedToTraveled(timePassed);
		
		if (isFinished = timePassed > brakeTo) {
			
			train.setRailLocation(to);
			
		} else {
			
			train.setRailLocation(
					from.alongRail(traveled * PIXEL_PER_METRES, connections.stream().map(RailLocation::getConnection).collect(Collectors.toCollection(ArrayList::new))));
			
		}
		train.recalculateSections();
		train.setSpeed(speed);
		
	}
	
	private double timePassedToTraveled(double timePassed) {
		
		if (timePassed < accelerateTo) {
			
			return .5 * acc * pow(timePassed, 2);
			
		} else if (timePassed < cruisTo) {
			
			return .5 * acc * pow(accelerateTo, 2) + Train.MAX_SPEED * (timePassed - accelerateTo);
			
		} else if (timePassed < brakeTo) {
			
			return length - .5 * dcc * pow(timePassed - brakeTo, 2);
			
		} else {
			
			return length;
			
		}
		
	}
	
	private double timePassedToSpeed(double timePassed) {
		
		if (timePassed < accelerateTo) {
			
			return timePassed * acc;
			
		} else if (timePassed < cruisTo) {
			
			return Train.MAX_SPEED;
			
		} else if (timePassed < brakeTo) {
			
			return (brakeTo - timePassed) * dcc;
			
		} else {
			
			return traveled = length;
			
		}
		
	}
	
	public boolean isFinished() {
		
		return isFinished;
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		arrows.entrySet().stream().filter((p) -> {
			return length + p.getKey() > traveled;
		}).forEach(p -> {
			
			AffineTransform before = g.getTransform();
			AffineTransform transfrom = p.getValue().getRailPointTransform();
			g.transform(transfrom);
			
			g.drawImage(colorToImage.get(1), -ARROW_WIDTH / 2, 0, ARROW_WIDTH, ARROW_LENGTH, null);
			
			g.setTransform(before);
			
		});
		
	}
	
	public static Path pathFind(RailLocation from, RailLocation to) {
		
		ArrayList<RailLocation> connections = trace(new ArrayList<>(), from, to);
		
		if (connections == null || (from.isForward() == to.isForward() && from.getT() == to.getT() && from.getConnection() == to.getConnection())) {
			
			System.out.println("no path");
			return null;
			
		} else {
			
			return new Path(connections, from, to);
			
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
	
}
