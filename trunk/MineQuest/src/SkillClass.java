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
import java.util.Random;


public class SkillClass {
	private int exp;
	private int level;
	private Ability ability_list[];
	private int abil_list_id;
	private String name;
	private String type;
	private mysql_interface sql_server;
	private Random generator;
	
	public SkillClass(String type, String name, mysql_interface sql) {
		this.name = name;
		sql_server = sql;
		this.type = type;
		generator = new Random();
		update();
	}
	
	private void addAbility(String string) {
		int i;
		
		try {
			sql_server.update("UPDATE abilities SET abil" + ability_list.length + "='" + string
					+ "' WHERE abil_list_id='" + abil_list_id + "'");
			ability_list = abilListSQL(abil_list_id);
			MineQuestListener.getQuester(name).getPlayer().sendMessage("You gained the ability " + string);
		} catch (SQLException e) {
			System.out.println("Failed to add ability " + string + " to mysql server");
			e.printStackTrace();
		}
		
		if (etc.getServer().getPlayer(name) != null) {
			etc.getServer().getPlayer(name).sendMessage("Gained ability " + string);
		}
		
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

		MineQuestListener.damageEntity(defend, getDamage(player, defend, defend.getName(), quester.getLevel()));

		expAdd(getExpMob(defend.getName()) + MineQuestListener.getAdjustment(), quester);
		
		return true;
	}

	public void blockDestroy(Player player, Block block, Quester quester) {
		int i;
		
		if (block.getStatus() == 0) {
			for (i = 0; i < ability_list.length; i++) {
				if (ability_list[i].isBound(player.getItemInHand())) {
					ability_list[i].parseLeftClick(player, block, quester);
					return;
				}
			}
		}

		if (level > 5) {
			if (isStoneWoodenTool(player.getItemInHand())) {
				if (block.getStatus() == 3) {
					if (isBlockGiveType(block.getType())) {
						player.giveItemDrop(getItemGive(block.getType()));
					}
				}
			}
		}
		
		if (isClassItem(block.getType())) {
			expAdd(2, quester);
		} else {
			expAdd(1, quester);
		}
	}

	private boolean isStoneWoodenTool(int itemInHand) {

		switch (itemInHand) {
		case 272:
		case 268:
		case 274:
		case 270:
		case 275:
		case 271:
		case 273:
		case 269:
		case 292:
		case 290:
			return true;
		default:
			return false;
		}
	}

	private Item getItemGive(int type2) {
		switch (type2) {
		case 14: //gold
			return new Item(266, 1);
		case 56: //diamond
			return new Item(264, 1);
		case 73: //red stone
			return new Item(331, 2);
		case 74: //more red stone
			return new Item(331, 2);
		default:
			return null;
		}
	}

	private boolean isBlockGiveType(int i) {
		switch (i) {
		case 14: //gold
		case 56: //diamond
		case 73: //red stone
		case 74: //more red stone
			return true;
		default:		
			return false;

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
		
		if (level >= 20) {
			return;
		}
		/*
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
				player.sendMessage("You are not high enough level to use that armor");
			}
		}*/
		
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
		int num = 400;
		if (!isCombatClass()) {
			num *= 2;
		}
		player.sendMessage("You are a level " + level + " " + type + " with " + exp + "/" + (num*(level+1)) + " Exp to next level");
		
		return;
	}

	public void enableAbility(String abil_name, Quester quester) {
		Ability abil = getAbility(abil_name);
		if (abil != null) {
			abil.enable(quester);
		}
	}
	
	public void expAdd(int expNum, Quester quester) {
		int num = 400;
		if (!isCombatClass()) {
			num *= 2;
		}
		exp += expNum;
		if (exp >= num * (level + 1)) {
			levelUp(quester);
		}
	}

	public Ability getAbility(String name) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].getName().equalsIgnoreCase(name)) {
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
	
	private int getDamage(Player player, LivingEntity defend, String name2, int plLevel) {
		int damage = 2;
		damage += (plLevel / 10);
		damage += (level / 5);
		
		if (generator.nextDouble() < getCritChance()) {
			damage *= 2;
			etc.getServer().getPlayer(name).sendMessage("Critical Hit!");
		}
		if (!isCombatClass()) {
			damage /= 4;
		}
		
		if (MineQuestListener.isSpecial(defend)) {
			return MineQuestListener.getSpecial(defend).defend(player, damage);
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
			else if (item_id == 268) return true;	// Wooden
		} else if (type.equals("Archer")) {
			if (item_id == 261) return true;
		} else if (type.equals("Miner")) {
			if (item_id == 274) return true;			// Stone
			else if (item_id == 278) return true;	// Diamond
			else if (item_id == 285) return true;	// Gold
			else if (item_id == 257) return true;	// Iron
			else if (item_id == 270) return true;	// Wooden
		} else if (type.equals("Lumberjack")) { 
			if (item_id == 275) return true;			// Stone
			else if (item_id == 279) return true;	// Diamond
			else if (item_id == 286) return true;	// Gold
			else if (item_id == 258) return true;	// Iron
			else if (item_id == 271) return true;	// Wooden
		} else if (type.equals("Digger")) {
			if (item_id == 273) return true;			// Stone
			else if (item_id == 277) return true;	// Diamond
			else if (item_id == 284) return true;	// Gold
			else if (item_id == 284) return true;	// Iron
			else if (item_id == 269) return true;	// Wooden
		} else if (type.equals("Farmer")) { 
			if (item_id == 292) return true;			// Stone
			else if (item_id == 293) return true;	// Diamond
			else if (item_id == 294) return true;	// Gold
			else if (item_id == 291) return true;	// Iron
			else if (item_id == 290) return true;	// Wooden
		}
		return false;
	}

	private boolean isCombatClass() {
		return type.equals("Warrior") || type.equals("Archer")
				|| type.equals("War Mage") || type.equals("Peace Mage");
	}

	private boolean isWearing(Player player, int i) {
		/*
		Inventory equip = player.getEquipment();
		
		return equip.hasItem(i, 1, 10000);*/
		return false;
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
		int num = 400;
		if (!isCombatClass()) {
			num *= 2;
		}
		level++;
		exp -= num * (level);
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

		if (type.equals("Warrior")) {
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
		} else if (type.equals("Archer")) {
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
		} else if (type.equals("WarMage")) {
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
		} else if (type.equals("PeaceMage")) {
			switch (level) {
			case 2:
				addAbility("Trape");
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
			if (ability_list[i].isBoundR(player.getItemInHand())) {
				ability_list[i].parseRightClick(player, blockClicked, quester);
				return true;
			}
		}
		
		return false;
	}

	public void save() {
		sql_server.update("UPDATE " + type + " SET exp='" + exp + "', level='" + level + 
								"' WHERE name='" + name + "'");
	}

	public void update() {
		ResultSet results;
		
		results = sql_server.query("SELECT * from " + type + " WHERE name='" + name + "'");
		
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
		if ((type.equals("WarMage") || type.equals("PeaceMage"))) {
			if (type.equals("WarMage")) {
				if (getAbility("Fireball") == null) {
					addAbility("Fireball");
				}
			} else {
				if (getAbility("Heal") == null) {
					addAbility("Heal");
				}
				if (getAbility("Heal Other") == null) {
					addAbility("Heal Other");
				}
			}
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
