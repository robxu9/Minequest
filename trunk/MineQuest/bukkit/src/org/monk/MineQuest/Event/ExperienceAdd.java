package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class ExperienceAdd extends NormalEvent {
	private Quester[] questers;
	private int exp;
	private int class_exp;
	private Quest quest;

	public ExperienceAdd(Quest quest, long delay, Quester questers[], int exp, int class_exp) {
		super(delay);
		this.questers = questers;
		this.exp = exp;
		this.class_exp = class_exp;
		this.quest = quest;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : questers) {
			quester.sendMessage("You gained " + exp + " exp from a quest");
			quester.expGain(exp);
			quester.sendMessage("You gained " + class_exp + " unassigned exp from a quest");
			quester.expClassGain(class_exp);
		}
		
		quest.getNextEvents(-1);
	}

}
