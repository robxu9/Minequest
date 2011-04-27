package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class CompleteQuestEvent extends NormalEvent {
	private Quest quest;
	private Party party;

	public CompleteQuestEvent(long delay, Quest quest, Party party) {
		super(delay);
		this.quest = quest;
		this.party = party;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : party.getQuesterArray()) {
			quester.completeQuest(quest.getProspect());
			if (!quest.getProspect().isRepeatable()) {
				quester.remQuestAvailable(quest.getProspect());
			}
		}
	}

}
