package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class ArrowEvent extends NormalEvent {

	private Location start;
	private Vector vector;

	public ArrowEvent(long delay, Location start, Vector vector) {
		super(delay);
		this.start = start;
		this.vector = vector;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		start.getWorld().spawnArrow(start, vector, (float).8, 8);
	}
	
	@Override
	public String getName() {
		return "Arrow Event";
	}

}
