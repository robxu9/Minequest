package org.monk.MineQuest.Event;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.QuestTask;

public class AreaEvent extends QuestEvent {
	protected LivingEntity[] entities;
	protected boolean[] flags;
	protected int radius;
	protected Location loc;

	public AreaEvent(Quest quest, long delay, int index, LivingEntity entities[], Location loc, int radius) {
		super(quest, delay, index);
		this.entities = entities;
		this.radius = radius;
		this.loc = loc;
		flags = new boolean[entities.length];
		for (boolean bool : flags) {
			bool = false;
		}
	}

	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		
		int i;
		for (i = 0; i < entities.length; i++) {
			if (MineQuest.distance(entities[i].getLocation(), loc) < radius) {
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
