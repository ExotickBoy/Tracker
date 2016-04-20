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
	
	boolean isComplete;
	
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
		
		Driver.scene.connections.forEach((connection) -> {
			
			connection.dank = 0;
			
		});
		
		connections.forEach((connection) -> {
			
			connection.getConnection().dank = 2;
			
		});
		
		this.connections = connections;
		
		from = connections.get(0);
		to = connections.get(connections.size() - 1);
		
		lenghtInPixels = connections.stream().map(RailLocation::getConnection).mapToDouble(RailConnection::getLength).sum();
		lenghtInPixels -= (from.isForward() ? 1 - from.getT() : from.getT()) * from.getConnection().getLength();
		lenghtInPixels -= (to.isForward() ? 1 - to.getT() : to.getT()) * to.getConnection().getLength();
		lenghtInPixels -= (to.isForward() ? 1 - to.getT() : to.getT()) * to.getConnection().getLength();
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
			System.out.println(timePassed + " accelerating ");
			
			speed = timePassed * train.getMaxAcceleration();
			traveled = .5 * train.getMaxAcceleration() * pow(timePassed, 2);
			
		} else if (timePassed < cruisTo) {
			System.out.println(timePassed + " curising");
			
			speed = Train.MAX_SPEED;
			traveled = .5 * train.getMaxAcceleration() * pow(accelerateTo, 2) + Train.MAX_SPEED * (timePassed - accelerateTo);
			
		} else if (timePassed < brakeTo) {
			System.out.println(timePassed + " braking");
			
			speed = (brakeTo - timePassed) * train.getMaxAcceleration();
			traveled = .5 * train.getMaxAcceleration() * pow(accelerateTo, 2) + Train.MAX_SPEED * (cruisTo - accelerateTo) //
					+ traveledInDeceleration - .5 * train.getMaxDeceleration() * pow(timePassed - brakeTo, 2);
					
		} else {
			
			System.out.println("done");
			
			traveled = lenght;
			isComplete = true;
			
		}
		
		train.setSpeed(speed);
		
		until = traveled * METERS_PER_PIXEL + ((from.isForward() ? 1 - from.getT() : from.getT()) * from.getConnection().length);
		System.out.println(until);
		connections.stream().anyMatch((connection) -> {
			
			until -= connection.getConnection().length;
			
			if (until < 0) {
				
				train.setLocation(new RailLocation(connection.isForward() ? (-until / connection.getConnection().length) : 1 - (-until / connection.getConnection().length),
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
		
		if (connections != null) {
			
			return new Path(connections);
			
		} else {
			
			System.out.println("no path");
			return null;
			
		}
		
	}
	
	public static ArrayList<RailLocation> trace(ArrayList<RailLocation> been, RailLocation from, RailLocation to) {
		
		been.add(from);
		
		if (from.getConnection() == to.getConnection() && from.isForward() == to.isForward() && //
				((to.isForward() && to.getT() < from.getT()) || (!to.isForward() && to.getT() > from.getT()))) {
				
			return been;
			
		} else {
			
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
