package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.QuestTask;

public class QuestEvent extends PeriodicEvent {
	private Quest quest;
	private int index;

	public QuestEvent(Quest quest, long delay, int index) {
		super(delay);
		this.quest = quest;
		this.index = index;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(true);
		eventComplete();
	}
	
	public void eventComplete() {
		Event[] events = quest.getNextEvents(index);
		if (events != null) {
			for (Event event : events) {
				MineQuest.getEventParser().addEvent(event);
			}
		}
	}
	
	@Override
	public String getName() {
		return "Generic Quest Event";
	}

}
