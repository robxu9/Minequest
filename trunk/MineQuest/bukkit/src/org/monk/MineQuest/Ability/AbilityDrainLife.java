package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityDrainLife extends Ability{

	public AbilityDrainLife(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(89, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 8;
	}
	
	@Override
	public String getName() {
		return "Drain Life";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		int drain = myclass.getGenerator().nextInt(3 + myclass.getCasterLevel()) + 1;
		if (entity != null) {
			entity.setHealth(entity.getHealth() - drain);
			quester.setHealth(player.getHealth() + drain);
		} else {
			player.sendMessage("Must be called on an Entity");
		}
	}

}
