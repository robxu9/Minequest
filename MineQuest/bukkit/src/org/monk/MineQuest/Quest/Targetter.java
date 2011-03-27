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
package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.Event.TargetEvent;
import org.monk.MineQuest.Quester.Quester;

public class Targetter extends Target {
	private TargetEvent[] events;

	public Targetter(TargetEvent events[]) {
		this.events = events;
	}
	
	@Override
	public Quester[] getTargets() {
		List<Quester> questers = new ArrayList<Quester>();
		
		for (TargetEvent event : events) {
			if (event.getTarget() != null) {
				questers.add(event.getTarget());
			}
		}
		return (Quester[])questers.toArray();
	}
}
