package org.monk.MineQuest.Quester.SkillClass.Combat;

import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;

public class Warrior extends CombatClass {

	public Warrior(Quester quester, String type) {
		super(quester, type);
	}
	
	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 272) return (level > 4);			// Stone
		else if (item == 276) return (level > 49);	// Diamond
		else if (item == 283) return (level > 2);	// Gold
		else if (item == 267) return (level > 19);	// Iron
		
		return super.canUse(itemStack);
	}
	
	@Override
	public int[] getClassArmorIds() {
		int item_ids[] = new int[4];

		item_ids[0] = 310;
		item_ids[1] = 313;
		item_ids[2] = 312;
		item_ids[3] = 311;

		return item_ids;
	}
	
	@Override
	protected int getExpMob(LivingEntity defend) {
		if (!isClassItem(quester.getPlayer().getItemInHand())) {
			return 3;
		}
		if (defend instanceof CraftCreeper) {
			return 10;
		} else {
			return 5;
		}
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
