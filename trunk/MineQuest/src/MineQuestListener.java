import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class MineQuestListener extends PluginListener {
    public static int getAdjustment() {
		int i;
		int avgLevel = 0;
		for (i = 0; i < questers.length; i++) {
			avgLevel += questers[i].getLevel();
		}
		avgLevel /= questers.length;
		
		return (avgLevel / 10);
	}
    @SuppressWarnings("unused")
	private Logger log;
	static private Quester questers[];
	private mysql_interface sql_server;

	private PropertiesFile prop;
	
	public void getQuesters() {
		int num, i;
		String names[];
		ResultSet results;
		try {
			// TODO: Check on sql query
			results = sql_server.query("SELECT * FROM questers");
			
			results.last();
			num = results.getRow();
			results.first();
			
			questers = new Quester[num];
			names = new String[num];
			for (i = 0; i < num; i++) {
				names[i] = results.getString("name");
				results.next();
			}
			for (i = 0; i < num; i++) {
				questers[i] = new Quester(names[i], sql_server);
			}
		} catch (SQLException e) {
			System.out.println("Failed to get questers - things are not going to work");
			e.printStackTrace();
		}
	}
	
	private Quester lookupQuester(String name) {
		int i;
		for (i = 0; i < questers.length; i++) {
			if (questers[i].getName().equals(name)) {
				return questers[i];
			}
		}
		System.out.println("No Quester Found");
		return null;
	}
	
	public void onArmSwing(Player player) {
		lookupQuester(player.getName()).checkItemInHand(player);
	}
	
	
	
	public boolean onBlockDestroy(Player player, Block block) {
		return lookupQuester(player.getName()).destroyBlock(player, block);
	}
	
	public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
		lookupQuester(player.getName()).rightClick(player, blockClicked, item);
	}
	
	public boolean onCommand(Player player, String[] split) {
		if (split[0].equals("/char")) {
			Quester quester = lookupQuester(player.getName());
			player.sendMessage("You are level " + quester.getLevel() + " with " + quester.getExp() + "/" + (400 * (quester.getLevel() + 1)) + " Exp");
			
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
		} else if (split[0].equals("/bind")) {
			if (split.length < 3) {
				player.sendMessage("Usage: /bind <ability> <l or r>");
				return true;
			}
			lookupQuester(player.getName()).bind(player, split[1], split[2]);
			return true;
		} else if (split[0].equals("/class")) {
			if (split.length < 2) {
				player.sendMessage("Usage: /class <class_name>");
				return true;
			}
			lookupQuester(player.getName()).getClass(split[1]).display(player);
			return true;
		} else if (split[0].equals("/health")) {
			return true;
		} else if (split[0].equals("/entities")) {
			List<LivingEntity> entity_list = etc.getServer().getLivingEntityList();
			int i;
			int zombie = 0;
			int creeper = 0;
			int spider = 0;
			int skeleton = 0;
			for (i = 0; i < entity_list.size(); i++) {
				if (entity_list.get(i).getName().equals("Spider")) {
					spider++;
				} else if (entity_list.get(i).getName().equals("Skeleton")) {
					skeleton++;
				} else if (entity_list.get(i).getName().equals("Zombie")) {
					zombie++;
				} else if (entity_list.get(i).getName().equals("Creeper")) {
					creeper++;
				} else if (entity_list.get(i).isMob()) {
					player.sendMessage(entity_list.get(i).getName() + " is a mob");
				}
			}
			player.sendMessage("There are " + creeper + " Creepers " + zombie + " Zombies " + skeleton + " Skeletons and " + spider + " Spiders of " + entity_list.size());
			
			return true;
		}
		return false;
	}
	
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,
			BaseEntity defender, int amount) {
		int attack = 1;
		int defend = 0;
		int mobid = 0;
		
		if (type != PluginLoader.DamageType.ENTITY) {
			return false;
		}
		
		if (defender.isAnimal()) {
			Player player = attacker.getPlayer();
			player.sendMessage("You attacked an Animal with a " + player.getItemInHand());
			return false;
		}
		if (attacker.getPlayer() != null) {
			if (defender.isMob()) {
				mobid = defender.getId();
				System.out.println("Got mob id " + mobid);
			} else {
				attack = 0;
			}
		} else if (defender.getPlayer() != null) {
			attack = 0;
			if (attacker.isMob()) {
				defend = 1;
				mobid = attacker.getId();
			}
		}
		
		if (attack == 1) {
			Player player = attacker.getPlayer();
			return lookupQuester(player.getName()).attack(player, defender);
		}

		if (defend == 1) {
			Player player = defender.getPlayer();
			player.sendMessage("Defend!");
			lookupQuester(player.getName()).defend(player, attacker, amount);
			return true;
		}
		
		return false;
	}
	
	public void onDisconnect(Player player) {
		lookupQuester(player.getName()).save();
	}
	
	public boolean onEquipmentChange(Player player) {
		lookupQuester(player.getName()).checkEquip(player);
		return super.onEquipmentChange(player);
	}

	public boolean onHealthChange(Player player, int oldValue, int newValue) {
		System.out.println("Call to onHealthChange " + oldValue + " " + newValue);
		System.out.println(player.getName());
		if (lookupQuester(player.getName()).isEnabled()) {
			lookupQuester(player.getName()).healthChange(player, oldValue, newValue);
			return false;
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
}
