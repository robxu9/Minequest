package org.monk.MineQuest.Quester.SkillClass.Resource;

import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.ResourceClass;

public class Lumberjack extends ResourceClass {

	public Lumberjack(Quester quester, String type) {
		super(quester, type);
	}
	
	public Lumberjack() {
		// shell
	}

	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 275) return (level > 4);			// Stone
		else if (item == 279) return (level > 49);	// Diamond
		else if (item == 286) return (level > 2);	// Gold
		else if (item == 258) return (level > 19);	// Iron
		
		return super.canUse(itemStack);
	}
	
	@Override
	public boolean isClassItem(ItemStack item) {
		int item_id = item.getTypeId();

		if (item_id == 275) return true;			// Stone
		else if (item_id == 279) return true;	// Diamond
		else if (item_id == 286) return true;	// Gold
		else if (item_id == 258) return true;	// Iron
		else if (item_id == 271) return true;	// Wooden
		
		return super.isClassItem(item);
	}

}
