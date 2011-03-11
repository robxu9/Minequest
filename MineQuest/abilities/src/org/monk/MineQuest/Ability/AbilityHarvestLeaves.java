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
import org.monk.MineQuest.Quester.SkillClass.Resource.Lumberjack;

public class AbilityHarvestLeaves extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		int i, j, k;
		Location loc = new Location(location.getWorld(), location.getX() - 15,
				location.getY() - 15, location.getZ() - 15);
		
		for (i = 0; i < 30; i++) {
			for (j = 0; j < 30; j++) {
				for (k = 0; k < 30; k++) {
					Block block = loc.getWorld().getBlockAt(loc);
					
					if (block.getType() == Material.LEAVES) {
						block.setType(Material.AIR);
						if (myclass.getGenerator().nextDouble() < .6) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.SAPLING, 1));
						}
					}
					
					loc.setZ(loc.getZ() + 1);
				}
				loc.setY(loc.getY() + 1);
			}
			loc.setX(loc.getX() + 1);
		}
	}

	@Override
	public SkillClass getClassType() {
		return new Lumberjack();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		
		cost.add(new ItemStack(Material.WOOD_SPADE, 1));
		
		return cost;
	}

	@Override
	public String getName() {
		return "Harvest Leaves";
	}

	@Override
	public int getReqLevel() {
		return 10;
	}

}
