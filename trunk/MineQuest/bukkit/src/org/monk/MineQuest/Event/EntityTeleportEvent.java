package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class EntityTeleportEvent extends NormalEvent {
	protected LivingEntity entity;
	protected Location location;

	public EntityTeleportEvent(long delay, LivingEntity entity, Location location) {
		super(delay);
		this.entity = entity;
		this.location = location;
	}

	@Override
	public void activate(EventParser eventParser) {
		entity.teleportTo(location);
	}

	@Override
	public String getName() {
		return "Generic Teleport Event";
	}

}
