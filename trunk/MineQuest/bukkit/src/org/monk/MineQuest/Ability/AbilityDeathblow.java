package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityDeathblow extends Ability {

	public AbilityDeathblow(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
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
	
	@Override
	public void useAbility(Quester quester, Location location, int l,
			LivingEntity entity) {
		// TODO Auto-generated method stub
		super.useAbility(quester, location, l, entity);
	}

}
