package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass;

public class AbilityFireball extends Ability {

	public AbilityFireball(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(263, 1));
		
		return list;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		
		double leftx, leftz;
		int x, z;
		if ((location.getX() == 0) && (location.getY() == 0) && (location.getZ() == 0)) {
			giveManaCost(player);
			return;
		}
		
		leftx = location.getX() % 1;
		leftz = location.getZ() % 1;
		x = (leftx < .5)?-1:1;
		z = (leftz < .5)?-1:1;
		
		Block nblock = player.getWorld().getBlockAt((int)location.getX(), 
				getNearestY((int)location.getX(), (int)location.getY(), (int)location.getZ()), 
				(int)location.getZ());
		nblock.setTypeId(51);
		
		nblock = player.getWorld().getBlockAt((int)location.getX() + x, 
				getNearestY((int)location.getX() + x, (int)location.getY(), (int)location.getZ()), 
				(int)location.getZ());
		nblock.setTypeId(51);
		
		nblock = player.getWorld().getBlockAt((int)location.getX() + x, 
				getNearestY((int)location.getX() + x, (int)location.getY(), (int)location.getZ() + z), 
				(int)location.getZ() + z);
		nblock.setTypeId(51);
		
		nblock = player.getWorld().getBlockAt((int)location.getX(), 
				getNearestY((int)location.getX(), (int)location.getY(), (int)location.getZ() + z), 
				(int)location.getZ() + z);
		nblock.setTypeId(51);
		
	}
}
