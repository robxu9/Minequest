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

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class QuestEvent extends PeriodicEvent {
	private Quest quest;
	private int index;

	public QuestEvent(Quest quest, long delay, int index) {
		super(delay);
		this.quest = quest;
		this.index = index;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(true);
		eventComplete();
	}
	
	public void eventComplete() {
		MineQuest.log("Event Complete");
		quest.issueNextEvents(index);
	}
	
	@Override
	public String getName() {
		return "Generic Quest Event";
	}

}
