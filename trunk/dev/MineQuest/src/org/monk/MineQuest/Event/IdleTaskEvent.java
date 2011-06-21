package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Idle.IdleTask;

public class IdleTaskEvent extends NormalEvent {

	private IdleTask idle;

	public IdleTaskEvent(long delay, IdleTask idle) {
		super(delay);
		this.idle = idle;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		idle.continueQuest();
	}

	@Override
	public String getName() {
		return "Idle Task Event";
	}

}
