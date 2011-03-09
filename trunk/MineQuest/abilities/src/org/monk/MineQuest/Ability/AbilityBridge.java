package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Miner;

public class AbilityBridge extends Ability {
	
	@Override
	public List<ItemStack> getManaCost() {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public String getName() {
		return "Bridge";
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		
	}

	@Override
	public SkillClass getClassType() {
		return new Miner();
	}

}
