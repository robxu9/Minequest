

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Store {
	private String name;
	private List<StoreBlock> blocks;
	private int num_page;
	private mysql_interface sql_server;
	private double radius;
	private Logger log;
	private Location start;
	private Location end;
	
	public Store(String store_name, Location start, Location end, mysql_interface mysql) {
    	sql_server = mysql;
        log = Logger.getLogger("Minecraft");
		name = store_name;
		
		this.start = start;
		this.end = end;
	}
	
	public void queryData() {
		ResultSet results;
		
		blocks = new ArrayList<StoreBlock>();
		results = sql_server.query("SELECT * FROM " + name + " ORDER BY type");
		try {
			while (results.next()) {
				try {
					blocks.add(new StoreBlock(sql_server, this, results.getString("type"), results.getInt("quantity"), results.getInt("price"), results.getInt("item_id")));
				} catch (SQLException e) {
					log.info("[MineQuest] Unable to query data for block");
					e.printStackTrace();
					return;
				}
			}
		} catch (SQLException e) {
			log.info("[MineQuest] Unable to query data for store");
			e.printStackTrace();
		}
		
		num_page = blocks.size() / 6;
		if ((blocks.size() % 6) > 0) {
			num_page++;
		}
	}
	
	public boolean inStore(Location loc) {
		if (MineQuest.greaterLoc(loc, start) && MineQuest.greaterLoc(end, loc)) {
			return true;
		}
		return false;
		//return MineQuest.distance(loc, location) < radius;
	}
	
	public boolean inStore(Player player) {
		return inStore(player.getLocation());
	}
	
	public void cost(Quester quester, String name, int quantity, boolean buy) {
		if (cost(quester, getBlock(name), quantity, buy)) {
			quester.getPlayer().sendMessage(name + " is not a valid block type for this store - Contact Admin to have it added");
		}
	}
	
	public void cost(Quester quester, int item_id, int quantity, boolean buy) {
		if (cost(quester, getBlock(item_id), quantity, buy)) {
			quester.getPlayer().sendMessage(name + " is not a valid block type for this store - Contact Admin to have it added");
		}
	}
	
	public boolean cost(Quester quester, StoreBlock block, int quantity, boolean buy) {
		if (block != null) {
			block.cost(quester, quantity, buy);
			return false;
		}
		
		return true;
	}
	
	public void sell(Quester quester, String name, int quantity) {
		if (sell(quester, getBlock(name), quantity)) {
			quester.getPlayer().sendMessage(name + " is not a valid block type for this store - Contact Admin to have it added");
		}
	}
	
	public void sell(Quester quester, int item_id, int quantity) {
		if (sell(quester, getBlock(item_id), quantity)) {
			quester.getPlayer().sendMessage(item_id + " is not a valid block id for this store - Contact Admin to have it added");
		}
	}
	
	public boolean sell(Quester quester, StoreBlock block, int quantity) {
		if (block != null) {
			block.sell(quester, quantity);
			return false;
		}
		
		return true;
	}
	
	public void buy(Quester quester, String name, int quantity) {
		if (buy(quester, getBlock(name), quantity)) {
			quester.getPlayer().sendMessage(name + " is not a valid block type for this store - Contact Admin to have it added");
		}
	}
	
	public void buy(Quester quester, int item_id, int quantity) {
		if (buy(quester, getBlock(item_id), quantity)) {
			quester.getPlayer().sendMessage(item_id + " is not a valid block id for this store - Contact Admin to have it added");
		}
	}
	
	public boolean buy(Quester quester, StoreBlock block, int quantity) {
		if (block != null) {
			block.buy(quester, quantity);
			return false;
		} else {
			return true;
		}
	}
	
	private StoreBlock getBlock(int item_id) {
		int i;
		
		for (i = 0; i < blocks.size(); i++) {
			if (blocks.get(i).getId() == item_id) {
				return blocks.get(i);
			}
		}
		
		return null;
	}
	
	private StoreBlock getBlock(String name) {
		int i;
		
		for (i = 0; i < blocks.size(); i++) {
			if (blocks.get(i).equals(name)) {
				return blocks.get(i);
			}
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return name.equals(obj);
		}
		if (obj instanceof Store) {
			return name.equals(((Store)obj).getName());
		}
		
		return super.equals(obj);
	}
	
	public void displayPage(Quester quester, int page) {
		Player player = quester.getPlayer();
		int i;
		
		if (page > num_page) {
			player.sendMessage("This store only has " + num_page + " pages");
			return;
		}
		if (page <= 0) {
			page = 1;
		}
		
		String cubes_string;
		if (quester.getCubes() > 1000000) {
			cubes_string = (((int)((double)quester.getCubes()) / 1000.0)/1000) + "M";
		} else if (quester.getCubes() > 1000) {
			cubes_string = ((double)quester.getCubes() / 1000.0) + "K";
		} else {
			cubes_string = quester.getCubes() + "";
		}
		player.sendMessage(name + ": page " + (page) + " of " + num_page + " - You have " + cubes_string + "C");
		player.sendMessage("     Type - Price - Quantity");
		
    	for (i = 6 * (page - 1); (i < (6 * (page))) && (i < blocks.size()); i++) {
    		blocks.get(i).display(player, i);
    	}
	}

}
