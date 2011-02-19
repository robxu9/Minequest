package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass;

public class AbilityDodge extends Ability {

	public AbilityDodge(String name, SkillClass myclass) {
		super(name, myclass);
		enabled = false;
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(288, 1));
		list.add(new ItemStack(288, 1));
		list.add(new ItemStack(288, 1));
		list.add(new ItemStack(288, 1));
		list.add(new ItemStack(288, 1));
		
		return list;
	}

	@Override
	public boolean isDefending() {
		return enabled;
	}
	
	@Override
	public int parseDefend(Quester quester, LivingEntity mob, int amount) {
		if (!enabled) return 0;
		
		Player player = quester.getPlayer();
		
		if (myclass.getGenerator().nextDouble() < (.01 + (.0025 * myclass.getLevel()))) {
			double rot = player.getLocation().getYaw() % 360;
			while (rot < 0) rot += 360;
			
			if ((rot  < 45) && (rot > 315)) {
				player.sendMessage("Dodging1");
				player.teleportTo(new Location(player.getWorld(), 
						(int)player.getLocation().getX() - 1, 
						getNearestY((int)player.getLocation().getX(), 
								(int)player.getLocation().getY(), 
								(int)player.getLocation().getZ()),
						player.getLocation().getZ(), player.getLocation().getYaw(), 
						player.getLocation().getPitch()));
			} else if ((rot > 45) && (rot < 135)) {
				player.sendMessage("Dodging2");
				player.teleportTo(new Location(player.getWorld(), 
						player.getLocation().getX(), 
						getNearestY((int)player.getLocation().getX(), 
								(int)player.getLocation().getY(), 
								(int)player.getLocation().getZ()),
						player.getLocation().getZ() - 1, player.getLocation().getYaw(), 
						player.getLocation().getPitch()));
			} else if ((rot > 135) && (rot < 225)) {
				player.sendMessage("Dodging3");
				player.teleportTo(new Location(player.getWorld(), 
						(int)player.getLocation().getX() + 1, 
						getNearestY((int)player.getLocation().getX(), 
								(int)player.getLocation().getY(), 
								(int)player.getLocation().getZ()), 
						player.getLocation().getZ(), player.getLocation().getYaw(), 
						player.getLocation().getPitch()));
			} else {
				player.sendMessage("Dodging4");
				player.teleportTo(new Location(player.getWorld(), 
						player.getLocation().getX(), 
						getNearestY((int)player.getLocation().getX(), 
								(int)player.getLocation().getY(), 
								(int)player.getLocation().getZ()),
						player.getLocation().getZ() + 1, player.getLocation().getYaw(), 
						player.getLocation().getYaw()));
			}
			return amount;
		}

		return 0;
	}
}
