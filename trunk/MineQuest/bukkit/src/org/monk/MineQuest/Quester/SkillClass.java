package org.monk.MineQuest.Quester;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;

public class SkillClass {
	private int abil_list_id;
	private Ability ability_list[];
	private int exp;
	private Random generator;
	private int level;
	private String name;
	private Quester quester;
	private String type;
	
	public SkillClass(Quester quester, String type, String name) {
		this.name = name;
		this.type = type;
		generator = new Random();
		this.quester = quester;
		update();
	}
	
	private Ability[] abilListSQL(int abilId) throws SQLException {
		Ability list[];
		int i;
		int count = 0;
		ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM abilities WHERE abil_list_id='" + abilId + "'");
		
		results.next();
		
		for (i = 0; i < 10; i++) {
			if (!results.getString("abil" + i).equals("0")) {
				count++;
			}
		}
		list = new Ability[count];
		
		for (i = 0; i < count; i++) {
			list[i] = Ability.newAbility(results.getString("abil" + i), this);
		}
		
		return list;
	}

	private void addAbility(String string) {
		try {
			MineQuest.getSQLServer().update("UPDATE abilities SET abil" + ability_list.length + "='" + string
					+ "' WHERE abil_list_id='" + abil_list_id + "'");
			ability_list = abilListSQL(abil_list_id);
			quester.sendMessage("You gained the ability " + string);
		} catch (SQLException e) {
			System.out.println("Failed to add ability " + string + " to mysql server");
			e.printStackTrace();
		}
		
		quester.sendMessage("Gained ability " + string);
		
		return;
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

	public boolean attack(Quester quester, LivingEntity defend, EntityDamageByEntityEvent event) {
		for (Ability abil : ability_list) {
			if (abil.isBound(quester.getPlayer().getItemInHand())) {
				if (abil.parseAttack(quester, defend)) {
					return true;
				}
			}
		}

		event.setDamage(getDamage(quester, defend, quester.getLevel()));

		expAdd(getExpMob(defend) + MineQuest.getAdjustment(), quester);
		
		return true;
	}

	public void blockDestroy(Block block, Quester quester) {
		for (Ability abil : ability_list) {
			if (abil.isBound(quester.getPlayer().getItemInHand())) {
				abil.parseLeftClick(quester, block);
				return;
			}
		}

//		if (level > 5) {
//			if (isStoneWoodenTool(quester.getPlayer().getItemInHand())) {
//				if (block.getStatus() == 3) {
//					if (isBlockGiveType(block.getType())) {
//						player.giveItemDrop(getItemGive(block.getType()));
//					}
//				}
//			}
//		}
		
		if (isClassItem(block.getType())) {
			expAdd(2, quester);
		} else {
			expAdd(1, quester);
		}
	}

	public void callAbilityL(Quester quester, Block block) {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, block.getLocation(), 1, null);
	}

	public void callAbilityL(Quester quester, Entity entity) {
		if (entity instanceof LivingEntity) {
			getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, entity.getLocation(), 1, (LivingEntity)entity);
		}
	}

	public void callAbilityR(Quester quester, Block block) {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, block.getLocation(), 0, null);
	}

	public void callAbilityR(Quester quester, Entity entity) {
		if (entity instanceof LivingEntity) {
			getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, entity.getLocation(), 0, (LivingEntity)entity);
		}
	}

	public boolean canUse(ItemStack itemStack) {
		int item = itemStack.getTypeId();
		
		if (type.equals("Warrior")) { // Warrior
			if (item == 272) return (level > 4);			// Stone
			else if (item == 276) return (level > 49);	// Diamond
			else if (item == 283) return (level > 2);	// Gold
			else if (item == 267) return (level > 19);	// Iron
		} else if (type.equals("Miner")) {
			if (item == 274) return (level > 4);			// Stone
			else if (item == 278) return (level > 49);	// Diamond
			else if (item == 285) return (level > 2);	// Gold
			else if (item == 257) return (level > 19);	// Iron
		} else if (type.equals("Lumberjack")) { 
			if (item == 275) return (level > 4);			// Stone
			else if (item == 279) return (level > 49);	// Diamond
			else if (item == 286) return (level > 2);	// Gold
			else if (item == 258) return (level > 19);	// Iron
		} else if (type.equals("Digger")) {
			if (item == 273) return (level > 4);			// Stone
			else if (item == 277) return (level > 49);	// Diamond
			else if (item == 284) return (level > 2);	// Gold
			else if (item == 284) return (level > 19);	// Iron
		} else if (type.equals("Farmer")) { 
			if (item == 292) return (level > 4);			// Stone
			else if (item == 293) return (level > 49);	// Diamond
			else if (item == 294) return (level > 2);	// Gold
			else if (item == 291) return (level > 19);	// Iron
		}
		
		return true;
	}
	
	public void checkEquip(Player player, PlayerInventory equip) {
		int item_ids[];
		int i;
		
		if (level >= 20) {
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
			if (equip.getBoots().getTypeId() == item_ids[i]) {
				if (equip.firstEmpty() != -1) {
					equip.addItem(new ItemStack(item_ids[i]));
				} else {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(item_ids[i]));
				}
				equip.setBoots(null);
				player.sendMessage("You are not high enough level to use those boots");
			}
			if (equip.getChestplate().getTypeId() == item_ids[i]) {
				if (equip.firstEmpty() != -1) {
					equip.addItem(new ItemStack(item_ids[i]));
				} else {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(item_ids[i]));
				}
				equip.setChestplate(null);
				player.sendMessage("You are not high enough level to use that chestplate");
			}
			if (equip.getHelmet().getTypeId() == item_ids[i]) {
				if (equip.firstEmpty() != -1) {
					equip.addItem(new ItemStack(item_ids[i]));
				} else {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(item_ids[i]));
				}
				equip.setHelmet(null);
				player.sendMessage("You are not high enough level to use that helmet");
			}
			if (equip.getLeggings().getTypeId() == item_ids[i]) {
				if (equip.firstEmpty() != -1) {
					equip.addItem(new ItemStack(item_ids[i]));
				} else {
					player.getWorld().dropItem(player.getLocation(), new ItemStack(item_ids[i]));
				}
				equip.setLeggings(null);
				player.sendMessage("You are not high enough level to use those leggings");
			}
		}
		
		return;
	}
	
	public int defend(Quester quester, LivingEntity entity, int amount) {
		int i;
		int armor[] = getClassArmorIds();
		boolean flag = true;
		int sum = 0;
		
		if (armor == null) return 0;
		
		for (i = 0; i < armor.length; i++) {
			if (isWearing(quester, armor[i])) {
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
			if (ability_list[i].isDefending()) {
				return sum + ability_list[i].parseDefend(quester, entity, amount - sum);
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
		player.sendMessage(" " + type + ": " + level + " - " + exp + "/" + (num*(level+1)));
		
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

	public Ability getAbility(ItemStack itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemInHand)) {
				return ability_list[i];
			}
		}
		
		return null;
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
	
	public int getCasterLevel() {
		return (level + 1) / 2;
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
	
	private int getDamage(Quester quester, LivingEntity defend, int plLevel) {
		int damage = 2;
		damage += (plLevel / 10);
		damage += (level / 5);
		
		if (generator.nextDouble() < getCritChance()) {
			damage *= 2;
			quester.sendMessage("Critical Hit!");
		}
		if (!isCombatClass()) {
			damage /= 4;
		}
		
//		if (MineQuestListener.isSpecial(defend)) {
//			return MineQuestListener.getSpecial(defend).defend(player, damage);
//		}
		
		return damage;
	}
	
	private int getExpMob(LivingEntity defend) {
		if (defend.equals("Creeper")) {
			if (type.equals("Warrior")) {
				return 10;
			} else {
				return 5;
			}
		} else if (defend.equals("Spider")) {
			if (type.equals("WarMage")) {
				return 10;
			} else {
				return 5;
			}
		} else if (defend.equals("Skeleton")) {
			if (type.equals("PeaceMage")) {
				return 10;
			} else {
				return 5;
			}
		} else if (defend.equals("Zombie")) {
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

	@SuppressWarnings("unused")
	private ItemStack getItemGive(int type2) {
		switch (type2) {
		case 14: //gold
			return new ItemStack(266, 1);
		case 56: //diamond
			return new ItemStack(264, 1);
		case 73: //red stone
			return new ItemStack(331, 2);
		case 74: //more red stone
			return new ItemStack(331, 2);
		default:
			return null;
		}
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isAbilityItem(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isAbilityItemL(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBoundL(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isAbilityItemR(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBoundR(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	@SuppressWarnings("unused")
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

	public boolean isClassItem(ItemStack item) {
		int i;
		int item_id = item.getTypeId();
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(item)) {
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
	
	private boolean isClassItem(Material type2) {
		return isClassItem(new ItemStack(type2.getId()));
	}

	private boolean isCombatClass() {
		return type.equals("Warrior") || type.equals("Archer")
				|| type.equals("War Mage") || type.equals("Peace Mage");
	}

	@SuppressWarnings("unused")
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

	private boolean isWearing(Quester quester, int i) {
		PlayerInventory equip = quester.getPlayer().getInventory();
		
		if (equip.getBoots().getTypeId() == i) {
			return true;
		} else if (equip.getChestplate().getTypeId() == i) {
			return true;
		} else if (equip.getHelmet().getTypeId() == i) {
			return true;
		} else if (equip.getLeggings().getTypeId() == i) {
			return true;
		}
		return false;
	}

	public boolean leftClick(ItemStack itemInHand, Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(quester.getPlayer().getItemInHand())) {
				ability_list[i].parseLeftClick(quester, null);
				return true;
			}
		}
		
		return false;
	}

	public boolean leftClick(Quester quester, Block block, int itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(quester.getPlayer().getItemInHand())) {
				ability_list[i].parseLeftClick(quester, block);
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

	public void listAbil(Quester quester) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			quester.sendMessage(ability_list[i].getName());
		}
	}

	public boolean rightClick(Player player, Block blockClicked, ItemStack item, Quester quester) {
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
		MineQuest.getSQLServer().update("UPDATE classes SET exp='" + exp + "', level='" + level + 
								"' WHERE name='" + name + "' AND class='" + type + "'");
	}

	public void unBind(ItemStack itemInHand, boolean l) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (l && (ability_list[i].isBoundL(itemInHand))) {
				ability_list[i].unBindL();
			} else if (ability_list[i].isBoundR(itemInHand)) {
				ability_list[i].unBindR();
			}
		}
	}

	public void update() {
		ResultSet results;
		
		results = MineQuest.getSQLServer().query("SELECT * FROM classes WHERE name='" + name + "' AND class='" + type + "'");
		
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
	

}
