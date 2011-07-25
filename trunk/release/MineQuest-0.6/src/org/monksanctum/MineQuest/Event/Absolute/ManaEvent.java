package org.monksanctum.MineQuest.Event.Absolute;

import org.monksanctum.MineQuest.MineQuest;
import org.monksanctum.MineQuest.Event.EventParser;
import org.monksanctum.MineQuest.Event.PeriodicEvent;
import org.monksanctum.MineQuest.Quester.Quester;

public class ManaEvent extends PeriodicEvent {

	private int amount;

	public ManaEvent(long delay, int amount) {
		super(delay);
		this.amount = amount;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : MineQuest.getQuesters()) {
			if (!quester.inQuest()) {
				quester.setMana(quester.getMana() + amount);
			}
		}
	}

}
