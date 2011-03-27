package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;

public class CheckMQMobs extends PeriodicEvent {

	public CheckMQMobs(long delay) {
		super(delay);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		MineQuest.checkMobs();
	}

}
