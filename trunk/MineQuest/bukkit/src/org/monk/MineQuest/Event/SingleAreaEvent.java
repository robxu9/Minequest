package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class SingleAreaEvent extends AreaEvent {

	public SingleAreaEvent(Quest quest, long delay, int index,
			Quester[] questers, Location loc, int radius) {
		super(quest, delay, index, questers, loc, radius);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		boolean flag = false;
		for (i = 0; i < questers.length; i++) {
			if (MineQuest.distance(questers[i].getPlayer().getLocation(), loc) < radius) {
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
