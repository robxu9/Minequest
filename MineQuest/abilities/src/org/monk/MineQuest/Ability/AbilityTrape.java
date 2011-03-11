package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.PeaceMage;

public class AbilityTrape extends Ability {

	public AbilityTrape() {
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(269, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 2;
	}
	
	@Override
	public String getName() {
		return "Trape";
	}
	
	@Override
	public int getCastTime() {
		return 5000;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		int i, j, k;
		int x, y, z;
		World world = location.getWorld();
		x = (int)location.getX();
		y = (int)location.getY();
		z = (int)location.getZ();
		
		for (i = 1; i < 3; i++) {
			for (j = -1; j < 2; j++) {
				for (k = -1; k < 2; k++) {
					Block nblock = world.getBlockAt(x + j, y - i, z + k);
					if ((quester.getQuest() == null) || (quester.getQuest().canEdit(quester, nblock))) {
						if (nblock.getType() != Material.BEDROCK) {
							nblock.setTypeId(0);
						}
					}
				}
			}
		}
	}

	@Override
	public SkillClass getClassType() {
		return new PeaceMage();
	}

}
