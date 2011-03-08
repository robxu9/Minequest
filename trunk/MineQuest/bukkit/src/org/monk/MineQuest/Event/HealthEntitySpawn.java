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
	private boolean stay;

	public HealthEntitySpawn(Quest quest, long delay, int task, Location location, CreatureType creatureType, int health, boolean stay) {
		super(quest, delay, task);
		this.health = health;
		this.creatureType = creatureType;
		this.entity = null;
		this.location = location;
		this.stay = stay;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		if (entity == null) {
			entity = location.getWorld().spawnCreature(location, creatureType);
			if (entity != null) {
				MineQuest.setMQMob(new HealthMob(entity, health));
			} else {
				MineQuest.log("Unable to create Health Entity");
				eventParser.setComplete(true);
			}
			
			return;
		}
		if (stay) {
			entity.teleportTo(location);
		}
		
		if (!(entity.getHealth() > 0)) {
			eventComplete();
			eventParser.setComplete(true);
		}
	}
	
	@Override
	public String getName() {
		return "Health Entity Spawner";
	}

}
