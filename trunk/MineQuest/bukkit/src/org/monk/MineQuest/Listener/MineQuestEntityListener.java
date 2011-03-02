package org.monk.MineQuest.Listener;


import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class MineQuestEntityListener extends EntityListener {
	
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent evente = ((EntityDamageByEntityEvent)event);
            if (evente.getDamager() instanceof Player) {
                    MineQuest.getQuester((Player)evente.getDamager()).attackEntity(event.getEntity(), evente);
            } else if (event.getEntity() instanceof Player) {
                    MineQuest.getQuester((Player)evente.getEntity()).defendEntity(evente.getDamager(), evente);
            }
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
