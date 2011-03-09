package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;

public class AbilityFireChain extends Ability {
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(263, 1));
		list.add(new ItemStack(263, 1));
		list.add(new ItemStack(263, 1));
		list.add(new ItemStack(263, 1));
		list.add(new ItemStack(263, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 10;
	}
	
	@Override
	public String getName() {
		return "Fire Chain";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = null;
		if (quester != null) {
			player = quester.getPlayer();
		}
		if (entity != null) {
			Ability fireball = Ability.newAbility("Fireball", myclass);
			LivingEntity this_entity;
			int i;

			this_entity = getRandomEntity(entity, 10);
			for (i = 0; i < 3 + myclass.getCasterLevel(); i++) {
				fireball.castAbility(quester, new Location(player.getWorld(), (int) entity.getLocation().getX(),
						(int) entity.getLocation().getY(), (int) entity.getLocation().getZ()),
						entity);
				this_entity = getRandomEntity(this_entity, 10);
				if (myclass.getGenerator().nextDouble() < .1) {
					break;
				}
			}
		} else {
			giveManaCost(player);
			player.sendMessage("FireChain must be bound to an attack");
			return;
		}	
	}

	@Override
	public SkillClass getClassType() {
		return new WarMage();
	}

}
