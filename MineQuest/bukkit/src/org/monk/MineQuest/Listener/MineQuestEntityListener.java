package org.monk.MineQuest.Listener;


import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.monk.MineQuest.MineQuest;

public class MineQuestEntityListener extends EntityListener {
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Monster) {
			MineQuest.addMob((Monster)entity);
			MineQuest.checkMobs();
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evente = ((EntityDamageByEntityEvent)event);
            if (evente.getDamager() instanceof Player) {
                MineQuest.getQuester((Player)evente.getDamager()).attackEntity(event.getEntity(), evente);
            } else if (event.getEntity() instanceof Player) {
                MineQuest.getQuester((Player)evente.getEntity()).defendEntity(evente.getDamager(), evente);
            }
			return;
		}
		
		if (event instanceof EntityDamageByProjectileEvent) {
			EntityDamageByProjectileEvent evente = ((EntityDamageByProjectileEvent)event);
            if (evente.getDamager() instanceof Player) {
                MineQuest.getQuester((Player)evente.getDamager()).attackEntity(event.getEntity(), evente);
            } else if (event.getEntity() instanceof Player) {
                MineQuest.getQuester((Player)evente.getEntity()).defendEntity(evente.getDamager(), evente);
            }
			return;
		}
		
		if (event.getEntity() instanceof Player) {
			MineQuest.getQuester((Player)event.getEntity()).defend(event);
		}
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (MineQuest.getQuester((Player)event.getEntity()).getHealth() > 0) {
//				event.clearDrops();
			}
		}
	}

}
