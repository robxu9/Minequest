package org.monk.MineQuest.Event;

import java.util.Calendar;

public class EventParser implements java.lang.Runnable {
	protected Event event;
	protected int id;
	protected boolean complete;
	private Calendar now;
	private boolean done;

	public EventParser(Event event) {
		this.event = event;
		complete = false;
		event.reset(getTime());
		done = false;
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
		}
	}

	public int getId() {
		return id;
	}

	public long getTime() {
		now = Calendar.getInstance();
		return now.getTimeInMillis();
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean b) {
		done = b;
	}
}
