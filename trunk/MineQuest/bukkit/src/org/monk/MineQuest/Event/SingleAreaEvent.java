package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class SingleAreaEvent extends AreaEvent {

	public SingleAreaEvent(Quest quest, long delay, int index,
			Party party, Location loc, int radius) {
		super(quest, delay, index, party, loc, radius);
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		boolean flag = false;
		Quester questers[] = party.getQuesterArray();
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
