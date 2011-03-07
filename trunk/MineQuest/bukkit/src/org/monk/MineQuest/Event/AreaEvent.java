package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.Quester;

public class AreaEvent extends QuestEvent {
	protected boolean[] flags;
	protected int radius;
	protected Location loc;
	protected Party party;

	public AreaEvent(Quest quest, long delay, int index, Party party, Location loc, int radius) {
		super(quest, delay, index);
		this.party = party;
		this.radius = radius;
		this.loc = loc;
		flags = new boolean[party.getQuesters().size()];
		int i;
		for (i = 0; i < flags.length; i++) {
			flags[i] = false;
		}
	}

	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		Quester questers[] = party.getQuesterArray();
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
