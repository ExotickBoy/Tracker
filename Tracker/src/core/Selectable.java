package core;

public interface Selectable {

	public default void select(){
		
		Driver.scene.addSelectable(this);
		Driver.scene.select(this);
				
	}
	
}
