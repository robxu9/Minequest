import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class SkillClass {
	private int exp;
	private int level;
	private Ability ability_list[];
	private int abil_list_id;
	private String name;
	private String type;
	private mysql_interface sql_server;
	Random generator;
	
	public SkillClass(String type, String name, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		this.type = type;
		generator = new Random();
		update();
		if ((name.equals("WarMage") || name.equals("PeaceMage")) && (ability_list.length == 0)) {
			if (name.equals("WarMage")) {
				addAbility("Fireball");
			} else {
				addAbility("Heal");
				addAbility("Heal Other");
			}
		}
	}
	
	private void addAbility(String string) {
		Ability[] new_list = new Ability[ability_list.length + 1];
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			new_list[i] = ability_list[i];
		}
		new_list[i] = new Ability(string, this);
		
		try {
			sql_server.update("UPDATE abilities SET abil" + ability_list.length + "='" + string
					+ "' WHERE abil_list_id='" + abil_list_id + "'");
		} catch (SQLException e) {
			System.out.println("Failed to add ability " + string + " to mysql server");
			e.printStackTrace();
		}
		
		ability_list = new_list;
		
		etc.getServer().getPlayer(name).sendMessage("Gained ability " + string);
		
		return;
	}

	private Ability[] abilListSQL(int abilId) throws SQLException {
		Ability list[];
		int i;
		int count = 0;
		ResultSet results = sql_server.query("SELECT * FROM abilities WHERE abil_list_id='" + abilId + "'");
		
		results.next();
		
		for (i = 0; i < 10; i++) {
			if (!results.getString("abil" + i).equals("0")) {
				count++;
			}
		}
		list = new Ability[count];
		
		for (i = 0; i < count; i++) {
			list[i] = new Ability(results.getString("abil" + i), this);
		}
		
		return list;
	}

	private double armorBlockChance(int[] armor) {
		switch (armor[0]) {
		case 302:
			return .25;
		case 310:
			return .20;
		case 306:
			return .15;
		case 314:
			return .10;
		case 298: 
			return .05;
		}
		
		return 0;
	}

	public boolean attack(Player player, LivingEntity defend, Quester quester) {
		int i;
		
		System.out.println("Call to " + name + ".attack()");
		
		if (defend == null) {
			player.sendMessage("Not Live Entity");
			return false;
		}

		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				if (ability_list[i].parseAttack(player, defend, quester)) {
					return true;
				}
			}
		}

		MineQuestListener.damageEntity(defend, getDamage(defend.getName(), quester.getLevel()));

		expAdd(getExpMob(defend.getName()) + MineQuestListener.getAdjustment(), quester);
		
		return true;
	}

	public void blockDestroy(Player player, Block block, Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				ability_list[i].parseLeftClick(player, block, quester);
				return;
			}
		}
		
		if (isClassItem(block.getType())) {
			expAdd(2, quester);
		} else {
			expAdd(1, quester);
		}
	}

	public boolean canUse(int item_id) {
		if (type.equals("Warrior")) { // Warrior
			if (item_id == 272) return (level > 4);			// Stone
			else if (item_id == 276) return (level > 49);	// Diamond
			else if (item_id == 283) return (level > 2);	// Gold
			else if (item_id == 267) return (level > 19);	// Iron
		} else if (type.equals("Miner")) {
			if (item_id == 274) return (level > 4);			// Stone
			else if (item_id == 278) return (level > 49);	// Diamond
			else if (item_id == 285) return (level > 2);	// Gold
			else if (item_id == 257) return (level > 19);	// Iron
		} else if (type.equals("Lumberjack")) { 
			if (item_id == 275) return (level > 4);			// Stone
			else if (item_id == 279) return (level > 49);	// Diamond
			else if (item_id == 286) return (level > 2);	// Gold
			else if (item_id == 258) return (level > 19);	// Iron
		} else if (type.equals("Digger")) {
			if (item_id == 273) return (level > 4);			// Stone
			else if (item_id == 277) return (level > 49);	// Diamond
			else if (item_id == 284) return (level > 2);	// Gold
			else if (item_id == 284) return (level > 19);	// Iron
		} else if (type.equals("Farmer")) { 
			if (item_id == 292) return (level > 4);			// Stone
			else if (item_id == 293) return (level > 49);	// Diamond
			else if (item_id == 294) return (level > 2);	// Gold
			else if (item_id == 291) return (level > 19);	// Iron
		}
		return true;
	}
	
	public void checkEquip(Player player, Inventory equip, Inventory inven) {
		int item_ids[];
		int i;
		
		if (level > 20) {
			return;
		}
		
		item_ids = getClassArmorIds();
		if (item_ids == null) {
			return;
		}
		if (!isCombatClass()) {
			if (level > 1) return;
		}
		
		for (i = 0; i < item_ids.length; i++) {
			if (equip.hasItem(item_ids[i], 1, 10000)) {
				equip.removeItem(new Item(item_ids[i], 1));
				player.giveItemDrop(item_ids[i], 1);
				equip.updateInventory();
				inven.updateInventory();
				player.sendMessage("You are not high enough level to use that armor");
			}
		}
		
		return;
	}
	
	public int defend(Player player, BaseEntity mob, int amount) {
		int i;
		int armor[] = getClassArmorIds();
		boolean flag = true;
		int sum = 0;
		
		if (armor == null) return 0;
		
		for (i = 0; i < armor.length; i++) {
			if (isWearing(player, armor[i])) {
				if (generator.nextDouble() < (.05 * i)) {
					sum++;
				}
			} else {
				flag = false;
			}
		}
		if (flag) {
			if (generator.nextDouble() < .4) {
				sum++;
			}
			
			if (generator.nextDouble() < armorBlockChance(armor)) {
				return amount;
			}
		}
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isDefending(player, mob)) {
				return sum + ability_list[i].parseDefend(player, mob, amount - sum);
			}
		}
		
		return sum;
	}

	public void disableAbility(String abil_name) {
		Ability abil = getAbility(abil_name);
		if (abil != null) {
			abil.disable();
		}
	}

	public void display(Player player) {
		player.sendMessage("You are a level " + level + " " + type + " with " + exp + "/" + (400*(level+1)) + " Exp to next level");
		
		return;
	}

	public void enableAbility(String abil_name, Quester quester) {
		Ability abil = getAbility(abil_name);
		if (abil != null) {
			abil.enable(quester);
		}
	}
	
	public void expAdd(int expNum, Quester quester) {
		exp += expNum;
		if (exp >= 400 * (level + 1)) {
			levelUp(quester);
		}
	}

	public Ability getAbility(String name) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].getName().equals(name)) {
				return ability_list[i];
			}
		}
		
		return null;
	}
	
	public int[] getClassArmorIds() {
		int[] item_ids;
		
		if (type.equals("Warrior")) {
			item_ids = new int[4];
			item_ids[0] = 310;
			item_ids[1] = 313;
			item_ids[2] = 312;
			item_ids[3] = 311;
		} else if (type.equals("Archer")) {
			item_ids = new int[4];
			item_ids[0] = 306;
			item_ids[1] = 309;
			item_ids[2] = 308;
			item_ids[3] = 307;
		} else if (type.equals("WarMage") || type.equals("PeaceMage")) {
			item_ids = new int[4];
			item_ids[0] = 314;
			item_ids[1] = 317;
			item_ids[2] = 316;
			item_ids[3] = 315;
		} else if (type.equals("Miner")) {
			item_ids = new int[4];
			item_ids[0] = 298;
			item_ids[1] = 301;
			item_ids[2] = 300;
			item_ids[3] = 299;
		} else {
			item_ids = null;
		}
		
		return item_ids;
	}
	
	private double getCritChance() {
		if ((getAbility("Deathblow") != null) && (getAbility("Deathblow").isEnabled())) {
			return 0.1;
		}
		
		return 0.05;
	}
	
	private int getDamage(String name2, int plLevel) {
		int damage = 2;
		damage += (plLevel / 10);
		damage += (level / 5);
		
		if (generator.nextDouble() < getCritChance()) {
			damage *= 2;
			etc.getServer().getPlayer(name).sendMessage("Critical Hit!");
		}
		
		return damage;
	}

	private int getExpMob(String name2) {
		if (name2.equals("Creeper")) {
			if (type.equals("Warrior")) {
				return 10;
			} else {
				return 5;
			}
		} else if (name2.equals("Spider")) {
			if (type.equals("WarMage")) {
				return 10;
			} else {
				return 5;
			}
		} else if (name2.equals("Skeleton")) {
			if (type.equals("PeaceMage")) {
				return 10;
			} else {
				return 5;
			}
		} else if (name2.equals("Zombie")) {
			if (type.equals("Archer")) {
				return 10;
			} else {
				return 5;
			}
		}
		return 3;
	}
	
	public Random getGenerator() {
		return generator;
	}
	
	public int getLevel() {
		return level;
	}

	@SuppressWarnings("unused")
	private LivingEntity getLiveEnt(BaseEntity defender) {
		if (defender instanceof LivingEntity) {
			return (LivingEntity)defender;
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isAbilityItem(int itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemInHand)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isClassItem(int item_id) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(item_id)) {
				return true;
			}
		}
		
		if (type.equals("Warrior")) { // Warrior
			if (item_id == 272) return true;			// Stone
			else if (item_id == 276) return true;	// Diamond
			else if (item_id == 283) return true;	// Gold
			else if (item_id == 267) return true;	// Iron
		} else if (type.equals("Archer")) {
			if (item_id == 261) return true;
		} else if (type.equals("Miner")) {
			if (item_id == 274) return true;			// Stone
			else if (item_id == 278) return true;	// Diamond
			else if (item_id == 285) return true;	// Gold
			else if (item_id == 257) return true;	// Iron
		} else if (type.equals("Lumberjack")) { 
			if (item_id == 275) return true;			// Stone
			else if (item_id == 279) return true;	// Diamond
			else if (item_id == 286) return true;	// Gold
			else if (item_id == 258) return true;	// Iron
		} else if (type.equals("Digger")) {
			if (item_id == 273) return true;			// Stone
			else if (item_id == 277) return true;	// Diamond
			else if (item_id == 284) return true;	// Gold
			else if (item_id == 284) return true;	// Iron
		} else if (type.equals("Farmer")) { 
			if (item_id == 292) return true;			// Stone
			else if (item_id == 293) return true;	// Diamond
			else if (item_id == 294) return true;	// Gold
			else if (item_id == 291) return true;	// Iron
		}
		return false;
	}

	private boolean isCombatClass() {
		return type.equals("Warrior") || type.equals("Archer")
				|| type.equals("War Mage") || type.equals("Peace Mage");
	}

	private boolean isWearing(Player player, int i) {
		Inventory equip = player.getEquipment();
		
		return equip.hasItem(i, 1, 10000);
	}

	public boolean leftClick(Player player, Block block, int itemInHand,
			Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				ability_list[i].parseLeftClick(player, block, quester);
				return true;
			}
		}
		
		return false;
	}

	public void levelUp(Quester quester) {
		int add_health;
		int size;
		level++;
		exp -= 400 * (level);
		quester.getPlayer().sendMessage("Congratulations on becoming a level " + level + " " + type);
		if (!isCombatClass()) return;
		if (type.equals("Warrior")) {
			size = 10;
		} else if (type.equals("Archer") || type.equals("PeaceMage")) {
			size = 6;
		} else {
			size = 4;
		}
		
		add_health = generator.nextInt(size) + 1;
		
		if (isCombatClass()) {
			quester.addHealth(add_health);
		}

		if (name.equals("Warrior")) {
			switch (level) {
			case 3:
				addAbility("PowerStrike");
				break;
			case 1:
				addAbility("Dodge");
				break;
			case 12:
				addAbility("Deathblow");
				break;
			case 5:
				addAbility("Sprint");
				break;
			default:
				break;
			}
		} else if (name.equals("Archer")) {
			switch (level) {
			case 1:
				addAbility("Dodge");
				break;
			case 3:
				addAbility("Sprint");
				break;
			case 5:
				addAbility("Hail of Arrows");
				break;
			case 7:
				addAbility("Fire Arrow");
				break;
			case 10:
				addAbility("Repulsion");
				break;
			default:
				break;
			}
		} else if (name.equals("WarMage")) {
			switch (level) {
			case 2:
				addAbility("Trap");
				break;
			case 3:
				addAbility("Drain Life");
				break;
			case 5:
				addAbility("Wall of Fire");
				break;
			case 7:
				addAbility("IceSphere");
				break;
			case 10:
				addAbility("FireChain");
				break;
			default:
				break;
			}
		} else if (name.equals("PeaceMage")) {
			switch (level) {
			case 2:
				addAbility("Trap");
				break;
			case 3:
				addAbility("Wall of Water");
				break;
			case 7:
				addAbility("Heal Aura");
				break;
			case 8:
				addAbility("Damage Aura");
				break;
			default:
				break;
			}
		}
	}
	
	public void listAbil(Player player) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			player.sendMessage(ability_list[i].getName());
		}
	}

	public boolean rightClick(Player player, Block blockClicked, Item item, Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				ability_list[i].parseRightClick(player, blockClicked, quester);
				return true;
			}
		}
		
		return false;
	}

	public void save() {
		try {
			sql_server.update("UPDATE " + type + " SET exp='" + exp + "', level='" + level + 
								"' WHERE name='" + name + "'");
		} catch (SQLException e) {
			System.out.println("Save failed for class " + type + " of player " + name);
			e.printStackTrace();
		}
	}

	public void update() {
		ResultSet results;
		
		try {
			results = sql_server.query("SELECT * from " + type + " WHERE name='" + name + "'");
		} catch (SQLException e) {
			System.out.println("Query failed for class " + type + " for " + name);
			e.printStackTrace();
			return;
		}
		
		try {
			results.next();
			exp = results.getInt("exp");
			level = results.getInt("level");
			abil_list_id = results.getInt("abil_list_id");
			ability_list = abilListSQL(abil_list_id);
		} catch (SQLException e) {
			System.out.println("Problem reading Ability");
			e.printStackTrace();
		}
	}

	public int getCasterLevel() {
		return (level + 1) / 2;
	}

	public void unBind(int itemInHand, boolean l) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (l && (ability_list[i].isBoundL(itemInHand))) {
				ability_list[i].unBindL();
			} else if (ability_list[i].isBoundR(itemInHand)) {
				ability_list[i].unBindR();
			}
		}
	}	
	
}
