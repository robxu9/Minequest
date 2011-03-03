package org.monk.MineQuest.Event;

public class NormalEvent implements Event {
	protected long delay;
	protected long reset_time;
	
	public NormalEvent(long delay) {
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
		
	}

	@Override
	public String getName() {
		return "Generic Event";
	}
}
