package org.monk.MineQuest.World;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Store.Store;

public class Town {
	private int center_x, center_z;
	private String name;
	private String person;
	private List<Property> properties;
	private Location spawn;
	private Location start;
	private List<Store> stores;
	private Property town;
	
	public Town(String name, World world) {
		ResultSet results = MineQuest.getSQLServer().query("SELECT * from towns WHERE name='" + name + "'");
		this.name = name;
		
		try {
			if (results.next()) {
				int height = results.getInt("height");
				Location start = new Location(world, (double)results.getInt("x"), 
						(double)results.getInt("y"), (double)results.getInt("z"));
				Location end = new Location(world, (double)results.getInt("max_x"), 
						(double)results.getInt("y") + height, (double)results.getInt("max_z"));
				Quester owner = MineQuest.getQuester(results.getString("owner"));
				
				town = new Property(owner, start, end, height > 0, 0);
				center_x = town.getCenterX();
				center_z = town.getCenterZ();
				spawn = new Location(world, 
						(double)results.getInt("spawn_x"), 
						(double)results.getInt("spawn_y"), 
						(double)results.getInt("spawn_z"));
			}
		} catch (SQLException e) {
			MineQuest.log("Error: could not initialize town " + name);
		}
		
		properties = new ArrayList<Property>();
		stores = new ArrayList<Store>();
		
		results = MineQuest.getSQLServer().query("SELECT * FROM " + name);
		try {
			while (results.next()) {
				int height = results.getInt("height");
				Location start = new Location(world, (double)results.getInt("x"), (double)results.getInt("y"), (double)results.getInt("z"));
				Location end = new Location(world, (double)results.getInt("max_x"), (double)results.getInt("y") + height, (double)results.getInt("max_z"));
				if (results.getBoolean("store_prop")) {
					String store = results.getString("name");
					
					stores.add(new Store(store, start, end));
				} else {
					Quester owner = MineQuest.getQuester(results.getString("name"));
					
					properties.add(new Property(owner, start, end, height > 0, results.getLong("price")));
				}
			}
		} catch (SQLException e) {
			MineQuest.log("Error: could not initialize properties of town " + name);
		}
		
		for (Store s : stores) {
			s.queryData();
		}
	}

	public double calcDistance(Location loc) {
		return MineQuest.distance(loc, town.getLocation());
	}

	public double calcDistance(Player player) {
		return calcDistance(player.getLocation());
	}
	
	public void checkMobs() {
		List<LivingEntity> livingEntities = MineQuest.getSServer().getWorld("world").getLivingEntities();
		
		for (LivingEntity livingEntity : livingEntities) {
			if (livingEntity instanceof Monster) {
				checkMob((Monster)livingEntity);
			}
		}
	}
	
	private void checkMob(Monster livingEntity) {
		if (inTown(livingEntity.getLocation())) {
			livingEntity.teleportTo(town.getEdge(livingEntity.getLocation()));
		}
	}

	public void createProperty(Player player) {
		if (town.canEdit(MineQuest.getQuester(player))) {
			person = player.getName();
			start = player.getLocation();
		} else {
			player.sendMessage("You do not have town permissions");
		}
	}
	
	public void createStore(Player player) {
		if (town.canEdit(MineQuest.getQuester(player))) {
			person = player.getName();
			start = player.getLocation();
		} else {
			player.sendMessage("You do not have town permissions");
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return name.equals(obj);
		}
		if (obj instanceof Town) {
			return name.equals(((Town)obj).getName());
		}
		return super.equals(obj);
	}
	
	public void finishProperty(Player player, boolean b) {
		if (town.canEdit(MineQuest.getQuester(player))) {
			Location end = player.getLocation();
			int x, y, z, max_x, max_z, height;
			if (end.getX() > start.getX()) {
				x = (int)start.getX();
				max_x = (int)end.getX();
			} else {
				x = (int)end.getX();
				max_x = (int)start.getX();
			}
			if (end.getZ() > start.getZ()) {
				z = (int)start.getZ();
				max_z = (int)end.getZ();
			} else {
				z = (int)end.getZ();
				max_z = (int)start.getZ();
			}
			if (end.getY() < start.getY()) {
				y = (int)end.getY();
				height = (int)(start.getY() - end.getY());
			} else {
				y = (int)start.getY();
				height = (int)(end.getY() - start.getY());;
			}
			if (!b) {
				height = 0;
			}

			Location start = new Location(player.getWorld(), (double)x, (double)y, (double)z);
			Location ends = new Location(player.getWorld(), max_x, (double)y + height, max_z);
			properties.add(new Property(null, start, ends, height > 0, 10000000));
			MineQuest.getSQLServer().update("INSERT INTO " + name + 
					" (name, x, y, z, max_x, max_z, height, store_prop, price) VALUES('null', '" + x + "', '" + y + "', '" + z
					+ "', '" + max_x + "', '" + max_z + "', '" + height
					+ "', '0', '10000000')");
		} else {
			player.sendMessage("You do not have town permissions");
		}
	}
	
	public void finishStore(Player player, String name) {
		if (town.canEdit(MineQuest.getQuester(player))) {
			Location end = player.getLocation();
			int x, y, z, max_x, max_z, height;
			if (end.getX() > start.getX()) {
				x = (int)start.getX();
				max_x = (int)end.getX();
			} else {
				x = (int)end.getX();
				max_x = (int)start.getX();
			}
			if (end.getZ() > start.getZ()) {
				z = (int)start.getZ();
				max_z = (int)end.getZ();
			} else {
				z = (int)end.getZ();
				max_z = (int)start.getZ();
			}
			if (end.getY() < start.getY()) {
				y = (int)end.getY();
				height = (int)(start.getY() - end.getY());
			} else {
				y = (int)start.getY();
				height = (int)(end.getY() - start.getY());;
			}
			MineQuest.getSQLServer().update("INSERT INTO " + this.name + 
					" (name, x, y, z, max_x, max_z, height, store_prop, price) VALUES('"
					+ name + "', '" + x + "', '" + y + "', '" + z
					+ "', '" + max_x + "', '" + max_z + "', '" + height
					+ "', '1', '10000000')");
			MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + " (item_id INT, price DOUBLE, quantity INT, type VARCHAR(30))");
			stores.add(new Store(name, this.name));
			stores.get(stores.size() - 1).queryData();
		} else {
			player.sendMessage("You do not have town permissions");
		}
	}
	
	public int[] getCenter() {
		int center[] = new int[2];
		
		center[0] = center_x;
		center[1] = center_z;
		
		return center;
	}
	
	public Location getLocation() {
		return spawn;
	}
	
	public String getName() {
		return name;
	}

	public Property getProperty(Location loc) {
		int i;
		
		for (i = 0; i < properties.size(); i++) {
			if (properties.get(i).inProperty(loc)) {
				return properties.get(i);
			}
		}
		
		return null;
	}

	public Property getProperty(Player player) {
		return getProperty(player.getLocation());
	}

	public Store getStore(Location loc) {
		int i;
		
		for (i = 0; i < stores.size(); i++) {
			if (stores.get(i).inStore(loc)) {
				return stores.get(i);
			}
		}
		
		return null;
	}
	
	public Store getStore(Player player) {
		return getStore(player.getLocation());
	}

	public Property getTownProperty() {
		return town;
	}

	public boolean inTown(Location loc) {
		return town.inProperty(loc);
	}

	public boolean inTown(Player player) {
		return inTown(player.getLocation());
	}

	public void setOwner(String string) {
		if (MineQuest.getQuester(string) != null) {
			MineQuest.getSQLServer().update("UPDATE town SET owner='" + string + "' WHERE name='" + name + "'");
		}
	}

	public void setPrice(Player player, long price) {
		Property prop = getProperty(player);
		if (prop != null) {
			getProperty(player).setPrice(price);
			MineQuest.getSQLServer().update("UPDATE " + name + " SET price='" + price + "' WHERE x='" + 
					prop.getX() + "' AND y='" + prop.getY() + "' AND z='" + prop.getZ() + "'");
		}
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		MineQuest.getSQLServer().update("UPDATE towns SET spawn_x='" + (int)spawn.getX() + "', spawn_y='" + 
				(int)spawn.getY() + "', spawn_z='" + (int)spawn.getZ() + "' WHERE name='" + name + "'");
	}

	public Location getSpawn() {
		return spawn;
	}

	public void buy(Quester quester, Property prop) {
		quester.setCubes(quester.getCubes() - prop.getPrice());
		
		prop.setOwner(quester);
		
		MineQuest.getSQLServer().update("UPDATE " + name + " SET name='" + quester.getName() 
				+ "' WHERE x='" + prop.getX() + "' AND z='" + prop.getZ() + "' AND y='" + prop.getY() + "'");
	}
}
