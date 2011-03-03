package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

public class EntitySpawnerNoMove extends EntitySpawnerEvent {

	public EntitySpawnerNoMove(long delay, World world, Location location,
			CreatureType creatureType, boolean superm) {
		super(delay, world, location, creatureType, superm);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		entity.teleportTo(location);
	}

	@Override
	public String getName() {
		return "Repeating Entity Spawner Event with No Movement";
	}
}
