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
	private boolean enabled;
	private String name;
	private SkillClass classes[];
	private int mana;
	private int max_mana;
	
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
	
	public void addHealth(int addition) {
		health += addition;
		max_health += addition;
	}

	public boolean attack(Player player, BaseEntity defender) {
		int i;
		
		LivingEntity defend = getLiveEnt(defender);
		
		System.out.println("Call to Quester.attack()");
		
		if (!enabled) return false;
		
		if (checkItemInHand(player)) return false;

		player.sendMessage("Attack from " + player.getName() + " to " + defend.getName());
		for (i = 0; i < classes.length; i++) {
			System.out.println("Checking " + classes[i].getType());
			if (classes[i].isClassItem(player.getItemInHand())) {
				classes[i].attack(player, defender, this);
				player.sendMessage("In class " + classes[i].getName());
				expGain(5);
				return true;
			}
		}
		
		return false;
	}
	
	public void bind(Player player, String name, String lr) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getAbility(name) != null) {
				if (lr.equals("l")) {
					classes[i].getAbility(name).bindl(player, new Item(player.getItemInHand(), 1));
				} else {
					classes[i].getAbility(name).bindr(player, new Item(player.getItemInHand(), 1));
				}
			}
		}
	}

	public int blockToClass(Block block) {
		int miner[] = {
			1,
			4,
			14,
			15,
			16,
			41,42,43,44,45,
			56, 57,
			73, 74, 48, 49
		};
		int lumber[] = {
			5,17,18,47,50,53,
			58,63,64,65,68
		};
		int digger[] = {
			2,3,6,12,13,60,78,
			82
		};
		int farmer[] = {
			37, 38, 39, 40,
			81, 83, 281, 282,
			295, 296, 297, 332
		};
		
		int i;
		
		for (i = 0; i < miner.length; i++) {
			if (block.getType() == miner[i]) {
				return 0;
			}
		}
		for (i = 0; i < lumber.length; i++) {
			if (block.getType() == lumber[i]) {
				return 1;
			}
		}
		for (i = 0; i < digger.length; i++) {
			if (block.getType() == digger[i]) {
				return 2;
			}
		}
		for (i = 0; i < farmer.length; i++) {
			if (block.getType() == farmer[i]) {
				return 3;
			}
		}
		return 4;
	}
	
	public boolean canCast(int i) {
		if (mana >= i) {
			mana -= i;
			return true;
		}
		return false;
	}
	
	public void checkEquip(Player player) {
		if (!enabled) return;
		
		Inventory equip = player.getEquipment();
		Inventory inven = player.getInventory();
		
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].checkEquip(player, equip, inven);
		}
		
	}
	
	public boolean checkItemInHand(Player player) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (!classes[i].canUse(player.getItemInHand())) {
				int id = player.getItemInHand();
				player.getInventory().removeItem(new Item(id, 1));
				player.getInventory().updateInventory();
				player.giveItemDrop(new Item(id, 1));
				player.sendMessage("You are not high enough level to use that weapon");
				return true;
			}
		}
		
		return false;
	}
	
	public void create() {
		int i, num;
		ResultSet results;
		String class_names[] = {
				"Warrior",
				"Archer",
				"WarMage",
				"PeaceMage",
				"Miner",
				"Lumberjack",
				"Digger",
				"Farmer"
		};
		
		String update_string = "INSERT INTO questers (name, exp, level, health, maxhealth, classes, mana, maxmana) VALUES('"
			+ name + "', '0', '0', '10', '10', '";
		update_string = update_string + class_names[0];
		for (i = 1; i < class_names.length; i++) {
			update_string = update_string + ", " + class_names[i];
		}
		update_string = update_string + "', '10', '10')";
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
	}
	
	public void defend(Player player, BaseEntity attacker, int amount) {
		int levelAdj = MineQuestListener.getAdjustment();
		if (levelAdj == 0) {
			levelAdj = 1;
		}
		amount *= levelAdj * 3;
		
		System.out.println("Damage to " + name + " is " + amount);
		if (!enabled) return;
		// TODO: write Quester.defend(player, mob);
		
		int i, sum = 0;
		
		for (i = 0; i < classes.length; i++) {
			sum += classes[i].defend(player, attacker, amount);
		}
		
		amount -= sum;
		
		if (amount < 0) {
			amount = 0;
		}
	}
	
	public boolean destroyBlock(Player player, Block block) {
		if (!enabled) return false;
		
		if (checkItemInHand(player)) {
			return true;
		}
		
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())) {
				classes[i].blockDestroy(player, block, this);
				return false;
			}
		}
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isClassItem(player.getItemInHand())) {
				classes[i].blockDestroy(player, block, this);
				expGain(2);
				return false;
			}
		}
		
		switch (blockToClass(block)) {
		case 0: // Miner
			getClass("Miner").blockDestroy(player, block, this);
			expGain(1);
			return false;
		case 1: // Lumberjack
			getClass("Lumberjack").blockDestroy(player, block, this);
			expGain(1);
			return false;
		case 2: // Digger
			getClass("Digger").blockDestroy(player, block, this);
			expGain(1);
			return false;
		case 3: // Farmer
			getClass("Farmer").blockDestroy(player, block, this);
			expGain(1);
			return false;
		default:
			break;
		}
		
		return false;
	}
	
	public void disable() {
		enabled = false;
		etc.getServer().getPlayer(name).sendMessage("MineQuest is now disabled for your character");
		
		return;
	}
	
	public void enable() {
		enabled = true;
		etc.getServer().getPlayer(name).sendMessage("MineQuest is now enabled for your character");
		
		return;
	}
	
	private void expGain(int i) {
		exp += i;
		if (exp > 400 * (level + 1)) {
			levelUp();
		}
	}

	public SkillClass getClass(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getType().equals(string)) {
				return classes[i];
			}
		}
		
		return null;
	}

	public int getExp() {
		return exp;
	}

	public int getHealth() {
		return health;
	}

	public int getLevel() {
		return level;
	}
	
	private LivingEntity getLiveEnt(BaseEntity defender) {
		List<LivingEntity> entity_list = etc.getServer().getLivingEntityList();
		int i;
		for (i = 0; i < entity_list.size(); i++) {
			if (defender.getId() == entity_list.get(i).getId()) {
				return entity_list.get(i);
			}
		}
		
		return null;
	}

	@SuppressWarnings("unused")
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
	
	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return etc.getServer().getPlayer(name);
	}

	public void healthChange(Player player, int oldValue, int newValue) {
		System.out.println(oldValue + " " + newValue);
		if (!enabled) return;

		if ((oldValue <= 0) && (newValue == 20)) {
			health = max_health;
		} else if ((oldValue > 20) || (oldValue < -30)) {
			oldValue = 20;
			health -= (oldValue - newValue);
		} else {
			health -= (oldValue - newValue);
		}
		
		
		
		newValue = 20 * health / max_health;
		
		if (newValue == 0) {
			newValue++;
		}
		
		player.setHealth(newValue);

		System.out.println("Health is " + health + "/" + max_health + " for " + name + " ");
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void levelUp() {
		Random generator = new Random();
		int add_health = generator.nextInt(3) + 1;
		level++;
		exp -= (400 * level);
		max_health += add_health;
		health += add_health;
		
		getPlayer().sendMessage("Congratulations on reaching character level " + level);
	}

	public boolean rightClick(Player player, Block blockClicked, Item item) {
		if (!enabled) return false;
		
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].rightClick(player, blockClicked, item, this)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void save() {
		try {
			int i;
			
			sql_server.update("UPDATE questers SET exp='" + exp + "', level='" + level + "', health='" 
					+ health + "', maxhealth='" + max_health + "', mana='" + mana + "', maxmana='" + max_mana
					+ "' WHERE name='" + name + "'");
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
			mana = results.getInt("mana");
			max_mana = results.getInt("maxmana");
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
}
