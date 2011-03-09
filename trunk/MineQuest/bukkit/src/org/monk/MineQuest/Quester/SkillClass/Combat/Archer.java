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
