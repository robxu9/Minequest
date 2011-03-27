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
package org.monk.MineQuest.Event.Relative;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.NormalEvent;

public class BlockEvent extends NormalEvent {
	protected Material newType;
	protected Block block;
	protected Entity entity;
	
	public BlockEvent(long delay, World world, Entity entity, int x, int y,
			int z, Material newType) {
		this(delay, world.getBlockAt(x, y, z), entity, newType);
	}

	public BlockEvent(long delay, Block block, Entity entity, Material newType) {
		super(delay);
		this.block = block;
		this.newType = newType;
		this.entity = entity;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		block.setType(newType);
	}

	@Override
	public String getName() {
		return "Generic Block Type Event";
	}

}
