package org.monk.MineQuest.Event;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.Quester.Quester;

public class HealthEvent extends NormalEvent {
	protected LivingEntity entity;
	protected int newHealth;
	
	public HealthEvent(long delay, Quester quester, int newHealth) {
		super(delay);
		this.entity = quester.getPlayer();
		this.newHealth = newHealth;
	}
	
	public HealthEvent(long delay, LivingEntity entity, int newHealth) {
		super(delay);
		this.entity = entity;
		this.newHealth = newHealth;
	}

	@Override
	public void activate(EventParser eventParser) {
		entity.setHealth(newHealth);
	}

	@Override
	public String getName() {
		return "Generic Health Event";
	}

}
