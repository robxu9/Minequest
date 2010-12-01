import java.sql.ResultSet;
import java.sql.SQLException;


public class SkillClass {
	private int exp;
	private int level;
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
	
	public void attack(Player player, Mob mob) {
		// TODO: write SkillClass.attack(player, mob);
	}
	
	public void defend(Player player, Mob mob) {
		// TODO: write SkillClass.defend(player, mob);
	}
	
	public void levelUp() {
		// TODO: write SkillClass.levelUp();
	}
	
	public void enableAbility() {
		// TODO: write SkillClass.enableAbility();
	}
	
	public void disableAbility() {
		// TODO: write SkillClass.disableAbility();
	}
	
	public boolean canUse(int item_id) {
		// TODO: write SkillClass.disableAbility();
		return false;
	}
	
	public String getName() {
		// TODO: write SkillClass.getName();
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
