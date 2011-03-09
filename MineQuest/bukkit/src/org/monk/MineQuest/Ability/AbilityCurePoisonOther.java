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

public class AbilityCurePoisonOther extends Ability {

	public AbilityCurePoisonOther(SkillClass myclass) {
		super(myclass);
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
		return "Cure Poison Other";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		if (entity instanceof Player) {
			Quester other = MineQuest.getQuester((Player)entity);
			if (other != null) {
				if (other.isPoisoned()) {
					other.curePoison();
				} else {
					player.sendMessage("Quester must be poisoned to Cure Poison");
					return;
				}
			} else {
				giveManaCost(player);
				player.sendMessage("entity is not a Quester");
				return;
			}
		} else {
			giveManaCost(player);
			player.sendMessage(getName() + " must be cast on another player");
		}
	}

}
