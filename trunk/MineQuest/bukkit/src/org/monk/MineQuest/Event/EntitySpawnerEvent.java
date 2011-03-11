/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2010  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
			if (entity != null) {
				if (superm) {
					MineQuest.setMQMob(new SpecialMob((Monster)entity));
				} else {
					MineQuest.setMQMob(new MQMob((Monster)entity));
				}
			}
		}
		
		if (complete) {
			MineQuest.getMob(entity).setHealth(0);
		}
		eventParser.setComplete(complete);
	}

	@Override
	public String getName() {
		return "Repeating Entity Spawner Event";
	}
}
