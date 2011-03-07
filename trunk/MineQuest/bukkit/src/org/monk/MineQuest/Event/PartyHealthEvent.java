package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quester.Quester;

public class PartyHealthEvent extends NormalEvent {
	private Party party;
	private double percent;

	public PartyHealthEvent(long delay, Party party, double percent) {
		super(delay);
		this.party = party;
		this.percent = percent;
	}
	
	@Override
	public String getName() {
		return "Party Health Event";
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : party.getQuesterArray()) {
			quester.setHealth((int)(quester.getMaxHealth() * percent));
		}
	}

}
