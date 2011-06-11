package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.NPCQuester;
import org.monk.MineQuest.Quester.Quester;

public class RespawnEvent extends PeriodicEvent {

	public RespawnEvent(long delay) {
		super(delay);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : MineQuest.getQuesters()) {
			if (quester instanceof NPCQuester) {
				((NPCQuester)quester).redo();
			}
		}
	}

	@Override
	public String getName() {
		return "NPC Respawn Event";
	}

}
