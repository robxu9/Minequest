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
package org.monk.MineQuest.Quest.CanEdit;

import org.bukkit.Location;
import org.monk.MineQuest.Quester.Quester;

public class CanEditBlock extends CanEdit {
	public Quester quester;
	private Location location;
	private int index;
	private boolean active;
	private int id;
	
	public CanEditBlock(Location location, int index) {
		this.location = location;
		this.index = index;
		this.active = false;
	}

	private boolean equals(Location location, Location location2) {
		if (((int)location.getX()) != ((int)location2.getX())) {
			return false;
		}
		if (((int)location.getY()) != ((int)location2.getY())) {
			return false;
		}
		if (((int)location.getZ()) != ((int)location2.getZ())) {
			return false;
		}
		
		return true;
	}

	@Override
	public Quester getTarget() {
		return quester;
	}

	@Override
	public boolean canEdit(Quester quester, Location loc) {
		if (equals(location, loc)) {
			this.quester = quester;
			this.active = !active;
			return true;
		}
		return false;
	}

	@Override
	public int getQuestIndex() {
		return index;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

}
