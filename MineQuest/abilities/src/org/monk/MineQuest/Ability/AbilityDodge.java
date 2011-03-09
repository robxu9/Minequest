package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.Warrior;

public class AbilityDodge extends Ability implements PassiveAbility {

	public AbilityDodge() {
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
	public int getReqLevel() {
		return 1;
	}
	
	@Override
	public String getName() {
		return "Dodge";
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
			Location location = player.getLocation();
			
			if ((rot  < 45) && (rot > 315)) {
				player.sendMessage("Dodging 1");
				location = new Location(player.getWorld(), 
						(int)player.getLocation().getX() - 1, 
						(int)player.getLocation().getY(), 
						player.getLocation().getZ(), player.getLocation().getYaw(), 
						player.getLocation().getPitch());
			} else if ((rot > 45) && (rot < 135)) {
				player.sendMessage("Dodging 2");
				location = new Location(player.getWorld(), 
						player.getLocation().getX(), 
						(int)player.getLocation().getY(), 
						player.getLocation().getZ() - 1, player.getLocation().getYaw(), 
						player.getLocation().getPitch());
			} else if ((rot > 135) && (rot < 225)) {
				player.sendMessage("Dodging 3");
				location = new Location(player.getWorld(), 
						(int)player.getLocation().getX() + 1, 
						(int)player.getLocation().getY(), 
						player.getLocation().getZ(), player.getLocation().getYaw(), 
						player.getLocation().getPitch());
			} else {
				player.sendMessage("Dodging 4");
				location = new Location(player.getWorld(), 
						player.getLocation().getX(), 
						getNearestY(player.getWorld(), (int)player.getLocation().getX(), 
								(int)player.getLocation().getY(), 
								(int)player.getLocation().getZ()),
						player.getLocation().getZ() + 1, player.getLocation().getYaw(), 
						player.getLocation().getPitch());
			}
			if (player.getWorld().getBlockAt(location).getType() == Material.AIR) {
				player.teleportTo(location);
			}
			return amount;
		}

		return 0;
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		// Passive
	}

	@Override
	public SkillClass getClassType() {
		return new Warrior();
	}
}
