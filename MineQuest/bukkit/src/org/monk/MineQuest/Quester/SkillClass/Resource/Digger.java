package org.monk.MineQuest.Quester.SkillClass.Resource;

import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.ResourceClass;

public class Digger extends ResourceClass {

	public Digger(Quester quester, String type) {
		super(quester, type);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (item == 273) return (level > 4);			// Stone
		else if (item == 277) return (level > 49);	// Diamond
		else if (item == 284) return (level > 2);	// Gold
		else if (item == 284) return (level > 19);	// Iron
		
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
