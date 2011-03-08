package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

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
		MineQuest.log("Event Complete");
		quest.issueNextEvents(index);
	}
	
	@Override
	public String getName() {
		return "Generic Quest Event";
	}

}
