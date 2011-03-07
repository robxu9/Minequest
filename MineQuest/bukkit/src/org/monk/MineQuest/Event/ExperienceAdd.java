package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quester.Quester;

public class ExperienceAdd extends NormalEvent {
	private int exp;
	private int class_exp;
	private Party party;

	public ExperienceAdd(long delay, Party party, int exp, int class_exp) {
		super(delay);
		this.party = party;
		this.exp = exp;
		this.class_exp = class_exp;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : party.getQuesterArray()) {
			quester.sendMessage("You gained " + exp + " exp from a quest");
			quester.expGain(exp);
			quester.sendMessage("You gained " + class_exp + " unassigned exp from a quest");
			quester.expClassGain(class_exp);
		}
	}

}
