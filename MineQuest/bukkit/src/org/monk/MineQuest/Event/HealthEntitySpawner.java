package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;

public class HealthEntitySpawner extends EntitySpawnerEvent {

	public HealthEntitySpawner(long delay, Location location,
			CreatureType creatureType, boolean superm) {
		super(delay, location, creatureType, superm);
		// TODO Auto-generated constructor stub
	}

}
