package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityDeathblow extends Ability implements PassiveAbility {

	public AbilityDeathblow(String name, SkillClass myclass) {
		super(name, myclass);
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(265, 1));
		list.add(new ItemStack(265, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 12;
	}
	
	@Override
	public String getName() {
		return "Deathblow";
	}

}
