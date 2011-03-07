package org.monk.MineQuest.Quest;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.Event;

public class QuestTask {
	protected Event[] events;
	protected int id;
	protected int[] ids;

	public QuestTask(Event events[], int id) {
		this.events = events;
		this.id = id;
		this.ids = new int[0];
	}
	
	public int getId() {
		return id;
	}
	
	public void issueEvents() {
		if (events != null) {
			int i;
			int new_ids[] = new int[ids.length + events.length];
			for (i = 0; i < ids.length; i++) {
				new_ids[i] = ids[i];
			}
			for (Event event : events) {
				new_ids[i++] = MineQuest.getEventParser().addEvent(event);
			}
			ids = new_ids;
			events = null;
		}
	}
	
	public void clearEvents() {
		MineQuest.getEventParser().cancel(ids);
	}

	public Event[] getEvents() {
		return events;
	}
}
