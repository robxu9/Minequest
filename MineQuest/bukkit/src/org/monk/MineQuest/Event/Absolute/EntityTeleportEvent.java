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

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.NormalEvent;
import org.monk.MineQuest.Quester.Quester;

public class EntityTeleportEvent extends NormalEvent {
	protected LivingEntity entity;
	protected Location location;
	private Quester quester;

	public EntityTeleportEvent(long delay, LivingEntity entity, Location location) {
		super(delay);
		this.entity = entity;
		this.location = location;
		quester = null;
	}

	public EntityTeleportEvent(int delay, Quester quester,
			Location location) {
		super(delay);
		this.quester = quester;
		this.location = location;
	}

	@Override
	public void activate(EventParser eventParser) {
		if (quester != null) {
			quester.getPlayer().teleportTo(location);
		} else {
			entity.teleportTo(location);
		}
	}

	@Override
	public String getName() {
		return "Generic Teleport Event";
	}

}
