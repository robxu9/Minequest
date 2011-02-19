package org.monk.MineQuest.Quester;


import org.bukkit.entity.Player;
import org.monk.MineQuest.World.Town;



public class ChestSet {
	private boolean add;
/*	private String name;
	private List<DoubleChest> chests;
	private int selected;
	private mysql_interface sql_server;
	private Town last;
	
	public ChestSet(mysql_interface server, String name, String select, Town last) {
		this.name = name;
		add = false;
		selected = -1;
		sql_server = server;
		chests = new ArrayList<DoubleChest>();
		
		ResultSet results = sql_server.query("SELECT * FROM " + name);
		try {
			while (results.next()) {
				Chest chest = (Chest) etc.getServer().getOnlyComplexBlock(results.getInt("x"), results.getInt("y"), results.getInt("z"));
				if (chest != null) {
					DoubleChest doublec = chest.findAttachedChest();
					
					if (doublec != null) {
						chests.add(doublec);
						if (results.getString("town").equals(select)) {
							selected = chests.size() - 1;
						}
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("[TownSpawn] [ChestSet] Error: reading query");
			e.printStackTrace();
		}
	}

	
	public ChestSet(mysql_interface server, String name) {
		this.name = name;
		add = false;
		selected = -1;
		sql_server = server;
		chests = new ArrayList<DoubleChest>();
		last = null;

		sql_server.update("INSERT INTO players (name, selected) VALUES('" + name + "', '" + name + "')");
		sql_server.update("CREATE TABLE IF NOT EXISTS " + name + " (town VARCHAR(30), x INT, y INT, z INT)");
	}
	

	public String getName() {
		return name;
	}

	public void clicked(Player player, DoubleChest chest) {
		if (add) {
			Town town = TownSpawnListener.getTown(chest.getBlock().getX(), chest.getBlock().getY(), chest.getBlock().getZ());
			
			if (town != null) {
				int i;
				
				for (i = 0; i < chests.size(); i++) {
					if (town.isWithin(new Location(chests.get(i).getX(), chests.get(i).getY(), chests.get(i).getZ()))) {
						player.sendMessage("You already have a stash in " + town.getName());
						add = false;
						
						return;
					}
				}
				
				chests.add(chest);
				sql_server.update("INSERT INTO " + name + " (town, x, y, z) VALUES('" + town.getName() + "', '" + chest.getX() + "', '" 
						+ chest.getY() + "', '" + chest.getZ() + "')");
			} else {
				int i;
				boolean flag = false;
				DoubleChest this_chest;
				
				for (i = 0; i < chests.size(); i++) {
					this_chest = chests.get(i);
					Town this_town = TownSpawnListener.getTown(this_chest.getBlock().getX(), 
							this_chest.getBlock().getY(), this_chest.getBlock().getZ());
					if (this_town == null) {
						flag = true;
					}
				}
				if (!flag) {
					chests.add(chest);
					sql_server.update("INSERT INTO " + name + " (town, x, y, z) VALUES('" + "none" + "', '" + chest.getX() + "', '" 
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
			moveContents(chests.get(i), chests.get(selected));
		}
		
		Block bl = chests.get(i).getBlock();

		selected = i;
		Town town = TownSpawnListener.getTown(bl.getX(), bl.getY(), bl.getZ());
		if (((town != null) && (sql_server.update("UPDATE players SET selected='" + town.getName() 
					+ "' WHERE name='" + name + "'") == -1))) {
			System.out.println("[TownSpawn] [ChestSet] Error: Unable to update selected chest");
			if (selected != -1) {
				System.out.println("[TownSpawn] [ChestSet] Performing chest dump");
				for (i = 0; i < chests.get(selected).getContentsSize(); i++) {
					System.out.println("[TownSpawn] [ChestSet] Item " + chests.get(selected).getItemFromSlot(i).getItemId() + " " 
							+ chests.get(selected).getItemFromSlot(i).getAmount());
				}
			} else {
				System.out.println("[TownSpawn] [ChestSet] No chest select - no dump available");
			}
		} else if ((town == null) && ((sql_server.update("UPDATE players SET selected='" 
				+ "none" + "' WHERE name='" + name + "'") == -1))) {
			System.out.println("[TownSpawn] [ChestSet] Error: Unable to update selected chest");
			if (selected != -1) {
				System.out.println("[TownSpawn] [ChestSet] Performing chest dump");
				for (i = 0; i < chests.get(selected).getContentsSize(); i++) {
					System.out.println("[TownSpawn] [ChestSet] Item " + chests.get(selected).getItemFromSlot(i).getItemId() + " " 
							+ chests.get(selected).getItemFromSlot(i).getAmount());
				}
			} else {
				System.out.println("[TownSpawn] [ChestSet] No chest select - no dump available");
			}
		}
	}

	private void moveContents(Inventory to, Inventory from) {
		int i;
		
		for (i = 0; i < from.getContentsSize(); i++) {
			to.addItem(from.getItemFromSlot(i));
			from.removeItem(i);
		}
	}*/

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
	/*	int i;
		
		for (i = 0; i < chests.size(); i++) {
			if ((town == null) && (TownSpawnListener.getTown(chests.get(i).getX(), chests.get(i).getY(), chests.get(i).getZ()) == null)) {
				if (chests.size() > 1) {
					if (i > 0) {
						setSelected(0);
					} else {
						setSelected(1);
						selected = 0;
					}
				}
				sql_server.update("DELETE FROM " + name + " WHERE town='" + "none" + "'");
				chests.remove(i);
				player.sendMessage("Chest is now longer instance of stash");
				return;
			} else if ((town != null) && (town.isWithin(new Location(chests.get(i).getX(), chests.get(i).getY(), chests.get(i).getZ())))) {
				if (chests.size() > 1) {
					if (i > 0) {
						setSelected(0);
					} else {
						setSelected(1);
						selected = 0;
					}
				}
				sql_server.update("DELETE FROM " + name + " WHERE town='" + town.getName() + "'");
				chests.remove(i);
				player.sendMessage("Chest is now longer instance of stash");
				return;
			}
		}
		
		player.sendMessage("You have no stash in this town currently");
		return;*/
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