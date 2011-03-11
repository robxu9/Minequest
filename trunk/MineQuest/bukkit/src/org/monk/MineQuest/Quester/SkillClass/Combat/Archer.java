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
package org.monk.MineQuest.Quester.SkillClass.Combat;

import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;

public class Archer extends CombatClass {

	public Archer(Quester quester, String type) {
		super(quester, type);
	}

	public Archer() {
		// Shell
	}

	@Override
	public int[] getClassArmorIds() {
		int item_ids[] = new int[4];

		item_ids[0] = 306;
		item_ids[1] = 309;
		item_ids[2] = 308;
		item_ids[3] = 307;

		return item_ids;
	}
	
	@Override
	protected int getExpMob(LivingEntity defend) {
		if (defend instanceof CraftZombie) {
			return 10;
		} else {
			return 5;
		}
	}
	
	@Override
	public boolean isClassItem(ItemStack item) {
		int item_id = item.getTypeId();
		
		if (item_id == 261) return true;
		
		return super.isClassItem(item);
	}
}
