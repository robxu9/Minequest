package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass;

public class AbilityHealOther extends Ability {

	public AbilityHealOther(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(326, 1));
		
		return list;
	}
	
	@Override
	public int getCastTime() {
		return 20000;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		if (entity instanceof Player) {
			Quester other = MineQuest.getQuester((Player)entity);
			if (other != null) {
				player.getInventory().addItem(new ItemStack(325, 1));
				if (other.getHealth() < other.getMaxHealth()) {
					other.setHealth(other.getHealth() + myclass.getCasterLevel() + myclass.getGenerator().nextInt(8) + 1);
				} else {
					player.sendMessage("Quester must not be at full health to heal");
					return;
				}
			} else {
				giveManaCost(player);
				player.sendMessage("entity is not a Quester");
				return;
			}
		} else {
			player.sendMessage(name + " must be cast on another player");
		}
	}

}
