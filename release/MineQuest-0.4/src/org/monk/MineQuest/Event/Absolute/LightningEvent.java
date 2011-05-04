package org.monk.MineQuest.Event.Absolute;

import org.bukkit.Location;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.NormalEvent;

public class LightningEvent extends NormalEvent {
	
	private Location location;

	public LightningEvent(long delay, Location location) {
		super(delay);
		this.location = location;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		location.getWorld().strikeLightning(location);
	}

}
