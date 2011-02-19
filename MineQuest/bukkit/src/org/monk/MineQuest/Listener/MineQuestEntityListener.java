package org.monk.MineQuest.Listener;


import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class MineQuestEntityListener extends EntityListener {
	
	@Override
	public void onEntityDamageByBlock(org.bukkit.event.entity.EntityDamageByBlockEvent event) {
		if (event.getEntity() instanceof Player) {
			MineQuest.getQuester((Player)event.getEntity()).defendBlock(event);
		}
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			MineQuest.getQuester((Player)event.getEntity()).defend(event);
		}
	}
	
	@Override
	public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
		if (event.getDamager() instanceof Player) {
			MineQuest.getQuester((Player)event.getDamager()).attackEntity(event.getEntity(), event);
		} else if (event.getEntity() instanceof Player) {
			MineQuest.getQuester((Player)event.getEntity()).defendEntity(event.getDamager(), event);
		}
	}
	
	@Override
	public void onEntityDamageByEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
		
		if (event.getDamager() instanceof Player) {
			MineQuest.getQuester((Player)event.getDamager()).attackEntity(event.getEntity(), event);
		} else if (event.getEntity() instanceof Player) {
			MineQuest.getQuester((Player)event.getEntity()).defendEntity(event.getDamager(), event);
		}
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			if (MineQuest.getQuester((Player)event.getEntity()).getHealth() > 0) {
				event.clearDrops();
			}
		}
	}

}
