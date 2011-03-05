package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class SingleAreaEvent extends AreaEvent {

	public SingleAreaEvent(Quest quest, long delay, int index,
			LivingEntity[] entities, Location loc, int radius) {
		super(quest, delay, index, entities, loc, radius);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		boolean flag = false;
		for (i = 0; i < entities.length; i++) {
			if (MineQuest.distance(entities[i].getLocation(), loc) < radius) {
				flag = true;
			}
		}
		
		eventParser.setComplete(flag);
		
		if (flag) {
			MineQuest.log("AreaEvent Complete");
			eventComplete();
		}
	}

}
