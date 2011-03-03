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
		for (Event event : quest.getNextEvents()) {
			MineQuest.getEventParser().addEvent(event);
		}
	}
	
	@Override
	public String getName() {
		return "Generic Quest Event";
	}

}
