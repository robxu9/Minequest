

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Town {
	private int center_x, center_z;
	private List<Store> stores;
	private List<Property> properties;
	private Property town;
	private String name;
	private Location spawn;
	private mysql_interface sql_server;
	
	public Town(String name, mysql_interface sql) {
		this.sql_server = sql;
		
		ResultSet results = sql_server.query("SELECT * from towns WHERE name='" + name + "'");
		
		try {
			if (results.next()) {
				int height = results.getInt("height");
				Location start = new Location(null, (double)results.getInt("x"), 
						(double)results.getInt("y"), (double)results.getInt("z"));
				Location end = new Location(null, (double)results.getInt("x"), 
						(double)results.getInt("y") + height, (double)results.getInt("z"));
				Quester owner = MineQuest.getQuester(results.getString("owner"));
				
				town = new Property(owner, start, end, height > 0);
				center_x = town.getCenterX();
				center_z = town.getCenterZ();
				spawn = new Location(owner.getPlayer().getLocation().getWorld(), 
						(double)results.getInt("spawn_x"), 
						(double)results.getInt("spawn_y"), 
						(double)results.getInt("spawn_z"));
			}
		} catch (SQLException e) {
			MineQuest.log("Error: could not initialize town " + name);
		}
		
		properties = new ArrayList<Property>();
		stores = new ArrayList<Store>();
		
		results = sql_server.query("SELECT * FROM " + name);
		try {
			while (results.next()) {
				int height = results.getInt("height");
				Location start = new Location(null, (double)results.getInt("x"), (double)results.getInt("y"), (double)results.getInt("z"));
				Location end = new Location(null, (double)results.getInt("x"), (double)results.getInt("y") + height, (double)results.getInt("z"));
				if (results.getBoolean("store_prop")) {
					String store = results.getString("name");
					
					stores.add(new Store(store, start, end, sql_server));
				} else {
					Quester owner = MineQuest.getQuester(results.getString("owner"));
					
					properties.add(new Property(owner, start, end, height > 0));
				}
			}
		} catch (SQLException e) {
			MineQuest.log("Error: could not initialize properties of town " + name);
		}
		
		for (Store s : stores) {
			s.queryData();
		}
	}

	public boolean inTown(Location loc) {
		return town.inProperty(loc);
	}

	public boolean inTown(Player player) {
		return inTown(player.getLocation());
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
	
	public void checkMobs() {
		//TODO: write checkMobs();
	}
	
	public int[] getCenter() {
		int center[] = new int[2];
		
		center[0] = center_x;
		center[1] = center_z;
		
		return center;
	}
	
	public Property getTownProperty() {
		return town;
	}
	
	public String getName() {
		return name;
	}
	
	public double calcDistance(Location loc) {
		return MineQuest.distance(loc, town.getLocation());
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

	public double calcDistance(Player player) {
		return calcDistance(player.getLocation());
	}

	public Location getLocation() {
		return spawn;
	}
}
