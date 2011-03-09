package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.Archer;

public class AbilitySprintA extends Ability {

	public AbilitySprintA() {
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(288, 1));

		return list;
	}
	
	@Override
	public String getName() {
		return "Sprint";
	}
	
	@Override
	public int getReqLevel() {
		return 3;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		
		player.sendMessage("Casting " + getName());
		Location loc = player.getLocation();
		double rot = loc.getYaw() % 360 - 90;
		while (rot < 0) rot += 360;
		
		if ((rot  < 45) || (rot > 315)) {
			loc.setX(loc.getX() - 1);
		} else if ((rot > 45) && (rot < 135)) {
			loc.setZ(loc.getZ() - 1);
		} else if ((rot > 135) && (rot < 225)) {
			loc.setX(loc.getX() + 1);
		} else {
			loc.setZ(loc.getZ() + 1);
		}
		if ((quester.getQuest() == null) || (quester.getQuest().canEdit(quester, location.getWorld().getBlockAt(location)))) {
			player.teleportTo(loc);
		}
	}

	@Override
	public SkillClass getClassType() {
		return new Archer();
	}

}
