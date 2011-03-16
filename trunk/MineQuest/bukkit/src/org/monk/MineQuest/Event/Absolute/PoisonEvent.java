package org.monk.MineQuest.Event.Absolute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.PeriodicEvent;
import org.monk.MineQuest.Quester.Quester;

public class PoisonEvent extends PeriodicEvent {
	private LivingEntity entity;
	private int amount;
	private int total;

	public PoisonEvent(long delay, LivingEntity entity, int amount, int total) {
		super(delay);
		this.entity = entity;
		if (entity instanceof Player) {
			((Player)entity).sendMessage("Time Poisoned!");
		}
		this.amount = amount;
		this.total = total;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		total -= amount;
		
		if (entity instanceof Player) {
			Quester quester = MineQuest.getQuester((Player)entity);
			quester.setHealth(quester.getHealth() - amount);
		} else if (MineQuest.getMob(entity) != null) {
			MineQuest.getMob(entity).damage(amount);
		} else {
			int newHealth = entity.getHealth() - amount;
			
			if (newHealth < 0) newHealth = 0;
			
			entity.setHealth(newHealth);
		}
		
		if (total <= 0) {
			eventParser.setComplete(true);
		}
	}

}
