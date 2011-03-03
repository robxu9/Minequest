package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.World.Town;

public class CheckMobEvent extends PeriodicEvent {
	private Town town;

	public CheckMobEvent(Town town) {
		super(1);
		this.town = town;
	}
	
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		town.checkMobs();
	}
	
	@Override
	public String getName() {
		return "Check Mobs: " + town.getName();
	}

}
