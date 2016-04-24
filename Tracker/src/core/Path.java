package core;

import java.util.ArrayList;
import java.util.Objects;

import items.RailConnection;
import items.RailLocation;
import items.Train;

import static java.lang.Math.*;

public class Path {
	
	private static final double METERS_PER_PIXEL = 15;
	
	ArrayList<RailLocation> connections;
	Train train;
	
	boolean isFinished;
	
	double lenghtInPixels;
	double lenght;
	
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
	
	private Path(ArrayList<RailLocation> connections) {
		
		this.connections = connections;
		
		from = connections.get(0);
		to = connections.get(connections.size() - 1);
		
		lenghtInPixels = connections.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum();
		lenghtInPixels -= (from.isForward() ? 1 - from.getT() : from.getT()) * from.getConnection().getLength();
		lenghtInPixels -= (to.isForward() ? to.getT() : 1 - to.getT()) * to.getConnection().getLength();
		// System.out.println((to.isForward() ? 1 - to.getT() : to.getT()) * to.getConnection().getLength());
		// System.out.println(to.getT() + " " + to.getConnection().length);
		lenght = lenghtInPixels / METERS_PER_PIXEL;
		
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
		traveledInCruis = lenght - (traveledInAcceleration + traveledInDeceleration);
		
		if (traveledInCruis < 0) {
		
		}
		
		cruisTo = (traveledInCruis / Train.MAX_SPEED) + accelerateTo;
		brakeTo = cruisTo + decelerationTime;
		
		// dv/dt = a
		// dp/dt = v = at
		// p = 2at^2
		
		// velocity is the integral of acceleration
		// position is the integral of velocity
		
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
			traveled = .5 * train.getMaxAcceleration() * pow(accelerateTo, 2) + Train.MAX_SPEED * (cruisTo - accelerateTo) //
					+ traveledInDeceleration - .5 * train.getMaxDeceleration() * pow(timePassed - brakeTo, 2);
					
		} else {
			
			traveled = lenght;
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
	
}
