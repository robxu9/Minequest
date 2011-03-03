package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityRepulsion extends Ability {

	public AbilityRepulsion(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(50, 1));
		list.add(new ItemStack(81, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 10;
	}
	
	@Override
	public String getName() {
		return "Repulsion";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		purgeEntities(quester.getPlayer(), 10, 0);
	}

}
