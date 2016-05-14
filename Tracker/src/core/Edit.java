package core;

public abstract class Edit {
	
	private boolean isFollowedFrom;
	
	public abstract void redo();
	
	public abstract void undo();
	
	public void setIsFollowedFrom(boolean isFollowedTo) {
		
		this.isFollowedFrom = isFollowedTo;
		
	}
	
	public boolean isFollwedFrom() {
		
		return isFollowedFrom;
		
	}
	
}
