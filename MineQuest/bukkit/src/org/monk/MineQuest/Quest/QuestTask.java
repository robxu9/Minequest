package org.monk.MineQuest.Quest;

import org.monk.MineQuest.Event.Event;

public class QuestTask {
	private Event[] events;

	public QuestTask(Event events[]) {
		this.events = events;
	}
	
	public Event[] getEvents() {
		return events;
	}
}
