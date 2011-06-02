package org.monk.MineQuest.Event.Target;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.Quester;

public class ExplosionEvent extends TargetedEvent {
	protected int damage;
	protected float radius;
	private long real_delay;

	public ExplosionEvent(long delay, Target target, float radius, int damage) {
		super(0, target);
		this.radius = radius;
		this.damage = damage;
		this.real_delay = delay;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : target.getTargets()) {
			MineQuest.getEventQueue().addEvent(
					new org.monk.MineQuest.Event.Absolute.ExplosionEvent(
							real_delay, 
							quester.getPlayer().getWorld(), 
							quester.getPlayer().getLocation().getX(), 
							quester.getPlayer().getLocation().getY(),
							quester.getPlayer().getLocation().getZ(), 
							radius, damage));
		}
	}

	@Override
	public String getName() {
		return "Targeted Explosion Event";
	}

}
