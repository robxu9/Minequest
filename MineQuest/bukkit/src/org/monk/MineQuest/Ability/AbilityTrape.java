package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityTrape extends Ability {

	public AbilityTrape(SkillClass myclass) {
		super(myclass);
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(3, 1));
		list.add(new ItemStack(269, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 2;
	}
	
	@Override
	public String getName() {
		return "Trape";
	}
	
	@Override
	public int getCastTime() {
		return 5000;
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		int i, j, k;
		int x, y, z;
		if (entity == null) {
			giveManaCost(player);
			player.sendMessage(getName() + " must be used on a living entity");
			return;
		}
		World world = entity.getWorld();
		x = (int)entity.getLocation().getX();
		y = (int)entity.getLocation().getY();
		z = (int)entity.getLocation().getZ();
		
		for (i = 1; i < 3; i++) {
			for (j = -1; j < 2; j++) {
				for (k = -1; k < 2; k++) {
					Block nblock = world.getBlockAt(x + j, y - i, z + k);

					if ((quester.getQuest() == null) || (quester.getQuest().canEdit(quester, nblock))) {
						nblock.setTypeId(0);
					}
				}
			}
		}
	}

}
