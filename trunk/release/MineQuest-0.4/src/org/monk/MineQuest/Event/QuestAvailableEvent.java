package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quester.Quester;

public class QuestAvailableEvent extends NormalEvent {

	private String quest;
	private Party party;

	public QuestAvailableEvent(long delay, String quest, Party party) {
		super(delay);
		this.quest = quest;
		this.party = party;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : party.getQuesters()) {
			if (!quester.isCompleted(new QuestProspect(quest))) {
				quester.addQuestAvailable(new QuestProspect(quest));
			}
		}
	}

}
