package org.monk.MineQuest.Event;

public class EntitySpawnerCompleteEvent extends NormalEvent {
	private EntitySpawnerEvent event;

	public EntitySpawnerCompleteEvent(long delay, EntitySpawnerEvent event) {
		super(delay);
		this.event = event;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		event.setComplete(true);
	}
	
	@Override
	public String getName() {
		return "Entity Spawner Destruction Event";
	}

}
