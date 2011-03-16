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
package org.monk.MineQuest.Event.Absolute;

import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.NormalEvent;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quester.Quester;

public class ExperienceAdd extends NormalEvent {
	private int exp;
	private int class_exp;
	private Party party;

	public ExperienceAdd(long delay, Party party, int exp, int class_exp) {
		super(delay);
		this.party = party;
		this.exp = exp;
		this.class_exp = class_exp;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		for (Quester quester : party.getQuesterArray()) {
			quester.sendMessage("You gained " + exp + " exp from a quest");
			quester.expGain(exp);
			quester.sendMessage("You gained " + class_exp + " unassigned exp from a quest");
			quester.expClassGain(class_exp);
		}
	}

}
