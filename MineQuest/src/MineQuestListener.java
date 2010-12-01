import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class MineQuestListener extends PluginListener {
    @SuppressWarnings("unused")
	private Logger log;
    private Quester questers[];
	private mysql_interface sql_server;
	private PropertiesFile prop;

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
	
	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/char")) {
			Quester quester = lookupQuester(player.getName());
			player.sendMessage("You are level " + quester.getLevel() + " with " + quester.getExp() + "/" + (100 * (quester.getLevel() + 1)) + " Exp");
			
			return true;
		} else if (split[0].equals("/save")) {
			lookupQuester(player.getName()).save();
			return true;
		} else if (split[0].equals("/load")) {
			lookupQuester(player.getName()).update();
			return true;
		} else if (split[0].equals("/quest")) {
			lookupQuester(player.getName()).enable();
			return true;
		} else if (split[0].equals("/noquest")) {
			lookupQuester(player.getName()).disable();
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
			new_questers[questers.length] = new Quester(player.getName(), 0, sql_server);
			questers = new_questers;
		} else {
			lookupQuester(player.getName()).update();
		}
	}
	
	public void onDisconnect(Player player) {
		lookupQuester(player.getName()).save();
	}
	
	public boolean onBlockDestroy(Player player, Block block) {
		lookupQuester(player.getName()).destroyBlock(player, block);
		return false;
	}
	
	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		if (lookupQuester(player.getName()).isEnabled()) {
			lookupQuester(player.getName()).healthChange(player, oldValue, newValue);
			return true;
		}
		return false;
	}
    
	public boolean onDamage(BaseEntity attacker, BaseEntity defender) {
		int attack = 1;
		int defend = 0;
		int i;
		int mobid = 0;
		List<Mob> mob_list = etc.getServer().getMobList();
		
		if (attacker.getPlayer() != null) {
			if (defender.isMob()) {
				mobid = defender.getId();
			}
		} else if (attacker.getPlayer() != null) {
			attack = 0;
			if (attacker.isMob()) {
				defend = 1;
				mobid = attacker.getId();
			}
		}
		
		if (attack == 1) {
			for (i = 0; i < mob_list.size(); i++) {
				if (mob_list.get(i).getId() == mobid) {
					Player player = attacker.getPlayer();
					lookupQuester(player.getName()).attack(attacker.getPlayer(), mob_list.get(i));
				}
			}
			return true;
		}

		if (defend == 1) {
			for (i = 0; i < mob_list.size(); i++) {
				if (mob_list.get(i).getId() == mobid) {
					Player player = attacker.getPlayer();
					lookupQuester(player.getName()).defend(attacker.getPlayer(), mob_list.get(i));
				}
			}
			return true;
		}
		
		return false;
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


	
	public void getQuesters() {
		int num, i;
		ResultSet results;
		try {
			// TODO: Check on sql query
			results = sql_server.query("SELECT * FROM questers");
			
			results.last();
			num = results.getRow();
			results.first();
			
			questers = new Quester[num];
			for (i = 0; i < num; i++) {
				questers[i] = new Quester(results.getString("name"), sql_server);
				results.next();
			}
		} catch (SQLException e) {
			System.out.println("Failed to get questers - things are not going to work");
			e.printStackTrace();
		}
	}
}
