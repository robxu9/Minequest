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
package org.monk.MineQuest.Quester.SkillClass.Resource;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.ResourceClass;

public class Digger extends ResourceClass {

	public Digger(Quester quester, String type) {
		super(quester, type);
	}
	
	public Digger() {
		// Shell
	}
	
	@Override
	public void blockBreak(BlockBreakEvent event) {
		super.blockBreak(event);

		if (event.getBlock().getType() == Material.GRAVEL) {
			if ((getAbility("Enhanced Flint") != null) && 
					(getAbility("Enhanced Flint").isEnabled())) {
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
						new ItemStack(Material.FLINT, 1));
			}
		}
	}

	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 273) return (level >= MineQuest.getStoneReqLevel());			// Stone
		else if (item == 277) return (level >= MineQuest.getDiamondReqLevel());	// Diamond
		else if (item == 284) return (level >= MineQuest.getGoldReqLevel());	// Gold
		else if (item == 284) return (level >= MineQuest.getIronReqLevel());	// Iron
		
		return super.canUse(itemStack);
	}
	
	@Override
	public boolean isClassItem(ItemStack item) {
		int item_id = item.getTypeId();

		if (item_id == 273) return true;			// Stone
		else if (item_id == 277) return true;	// Diamond
		else if (item_id == 284) return true;	// Gold
		else if (item_id == 284) return true;	// Iron
		else if (item_id == 269) return true;	// Wooden
		
		return super.isClassItem(item);
	}

}
