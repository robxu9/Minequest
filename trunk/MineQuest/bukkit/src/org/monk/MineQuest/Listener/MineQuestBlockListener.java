package org.monk.MineQuest.Listener;


import org.bukkit.event.block.BlockListener;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.World.Property;
import org.monk.MineQuest.World.Town;

public class MineQuestBlockListener extends BlockListener {
	
	@Override
	public void onBlockDamage(org.bukkit.event.block.BlockDamageEvent event) {
		Town town = MineQuest.getTown(event.getBlock().getLocation());
		Quester quester = MineQuest.getQuester(event.getPlayer());
		
		if (quester.inQuest()) {
			event.setCancelled(true);
			quester.sendMessage("A Mystical Force is keeping you from Modifying the world!");
		}
	
		quester.checkItemInHand();
		if (quester.checkItemInHandAbilL()) {
			quester.callAbilityL(event.getBlock());
		}
		quester.destroyBlock(event);
		
		if (town != null) {
			Property prop = town.getProperty(event.getBlock().getLocation());
			
			if (prop != null) {
				if (prop.canEdit(quester)) {
					return;
				} else {
					event.getPlayer().sendMessage("You are not authorized to modify this property - please get the proper authorization");
					quester.dropRep(20);
					event.setCancelled(true);
					return;
				}
			} else {
				prop = town.getTownProperty();
				if (prop.canEdit(quester)) {
					return;
				} else {
					event.getPlayer().sendMessage("You are not authorized to modify town - please get the proper authorization");
					quester.dropRep(10);
					event.setCancelled(true);
					return;
				}
			}	
		}
		
		super.onBlockDamage(event);
	}
	
	@Override
	public void onBlockRightClick(org.bukkit.event.block.BlockRightClickEvent event) {
		if (!MineQuest.getQuester(event.getPlayer()).isEnabled()) {
			return;
		}
		Quester quester = MineQuest.getQuester(event.getPlayer());
		
		quester.checkItemInHand();
		if (quester.checkItemInHandAbilR()) {
			quester.callAbilityR(event.getBlock());
		}
		
		super.onBlockRightClick(event);
	}
	
	@Override
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
		Town town = MineQuest.getTown(event.getBlock().getLocation());
		Quester quester = MineQuest.getQuester(event.getPlayer());
		
		if (quester.inQuest()) {
			event.setCancelled(true);
			quester.sendMessage("A Mystical Force is keeping you from Modifying the world!");
		}
		
		quester.checkItemInHand();
		if (quester.checkItemInHandAbilR()) {
			quester.callAbilityR(event.getBlock());
		}
		
		if (town != null) {
			Property prop = town.getProperty(event.getBlock().getLocation());
			
			if (prop != null) {
				if (prop.canEdit(MineQuest.getQuester(event.getPlayer()))) {
					return;
				} else {
					event.getPlayer().sendMessage("You are not authorized to modify this property - please get the proper authorization");
					MineQuest.getQuester(event.getPlayer()).dropRep(20);
					event.setCancelled(true);
					return;
				}
			} else {
				prop = town.getTownProperty();
				if (prop.canEdit(MineQuest.getQuester(event.getPlayer()))) {
					return;
				} else {
					event.getPlayer().sendMessage("You are not authorized to modify town - please get the proper authorization");
					MineQuest.getQuester(event.getPlayer()).dropRep(10);
					event.setCancelled(true);
					return;
				}
			}
			
		}
		
		super.onBlockPlace(event);
	}

}
