package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.Warrior;

public class AbilityDeathblow extends Ability implements PassiveAbility {
	
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
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		// Passive
	}

	@Override
	public SkillClass getClassType() {
		return new Warrior();
	}

}
