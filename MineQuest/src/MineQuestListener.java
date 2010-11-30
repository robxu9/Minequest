import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;


public class MineQuestListener extends PluginListener {
    private Logger log;
    private Quester questers[];
	private mysql_interface sql_server;
	private PropertiesFile prop;
	
	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/char")) {
			Quester quester = lookupQuester(player.getName());
			player.sendMessage("You are level " + quester.getLevel() + " with " + quester.getExp() + "/" + (100 * (quester.getLevel() + 1)) + " Exp");
			
			return true;
		}
		return false;
	}
	
	public void onLogin(Player player) {
		Quester new_questers[];
		int i;
		
		
		if (lookupQuester(player.getName()) == null) {
			
			new_questers = new Quester[questers.length + 1];
			for (i = 0; i < questers.length; i++) {
				new_questers[i] = questers[i];
			}
			new_questers[questers.length] = new Quester(player.getName(), 0, 0, sql_server);
			new_questers[questers.length].create();
			questers = new_questers;
		}
	}
    
	public boolean onDamage(BaseEntity attacker, BaseEntity defender) {
		int attack = 1;
		int i;
		int mobid = 0;
		List<Mob> mob_list = etc.getServer().getMobList();
		
		if (attacker.getPlayer() != null) {
			if (defender.isMob()) {
				mobid = defender.getId();
			}
		}
		
		if (attack == 1) {
			for (i = 0; i < mob_list.size(); i++) {
				if (mob_list.get(i).getId() == mobid) {
					Player player = attacker.getPlayer();
					lookupQuester(player.getName()).damage(mob_list.get(i).getName());
				}
			}
		}
		
		return true;
	}


	private Quester lookupQuester(String name) {
		int i;
		for (i = 0; i < questers.length; i++) {
			if (questers[i].getName().equals(name)) {
				return questers[i];
			}
		}
		return null;
	}


	public void setup() {
		String url, port, db, user, pass;
		
        log = Logger.getLogger("Minecraft");

		prop = new PropertiesFile("minequest.properties");
		url = prop.getString("url", "localhost");
		port = prop.getString("port", "3306");
		db = prop.getString("db", "cubonomy");
		user = prop.getString("user", "root");
		pass = prop.getString("pass", "root");
		sql_server = new mysql_interface();
		sql_server.setup(url, port, db, user, pass);
		
		getQuesters();
	}
	
	public void getQuesters() {
		int num, i;
		ResultSet results;
		try {
			results = sql_server.query("SELECT * FROM questers");
			
			results.last();
			num = results.getRow();
			results.first();
			
			questers = new Quester[num];
			for (i = 0; i < num; i++) {
				questers[i] = new Quester(results.getString("name"), results.getInt("level"), results.getInt("exp"), sql_server);
				results.next();
			}
		} catch (SQLException e) {
			System.out.println("Failed to get questers - things are not going to work");
			e.printStackTrace();
		}
	}

	
	private class Quester {
		private String name;
		private int level;
		private int exp;
		private mysql_interface sql_interface;
		
		public Quester(String name, int level, int exp, mysql_interface sql) {
			this.name = name;
			this.level = level;
			this.exp = exp;
			sql_interface = sql;
		}
		
		public int getExp() {
			return exp;
		}

		public void create() {
			try {
				sql_server.update("INSERT INTO questers (name, level, exp) VALUE('" + name + "', '" + level + "', '" + exp + "')");
			} catch (SQLException e) {
				System.out.println("Unable to create player " + name);
				e.printStackTrace();
			}			
		}
		
		public void damage(String name) {
			exp += expLookup(name);
			if (exp >= (100 * level)) {
				exp = 0;
				level++;
				etc.getServer().getPlayer(name).sendMessage("Level Up - " + level);
				save();
			}
		}
		
		public int expLookup(String name) {
			return 2;
		}
		
		public String getName() {
			return name;
		}
		
		public void save() {
			try {
				sql_server.update("UPDATE questers SET level='" + level + "', exp='" + exp + "' WHERE name='" + name + "'");
			} catch (SQLException e) {
				System.out.println("Save failed for " + name);
				e.printStackTrace();
			}
		}
		
		public int getLevel() {
			return level;
		}
	}
	
	private class mysql_interface {
		private Statement stmt;
		String url;
		java.sql.Connection con;
	    private Logger log;
		
		
		public void setup(String location, String port, String db, String user, String pass) {
			url = "jdbc:mysql://" + location + ":" + port + "/" + db;
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("You appear to be missing MySQL JDBC");
				e.printStackTrace();
				return;
			}
			try {
				con = (Connection) DriverManager.getConnection(url, user, pass);// + "?autoReconnect=true&user=" + user + "&password=" + pass);
			} catch (SQLException e) {
				System.out.println("Unable to Connect to MySQL Databse");
				e.printStackTrace();
				return;
			}
			
			 try {
				stmt = (Statement) con.createStatement();
			} catch (SQLException e) {
				System.out.println("Failed to setup MySQL Statement");
				e.printStackTrace();
			}
	        log = Logger.getLogger("Minecraft");
		}
		
		public ResultSet query(String the_query) throws SQLException {
			log.info("[MineQuest] (MySQL) " + the_query);
			return stmt.executeQuery(the_query);
		}
		
		public int update(String sql) throws SQLException {
			log.info("[MineQuest] (MySQL) " + sql);
			return stmt.executeUpdate(sql);
		}
	}
}
