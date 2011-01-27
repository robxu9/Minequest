

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MineQuest extends JavaPlugin {
	static private List<Town> towns = new ArrayList<Town>();
	static private List<Quester> questers = new ArrayList<Quester>();
	static private mysql_interface sql_server;
	static private Logger log;
	private static Server server;
	private static Location start;
	private static String namer;
	private MineQuestBlockListener bl;
	private MineQuestEntityListener el;
	private MineQuestPlayerListener pl;
//	private MineQuestServerListener sl;
//	private MineQuestVehicleListener vl;
//	private MineQuestWorldListener wl;
	

    public MineQuest(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		List<String> names = new ArrayList<String>();
		String url, port, db, user, pass;
		PropertiesFile minequest = new PropertiesFile("minequest.properties");
		url = minequest.getString("url", "localhost");
		port = minequest.getString("port", "3306");
		db = minequest.getString("db", "minequest_new");
		user = minequest.getString("user", "root");
		pass = minequest.getString("pass", "jasonamm");
		sql_server = new mysql_interface(url, port, db, user, pass, minequest.getInt("silent", 0));
		
		ResultSet results = sql_server.query("SELECT * FROM towns");
		
		server = getServer();
		
		try {
			while (results.next()) {
				names.add(results.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String name : names) {
			towns.add(new Town(name, sql_server));
		}

		names.clear();
		results = sql_server.query("SELECT * FROM questers");
		
		try {
			while (results.next()) {
				names.add(results.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String name : names) {
			questers.add(new Quester(name, sql_server));
		}
		
		bl = new MineQuestBlockListener();
		el = new MineQuestEntityListener();
		pl = new MineQuestPlayerListener();
//		sl = new MineQuestServerListener();
//		wl = new MineQuestWorldListener();
//		vl = new MineQuestVehicleListener();
		
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_BLOCK, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_ENTITY, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGEDBY_PROJECTILE, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGED, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, bl, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, bl, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, bl, Priority.Normal, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
        start = null;
	}
	
	static public double distance(Location loc1, Location loc2) {
		double x, y, z;
		
		x = loc1.getX() - loc2.getX();
		y = loc1.getY() - loc2.getY();
		z = loc1.getZ() - loc2.getZ();
		
		return Math.sqrt(x*x + y*y + z*z);
	}

	static public List<Town> getTowns() {
		return towns;
	}
	
	static public Town getTown(Location loc) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).inTown(loc)) {
				return towns.get(i);
			}
		}
		
		return null;
	}
	
	static public Town getTown(Player player) {
		return getTown(player.getLocation());
	}
	
	static public Town getTown(String name) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).equals(name)) {
				return towns.get(i);
			}
		}
		
		return null;
	}

	static public List<Quester> getQuesters() {
		return questers;
	}
	
	static public Quester getQuester(String name) {
		int i;
		
		for (i = 0; i < questers.size(); i++) {
			if (questers.get(i).equals(name)) {
				return questers.get(i);
			}
		}

		log("[WARNING] Cannot find quester " + name);
		return null;
	}
	
	static public Quester getQuester(Player player) {
		return getQuester(player.getName());
	}
	
	static public void addTown(Town town) {
		towns.add(town);
	}
	
	static public void addQuester(Quester quester) {
		questers.add(quester);
	}
	
	static public void remTown(Town town) {
		towns.remove(town);
	}
	
	static public void remTown(String name) {
		towns.remove(getTown(name));
	}
	
	static public void remQuester(Quester quester) {
		questers.remove(quester);
	}
	
	static public void remQuester(String name) {
		questers.remove(getQuester(name));
	}

	public static int getAdjustment() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static mysql_interface getSQLServer() {
		return sql_server;
	}

	public static void log(String string) {
		//log.info("[MineQuest] " + string);
		System.out.println("[MineQuest] " + string);
	}

	public static boolean greaterLoc(Location loc, Location loc2) {
		if (loc.getX() < loc2.getX()) {
			return false;
		}
		if (loc.getY() < loc2.getY()) {
			return false;
		}
		if (loc.getZ() < loc2.getZ()) {
			return false;
		}
		return true;
	}

	public static Town getNearestTown(Location to) {
		if (towns.size() == 0) return null;
		
		Town town = towns.get(0);
		int i;
		
		for (i = 1; i < towns.size(); i++) {
			if (distance(to, town.getLocation()) > distance(to, towns.get(i).getLocation())) {
				town = towns.get(i);
			}
		}
		
		return town;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}

	public static Server getSServer() {
		return server;
	}

	public static String listSpellComps(String string) {
		
		if (string.equalsIgnoreCase("PowerStrike")) {
			return "Wooden Sword";
		} else if (string.equalsIgnoreCase("Dodge")) {
			return "5 Feathers";
		} else if (string.equalsIgnoreCase("Deathblow")) {
			return "2 Steel";
		} else if (string.equalsIgnoreCase("Sprint")) {
			return "Feather";
		} else if (string.equalsIgnoreCase("Fire Arrow")) {
			return "Coal";
		} else if (string.equalsIgnoreCase("Hail of Arrows")) {
			return "10 Arrows";
		} else if (string.equalsIgnoreCase("Repulsion")) {
			return "Torch + Cactus";
		} else if (string.equalsIgnoreCase("Fireball")) {
			return "Coal";
		} else if (string.equalsIgnoreCase("FireChain")) {
			return "5 Coal";
		} else if (string.equalsIgnoreCase("Wall of Fire")) {
			return "7 Dirt + 3 Coal";
		} else if (string.equalsIgnoreCase("Wall of Water")) {
			return "7 Dirt + 2 Water";
		} else if (string.equalsIgnoreCase("IceSphere")) {
			return "5 Snow";
		} else if (string.equalsIgnoreCase("Drain Life")) {
			return "Lightstone";
		} else if (string.equalsIgnoreCase("Fire Resistance")) {
			return "Netherstone";
		} else if (string.equalsIgnoreCase("Trap")) {
			return "6 Dirt + Shovel";
		} else if (string.equalsIgnoreCase("Heal")) {
			return "Water";
		} else if (string.equalsIgnoreCase("Heal Other")) {
			return "Water";
		} else if (string.equalsIgnoreCase("Heal Aura")) {
			return "2 Bread";
		} else if (string.equalsIgnoreCase("Damage Aura")) {
			return "Flint and Steel";
		}
		
		return "Unknown Ability";
	}
	
	public static boolean isMayor(Quester quester) {
		if (quester.equals("jmonk")) {
			return true;
		}
		
		for (Town t : towns) {
			if (t.getTownProperty().getOwner().equals(quester)) {
				return true;
			}
		}
		
		return false;
	}

	public static void createTown(Player player) {
		if (!isMayor(getQuester(player))) {
			player.sendMessage("Only mayors are allowed to create towns");
		} else {
			start = player.getLocation();
			namer = player.getName();
		}
	}

	public static void finishTown(Player player, String name) {
		if (!isMayor(getQuester(player))) {
			player.sendMessage("Only mayors are allowed to create towns");
		} else {
			if (namer.equals(player.getName())) {
				Location end = player.getLocation();
				int x, z, max_x, max_z;
				int spawn_x, spawn_y, spawn_z;
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
				spawn_x = (x + max_x) / 2;
				spawn_y = (int)(start.getY() + end.getY()) / 2;
				spawn_z = (z + max_z) / 2;
				sql_server.update("INSERT INTO towns (name, x, z, max_x, max_z, spawn_x, spawn_y, spawn_z, owner, height, y) VALUES('"
						+ name + "', '" + x + "', '" + z + "', '" + max_x + "', '" + max_z + "', '" + spawn_x + "', '"
						+ spawn_y + "', '" + spawn_z + "', '" + player.getName() + "', '0', '0')");
				sql_server.update("CREATE TABLE IF NOT EXISTS " + name + 
						"(height INT, x INT, y INT, z INT, max_x INT, max_z INT, price INT, name VARCHAR(30), store_prop BOOLEAN)");
				towns.add(new Town(name, sql_server));
			} else {
				player.sendMessage(namer + " is in the process of creating a town - use /createtown to start a new creation");
			}
		}
	}
}
