import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class Quester {
	private int exp;
	private int level;
	private int health;
	private int max_health;
	private mysql_interface sql_server;
	@SuppressWarnings("unused")
	private Item last_used;
	private boolean enabled;
	private String name;
	private SkillClass classes[];
	
	public Quester(String name, int x, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		create();
		update();
	}
	
	public Quester(String name, mysql_interface sql) {
		sql_server = sql;
		this.name = name;
		update();
	}
	
	public void attack(Player player, BaseEntity defender) {
		int i;
		System.out.println("Call to Quester.attack()");
		
		if (!enabled) return;

		player.sendMessage("Attack from " + player.getName() + " to " + defender.getName());
		for (i = 0; i < classes.length; i++) {
			System.out.println("Checking " + classes[i].getType());
			if (classes[i].isClassItem(player.getItemInHand())) {
				classes[i].attack(player, defender, this);
				player.sendMessage("In class " + classes[i].getName());
				exp += 5;
				if (exp > 100 * (level + 1)) {
					levelUp();
					player.sendMessage("Congradulations " + name + ", you are now a level " + level + " character");
				}
				return;
			}
		}
	}
	
	public void defend(Player player, BaseEntity attacker) {
		System.out.println("Call to Quester.defend()");
		if (!enabled) return;
		// TODO: write Quester.defend(player, mob);
		player.sendMessage("Your health was " + player.getHealth());
		player.setHealth(player.getHealth() - 5);
		player.sendMessage("Now it is " + player.getHealth());
	}
	
	private void levelUp() {
		Random generator = new Random();
		int add_health = generator.nextInt(3) + 1;
		level++;
		exp = 0;
		max_health += add_health;
		health += add_health;
	}
	
	public void addHealth(int addition) {
		health += addition;
		max_health += addition;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void enable() {
		enabled = true;
		etc.getServer().getPlayer(name).sendMessage("MineQuest is now enabled for your character");
		
		return;
	}
	
	public void disable() {
		enabled = false;
		etc.getServer().getPlayer(name).sendMessage("MineQuest is now disabled for your character");
		
		return;
	}
	
	public void create() {
		int i, num;
		ResultSet results;
		String class_names[] = {
				"Warrior",
				"Archer",
				//"WarMage",
				//"PeaceMage",
				"Miner",
				"Lumberjack",
				"Digger",
				"Farmer"
		};
		
		String update_string = "INSERT INTO questers (name, exp, level, health, maxhealth, classes) VALUES('"
			+ name + "', '0', '0', '10', '10', '";
		update_string = update_string + class_names[0];
		for (i = 1; i < class_names.length; i++) {
			update_string = update_string + ", " + class_names[i];
		}
		update_string = update_string + "')";
		try {
			sql_server.update(update_string);
		} catch (SQLException e) {
			System.out.println("Failed to add player to database");
			e.printStackTrace();
			return;
		}
		
		try {
			results = sql_server.query("SELECT MAX(abil_id) FROM abilities");
			results.next();
			num = results.getInt("id");
		} catch (SQLException e) {
			System.out.println("Unable to get max ability id");
			num = 0;
		}
		num++;
		
		for (i = 0; i < class_names.length; i++) {
			update_string = "INSERT INTO " + class_names[i] + "(name, exp, level, abil_list_id) VALUES('"
								+ name + "', '0', '0', '" + (num + i) + "')";
			try {
				sql_server.update(update_string);
				//sql_server.update("INSERT INTO abilities (abil_list_id) VALUES('" + (num + i) + "')");
			} catch (SQLException e) {
				System.out.println("Unable to insert");
				e.printStackTrace();
			}
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public void save() {
		try {
			int i;
			
			sql_server.update("UPDATE questers SET exp='" + exp + "', level='" + level + "', health='" 
					+ health + "', maxhealth='" + max_health + "' WHERE name='" + name + "'");
			for (i = 0; i < classes.length; i++) {
				classes[i].save();
			}
		} catch (SQLException e) {
			System.out.println("Unable to save " + name + " to database");
		}
	}
	
	public void update() {
		ResultSet results;
		String split[];
		int i;
		
		try {
			results = sql_server.query("SELECT * FROM questers WHERE name='" + name + "'");
			results.next();
		} catch (SQLException e) {
			System.out.println("Issue querying name");
			e.printStackTrace();
			return;
		}
		try {
			split = results.getString("classes").split(", ");
			exp = results.getInt("exp");
			level = results.getInt("level");
			health = results.getInt("health");
			max_health = results.getInt("maxhealth");
			last_used = null;
			enabled = true;
			
		} catch (SQLException e) {
			System.out.println("Issue getting parameters");
			e.printStackTrace();
			return;
		}
		
		classes = new SkillClass[split.length];
		for (i = 0; i < split.length; i++) {
			classes[i] = new SkillClass(split[i], name, sql_server);
		}
		enabled = true;
	}

	public int getExp() {
		return exp;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public void healthChange(Player player, int oldValue, int newValue) {
		System.out.println("Call to healthChange");
		
		if (!enabled) return;
		
		health -= (oldValue - newValue);
		
		LivingEntity pl = getLiveEnt(player);
		
		if (pl == null) {
			System.out.println("Failed to get Live Entity");
		} else {
			System.out.println("Got Player");
		}
		player.sendMessage("health is currently " + pl.getHealth());
		
		pl.setHealth(19);
	}
	
	private LivingEntity getLiveEnt(Player player) {
		List<LivingEntity> entity_list = etc.getServer().getLivingEntityList();
		int i;
		for (i = 0; i < entity_list.size(); i++) {
			if (player.getName() == entity_list.get(i).getName()) {
				return entity_list.get(i);
			}
		}
		
		return null;
	}

	public void destroyBlock(Player player, Block block) {
		if (!enabled) return;
		// TODO Auto-generated method stub
		
	}
}
