package org.monk.MineQuest.Quester.SkillClass.Combat;

import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;

public class PeaceMage extends CombatClass {

	public PeaceMage(Quester quester, String type) {
		super(quester, type);
		// TODO Auto-generated constructor stub
	}
	
	public PeaceMage() {
		// Shell
	}

	@Override
	public int[] getClassArmorIds() {
		int item_ids[] = new int[4];

		item_ids[0] = 314;
		item_ids[1] = 317;
		item_ids[2] = 316;
		item_ids[3] = 315;

		return item_ids;
	}
	
	@Override
	protected int getExpMob(LivingEntity defend) {
		if (defend instanceof CraftSkeleton) {
			return 10;
		} else {
			return 5;
		}
	}

}
