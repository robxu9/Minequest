package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;

public class PeriodicEvent implements Event {
	protected long delay;
	protected long reset_time;
	
	public PeriodicEvent(long delay) {
		this.delay = delay;
		reset_time = 0;
	}

	@Override
	public boolean isPassed(long time) {
		return (time - reset_time) > delay;
	}

	@Override
	public void reset(long time) {
		reset_time = time;
	}

	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
	}

	@Override
	public String getName() {
		return "Generic Periodic Event";
	}

}
