package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;

public class AbilityFireball extends Ability {

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(263, 1));
		
		return list;
	}
	
	@Override
	public String getName() {
		return "Fireball";
	}
	
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
		if ((location.getX() == 0) && (location.getY() == 0) && (location.getZ() == 0)) {
			giveManaCost(player);
			return;
		}
		
		leftx = location.getX() % 1;
		leftz = location.getZ() % 1;
		x = (leftx < .5)?-1:1;
		z = (leftz < .5)?-1:1;
		
		Block nblock = world.getBlockAt((int)location.getX(), 
				getNearestY(location.getWorld(), (int)location.getX(), (int)location.getY(), (int)location.getZ()), 
				(int)location.getZ());
		nblock.setTypeId(51);
		
		nblock = world.getBlockAt((int)location.getX() + x, 
				getNearestY(location.getWorld(), (int)location.getX() + x, (int)location.getY(), (int)location.getZ()), 
				(int)location.getZ());
		nblock.setTypeId(51);
		
		nblock = world.getBlockAt((int)location.getX() + x, 
				getNearestY(location.getWorld(), (int)location.getX() + x, (int)location.getY(), (int)location.getZ() + z), 
				(int)location.getZ() + z);
		nblock.setTypeId(51);
		
		nblock = world.getBlockAt((int)location.getX(), 
				getNearestY(location.getWorld(), (int)location.getX(), (int)location.getY(), (int)location.getZ() + z), 
				(int)location.getZ() + z);
		nblock.setTypeId(51);
		
		if (entity != null) {
			if (MineQuest.getMob(entity) != null) {
				MineQuest.getMob(entity).damage(2 + (myclass.getCasterLevel() / 2));
			} else if (entity instanceof Player) {
				MineQuest.getQuester((Player)entity).damage(2 + (myclass.getCasterLevel() / 2));
			} else {
				entity.setHealth(entity.getHealth() - (2 + (myclass.getCasterLevel() / 2)));
			}
		}
	}

	@Override
	public SkillClass getClassType() {
		return new WarMage();
	}
}
