package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Farmer;

public class AbilityPurgeAnimals extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		purgeEntities(quester.getPlayer(), 10, PurgeType.ANIMAL);
	}

	@Override
	public SkillClass getClassType() {
		return new Farmer();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		
		cost.add(new ItemStack(Material.TORCH, 1));
		cost.add(new ItemStack(Material.STICK, 1));
		cost.add(new ItemStack(Material.STICK, 1));
		cost.add(new ItemStack(Material.STICK, 1));
		cost.add(new ItemStack(Material.STICK, 1));
		cost.add(new ItemStack(Material.STICK, 1));
		
		return cost;
	}

	@Override
	public String getName() {
		return "Purge Animals";
	}

	@Override
	public int getReqLevel() {
		return 10;
	}

}
