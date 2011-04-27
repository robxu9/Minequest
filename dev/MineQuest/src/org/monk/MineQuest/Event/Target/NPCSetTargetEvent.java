package org.monk.MineQuest.Event.Target;

import org.bukkit.Location;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.NPCQuester;
import org.monk.MineQuest.Quester.Quester;

public class NPCSetTargetEvent extends TargetedEvent {
	private Location location;

	public NPCSetTargetEvent(long delay, Target target, Location location) {
		super(delay, target);
		this.location = location;
	}

	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : target.getTargets()) {
			if (quester instanceof NPCQuester) {
				((NPCQuester)quester).setTarget(location);
			}
		}
	}
	
	@Override
	public String getName() {
		return "NPC Set Target Event";
	}
}
