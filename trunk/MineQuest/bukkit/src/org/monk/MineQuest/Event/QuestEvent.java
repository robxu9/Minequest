package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class QuestEvent extends PeriodicEvent {
	private Quest quest;

	public QuestEvent(Quest quest, long delay) {
		super(delay);
		this.quest = quest;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(true);
		eventComplete();
	}
	
	public void eventComplete() {
		Event[] events = quest.getNextEvents();
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
