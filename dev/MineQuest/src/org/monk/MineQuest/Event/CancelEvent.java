package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Quest;

public class CancelEvent extends NormalEvent
{

	private Quest quest;
	private int cancel_id;

	public CancelEvent(long delay, Quest quest, int id) {
		super(delay);
		this.quest = quest;
		this.cancel_id = id;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		quest.getEvent(cancel_id).cancelEvent();
	}

}
