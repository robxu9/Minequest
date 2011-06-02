package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;

public class AbilityBackStab extends Ability {
	public AbilityBackStab() {
		super();
		config = new int[] {250, 0, 1, 1};
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		if (quester == null) return;
		Player player = quester.getPlayer();
		if (player == null) return;
		if (entity == null) {
			quester.sendMessage(getName() + " must be used on an entity");
			giveManaCost(player);
			return;
		}
		
		if (checkBehind(player, entity)) {
			int mult = config[0] + config[1] * myclass.getCasterLevel();
			double multiply = ((double)mult) / 100;
			int damage;
			if (myclass instanceof CombatClass) {
				damage = ((CombatClass)myclass).getDamage();
			} else {
				damage = config[3];
			}
			MineQuest.damage(entity, (int)(damage * multiply));
		} else {
			notify(quester, getName() + " must be used from behind the target");
			if (config[3] > 0) {
				giveManaCost(player);
			}
		}
	}

	private boolean checkBehind(Player player, LivingEntity entity) {
		Location source = player.getLocation();
		Location dest = entity.getLocation();
		double rot = Math.PI * (source.getYaw() / -180);
		Vector vec = new Vector(Math.sin(rot), 0, Math.cos(rot));
		if (Math.abs(source.getYaw() - dest.getYaw()) > 30) {
			return false;
		}
		Vector difference = new Vector(dest.getX() - source.getX(), 
										dest.getY() - source.getY(), 
										dest.getZ() - source.getZ());
		if (vec.dot(difference) < 0) {
			return false;
		}
		
		return true;
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(Material.BONE, 1));
		list.add(new ItemStack(Material.BONE, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		
		return list;
	}

	@Override
	public String getName() {
		return "Back Stab";
	}

	@Override
	public int getReqLevel() {
		return 5;
	}

	@Override
	public String getSkillClass() {
		return "Rogue";
	}

}
