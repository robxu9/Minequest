/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
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
package org.monk.MineQuest.Listener;


import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.monk.MineQuest.MineQuest;

public class MineQuestEntityListener extends EntityListener {
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (MineQuest.canCreate(entity)) {
			if (entity instanceof Monster) {
				MineQuest.addMob((Monster)entity);
				MineQuest.checkMobs();
			}
		} else {
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evente = ((EntityDamageByEntityEvent)event);
            if (evente.getDamager() instanceof HumanEntity) {
                MineQuest.getQuester((Player)evente.getDamager()).attackEntity(event.getEntity(), evente);
            }
            if (!event.isCancelled()) {
	            if (event.getEntity() instanceof HumanEntity) {
	                MineQuest.getQuester((Player)evente.getEntity()).defendEntity(evente.getDamager(), evente);
	            } else if ((event.getEntity() instanceof LivingEntity) && 
	            		(MineQuest.getMob((LivingEntity)event.getEntity()) != null)) {
	            	if (evente.getDamager() instanceof LivingEntity) {
		            	evente.setDamage(MineQuest.getMob((LivingEntity)event.getEntity()).defend(evente.getDamage(), 
		            			(LivingEntity)evente.getDamager()));
	            	} else {
		            	evente.setDamage(MineQuest.getMob((LivingEntity)event.getEntity()).defend(evente.getDamage(), 
		            			null));
	            	}
	            }
            }
			return;
		}
		
		if (event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent evente = ((EntityDamageByProjectileEvent)event);
            if (evente.getDamager() instanceof HumanEntity) {
                MineQuest.getQuester((Player)evente.getDamager()).attackEntity(event.getEntity(), evente);
            }
            if (!event.isCancelled()) {
	            if (event.getEntity() instanceof HumanEntity) {
	                MineQuest.getQuester((Player)evente.getEntity()).defendEntity(evente.getDamager(), evente);
	            } else if ((event.getEntity() instanceof LivingEntity) && 
	            		(MineQuest.getMob((LivingEntity)event.getEntity()) != null)) {
	            	evente.setDamage(MineQuest.getMob((LivingEntity)event.getEntity()).defend(evente.getDamage(), 
	            			(LivingEntity)evente.getDamager()));
	            }
            }
			return;
		}
		
		if (event.getEntity() instanceof HumanEntity) {
			if (MineQuest.getQuester((Player)event.getEntity()) != null) {
				MineQuest.getQuester((Player)event.getEntity()).defend(event);
			}
		} else if ((event.getEntity() instanceof LivingEntity) && MineQuest.getMob((LivingEntity)event.getEntity()) != null) {
			MineQuest.getMob((LivingEntity)event.getEntity()).damage(event.getDamage());
        }
	}
}
