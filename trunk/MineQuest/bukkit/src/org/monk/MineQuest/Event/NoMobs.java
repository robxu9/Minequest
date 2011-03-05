package org.monk.MineQuest.Event;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NoMobs extends PeriodicEvent {
	private boolean complete;
	private World world;

	public NoMobs(long delay, World world) {
		super(delay);
		this.world = world;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (LivingEntity entity : world.getLivingEntities()) {
			if (!(entity instanceof Player)) {
				entity.setHealth(0);
			}
		}
		
		if (complete) {
			eventParser.setComplete(true);
		}
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}
