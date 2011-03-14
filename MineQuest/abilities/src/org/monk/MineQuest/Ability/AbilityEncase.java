package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;

public class AbilityEncase extends Ability {

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = null;
		World world;
		if (quester != null) {
			player = quester.getPlayer();
		}
		
		if (player != null) {
			world = player.getWorld();
		} else {
			world = entity.getWorld();
		}
		
		double leftx, leftz;
		int x, z;
		
		leftx = location.getX() % 1;
		leftz = location.getZ() % 1;
		x = (leftx < .5)?-1:1;
		z = (leftz < .5)?-1:1;
		
		// In progress
		if (x > 0) {
			
		} else {
			
		}
	}

	@Override
	public SkillClass getClassType() {
		return new WarMage();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> cost = new ArrayList<ItemStack>();
		int i;
		
		for (i = 0; i < 10; i++) {
			cost.add(new ItemStack(Material.COBBLESTONE, 1));
		}
		
		return cost;
	}

	@Override
	public String getName() {
		return "Encase";
	}

	@Override
	public int getReqLevel() {
		return 20;
	}

}
