package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monksanctum.MineQuest.MineQuest;
import org.monksanctum.MineQuest.Ability.Ability;
import org.monksanctum.MineQuest.Quester.Quester;

public class AbilityPickPocket extends Ability {
	
	public AbilityPickPocket() {
		super();
		config = new int[] {1, 1};
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		if (quester == null) return;
		if (entity == null) {
			quester.sendMessage("Must be cast on a target");
			giveCost(quester.getPlayer());
			return;
		}
		
		if (!(entity instanceof Player)) {
			quester.sendMessage("You cannot Pick Pocket a " + 
					Quester.getCreatureName(entity.getClass().toString()));
			giveCost(quester.getPlayer());
		} else {
			int ch = config[0] + config[1] * myclass.getCasterLevel();
			double chance = ((double)ch) / 100;
			
			if (myclass.getGenerator().nextDouble() < chance) {
				Quester victim = MineQuest.getQuester(entity);
				if (victim != null) {
					ItemStack item = victim.stolen();
					if (item != null) {
						quester.getPlayer().getInventory().addItem(item);
						quester.sendMessage("You stole " + item.getAmount() + 
														" "+ item.getType());
					} else {
						quester.sendMessage("Nothing to steal");
					}
				}
			} else {
				Quester victim = MineQuest.getQuester(entity);
				if (victim != null) {
					victim.startled();
				}
				if (quester.getAbility("Temporary Invisibility") != null) {
					quester.getAbility("Temporary Invisibility").setActive(false);
				}
				quester.sendMessage("Pick Pocket Failed");
			}
		}
	}

	@Override
	public List<ItemStack> getSpellComps() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(Material.BONE, 1));
		list.add(new ItemStack(Material.BONE, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		
		return list;
	}

	@Override
	public String getName() {
		return "Pick Pocket";
	}

	@Override
	public int getReqLevel() {
		return 8;
	}

	@Override
	public String getSkillClass() {
		return "Rogue";
	}

	@Override
	public int getIconLoc() {
		return 45;
	}

}
