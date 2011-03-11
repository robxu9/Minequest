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

public class AbilityGrowWheat extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		int x, z;
		
		for (z = -2; z < 3; z++) {
			for (x = -2; x < 3; x++) {
				if (location.getWorld().getBlockAt(
						(int)location.getX() + x, 
						(int)location.getY() - 1, 
						(int)location.getZ() + z).getType() == Material.SOIL) {
					location.getWorld().getBlockAt(
							(int)location.getX() + x, 
							(int)location.getY(), 
							(int)location.getZ() + z).setType(Material.CROPS);
				}
			}
		}
	}

	@Override
	public SkillClass getClassType() {
		return new Farmer();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		int i;
		
		for (i = 0; i < 10; i++) {
			cost.add(new ItemStack(Material.SEEDS, 1));
		}
		
		return cost;
	}

	@Override
	public String getName() {
		return "Grow Wheat";
	}

	@Override
	public int getReqLevel() {
		return 30;
	}

}
