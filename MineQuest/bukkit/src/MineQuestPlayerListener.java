

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MineQuestPlayerListener extends PlayerListener {
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		MineQuest.getQuester(event.getPlayer()).move(event.getFrom(), event.getTo());
		super.onPlayerMove(event);
	}

	public void onPlayerJoin(PlayerEvent event) {
		if (MineQuest.getQuester(event.getPlayer()) == null) {
			MineQuest.addQuester(new Quester(event.getPlayer(), 0, MineQuest.getSQLServer()));
		} else {
			MineQuest.getQuester(event.getPlayer()).update(event.getPlayer());
		}
		super.onPlayerJoin(event);
	}
	
	
	
	@Override
	public void onPlayerTeleport(PlayerMoveEvent event) {
		MineQuest.getQuester(event.getPlayer()).teleport();
	}
	
	public void onPlayerQuit(PlayerEvent event) {
		if (MineQuest.getQuester(event.getPlayer()) != null) {
			MineQuest.getQuester(event.getPlayer()).save();
		}
//		MineQuest.remQuester(MineQuest.getQuester(event.getPlayer()));
		super.onPlayerQuit(event);
	}
	
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String split[] = event.getMessage().split(" ");
		Player player = event.getPlayer();
		
		if (split[0].equals("/updatespawn")) {
			player.sendMessage("Not implemented yet");
			event.setCancelled(true);
		} else if (split[0].equals("/spawn")) {
			int index, i;
			double distance;
			List<Town> towns = MineQuest.getTowns();
			
			if (towns.size() == 0) {
				player.sendMessage("There are no spawns - Contact your administrator");
				event.setCancelled(true);
			}
			index = 0;
			distance = towns.get(0).calcDistance(player);
			for (i = 1; i < towns.size(); i++) {
				if (towns.get(i).calcDistance(player) < distance) {
					distance = towns.get(i).calcDistance(player);
					index = i;
				}
			}
			player.teleportTo(towns.get(index).getLocation());
			player.sendMessage("Welcome to " + towns.get(index).getName());
			event.setCancelled(true);
		} else if (split[0].equals("/townloc")) {
			player.sendMessage("You are at " + (int)player.getLocation().getX() + " " + (int)player.getLocation().getY() + " " + (int)player.getLocation().getZ());
			event.setCancelled(true);
		} else if (split[0].equals("/mystash")) {
			MineQuest.getQuester(player).getChestSet(player).add(player);
			event.setCancelled(true);
		} else if (split[0].equals("/cancel")) {
			MineQuest.getQuester(player).getChestSet(player).cancelAdd(player);
			event.setCancelled(true);
		} else if (split[0].equals("/dropstash")) {
			Town town = MineQuest.getTown(player);
			MineQuest.getQuester(player).getChestSet(player).rem(player, town);
			event.setCancelled(true);
		} else if (split[0].equals("/char")) {
			Quester quester = MineQuest.getQuester(player);
			player.sendMessage("You are level " + quester.getLevel() + " with " + quester.getExp() + "/" + (400 * (quester.getLevel() + 1)) + " Exp");

			event.setCancelled(true);
		} else if (split[0].equals("/minequest")) {
			player.sendMessage("Minequest Commands:");
			player.sendMessage("    /save - save progress of character");
			player.sendMessage("    /load - load progress - removing unsaved experience/levels");
			player.sendMessage("    /quest - enable minequest for your character (enabled by default)");
			player.sendMessage("    /noquest - disable minequest for your character");
			player.sendMessage("    /char - information about your character level");
			player.sendMessage("    /class <classname> - information about a specific class");
			player.sendMessage("    /health - display your health");
			player.sendMessage("    /abillist [classname] - display all abilities or for a specific class");
			player.sendMessage("    /enableabil <ability name> - enable an ability (enabled by default)");
			player.sendMessage("    /disableabil <ability name> - disable an ability");
			player.sendMessage("    /bind <ability name> <l or r> - bind an ability to current item");
			player.sendMessage("    /unbind - unbind current item from all abilities");
			player.sendMessage("    /spellcomp <ability name> - list the components required for an ability");
			event.setCancelled(true);
		} else if (split[0].equals("/save")) {
			MineQuest.getQuester(player).save();
			event.setCancelled(true);
		} else if (split[0].equals("/load")) {
			MineQuest.getQuester(player).update();
			event.setCancelled(true);
		} else if (split[0].equals("/quest")) {
			MineQuest.getQuester(player).enable();
			event.setCancelled(true);
		} else if (split[0].equals("/zombie")) {
			//Mob mymob = new Mob("Zombie", new Location(player.getX() + 3, player.getY(), player.getZ()));
			//mymob.spawn();
			event.setCancelled(true);
		} else if (split[0].equals("/abillist")) {
			if (split.length < 2) {
				MineQuest.getQuester(player).listAbil(MineQuest.getQuester(player));
			} else {
				MineQuest.getQuester(player).getClass(split[1]);
			}
			event.setCancelled(true);
		} else if (split[0].equals("/unbind")) {
			MineQuest.getQuester(player).unBind(player.getItemInHand());
			event.setCancelled(true);
		} else if (split[0].equals("/enableabil")) {
			if (split.length < 2) return;
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			MineQuest.getQuester(player).enableabil(abil);
			event.setCancelled(true);
		} else if (split[0].equals("/disableabil")) {
			if (split.length < 2) return;
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			MineQuest.getQuester(player).disableabil(abil);
			event.setCancelled(true);
		} else if (split[0].equals("/noquest")) {
			MineQuest.getQuester(player).disable();
			event.setCancelled(true);
		} else if (split[0].equals("/bind")) {
			if (split.length < 3) {
				player.sendMessage("Usage: /bind <ability> <l or r>");
				event.setCancelled(true);
			}
			String abil = split[1];
			int i;
			for (i = 2; i < split.length - 1; i++) abil = abil + " " + split[i];
			MineQuest.getQuester(player).bind(player, abil, split[split.length - 1]);
			event.setCancelled(true);
		} else if (split[0].equals("/class")) {
			if (split.length < 2) {
				player.sendMessage("Usage: /class <class_name>");
				event.setCancelled(true);
			}
			MineQuest.getQuester(player).getClass(split[1]).display(player);
			event.setCancelled(true);
		} else if (split[0].equals("/health")) {
			player.sendMessage("Your health is " + MineQuest.getQuester(player).getHealth() + "/" + MineQuest.getQuester(player).getMaxHealth());
			event.setCancelled(true);
		} else if (split[0].equals("/spellcomp")) {
			if (split.length < 2) {
				return;
			}
			String abil = split[1];
			int i;
			for (i = 2; i < split.length; i++) abil = abil + " " + split[i];
			//player.sendMessage(listSpellComps(abil));
			event.setCancelled(true);
		} else if (split[0].equals("/store")) {
    		int page;
    		if (split.length > 1) {
    			page = Integer.parseInt(split[1]);
    		} else {
    			page = 0;
    		}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.displayPage(MineQuest.getQuester(player), page);
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        } else if (split[0].equals("/buy")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.buy(MineQuest.getQuester(player), split[1], Integer.parseInt(split[2]));
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/buyi")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.buy(MineQuest.getQuester(player), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/costb")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.cost(MineQuest.getQuester(player), split[1], Integer.parseInt(split[2]), true);
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/costs")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.cost(MineQuest.getQuester(player), split[1], Integer.parseInt(split[2]), false);
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/sell")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.sell(MineQuest.getQuester(player), split[1], Integer.parseInt(split[2]));
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/selli")) {
        	if (split.length < 3) {
        		return;
        	}
    		Town town = MineQuest.getTown(player);
    		if (town != null) {
    			Store store = town.getStore(player);
    			
    			if (store != null) {
    				store.sell(MineQuest.getQuester(player), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    			} else {
    				player.sendMessage("You are not in a store");
    			}
    		} else {
    			player.sendMessage("You are not in a town - stores are only found in towns");
    		}
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/cubes")) {
        	String cubes_string;
        	double cubes = MineQuest.getQuester(player).getCubes();
        	
	    	if (cubes > 1000000) {
	    		cubes_string = ((double)cubes / 1000000.0) + "MC";
	    	} else if (cubes > 1000) {
	    		cubes_string = ((double)cubes / 1000.0) + "KC";
	    	} else {
	    		cubes_string = cubes + "C";
	    	}
			player.sendMessage("You have " + cubes_string);
			event.setCancelled(true);
        	return;
        } else if (split[0].equals("/updatedb")) {
        	player.sendMessage("This is not imlpemented yet");
			event.setCancelled(true);
			return;
        } else if (split[0].equals("/cubonomy")) {
        	if ((split.length == 2) && (Integer.parseInt(split[1]) == 2)) {
				player.sendMessage("Available Commands (2 of 2):");
				player.sendMessage("    /costb <store_index or nam> <amount>");
				player.sendMessage("    /costs <store_index or nam> <amount>");
				player.sendMessage("    /cubes");
				player.sendMessage("    /location");
				player.sendMessage("In case of crash try /updatedb");
        	} else {
				player.sendMessage("Available Commands (1 of 2):");
				player.sendMessage("    /store <page_num>");
				player.sendMessage("    /register <drupal_name>");
				player.sendMessage("    /buy <store_index or nam> <amount>");
				player.sendMessage("    /sell <store_index or name> <amount>");
				player.sendMessage("    /buyi <item_id> <amount>");
				player.sendMessage("    /selli <item_id> <amount>");
				player.sendMessage("for more type: /cubonomy 2");
        	}
			event.setCancelled(true);
			return;
        } else if (split[0].equals("/saveall")) {
			player.sendMessage("Saving All Chunks...");
			//useConsoleCommand("save-all");
			player.sendMessage("Save Complete");
        }
		
		
		
		super.onPlayerCommand(event);
	}
}
