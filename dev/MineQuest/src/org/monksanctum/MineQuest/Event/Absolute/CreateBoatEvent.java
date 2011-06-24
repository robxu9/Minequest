package org.monksanctum.MineQuest.Event.Absolute;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.monksanctum.MineQuest.Event.EventParser;
import org.monksanctum.MineQuest.Event.NormalEvent;

public class CreateBoatEvent extends NormalEvent {
	private CraftWorld world;
	private double x;
	private double y;
	private double z;
	private CraftBoat boat;

	public CreateBoatEvent(long delay, World world, double x, double y, double z) {
		super(delay);
		this.world = (CraftWorld) world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		this.boat = (CraftBoat) world.spawnBoat(new Location(world, x, y, z));
		
		super.activate(eventParser);
	}
	
	public CraftBoat getBoat() {
		return boat;
	}

	@Override
	public String getName() {
		return "Create Boat Event";
	}
	
	

}
