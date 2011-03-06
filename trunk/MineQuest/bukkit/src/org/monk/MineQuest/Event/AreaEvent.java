package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class AreaEvent extends QuestEvent {
	protected Quester[] questers;
	protected boolean[] flags;
	protected int radius;
	protected Location loc;

	public AreaEvent(Quest quest, long delay, int index, Quester questers[], Location loc, int radius) {
		super(quest, delay, index);
		this.questers = questers;
		this.radius = radius;
		this.loc = loc;
		flags = new boolean[questers.length];
		for (boolean bool : flags) {
			bool = false;
		}
	}

	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		for (i = 0; i < questers.length; i++) {
			if (MineQuest.distance(questers[i].getPlayer().getLocation(), loc) < radius) {
				flags[i] = true;
			}
		}
		
		boolean flag = true;
		
		for (boolean flg : flags) {
			if (!flg) {
				flag = false;
			}
		}
		
		eventParser.setComplete(flag);
		
		if (flag) {
			eventComplete();
		}
	}
	
	@Override
	public String getName() {
		return "Area Event";
	}
}
