package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;

public class EntitySpawnerNoMove extends EntitySpawnerEvent {

	public EntitySpawnerNoMove(long delay, Location location,
			CreatureType creatureType, boolean superm) {
		super(delay, location, creatureType, superm);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		if (!complete) {
			super.activate(eventParser);
		} else {
			eventParser.setComplete(true);
		}
		
		if (entity != null) entity.teleportTo(location);
	}

	@Override
	public String getName() {
		return "Repeating Entity Spawner Event with No Movement";
	}
}
