package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.monk.MineQuest.MQMob;
import org.monk.MineQuest.MineQuest;

public class EntitySpawnerEvent extends PeriodicEvent {
	private LivingEntity entity;
	private World world;
	private Location location;
	private CreatureType creatureType;
	private boolean complete;

	public EntitySpawnerEvent(long delay, World world, Location location, CreatureType creatureType) {
		super(delay);
		this.world = world;
		this.location = location;
		this.creatureType = creatureType;
		entity = null;
		complete = false;
	}
	
	public void setComplete(boolean state) {
		complete = state;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		MineQuest.log("Checking Creature");
		
		if ((entity == null) || (entity.getHealth() <= 0)) {
			MineQuest.log("Spawning Creature");
			entity = world.spawnCreature(location, creatureType);
			MineQuest.addMQMob(new MQMob((Monster)entity));
		}
		
		eventParser.setComplete(complete);
	}

	@Override
	public String getName() {
		return "Repeating Entity Spawner Event";
	}
}
