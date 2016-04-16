package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import interfaces.Selectable;
import items.RailConnection;
import items.RailPoint;
import items.RailSignal;
import items.Train;
import items.TrainStop;

public final class Scene implements Serializable {
	
	private static final String FAILED_HEADING = "Failure";
	private static final String FAILED_TO_WRITE_MESSAGE = "Failed to write file";
	private static final String FAILED_TO_READ_OUT_OF_DATE = "Outdated file version";
	private static final String FAILED_TO_READE_NOT_FOUND = "Couldn't find file";
	
	private static final long serialVersionUID = 10L;
	
	public ArrayList<RailPoint> selected = new ArrayList<>();
	
	public ArrayList<RailPoint> railPoints = new ArrayList<>();
	public ArrayList<RailConnection> connections = new ArrayList<>();
	public ArrayList<Train> trains = new ArrayList<>();
	public ArrayList<TrainStop> trainStops = new ArrayList<>();
	public ArrayList<RailSignal> railSignals = new ArrayList<>();
	
	public String name;
	
	public Scene() {
	
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public void select(Selectable selectable) {
		// TODO Auto-generated method stub
		
	}
	
	public void save(File to) {
		
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(to)));
			
			oos.writeObject(this);
			
			oos.close();
			
			if (name == null) {
				name = to.getName().trim();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, FAILED_TO_WRITE_MESSAGE, FAILED_HEADING, JOptionPane.WARNING_MESSAGE);
			
		}
		
	}
	
	public static Scene load(File from) {
		
		ObjectInputStream ois;
		
		try {
			
			ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(from)));
			Scene scene = (Scene) ois.readObject();
			ois.close();
			
			if (scene.name == null) {
				scene.name = from.getName().trim();
			}
			
			return scene;
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, FAILED_TO_READE_NOT_FOUND, FAILED_HEADING, JOptionPane.WARNING_MESSAGE);
			
		} catch (InvalidClassException e) {
			
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, FAILED_TO_READ_OUT_OF_DATE, FAILED_HEADING, JOptionPane.WARNING_MESSAGE);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null, e.getMessage(), FAILED_HEADING, JOptionPane.WARNING_MESSAGE);
			
		}
		
		return null;
		
	}
	
}
