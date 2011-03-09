package org.monk.MineQuest.Ability;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityBridge extends Ability {

	public AbilityBridge(String name, SkillClass myclass) {
		super(myclass);
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		// TODO Auto-generated method stub
		return super.getManaCost();
	}
	
	@Override
	public String getName() {
		return "Bridge";
	}

}
