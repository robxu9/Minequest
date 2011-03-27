package org.monk.MineQuest.Event;

import org.monk.MineQuest.Ability.Ability;

public class AbilityEvent extends NormalEvent {
	private Ability ability;

	public AbilityEvent(long delay, Ability ability) {
		super(delay);
		this.ability = ability;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		ability.eventActivate();
	}

}
