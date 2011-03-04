package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityFireArrow extends Ability {

	public AbilityFireArrow(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(263, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 7;
	}
	
	@Override
	public String getName() {
		return "Fire Arrow";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		Player player = quester.getPlayer();
		
		if ((entity != null) && (player.getInventory().getItemInHand().getTypeId() == 261)) {
			Location loc = entity.getLocation();
			Block block = player.getWorld().getBlockAt((int)loc.getX(), 
					getNearestY(location.getWorld(), (int)location.getX(), (int)location.getY(), (int)location.getZ()), 
					(int)loc.getZ());
			block.setTypeId(51);
			
		} else {
			giveManaCost(player);
			player.sendMessage("Fire Arrow must be bound to a bow attack - Recommended that it is ranged");
			return;
		}
	}

}
