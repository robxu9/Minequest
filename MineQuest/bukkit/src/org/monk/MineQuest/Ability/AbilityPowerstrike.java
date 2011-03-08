package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityPowerstrike extends Ability{

	public AbilityPowerstrike(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(268, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 3;
	}
	
	@Override
	public String getName() {
		return "PowerStrike";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		if (entity != null) {
			if (MineQuest.getMob(entity) != null) {
				MineQuest.getMob(entity).damage(10);
			} else if (entity instanceof Player) {
				MineQuest.getQuester((Player)entity).damage(10);
			} else {
				entity.setHealth(entity.getHealth() - 10);
			}
		} else {
			giveManaCost(quester.getPlayer());
			quester.getPlayer().sendMessage("PowerStrike must be bound to an attack");
			return;
		}
	}

}
