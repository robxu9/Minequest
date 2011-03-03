package org.monk.MineQuest.Event;


public class PeriodicEvent extends NormalEvent {
	protected long delay;
	protected long reset_time;
	
	public PeriodicEvent(long delay) {
		super(delay);
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
