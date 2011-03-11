package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Digger;

public class AbilityEnhancedFlint extends Ability implements PassiveAbility {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		// Passive Ability		
	}

	@Override
	public SkillClass getClassType() {
		return new Digger();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		int i;
		
		for (i = 0; i < 64; i++) {
			cost.add(new ItemStack(Material.GRAVEL, 1));
		}
		
		return cost;
	}

	@Override
	public String getName() {
		return "Enhanced Flint";
	}

	@Override
	public int getReqLevel() {
		return 30;
	}

}
