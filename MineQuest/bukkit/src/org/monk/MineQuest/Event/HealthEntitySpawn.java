package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Mob.HealthMob;
import org.monk.MineQuest.Quest.Quest;

public class HealthEntitySpawn extends QuestEvent {
	private int health;
	private CreatureType creatureType;
	private LivingEntity entity;
	private Location location;

	public HealthEntitySpawn(Quest quest, long delay, int task, Location location, CreatureType creatureType, int health) {
		super(quest, delay, task);
		this.health = health;
		this.creatureType = creatureType;
		this.entity = null;
		this.location = location;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		if (entity == null) {
			entity = location.getWorld().spawnCreature(location, creatureType);
			MineQuest.setMQMob(new HealthMob(entity, health));
			
			return;
		}
		entity.teleportTo(location);
		
		eventParser.setComplete(!(entity.getHealth() > 0));
		if (!(entity.getHealth() > 0)) {
			eventComplete();
		}
	}

}
