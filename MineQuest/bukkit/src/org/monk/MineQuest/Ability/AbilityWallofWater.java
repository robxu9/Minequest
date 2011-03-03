package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.BlockCDEvent;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass;

public class AbilityWallofWater extends Ability {

	public AbilityWallofWater(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(326, 1));
		list.add(new ItemStack(326, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		
		return list;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		double rot = player.getLocation().getYaw() % 360 - 90;
		int x_change, z_change;
		int x, z;
		int i;
		
		player.getInventory().addItem(new ItemStack(325, 1));
		player.getInventory().addItem(new ItemStack(325, 1));
		
		while (rot < 0) rot += 360;
		
		
		if ((rot  < 45) || (rot > 315)) {
			x_change = 0;
			z_change = 1;
			x = (int)player.getLocation().getX() - 2;
			z = (int)player.getLocation().getZ() - 3;
		} else if ((rot > 45) && (rot < 135)) {
			x_change = 1;
			z_change = 0;
			x = (int)player.getLocation().getX() - 3;
			z = (int)player.getLocation().getZ() - 2;
		} else if ((rot > 135) && (rot < 225)) {
			x_change = 0;
			z_change = 1;
			x = (int)player.getLocation().getX() + 3;
			z = (int)player.getLocation().getZ() - 3;
		} else {
			x_change = 1;
			z_change = 0;
			x = (int)player.getLocation().getX() - 3;
			z = (int)player.getLocation().getZ() + 3;
		}

		World world = player.getWorld();
		for (i = 0; i < 7; i++) {
			Block nblock = world.getBlockAt(x, getNearestY(x, (int)player.getLocation().getY(), z), z);
			//nblock.setTypeId(8);
			MineQuest.getEventParser().addEvent(new BlockCDEvent(0, 60000, nblock, Material.LAVA));
			x += x_change;
			z += z_change;
		}
	}

}
