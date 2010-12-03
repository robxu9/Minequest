import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;


public class SkillClass {
	private int exp;
	private int level;
	@SuppressWarnings("unused")
	private Ability ability_list[];
	private String name;
	private String type;
	private int mana;
	private mysql_interface sql_server;
	
	public SkillClass(String type, String name, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		this.type = type;
		update();
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

	public void attack(Player player, BaseEntity defender, Quester quester) {
		int i;
		
		LivingEntity defend = getLiveEnt(defender);
		System.out.println("Call to " + name + ".attack()");
		if (defend == null) {
			System.out.println("oops");
		}
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				ability_list[i].parseAttack(player, defend, quester);
			}
		}
		
		
		defend.setHealth(defend.getHealth() - getDamage(defend.getName(), quester.getLevel(), MineQuestListener.getAdjustment()));
		
		expAdd(getExpMob(defend.getName()), quester);
		
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
		
		if (type.equals("Warrior")) {
			item_ids = new int[4];
			item_ids[0] = 310;
			item_ids[1] = 311;
			item_ids[2] = 312;
			item_ids[3] = 313;
		} else if (type.equals("Archer")) {
			item_ids = new int[4];
			item_ids[0] = 306;
			item_ids[1] = 307;
			item_ids[2] = 308;
			item_ids[3] = 309;
		} else if (type.equals("WarMage") || type.equals("PeaceMage")) {
			item_ids = new int[4];
			item_ids[0] = 314;
			item_ids[1] = 315;
			item_ids[2] = 316;
			item_ids[3] = 317;
		} else {
			return;
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
		
		if (level <= 2) {
			item_ids = new int[4];
			item_ids[0] = 298;
			item_ids[1] = 299;
			item_ids[2] = 300;
			item_ids[3] = 301;
		}

		for (i = 0; i < item_ids.length; i++) {
			if (equip.hasItem(item_ids[i], 1, 10000)) {
				equip.removeItem(new Item(item_ids[i], 1));
				player.giveItemDrop(item_ids[i], 1);
				equip.updateInventory();
				inven.updateInventory();
				player.sendMessage("You are not high enough level to use that");
			}
		}
		
		return;
	}
	
	public int defend(Player player, BaseEntity mob, int amount) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isDefending(player, mob)) {
				return ability_list[i].parseDefend(player, mob, amount);
			}
		}
		
		return 0;
	}
	
	public void disableAbility() {
		// TODO: write SkillClass.disableAbility();
	}
	
	public void display(Player player) {
		player.sendMessage("You are a level " + level + " " + type + " with " + exp + "/" + (400*(level+1)) + " Exp to next level");
		
		return;
	}

	public void enableAbility() {
		// TODO: write SkillClass.enableAbility();
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
	
	@SuppressWarnings("unused")
	private String getAbilName(int abilID) throws SQLException {
		ResultSet results;
		
		results = sql_server.query("SELECT * from abilnames WHERE abilid='" + abilID + "'");
		
		return results.getString("name");
	}
	
	private int getDamage(String name2, int plLevel, int adjustment) {
		int damage = 2;
		damage += (plLevel / 10);
		damage += (level / 5);
		damage -= adjustment;
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

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
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

	public void levelUp(Quester quester) {
		Random generator = new Random();
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

	}

	public void rightClick(Player player, Block blockClicked, Item item, Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(player.getItemInHand())) {
				ability_list[i].parseRightClick(player, blockClicked, quester);
			}
		}
		
		return;
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
		int abil_id;
		
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
			abil_id = results.getInt("abil_list_id");
			ability_list = abilListSQL(abil_id);
		} catch (SQLException e) {
			System.out.println("Problem reading Ability");
			e.printStackTrace();
		}
	}

	public boolean isAbilityItem(int itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemInHand)) {
				return true;
			}
		}
		
		return true;
	}	
	
	
}
