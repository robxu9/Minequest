/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
		Quester questers[] = null;
		if (party != null) questers = party.getQuesterArray();
		if (questers != null) {
			for (i = 0; i < questers.length; i++) {
				if (MineQuest.distance(questers[i].getPlayer().getLocation(), loc) < radius) {
					flags[i] = true;
				}
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
