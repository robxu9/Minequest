package org.monk.MineQuest.Event;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Mob.SpecialMob;

public class SpecialMobHandler extends PeriodicEvent {
	protected SpecialMob mob;

	public SpecialMobHandler(long delay, SpecialMob mob) {
		super(delay);
		this.mob = mob;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		if (mob.getMonster() instanceof Monster) {
			if (((Monster)mob.getMonster()).getTarget() == null) {
				((Monster)mob.getMonster()).setTarget(getNearestPlayer(mob.getMonster()));
			}
		}
		
		Location loc = mob.getMonster().getLocation();
		Block block = mob.getMonster().getWorld().getBlockAt((int)loc.getX(), 
				Ability.getNearestY(mob.getMonster().getWorld(), (int)loc.getX(), (int)loc.getY(), (int)loc.getZ()) - 1, (int)loc.getZ());
		
		if (block.getType() != Material.DIRT){
			MineQuest.getEventParser().addEvent(new BlockEvent(30000, block, block.getType()));
			MineQuest.getEventParser().addEvent(new BlockEvent(10, block, Material.DIRT));
		}

		super.activate(eventParser);
		
		if (mob.getMonster().getHealth() <= 0) {
			mob.dropLoot();
			eventParser.setComplete(true);
		}
	}

	private LivingEntity getNearestPlayer(LivingEntity livingEntity) {
		List<LivingEntity> entities = livingEntity.getWorld().getLivingEntities();
		double distance = 100000;
		LivingEntity player = null;
		
		for (LivingEntity entity : entities) {
			if (entity instanceof Player) {
				if (player == null) {
					distance = MineQuest.distance(entity.getLocation(), livingEntity.getLocation());
					player = entity;
				} else if (MineQuest.distance(entity.getLocation(), livingEntity.getLocation()) < distance) {
					distance = MineQuest.distance(entity.getLocation(), livingEntity.getLocation());
					player = entity;
				}
			}
		}
		
		return player;
	}

}
