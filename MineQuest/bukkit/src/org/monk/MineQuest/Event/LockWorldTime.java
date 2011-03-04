package org.monk.MineQuest.Event;

import org.bukkit.World;

public class LockWorldTime extends PeriodicEvent {

	private World world;
	private long time;
	private long time_2;

	public LockWorldTime(long delay, World world, long time, long time_2) {
		super(delay);
		this.world = world;
		this.time = time;
		this.time_2 = time_2;
		world.setTime(time);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		if (world.getTime() > time_2) {
			world.setTime(time);
		}
	}

}
