package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityLavaToWater extends Ability {

	public AbilityLavaToWater(SkillClass myclass) {
		super(myclass);
	}

	@Override
	public int getReqLevel() {
		return 30;
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		cost.add(new ItemStack(Material.SNOW_BLOCK, 1));
		
		return cost;
	}
	
	@Override
	public String getName() {
		return "Lava To Water";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Location loc = quester.getPlayer().getLocation();
		int x, y, z;
		
		for (x = (int)(loc.getX() - 15); x < (int)(loc.getX() + 15); x++) {
			for (y = (int)(loc.getY() - 15); y < (int)(loc.getY() + 15); y++) {
				for (z = (int)(loc.getZ() - 15); z < (int)(loc.getZ() + 15); z++) {
					Location block_loc = new Location(loc.getWorld(), x, y, z);
					
					if (loc.getWorld().getBlockAt(block_loc).getType() == Material.LAVA) {
						loc.getWorld().getBlockAt(block_loc).setType(Material.WATER);
					}
					if (loc.getWorld().getBlockAt(block_loc).getType() == Material.STATIONARY_LAVA) {
						loc.getWorld().getBlockAt(block_loc).setType(Material.WATER);
					}
				}
			}
		}
	}

}
