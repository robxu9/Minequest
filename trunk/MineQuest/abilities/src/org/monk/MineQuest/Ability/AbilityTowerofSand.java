package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Digger;

public class AbilityTowerofSand extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Location loc = new Location(location.getWorld(), location.getX(),
				location.getY(), location.getZ());
		int i;
		
		for (i = 0; i < 64; i++) {
			loc.setY(loc.getY() + 1);
			
			Block block = loc.getWorld().getBlockAt(loc);
			
			if ((block != null) && (block.getType() == Material.AIR)) {
				block.setType(Material.SAND);
			}
		}
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
			cost.add(new ItemStack(Material.SAND, 1));
		}
		
		return cost;
	}

	@Override
	public String getName() {
		return "Tower of Sand";
	}

	@Override
	public int getReqLevel() {
		return 10;
	}

}
