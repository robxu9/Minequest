package org.monk.MineQuest.Event;

public interface Event {
	/**
	 * This checks if the event should have been activated
	 * by now, returning true indicates it should be activated.
	 * 
	 * @param time current time
	 * @return true to request activation
	 */
	public boolean isPassed(long time);
	
	/**
	 * Resets the time of the event so that time of the event
	 * can be relative to activation, will be called when event
	 * is added to the event queue.
	 * 
	 * @param time current time
	 */
	public void reset(long time);
	
	/**
	 * Called when event has passed time for activation, the actual
	 * event happens now.
	 * 
	 * @param eventParser This is the parser calling the event.
	 */
	public void activate(EventParser eventParser);
	
	/**
	 * Gets identifiable name for the event
	 * 
	 * @return Name of event
	 */
	public String getName();
	
	/**
	 * Used for local organization. Does not have to be unique.
	 */
	void setId(int id);
	public int getId();
}
