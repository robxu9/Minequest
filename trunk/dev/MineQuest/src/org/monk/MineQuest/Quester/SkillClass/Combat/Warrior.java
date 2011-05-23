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
package org.monk.MineQuest.Quester.SkillClass.Combat;

import java.util.Random;

import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;

public class Warrior extends CombatClass {

	public Warrior(Quester quester, String type) {
		super(quester, type);
	}
//	
//	public Warrior() {
//		// Shell
//		level = 10;
//		ability_list = new Ability[0];
//		type = MineQuest.getWarriorName();
//		generator = new Random();
//	}

	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 272) return (level >= MineQuest.getStoneReqLevel());		// Stone
		else if (item == 276) return (level >= MineQuest.getDiamondReqLevel());	// Diamond
		else if (item == 283) return (level >= MineQuest.getGoldReqLevel());	// Gold
		else if (item == 267) return (level >= MineQuest.getIronReqLevel());	// Iron
		
		return super.canUse(itemStack);
	}
	
	@Override
	public boolean isClassItem(ItemStack item) {
		int item_id = item.getTypeId();
		
		if (item_id == 272) return true;			// Stone
		else if (item_id == 276) return true;	// Diamond
		else if (item_id == 283) return true;	// Gold
		else if (item_id == 267) return true;	// Iron
		else if (item_id == 268) return true;	// Wooden
		
		return super.isClassItem(item);
	}

}
