package org.monk.MineQuest.Event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Quester.Quester;

public class AuraEvent extends NormalEvent {
	protected LivingEntity player;
	protected World world;
	private Ability ability;
	private long total_time;
	private int change;
	private boolean players;
	private long count;

	public AuraEvent(Ability ability, Quester quester, long delay, long total_time, int change, boolean players) {
		super(delay);
		player = quester.getPlayer();
		world = player.getWorld();
		this.ability = ability;
		this.total_time = total_time;
		this.count = 0;
		this.change = change;
		this.players = players;
	}

	public void activate(EventParser eventParser) {
		List<LivingEntity> nearby = ability.getEntities(player, 15);
		List<LivingEntity> affected = sort(nearby, players);
		
		for (LivingEntity entity : affected) {
			if (players) {
				MineQuest.getQuester((Player)entity).setHealth(MineQuest.getQuester((Player)entity).getHealth() + change);
			} else {
				MineQuest.getMob(entity).setHealth(entity.getHealth() + change);
			}
		}
		
		count += delay;
		if (count < total_time) {
			eventParser.setComplete(false);
		} else {
			eventParser.setComplete(true);
		}
	}

	private List<LivingEntity> sort(List<LivingEntity> nearby, boolean players) {
		List<LivingEntity> ret = new ArrayList<LivingEntity>();
		
		for (LivingEntity entity : nearby) {
			if (players && (entity instanceof Player)) {
				ret.add(entity);
			} else if (!players && !(entity instanceof Player)){
				ret.add(entity);
			}
		}
		
		return ret;
	}
}
