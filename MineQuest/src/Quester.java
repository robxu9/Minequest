import java.sql.ResultSet;
import java.sql.SQLException;


public class Quester {
	private int exp;
	private int level;
	private int health;
	private int max_health;
	private mysql_interface sql_server;
	private Item last_used;
	private boolean enabled;
	private String name;
	private SkillClass classes[];
	
	public Quester(String name, int x, mysql_interface sql) {
		String class_names[] = {
				"Warrior",
				"Archer",
				"War Mage",
				"Peace Mage",
				"Miner",
				"Lumberjack",
				"Digger",
				"Farmer"
		};
		this.name = name;
		sql_server = sql;
		int i, num;
		ResultSet results;
		
		String update_string = "INSERT INTO questers (name, exp, level, health, maxhealth, classes) VALUES('"
			+ name + "', '0', '0', '50', '50', '";
		update_string = update_string + class_names[0];
		for (i = 1; i < class_names.length; i++) {
			update_string = update_string + ", " + class_names[i];
		}
		update_string = update_string + "'";
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
				sql_server.update("INSERT INTO abilities (abil_list_id) VALUES('" + (num + i) + "')");
			} catch (SQLException e) {
				System.out.println("Unable to insert");
				e.printStackTrace();
			}
		}
		update();
	}
	
	public Quester(String name, mysql_interface sql) {
		sql_server = sql;
		this.name = name;
		update();
	}
	
	public void attack(Player player, Mob mob) {
		// TODO: write Quester.attack(player, mob);
	}
	
	public void defend(Player player, Mob mob) {
		// TODO: write Quester.defend(player, mob);
	}
	
	private void levelUp() {
		// TODO: write Quester.levelUp();
	}
	
	public void addHealth(int addition) {
		// TODO: write Quester.addHealth(addition);
	}
	
	public boolean isEnabled() {
		// TODO: write Quester.isEnabled();
		return false;
	}
	
	public void enable() {
		// TODO: write Quester.enable();
	}
	
	public void disable() {
		// TODO: write Quester.disable();
	}
	
	public void create() {
		// TODO: write Quester.create();
	}
	
	public int getHealth() {
		// TODO: write Quester.getHealth();
		return 0;
	}
	
	public void save() {
		// TODO: write Quester.save();
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
			this.name = name;
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
			classes[i] = new SkillClass(name, sql_server);
		}
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
		// TODO Auto-generated method stub
	}

	public void destroyBlock(Player player, Block block) {
		// TODO Auto-generated method stub
		
	}
}
