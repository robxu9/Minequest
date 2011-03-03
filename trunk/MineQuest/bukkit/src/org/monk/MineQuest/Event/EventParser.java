package org.monk.MineQuest.Event;

import java.util.Calendar;

import org.monk.MineQuest.MineQuest;

public class EventParser implements java.lang.Runnable {
	protected Event event;
	protected int id;
	protected boolean complete;
	private Calendar now;

	public EventParser(Event event) {
		this.event = event;
		complete = false;
		event.reset(getTime());
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public boolean getComplete() {
		return complete;
	}

	public void run() {
		now = Calendar.getInstance();
		if (!complete && event.isPassed(now.getTimeInMillis())) {
			complete = true;
			event.activate(this);
			if (!complete) {
				event.reset(getTime());
			}
		} else if (complete) {
			MineQuest.getSServer().getScheduler().cancelTask(id);
		}
	}

	public int getId() {
		return id;
	}

	public long getTime() {
		now = Calendar.getInstance();
		return now.getTimeInMillis();
	}
}
