package core;

import static java.lang.Math.pow;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import interfaces.Drawable;
import items.RailConnection;
import items.RailLocation;
import items.Train;
import utils.RootFinder;

public class Path implements Drawable {
	
	private static final double METRES_PER_PIXEL = 15;
	
	private static final String RED_ARROW_TEXTURE_PATH = "src/path_red.png";
	private static final String AMBER_ARROW_TEXTURE_PATH = "src/path_amber.png";
	private static final String GREEN_ARROW_TEXTURE_PATH = "src/path_green.png";
	
	private static final int RED_COLOR = 0;
	private static final int AMBER_COLOR = 1;
	private static final int GREEN_COLOR = 2;
	
	private static final int ACCELERATING_COLOR = AMBER_COLOR;
	private static final int CRUISING_COLOR = GREEN_COLOR;
	private static final int DECELERATING_COLOR = RED_COLOR;
	
	private static final int ARROW_WIDTH = 30;
	private static final int ARROW_LENGTH = 15;
	private static final double SPACE_BETWEEN_ARROWS = 30;
	
	private transient static BufferedImage redArrowTexture;
	private transient static BufferedImage amberArrowTexture;
	private transient static BufferedImage greenArrowTexture;
	
	private static ArrayList<BufferedImage> colors = new ArrayList<>();
	
	private ArrayList<RailLocation> connections;
	private Train train;
	
	private boolean isFinished = false;
	
	private double lengthInPixels;
	private double length;
	
	private long startsTime;
	private double traveled;
	private double speed;
	
	private RailLocation from;
	private RailLocation to;
	
	private ArrayList<Integer> arrowColors = new ArrayList<>();
	private ArrayList<Double> arrowDistances = new ArrayList<>();
	private ArrayList<RailLocation> arrowLocations = new ArrayList<>();
	
	private ArrayList<Movement> movements = new ArrayList<>();
	
	static {
		
		try {
			
			redArrowTexture = ImageIO.read(new File(RED_ARROW_TEXTURE_PATH));
			amberArrowTexture = ImageIO.read(new File(AMBER_ARROW_TEXTURE_PATH));
			greenArrowTexture = ImageIO.read(new File(GREEN_ARROW_TEXTURE_PATH));
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		colors.add(redArrowTexture);
		colors.add(amberArrowTexture);
		colors.add(greenArrowTexture);
		
	}
	
	private Path(ArrayList<RailLocation> connections, RailLocation from, RailLocation to) {
		
		this.connections = connections;
		
		this.from = from;
		this.to = to;
		
		lengthInPixels = connections.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum();
		lengthInPixels -= from.isForward() ? from.getConnection().getDistanceFromEnd(from.getT()) : from.getConnection().getDistanceFromStart(from.getT());
		lengthInPixels -= to.isForward() ? to.getConnection().getDistanceFromStart(to.getT()) : to.getConnection().getDistanceFromEnd(to.getT());
		length = lengthInPixels / METRES_PER_PIXEL;
		
		// arrow
		
		int amount = (int) (length * METRES_PER_PIXEL / SPACE_BETWEEN_ARROWS);
		for (int i = 0; i <= amount; i++) {
			
			double along = -length * i / amount;
			RailLocation at = to.alongRail(along * METRES_PER_PIXEL,
					connections.stream().map(RailLocation::getConnection).collect(ArrayList::new, (list, e) -> list.add(0, e), (list1, list2) -> list1.addAll(0, list2)));
			arrowDistances.add(along);
			arrowLocations.add(at);
			arrowColors.add(AMBER_COLOR);
			
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
		
		movements.clear();
		movements.add(new Movement(0, 0, length, 0, 0, train.getMaxAcceleration(), train.getMaxDeceleration()));
		
		IntStream.range(0, arrowDistances.size()).forEach((i) -> {
			
			double distance = length + arrowDistances.get(i);
						
			movements.stream().filter((movement) -> {
				
				return movement.getTraveledOffset() <= distance && distance <= movement.getTraveledOffset() + movement.getDistance();
				
			}).findFirst().ifPresent(movement -> {
				
				int color;
				
				switch (movement.getStateAtTraveled(distance)) {
					
					case Movement.ACCELERATING_STATE:
						
						color = ACCELERATING_COLOR;
						break;
					
					case Movement.CRUISING_STATE:
						
						color = CRUISING_COLOR;
						break;
					
					case Movement.DECELERATING_STATE:
						
						color = DECELERATING_COLOR;
						break;
					
					default:
						
						color = AMBER_COLOR;
						break;
					
				}
				
				arrowColors.set(i, color);
				
			});
			
		});
		
	}
	
	public void updateTrain(long time) {
		
		double timePassed = (time - startsTime) / 1000.; // s
		
		double ends = movements.stream().mapToDouble(Movement::getDuration).sum();
		
		if (ends <= timePassed) {
			
			train.setRailLocation(to);
			train.setSpeed(0);
			isFinished = true;
			
		} else {
			
			speed = movements.stream().filter((movement) -> {
				
				return movement.getStartTime() < timePassed && timePassed < movement.getEndTime();
				
			}).findFirst().get().getSpeedAtTime(timePassed);
			
			traveled = movements.stream().filter((movement) -> {
				
				return movement.getStartTime() < timePassed && timePassed < movement.getEndTime();
				
			}).findFirst().get().getTraveledAtTime(timePassed);
			
			train.setSpeed(speed);
			train.setRailLocation(
					from.alongRail(traveled * METRES_PER_PIXEL, connections.stream().map(RailLocation::getConnection).collect(Collectors.toCollection(ArrayList::new))));
			
		}
		train.recalculateSections();
		
	}
	
	public boolean isFinished() {
		
		return isFinished;
		
	}
	
	@Override
	public void draw(Graphics2D g) {
		
		IntStream.range(0, arrowLocations.size()).filter((i) -> {
			
			return length + arrowDistances.get(i) > traveled;
			
		}).forEach((i) -> {
			
			AffineTransform before = g.getTransform();
			AffineTransform transfrom = arrowLocations.get(i).getRailPointTransform();
			g.transform(transfrom);
			
			g.drawImage(colors.get(arrowColors.get(i)), -ARROW_WIDTH / 2, 0, ARROW_WIDTH, ARROW_LENGTH, null);
			
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
	
	private class Movement {
		
		private static final double MAX_SPEED = Train.MAX_SPEED;
		
		private static final int ACCELERATING_STATE = 0;
		private static final int CRUISING_STATE = 1;
		private static final int DECELERATING_STATE = 2;
		
		private double startSpeed;
		private double endSpeed;
		
		private double traveledOffset;
		private double startTime;
		
		private double distance;
		private double duration;
		
		private double accelerateTo;
		private double brakeFrom;
		
		private double acceleration;
		private double deceleration;
		
		private double x1;
		private double x2;
		private double y1;
		private double y2;
		
		private double cc;
		
		public Movement(double startSpeed, double endSpeed, double distance, double startTime, double traveledOffset, double acceleration, double deceleration) {
			
			this.acceleration = acceleration;
			this.deceleration = deceleration;
			
			this.traveledOffset = traveledOffset;
			this.startTime = startTime;
			
			this.startSpeed = startSpeed;
			this.endSpeed = endSpeed;
			this.distance = distance;
			
			x1 = -startSpeed / acceleration;
			y1 = -.5 * acceleration * x1 * x1;
			
			x2 = (MAX_SPEED - endSpeed) / deceleration;
			y2 = .5 * deceleration * x2 * x2;
			
			double timeInAcceleration = (MAX_SPEED - startSpeed) / acceleration;
			double timeInDeceleration = (MAX_SPEED - endSpeed) / deceleration;
			
			double m1 = (startSpeed + acceleration * x1) / acceleration;
			double m2 = (endSpeed + deceleration * x2) / deceleration;
			
			double traveledInAcceleration = .5 * acceleration * pow(m1 - x1, 2) + y1; // this is wrong but works ?!
			double traveledInDeceleration = .5 * deceleration * pow(m2 - x2, 2) + y2;
			
			if (traveledInAcceleration + traveledInDeceleration > distance) {
				
				// likely to be the real rak
				
				double a = acceleration;
				double d = deceleration;
				double i = x1;
				double j = y1;
				double l = y2;
				
				double aq = -a - (a * a) / d;
				double bq = 2 * a * i + (2 * a * a * i) / d;
				double cq = (-a * a * i * i) / d + 2 * l - 2 * j - a * i * i;
				
				double[] solutions = RootFinder.quadraticRoots(aq, bq, cq);
				System.out.println(Arrays.toString(solutions));
				
				accelerateTo = solutions[0];
				brakeFrom = accelerateTo;
				
				x2 = (a * accelerateTo + d * accelerateTo - a * i) / d;
				duration = x2 - (MAX_SPEED / deceleration) + timeInDeceleration;
				
			} else {
				
				cc = -.5 * acceleration * pow((MAX_SPEED - startSpeed) / acceleration, 2);
				
				accelerateTo = timeInAcceleration;
				
				double traveledInCruis = distance - traveledInAcceleration - traveledInDeceleration;
				
				y2 += traveledInCruis;
				x2 += traveledInCruis / MAX_SPEED - cc / MAX_SPEED;
				
				brakeFrom = x2 - timeInDeceleration;
				duration = x2 - (MAX_SPEED / deceleration) + timeInDeceleration;
				
			}
			
		}
		
		public double getTraveledOffset() {
			
			return traveledOffset;
			
		}
		
		public double getDuration() {
			
			return duration;
			
		}
		
		public double getDistance() {
			
			return distance;
			
		}
		
		public double getTraveledAtTime(double timePassed) {
			
			timePassed -= startTime;
			
			if (timePassed < accelerateTo) { // acceleration
				
				return traveledOffset + .5 * acceleration * pow(timePassed - x1, 2) + y1;
				
			} else if (timePassed > brakeFrom) { // deceleration
				
				return traveledOffset - .5 * deceleration * pow(timePassed - x2, 2) + y2;
				
			} else { // cruise
				
				return traveledOffset + MAX_SPEED * timePassed + cc;
				
			}
			
		}
		
		public double getSpeedAtTime(double timePassed) {
			
			timePassed -= startTime;
			
			if (timePassed < accelerateTo) { // acceleration
				
				return startSpeed + acceleration * timePassed;
				
			} else if (timePassed > brakeFrom) { // deceleration
				
				return endSpeed + deceleration * (duration - timePassed);
				
			} else { // cruise
				
				return MAX_SPEED;
				
			}
			
		}
		
		public int getStateAtTraveled(double traveled) {
			
			if (traveled < getTraveledAtTime(accelerateTo)) { // acceleration
				
				return ACCELERATING_STATE;
				
			} else if (traveled > getTraveledAtTime(brakeFrom)) { // deceleration
				
				return DECELERATING_STATE;
				
			} else { // cruise
				
				return CRUISING_STATE;
				
			}
			
		}
		
		private double getStartTime() {
			
			return startTime;
			
		}
		
		private double getEndTime() {
			
			return startTime + duration;
			
		}
		
	}
	
}
