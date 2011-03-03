package org.monk.MineQuest.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.monk.MineQuest.MineQuest;

public class EventParser implements java.lang.Runnable {
	static private long start_time;
	static private Calendar now;
	static private List<Event> events;
	static private boolean enabled;
	
	public EventParser() {
		events = new ArrayList<Event>();
		now = Calendar.getInstance();
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void addEvent(Event event) {
		now = Calendar.getInstance();
		event.reset(now.getTimeInMillis());
		events.add(event);
	}
	
	public void delEvent(Event event) {
		now = Calendar.getInstance();
		events.remove(event);
	}
	
	public List<Event> getQueue() {
		now = Calendar.getInstance();
		return events;
	}
	
	public long getTime() {
		now = Calendar.getInstance();
		return now.getTimeInMillis();
	}

	public void run() {
		now = Calendar.getInstance();
		if (enabled) {
			for (Event event : events) {
				if (event.isPassed(now.getTimeInMillis())) {
					events.remove(event);
					event.activate(this);
				}
			}
		}
	}
	
}
