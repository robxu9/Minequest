package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityFireResistance extends Ability {

	public AbilityFireResistance(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getName() {
		return "Fire Resistance";
	}
	
	@Override
	public int getReqLevel() {
		return 10;
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(87, 1));
		
		return list;
	}

}
