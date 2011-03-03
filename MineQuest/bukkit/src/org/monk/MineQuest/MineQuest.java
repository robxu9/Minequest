package org.monk.MineQuest;


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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.monk.MineQuest.Event.CheckMobEvent;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Listener.MineQuestBlockListener;
import org.monk.MineQuest.Listener.MineQuestEntityListener;
import org.monk.MineQuest.Listener.MineQuestPlayerListener;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.World.Town;

/**
 * This is the main class of MineQuest. It holds static lists of players in the server,
 * Towns in the server, Properties owned by players, and in the future will hold lists
 * of quest status information. It has public static methods to access all of these
 * including the bukkit server.
 * 
 * @author jmonk
 *
 */
public class MineQuest extends JavaPlugin {
	private static EventParser eventParser;
//	private MineQuestServerListener sl;
//	private MineQuestVehicleListener vl;
//	private MineQuestWorldListener wl;
	static private Logger log;
	private static String namer;
	static private List<Quester> questers = new ArrayList<Quester>();
	private static Server server;
	static private MysqlInterface sql_server;
	private static Location start;
	static private List<Town> towns = new ArrayList<Town>();
	/**
	 * Adds a Quester to the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param quester Quester to be added
	 */
	static public void addQuester(Quester quester) {
		questers.add(quester);
	}
	/**
	 * Adds a town to the MineQuest Server. 
	 * Does not modify mysql database.
	 * 
	 * @param town Town to be added
	 */
	static public void addTown(Town town) {
		towns.add(town);
	}
	/**
	 * Starts the creation of town based on Player
	 * Location.
	 * 
	 * @param player Player Creating the Town
	 */
	public static void createTown(Player player) {
		if (!isMayor(getQuester(player))) {
			player.sendMessage("Only mayors are allowed to create towns");
		} else {
			start = player.getLocation();
			namer = player.getName();
		}
	}
	

	
	
    /**
     * This is a utility for various parts of MineQuest to calculate
     * the distance between two locations.
     * 
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return Distance between first and second locations
     */
	static public double distance(Location loc1, Location loc2) {
		double x, y, z;
		
		x = loc1.getX() - loc2.getX();
		y = loc1.getY() - loc2.getY();
		z = loc1.getZ() - loc2.getZ();
		
		return Math.sqrt(x*x + y*y + z*z);
	}
    
    /**
	 * Finishes creation of town based on Player
	 * Location.
	 * 
	 * @param player Player Creating Town
	 * @param name Name of Town
	 */
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
				towns.add(new Town(name, getSServer().getWorld("world")));
			} else {
				player.sendMessage(namer + " is in the process of creating a town - use /createtown to start a new creation");
			}
		}
	}
    
    /**
	 * Gets the difficulty adjustement of the MineQuest Server.
	 * As the level of players goes up the natural encounter
	 * of monsters gets harder to compensate.
	 * 
	 * @return Adjustment Factor to be used
	 */
	public static int getAdjustment() {
		// TODO Auto-generated method stub
		return 0;
	}
	
    /**
     * Gets the EventParser being used by MineQuest.
     * 
     * @return EventParser
     */
    static public EventParser getEventParser() {
    	return eventParser;
    }
	
	/**
	 * Returns whatever town has the closest spawn point to
	 * the Location.
	 * 
	 * @param to Location
	 * @return Closest Town
	 */
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
	
	/**
	 * Gets a Quester of a specific Player
	 * 
	 * @param player Player that is a Quester
	 * @return Quester or NULL if none found
	 */
	static public Quester getQuester(Player player) {
		return getQuester(player.getName());
	}
	
	/**
	 * Gets a Quester with a specific player name
	 * 
	 * @param name Name of Quester
	 * @return Quester with Name name or NULL
	 */
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
	
	/**
	 * Returns lists of Questers within server.
	 * 
	 * @return List of Questers
	 */
	static public List<Quester> getQuesters() {
		return questers;
	}

	/**
	 * Gets an interface to the mysql server being used by
	 * MineQuest.
	 * 
	 * @return mysql_interface of MineQuest DB
	 */
	public static MysqlInterface getSQLServer() {
		return sql_server;
	}
	
	/**
	 * Returns the Bukkit Server.
	 * 
	 * @return Bukkit Server
	 */
	public static Server getSServer() {
		return server;
	}
	 
	/**
	 * Gets a town that a specific location is within.
	 * 
	 * @param loc Location within town.
	 * @return Town that location is in or NULL is none exists
	 */
	static public Town getTown(Location loc) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).inTown(loc)) {
				return towns.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a town that a specific player is within.
	 * 
	 * @param player Player within town
	 * @return Town that player is in or NULL if none exists
	 */
	static public Town getTown(Player player) {
		return getTown(player.getLocation());
	}
	
	/**
	 * Gets a town based on name of the town.
	 * 
	 * @param name Name of the town
	 * @return Town with Name name or NULL is none exists
	 */
	static public Town getTown(String name) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).equals(name)) {
				return towns.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the list of towns in the server.
	 * 
	 * @return List of towns
	 */
	static public List<Town> getTowns() {
		return towns;
	}
	
	/**
	 * Determines if all three axis of loc have higher value
	 * than loc2.
	 * 
	 * @param loc Larger Location
	 * @param loc2 Smaller Location
	 * @return Boolean true if loc is greater
	 */
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
	
	/**
	 * Determines if a Quester is a Mayor of any town.
	 * Used to determine permissions for creation of towns.
	 * 
	 * @param quester Quester to Test if Mayor
	 * @return Boolean true if Quester is a Mayor
	 */
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
	
	/**
	 * Gets a string containing the spell components for a given ability.
	 * 
	 * @param string Ability Name
	 * @return String of Components
	 */
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

	/**
	 * Prints to screen the message preceded by [MineQuest].
	 * 
	 * @param string Message to Print
	 */
	public static void log(String string) {
		//log.info("[MineQuest] " + string);
		System.out.println("[MineQuest] " + string);
	}

	/**
	 * Removes a Quester from the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param quester Quester to be removed
	 */
	static public void remQuester(Quester quester) {
		questers.remove(quester);
	}

	/**
	 * Removes a Quester from the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param name Name of Quester to be removed
	 */
	static public void remQuester(String name) {
		questers.remove(getQuester(name));
	}

	/**
	 * Removes a Town from the MineQuester Server.
	 * Does not modify mysql database.
	 * 
	 * @param name Name of Town to remove
	 */
	static public void remTown(String name) {
		towns.remove(getTown(name));
	}

	/**
	 * Removes a Town from the MineQuester Server.
	 * Does not modify mysql database.
	 * 
	 * @param town Town to remove
	 */
	static public void remTown(Town town) {
		towns.remove(town);
	}

	private MineQuestBlockListener bl;

	private MineQuestEntityListener el;

	private MineQuestPlayerListener pl;

	public MineQuest() {
	}

	/**
	 * Sets up an instance of MineQuest. There should never be more than
	 * one instance of MineQuest required. If enabled this method will load all of the
	 * static variables with required information and creating a second instance
	 * will reset all of those, and eventually create inconsistancies in the server.
	 * 
	 * This loads all adjustable parameters from minequest.properties, including
	 * database location and login parameters.
	 */
    protected void setEnabled(boolean enabled) {
    	// TODO Auto-generated method stub
    	super.setEnabled(enabled);
    	if (enabled) {
	        
	        eventParser = new EventParser();
	        eventParser.enable();
	        
	        if (getServer().getScheduler().scheduleSyncRepeatingTask(this, eventParser, 1, 1) == -1) {
	        	MineQuest.log("There was a problem setting up EventParser!");
	        }
	        
			List<String> names = new ArrayList<String>();
			String url, port, db, user, pass;
			PropertiesFile minequest = new PropertiesFile("minequest.properties");
			url = minequest.getString("url", "localhost");
			port = minequest.getString("port", "3306");
			db = minequest.getString("db", "minequest_new");
			user = minequest.getString("user", "root");
			pass = minequest.getString("pass", "1234");
			sql_server = new MysqlInterface(url, port, db, user, pass, minequest.getInt("silent", 0));
	
			ResultSet results = sql_server.query("SELECT * FROM questers");
			
			try {
				while (results.next()) {
					names.add(results.getString("name"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (String name : names) {
				questers.add(new Quester(name));
			}
			
			names.clear();
			results = sql_server.query("SELECT * FROM towns");
			
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
				towns.add(new Town(name, getServer().getWorld("world")));
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
	        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, pl, Priority.Normal, this);
	        pm.registerEvent(Event.Type.PLAYER_MOVE, pl, Priority.Normal, this);
	        pm.registerEvent(Event.Type.PLAYER_TELEPORT, pl, Priority.Normal, this);
	        pm.registerEvent(Event.Type.ENTITY_COMBUST, el, Priority.Normal, this);
	        pm.registerEvent(Event.Type.ENTITY_DAMAGED, el, Priority.Normal, this);
	        pm.registerEvent(Event.Type.BLOCK_DAMAGED, bl, Priority.Normal, this);
	        pm.registerEvent(Event.Type.BLOCK_PLACED, bl, Priority.Normal, this);
	        pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, bl, Priority.Normal, this);
	        
	        PluginDescriptionFile pdfFile = this.getDescription();
	        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	        start = null;
			
			for (Town town : towns) {
				eventParser.addEvent(new CheckMobEvent(town));
			}
    	}
    }
	@Override
	public void onDisable() {
	}
	@Override
	public void onEnable() {
	}
}
