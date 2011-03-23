package org.monk.MineQuest.Event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class PoisonEvent extends PeriodicEvent {
	private LivingEntity entity;
	private int amount;
	private int total;

	public PoisonEvent(long delay, LivingEntity entity, int amount, int total) {
		super(delay);
		this.entity = entity;
		if (entity instanceof Player) {
			((Player)entity).sendMessage("Time Poisoned! No Cure Available!");
		}
		this.amount = amount;
		this.total = total;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		total -= amount;
		
		MineQuest.damage(entity, amount);
		
		if (total <= 0) {
			eventParser.setComplete(true);
		}
	}

}
