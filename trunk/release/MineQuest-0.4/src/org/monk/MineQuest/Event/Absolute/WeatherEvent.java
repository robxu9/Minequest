package org.monk.MineQuest.Event.Absolute;

import org.bukkit.World;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.NormalEvent;

public class WeatherEvent extends NormalEvent {
	private World world;
	private boolean hasStorm;
	private int duration;

	public WeatherEvent(long delay, World world, boolean hasStorm, int duration) {
		super(delay);
		this.world = world;
		this.hasStorm = hasStorm;
		this.duration = duration;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		world.setStorm(hasStorm);
		world.setWeatherDuration(duration);
	}

}
