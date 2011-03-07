package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Mob.MQMob;
import org.monk.MineQuest.Mob.SpecialMob;

public class EntitySpawnerEvent extends PeriodicEvent {
	protected LivingEntity entity;
	protected World world;
	protected Location location;
	protected CreatureType creatureType;
	protected boolean complete;
	protected boolean superm;

	public EntitySpawnerEvent(long delay, Location location, CreatureType creatureType, boolean superm) {
		super(delay);
		this.world = location.getWorld();
		this.location = location;
		this.creatureType = creatureType;
		this.superm = superm;
		entity = null;
		complete = false;
	}
	
	public void setComplete(boolean state) {
		complete = state;
	}
	
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		
		if ((entity == null) || (entity.getHealth() <= 0)) {
			entity = world.spawnCreature(location, creatureType);
			if (superm) {
				MineQuest.setMQMob(new SpecialMob((Monster)entity));
			} else {
				MineQuest.setMQMob(new MQMob((Monster)entity));
			}
		}
		
		if (complete) {
			entity.setHealth(0);
		}
		eventParser.setComplete(complete);
	}

	@Override
	public String getName() {
		return "Repeating Entity Spawner Event";
	}
}
