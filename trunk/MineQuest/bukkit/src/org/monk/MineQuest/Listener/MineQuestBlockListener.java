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
		
		if (quester.isDebug()) {
			quester.sendMessage(event.getBlock().getX() + " " + 
					event.getBlock().getY() + " " + event.getBlock().getZ()
					 + " " + event.getBlock().getType());
		}
		
		if (quester.inQuest()) {
			event.setCancelled(quester.getQuest().canEdit(quester, event.getBlock()));
		}
	
		quester.checkItemInHand();
		if (quester.checkItemInHandAbil()) {
			quester.callAbility(event.getBlock());
			event.setCancelled(true);
			return;
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
		Quester quester = MineQuest.getQuester(event.getPlayer());
		
		if (quester.inQuest()) {
			quester.getQuest().canEdit(quester, event.getBlock());
		}
		
		if (quester.isDebug()) {
			quester.sendMessage(event.getBlock().getX() + " " + 
					event.getBlock().getY() + " " + event.getBlock().getZ()
					 + " " + event.getBlock().getType());
		}
		
		quester.checkItemInHand();
		if (quester.checkItemInHandAbil()) {
			quester.callAbility(event.getBlock());
			return;
		}
		
		quester.rightClick(event.getBlock());
		
		super.onBlockRightClick(event);
	}
	
	@Override
	public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
		Town town = MineQuest.getTown(event.getBlock().getLocation());
		Quester quester = MineQuest.getQuester(event.getPlayer());
		
		if (quester.isDebug()) {
			quester.sendMessage(event.getBlock().getX() + " " + 
					event.getBlock().getY() + " " + event.getBlock().getZ()
					 + " " + event.getBlock().getType());
		}
		
		if (quester.inQuest()) {
			event.setCancelled(quester.getQuest().canEdit(quester, event.getBlock()));
		}
		
		quester.checkItemInHand();
		if (quester.checkItemInHandAbil()) {
			quester.callAbility(event.getBlock());
			event.setCancelled(true);
			return;
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
