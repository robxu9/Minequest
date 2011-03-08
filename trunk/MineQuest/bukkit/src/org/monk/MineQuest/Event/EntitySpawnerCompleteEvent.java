package org.monk.MineQuest.Event;

public class EntitySpawnerCompleteEvent extends NormalEvent {
	protected EntitySpawnerEvent[] events;

	public EntitySpawnerCompleteEvent(long delay, EntitySpawnerEvent[] eventss) {
		super(delay);
		this.events = eventss;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (EntitySpawnerEvent event : events) {
			event.setComplete(true);
		}
	}
	
	@Override
	public String getName() {
		return "Entity Spawner Destruction Event";
	}
}
