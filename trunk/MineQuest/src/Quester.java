/*
 * MineQuest - Hey0 Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2010  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Quester {
	private SkillClass classes[];
	private double distance;
	private boolean enabled;
	private int exp;
	private int health;
	private long last_time = etc.getServer().getTime();
	private int level;
	private boolean loginFlag;
	private long loginTime;
	private int max_health;
	private String name;
	private int poison_timer;
	private mysql_interface sql_server;
	private Item give_item;
	
	public Quester(String name, int x, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		create();
		update();
		distance = 0;
		enabled = false;
		give_item = null;
	}
	
	public Quester(String name, mysql_interface sql) {
		sql_server = sql;
		this.name = name;
		update();
		enabled = false;
		give_item = null;
	}
	
	public void addHealth(int addition) {
		health += addition;
		max_health += addition;
	}

	public boolean attack(Player player, BaseEntity defender, int amount) {
		int i;
		
		LivingEntity defend = getLiveEnt(defender);
		
		
		if (defend == null) {
			player.sendMessage("Not Live Entity");
		}
		
		if (checkItemInHand(player)) return false;

		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())) {
				if (classes[i].attack(player, defend, this)) {
					amount = 0;
					return false;
				}
			}
		}
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isClassItem(player.getItemInHand())) {
				classes[i].attack(player, defend, this);
				expGain(5);
				amount = 0;
				return false;
			}
		}
		
		return false;
	}
	
	public void bind(Player player, String name, String lr) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].unBind(player.getItemInHand(), lr.equals("l"));
		}
		
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
	
	public boolean canCast(List<Item> list) {
		int i;
		Inventory inven = etc.getServer().getPlayer(name).getInventory();
		
		for (i = 0; i < list.size(); i++) {
			if (inven.hasItem(list.get(i).getItemId(), list.get(i).getAmount(), 10000)) {
				inven.removeItem(list.get(i));
			} else {
				while (i-- > 0) {
					inven.addItem(list.get(i));
				}
				return false;
			}
		}
		return true;
	}
	
	public void checkEquip(Player player) {
		if (!enabled) return;
		
		Inventory equip = player.getInventory();//getEquipment();
		Inventory inven = player.getInventory();
		
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].checkEquip(player, equip, inven);
		}
		
	}

	public boolean checkItemInHand(Player player) {
		int i;
		
		if ((etc.getServer().getTime() - last_time) > 10) {
			last_time = etc.getServer().getTime();
			for (i = 0; i < classes.length; i++) {
				if (!classes[i].canUse(player.getItemInHand())) {
					int id = player.getItemInHand();
					player.getInventory().removeItem(new Item(id, 1));
					player.giveItemDrop(new Item(id, 1));
					player.sendMessage("You are not high enough level to use that weapon");
					return true;
				}
			}
		}

		return false;
	}

	public boolean checkItemInHandAbil(Player player) {
		int i;
		
		if ((player.getItemInHand() == 261) || (player.getItemInHand() == 332)) {
			return false;
		}

		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())) {
				return classes[i].leftClick(player, new Block(1, 0, 0, 0), player.getItemInHand(), this);
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

		sql_server.update(update_string);
			
		try {
			results = sql_server.query("SELECT * FROM abilities");
			results.last();
			num = results.getRow();
		} catch (SQLException e) {
			System.out.println("Unable to get max ability id");
			num = 0;
		}
		num++;
		
		for (i = 0; i < class_names.length; i++) {
			update_string = "INSERT INTO " + class_names[i] + "(name, exp, level, abil_list_id) VALUES('"
								+ name + "', '0', '0', '" + (num + i) + "')";

			sql_server.update(update_string);
			sql_server.update("INSERT INTO abilities (abil_list_id) VALUES('" + (num + i) + "')");
		}
	}
	
	public void curePoison() {
		poison_timer = 0;
	}
	
	public void defend(Player player, BaseEntity attacker, int amount) {
		int levelAdj = MineQuestListener.getAdjustment();
		if (levelAdj == 0) {
			levelAdj = 1;
		}
		
		amount *= levelAdj * 3;
		if (MineQuestListener.isSpecial((LivingEntity)attacker)) {
			amount = MineQuestListener.getSpecial((LivingEntity)attacker).attack(this, player, amount);
		}
		
		System.out.println("Damage to " + name + " is " + amount);
		if (!enabled) return;
		
		int i, sum = 0;
		
		for (i = 0; i < classes.length; i++) {
			sum += classes[i].defend(player, attacker, amount);
		}
		
		amount -= sum;
		
		if (amount >= 20) {
			amount = 19;
		}
		if (amount < 0) {
			amount = 0;
		}
	}
	
	public boolean destroyBlock(Player player, Block block) {
		if (!enabled) return false;
		
		if (checkItemInHand(player)) {
			return true;
		}
		if (checkItemInHandAbil(player)) {
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
				expGain(1);
				return false;
			}
		}
		
		switch (blockToClass(block)) {
		case 0: // Miner
			getClass("Miner").blockDestroy(player, block, this);
			return false;
		case 1: // Lumberjack
			getClass("Lumberjack").blockDestroy(player, block, this);
			return false;
		case 2: // Digger
			getClass("Digger").blockDestroy(player, block, this);
			return false;
		case 3: // Farmer
			getClass("Farmer").blockDestroy(player, block, this);
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
	
	public void disableabil(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].disableAbility(string);
		}
	}

	public void enable() {
		enabled = true;
		etc.getServer().getPlayer(name).sendMessage("MineQuest is now enabled for your character");
		
		return;
	}

	public void enableabil(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].enableAbility(string, this);
		}
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
			if (classes[i].getType().equalsIgnoreCase(string)) {
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
		/*List<LivingEntity> entity_list = etc.getServer().getLivingEntityList();
		int i;
		for (i = 0; i < entity_list.size(); i++) {
			if (defender.getId() == entity_list.get(i).getId()) {
				return entity_list.get(i);
			}
		}*/
		if (defender instanceof LivingEntity) {
			return (LivingEntity)defender;
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

	public int getMaxHealth() {
		return max_health;
	}

	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return etc.getServer().getPlayer(name);
	}
	
    public boolean healthChange(Player player, int oldValue, int newValue) {
//        boolean flag = false;
//        if (newValue <= 0) {
//                flag = true;
//        }
        System.out.println(oldValue + " " + newValue);
        if (!enabled) return false;
        
        if (loginFlag) {
        	loginFlag = false;
        	return false;
        }
        
        if (oldValue - newValue >= 20) {
//                health = -1;
//                return false;
        }

        if ((oldValue <= 0) && (newValue == 20)) {
                health = max_health;
        } else if ((oldValue > 20) || (oldValue < -30)) {
                oldValue = 20;
                health -= (oldValue - newValue);
        } else {
                health -= (oldValue - newValue);
        }
        
        newValue = 20 * health / max_health;
        
        if ((newValue == 0) && (health > 0)) {
        	newValue++;
        }
        if (health > max_health) {
        	health = max_health;
        }
        
        player.setHealth(newValue);

        System.out.println("Health is " + health + "/" + max_health + " for " + name + " ");

        return false;
    }


	public boolean isEnabled() {
		return enabled;
	}

	public boolean isPoisoned() {
		return (poison_timer > 0);
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

	public void listAbil(Player player) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].listAbil(player);
		}
	}

	private long loggedIn() {
		return etc.getServer().getTime() - loginTime;
	}

	public void move(Location from, Location to) {
		double move, x, y, z;
		
		if (give_item != null) {
			etc.getServer().getPlayer(name).giveItem(give_item);
			give_item = null;
		}
		
		if (poison_timer > 0) {
			x = from.x - to.x;
			y = from.y - to.y;
			z = from.z - to.z;
			move = Math.sqrt(x*x + y*y + z*z);
			distance += move;
		}
		while ((distance > 5) && (poison_timer > 0)) {
			distance -= 5;
			poison_timer -= 1;
			setHealth(getHealth() - 1);
		}
	}

	public void parseExplosion(BaseEntity attacker, Player player, int amount) {
		if (MineQuestListener.getSpecialList().contains((LivingEntity)attacker)) {
			amount *= 2;
		}
	}

	public void parseFire(PluginLoader.DamageType type, int amount) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if ((classes[i].getAbility("Fire Resistance") != null) && classes[i].getAbility("Fire Resistance").isEnabled()) {
				classes[i].getAbility("Fire Resistance").parseFire(type, amount);
				return;
			}
		}
	}

	public void poison() {
		poison_timer += 10;
	}

	public boolean rightClick(Player player, Block blockClicked, Item item) {
		if (!enabled) return false;
		
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].rightClick(player, blockClicked, item, this)) {
				blockClicked.setType(0);
				return true;
			}
		}
		
		return false;
	}

	public void save() {
			int i;
			
		if (sql_server.update("UPDATE questers SET exp='" + exp + "', level='" + level + "', health='" 
				+ health + "', maxhealth='" + max_health + "', mana='" + 0 + "', maxmana='" + 0
				+ "' WHERE name='" + name + "'") == -1) {
			etc.getServer().getPlayer(name).sendMessage("May not have saved properly, please try again");
		}
		for (i = 0; i < classes.length; i++) {
			classes[i].save();
		}
		enabled = false;
	}
	
	public void setHealth(int i) {

		/*int newValue;
		
		

		if (i > max_health) {
			i = max_health;
		}
		health = i;
		
		newValue = 20 * health / max_health;
		
		if ((newValue == 0) && (health > 0)) {
			newValue++;
		}*/
		
		etc.getServer().getPlayer(name).setHealth(i);
		
	}

	public void unBind(int itemInHand) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].unBind(itemInHand, true);
			classes[i].unBind(itemInHand, false);
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
		loginFlag = true;
		loginTime = etc.getServer().getTime();
	}

	public void updateHealth(Player player) {
		int newValue;
		
		newValue = 20 * health / max_health;
		
		if ((newValue == 0) && (health > 0)) {
			newValue++;
		}

		
		player.setHealth(newValue);
	}
}
