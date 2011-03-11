package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Lumberjack;

public class AbilityGrowTrees extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Location loc = new Location(location.getWorld(), location.getX() - 2,
				location.getY(), location.getZ() - 2);
		Location loc_2 = new Location(location.getWorld(), location.getX() - 2,
				location.getY(), location.getZ() + 2);
		Location loc_3 = new Location(location.getWorld(), location.getX() + 2,
				location.getY(), location.getZ() + 2);
		Location loc_4 = new Location(location.getWorld(), location.getX() + 2,
				location.getY(), location.getZ() - 2);
		
		loc.getWorld().generateTree(loc, TreeType.TREE);
		loc.getWorld().generateTree(loc_2, TreeType.TREE);
		loc.getWorld().generateTree(loc_3, TreeType.TREE);
		loc.getWorld().generateTree(loc_4, TreeType.TREE);
	}

	@Override
	public SkillClass getClassType() {
		return new Lumberjack();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		
		cost.add(new ItemStack(Material.SAPLING, 1));
		cost.add(new ItemStack(Material.SAPLING, 1));
		cost.add(new ItemStack(Material.SAPLING, 1));
		cost.add(new ItemStack(Material.SAPLING, 1));
		
		return null;
	}

	@Override
	public String getName() {
		return "Grow Trees";
	}

	@Override
	public int getReqLevel() {
		return 30;
	}

}
