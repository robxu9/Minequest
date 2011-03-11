/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2010  Jason Monk
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

import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.ResourceClass;

public class Farmer extends ResourceClass {

	public Farmer(Quester quester, String type) {
		super(quester, type);
	}
	
	public Farmer() {
		// Shell
	}

	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 292) return (level > 4);			// Stone
		else if (item == 293) return (level > 49);	// Diamond
		else if (item == 294) return (level > 2);	// Gold
		else if (item == 291) return (level > 19);	// Iron
		
		return super.canUse(itemStack);
	}
	
	@Override
	public boolean isClassItem(ItemStack item) {
		int item_id = item.getTypeId();

		if (item_id == 292) return true;			// Stone
		else if (item_id == 293) return true;	// Diamond
		else if (item_id == 294) return true;	// Gold
		else if (item_id == 291) return true;	// Iron
		else if (item_id == 290) return true;	// Wooden
		
		return super.isClassItem(item);
	}

}
