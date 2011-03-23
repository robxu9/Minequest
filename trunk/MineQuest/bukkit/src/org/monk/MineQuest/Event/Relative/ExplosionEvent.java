package org.monk.MineQuest.Event.Relative;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.EventParser;

public class ExplosionEvent extends RelativeEvent {

	private double x;
	private double y;
	private double z;
	private float radius;
	private int damage;
	private CraftWorld world;
	private Entity entity;

	public ExplosionEvent(long delay, CraftWorld world, Entity entity, double x, double y, double z, float radius, int damage) {
		super(delay);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.damage = damage;
		this.entity = entity;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		Location loc = entity.getLocation();
		world.getHandle().a(null, x + loc.getX(), y + loc.getY(), z + loc.getZ(), radius);
		
		List<LivingEntity> entities = world.getLivingEntities();
		Location location = new Location(null, x + loc.getX(), y + loc.getY(), z + loc.getZ());
		for (LivingEntity entity : entities) {
			if (MineQuest.distance(location, entity.getLocation()) < radius) {
				MineQuest.damage(entity, damage);
			}
		}
	}

}
