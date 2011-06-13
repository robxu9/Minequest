package org.monk.MineQuest.Event.Target;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.MessageEvent;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.Quester;

public class StartQuestEvent extends TargetedEvent {

	private String quest;

	public StartQuestEvent(long delay, Target target, String quest) {
		super(delay, target);
		this.quest = quest;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		Party party = new Party();
		
		for (Quester quester : target.getTargets()) {
			party.addQuester(quester);
		}

		if (party.getQuesters().size() > 0) {
			if (!party.getQuesters().get(0).inQuest()) {
				party.getQuesters().get(0).startQuest(quest);
				MineQuest.getEventQueue().addEvent(new MessageEvent(10, party, "Starting Quest: " + (new QuestProspect(quest)).getName()));
			}
		}
	}

	@Override
	public String getName() {
		return "Start Quest Event";
	}

}
