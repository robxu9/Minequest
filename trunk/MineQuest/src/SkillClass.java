import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;


public class SkillClass {
	private int exp;
	private int level;
	@SuppressWarnings("unused")
	private Ability ability_list[];
	private String name;
	private String type;
	private mysql_interface sql_server;
	
	public SkillClass(String type, String name, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		this.type = type;
		update();
	}
	
	public void attack(Player player, Mob mob, Quester quester) {
		mob.setHealth(mob.getHealth() - 10);
		
		exp += 10;
		if (exp > 100 * (level + 1)) {
			levelUp(quester);
			player.sendMessage("Congradulations " + name + ", you are now a level " + level + " " + type);
		}
	}
	
	public void defend(Player player, Mob mob) {
		// TODO: write SkillClass.defend(player, mob);
	}
	
	public void levelUp(Quester quester) {
		Random generator = new Random();
		int add_health;
		int size;
		level++;
		exp = 0;
		if (!isCombatClass()) return;
		if (type.equals("Warrior")) {
			size = 10;
		} else if (type.equals("Archer") || type.equals("Peace Mage")) {
			size = 6;
		} else {
			size = 4;
		}
		
		add_health = generator.nextInt(size) + 1;
		
		quester.addHealth(add_health);
	}
	
	private boolean isCombatClass() {
		return type.equals("Warrior") || type.equals("Archer")
				|| type.equals("War Mage") || type.equals("Peace Mage");
	}

	public void enableAbility() {
		// TODO: write SkillClass.enableAbility();
	}
	
	public void disableAbility() {
		// TODO: write SkillClass.disableAbility();
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
	
	public boolean isClassItem(int item_id) {
		if (type.equals("Warrior")) { // Warrior
			if (item_id == 272) return true;			// Stone
			else if (item_id == 276) return true;	// Diamond
			else if (item_id == 283) return true;	// Gold
			else if (item_id == 267) return true;	// Iron
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
	
	public String getName() {
		return name;
	}
	
	public void blockDestroy(Player player, Block block) {
		// TODO: write SkillClass.blockDestroy(player, block);
	}
	
	public Ability getAbility(String name) {
		// TODO: write SkillClass.getAbility(name);
		return null;
	}

	public void save() {
		try {
			sql_server.update("UPDATE " + type + " SET exp='" + exp + "', level='" + level + 
								" WHERE name='" + name + "'");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Ability[] abilListSQL(int abilId) throws SQLException {
		Ability list[];
		int i;
		int count = 0;
		ResultSet results = sql_server.query("SELECT * FROM abilities WHERE abil_list_id='" + abilId + "'");
		
		for (i = 0; i < 10; i++) {
			if (results.getString("abil" + i) != null) {
				count++;
			}
		}
		list = new Ability[count];
		
		for (i = 0; i < count; i++) {
			int AbilID = Integer.parseInt(results.getString("abil" + i));
			list[i] = new Ability(getAbilName(AbilID), AbilID);
		}
		
		return list;
	}

	private String getAbilName(int abilID) throws SQLException {
		ResultSet results;
		
		results = sql_server.query("SELECT * from abilnames WHERE abilid='" + abilID + "'");
		
		return results.getString("name");
	}	
	
	
}
