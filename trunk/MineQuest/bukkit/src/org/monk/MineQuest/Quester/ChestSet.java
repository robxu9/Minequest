/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.monk.MineQuest.Quester;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.World.Town;



public class ChestSet {
	private boolean add;
	private Quester quester;
	private List<Location> chests;
	private int selected;
	
	public ChestSet(Quester quester, String select, Town last) {
		this.quester = quester;
		add = false;
		selected = -1;
		chests = new ArrayList<Location>();
		
		ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM " + quester.getName() + "_chests");
		try {
			while (results.next()) {
				chests.add(new Location(quester.getPlayer().getWorld(), results.getInt("x"), results.getInt("y"), results.getInt("z")));
				if (results.getString("town").equals(select)) {
					selected = chests.size() - 1;
				}
			}
		} catch (SQLException e) {
			System.out.println("[TownSpawn] [ChestSet] Error: reading query");
			e.printStackTrace();
		}
	}

	
	public ChestSet(Quester quester) {
		this.quester = quester;
		add = false;
		selected = -1;
		chests = new ArrayList<Location>();

		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + quester.getName() + "_chests (town VARCHAR(30), x INT, y INT, z INT)");
	}
	

	public String getName() {
		return quester.getName();
	}

	public void clicked(Player player, Block chest) {
		if (add) {
			Town town = MineQuest.getTown(chest.getLocation());
			
			if (town != null) {
				int i;
				
				for (i = 0; i < chests.size(); i++) {
					if (town.inTown(chests.get(i))) {
						player.sendMessage("You already have a stash in " + town.getName());
						add = false;
						
						return;
					}
				}
				
				chests.add(chest.getLocation());
				MineQuest.getSQLServer().update("INSERT INTO " + quester.getName() + "_chests (town, x, y, z) VALUES('" + town.getName() + "', '" + chest.getX() + "', '" 
						+ chest.getY() + "', '" + chest.getZ() + "')");
			} else {
				int i;
				boolean flag = false;
				
				for (i = 0; i < chests.size(); i++) {
					Town this_town = MineQuest.getTown(chests.get(i));
					if (this_town == null) {
						flag = true;
					}
				}
				if (!flag) {
					chests.add(chest.getLocation());
					MineQuest.getSQLServer().update("INSERT INTO " + quester.getName() + "_chests (town, x, y, z) VALUES('" + "none" + "', '" + chest.getX() + "', '" 
							+ chest.getY() + "', '" + chest.getZ() + "')");
				} else {
					player.sendMessage("You already have a stash outside of towns");
				}
			}
		}
		add = false;
		int i;
		
		for (i = 0; i < chests.size(); i++) {
			if ((chest.getX() == chests.get(i).getX()) 
					&& (chest.getY() == chests.get(i).getY()) 
					&& (chest.getZ() == chests.get(i).getZ())) {
				if (selected != i) {
					setSelected(i);
				}
				
				return;
			}
		}
	}

	public void setSelected(int i) {
		System.out.println("Setting selected to " + i + " from " + selected);
		if (selected != -1) {
			moveContents(getChest(chests.get(i)).getInventory(), getChest(chests.get(selected)).getInventory());
		}

		selected = i;
		Town town = MineQuest.getTown(chests.get(i));
		if (((town != null) && (MineQuest.getSQLServer().update("UPDATE questers SET selected_chest='" + town.getName() 
					+ "' WHERE quester.getName()='" + quester.getName() + "'") == -1))) {
			System.out.println("[TownSpawn] [ChestSet] Error: Unable to update selected chest");
			if (selected != -1) {
				System.out.println("[TownSpawn] [ChestSet] Performing chest dump");
				Inventory inven = getChest(chests.get(selected)).getInventory();
				for (i = 0; i < inven.getContents().length; i++) {
					System.out.println("[TownSpawn] [ChestSet] Item " + inven.getItem(i).getTypeId() + " " 
							+ inven.getItem(i).getAmount());
				}
			} else {
				System.out.println("[TownSpawn] [ChestSet] No chest select - no dump available");
			}
		} else if ((town == null) && ((MineQuest.getSQLServer().update("UPDATE players SET selected_chest='" 
				+ "none" + "' WHERE quester.getName()='" + quester.getName() + "'") == -1))) {
			System.out.println("[TownSpawn] [ChestSet] Error: Unable to update selected chest");
			if (selected != -1) {
				System.out.println("[TownSpawn] [ChestSet] Performing chest dump");
				Inventory inven = getChest(chests.get(selected)).getInventory();
				for (i = 0; i < inven.getContents().length; i++) {
					System.out.println("[TownSpawn] [ChestSet] Item " + inven.getItem(i).getTypeId() + " " 
							+ inven.getItem(i).getAmount());
				}
			} else {
				System.out.println("[TownSpawn] [ChestSet] No chest select - no dump available");
			}
		}
	}

	private Chest getChest(Location location) {
		// TODO Auto-generated method stub
		return null;
	}


	private void moveContents(Inventory to, Inventory from) {
		int i;
		
		for (i = 0; i < from.getContents().length; i++) {
			to.getContents()[i] = from.getContents()[i];
		}
		from.clear();
	}

	public void add(Player player) {
		if (!add) {
			player.sendMessage("Adding next chest opened (please use empty chest, may destroy contents)");
			add = true;
		} else {
			player.sendMessage("Already adding a chest...");
		}
		
		return;
	}
	
	public void rem(Player player, Town town) {
		int i;
		
		for (i = 0; i < chests.size(); i++) {
			if ((town == null) && (MineQuest.getTown(chests.get(i)) == null)) {
				if (chests.size() > 1) {
					if (i > 0) {
						setSelected(0);
					} else {
						setSelected(1);
						selected = 0;
					}
				}
				MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + "_chests WHERE town='" + "none" + "'");
				chests.remove(i);
				player.sendMessage("Chest is now longer instance of stash");
				return;
			} else if ((town != null) && (town.inTown(chests.get(i)))) {
				if (chests.size() > 1) {
					if (i > 0) {
						setSelected(0);
					} else {
						setSelected(1);
						selected = 0;
					}
				}
				MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + "_chests WHERE town='" + town.getName() + "'");
				chests.remove(i);
				player.sendMessage("Chest is now longer instance of stash");
				return;
			}
		}
		
		player.sendMessage("You have no stash in this town currently");
		return;
	}

	public void cancelAdd(Player player) {
		if (add) {
			add = false;
			player.sendMessage("Adding cancelled");
		} else {
			player.sendMessage("You were not adding a chest...");
		}
		
		return;
	}

}