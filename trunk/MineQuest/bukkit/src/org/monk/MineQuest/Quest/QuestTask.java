package org.monk.MineQuest.Quest;

import org.monk.MineQuest.Event.Event;

public class QuestTask {
	private Event[] events;
	private int id;

	public QuestTask(Event events[], int id) {
		this.events = events;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public Event[] getEvents() {
		Event[] ret = events;
		events = null;
		return ret;
	}
}
