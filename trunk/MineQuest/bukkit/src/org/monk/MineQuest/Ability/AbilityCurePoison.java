package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityCurePoison extends Ability {

	public AbilityCurePoison(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(39, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 5;
	}
	
	@Override
	public int getCastTime() {
		return 5000;
	}
	
	@Override
	public String getName() {
		return "Cure Poison";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		
		if (quester.isPoisoned()) {
			quester.curePoison();
		} else {
			player.sendMessage("Quester must not be poisoned to cure poison");
			return;
		}
	}

}
