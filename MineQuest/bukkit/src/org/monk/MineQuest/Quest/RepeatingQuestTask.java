package org.monk.MineQuest.Quest;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.Event;

public class RepeatingQuestTask extends QuestTask {

	public RepeatingQuestTask(Event[] events, int id) {
		super(events, id);
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
		}
	}

}
