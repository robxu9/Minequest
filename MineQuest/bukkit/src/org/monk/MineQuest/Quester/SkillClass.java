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

/**
 * Holds information referring to a Questers specific class
 * such as Warrior. Handles the list of abilities associated
 * with the class and any damage/experience required.
 * 
 * @author jmonk
 */
public class SkillClass {
	private int abil_list_id;
	private Ability ability_list[];
	private int exp;
	private Random generator;
	private int level;
	private Quester quester;
	private String type;
	
	/**
	 * Load Class information from MineQuest DB.
	 * 
	 * @param quester Quester that has the class
	 * @param type Name of the class
	 */
	public SkillClass(Quester quester, String type) {
		this.type = type;
		generator = new Random();
		this.quester = quester;
		update();
	}
	
	/**
	 * Reload the ability list from the MySQL database.
	 * Used on initial creation of class and upon modification
	 * of the ability list.
	 * 
	 * @param abilId ID of abilities in table
	 * @return List of Abilities for the abilId
	 * @throws SQLException Upon failure to Query the Database.
	 */
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

	/**
	 * Adds an ability to the class in the DB. Then
	 * reloads the ability list for the class.
	 * 
	 * @param string Name of ability to be added.
	 */
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

	/**
	 * Gets the chance for an armor set to block an
	 * attack.
	 * 
	 * @param armor Set of armor to check.
	 * @return Chance of armor blocking attack.
	 */
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

	/**
	 * Called whenever an attack is made by a Quester. Checks for
	 * bound abilities and if none are found sets the damage of the
	 * event as required.
	 * 
	 * @param defend Entity that is defending the attack
	 * @param event Event created for this attack
	 * @return boolean true
	 */
	public boolean attack(LivingEntity defend, EntityDamageByEntityEvent event) {
		for (Ability abil : ability_list) {
			if (abil.isBound(quester.getPlayer().getItemInHand())) {
				if (abil.parseAttack(quester, defend)) {
					return true;
				}
			}
		}

		event.setDamage(getDamage(defend));

		expAdd(getExpMob(defend) + MineQuest.getAdjustment());
		
		return true;
	}

	/**
	 * Called any time a player is destroying a block of this
	 * classes type or if the block has no class type then when
	 * the player is using this classes weapon/tool. Handles
	 * parsing of activated abilities.
	 * 
	 * @param block Block being destroyed
	 */
	public void blockDestroy(Block block) {
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
			expAdd(2);
		} else {
			expAdd(1);
		}
	}

	/**
	 * Will activate any abilities that are left bound on
	 * the quester's item in hand.
	 * 
	 * @param block Block selected for Casting
	 */
	public void callAbilityL(Block block) {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, block.getLocation(), 1, null);
	}

	/**
	 * Will activate any abilities that are left bound on
	 * the quester's item in hand.
	 * 
	 * @param entity Entity selected for Casting
	 */
	public void callAbilityL(Entity entity) {
		if (entity instanceof LivingEntity) {
			getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, entity.getLocation(), 1, (LivingEntity)entity);
		}
	}

	/**
	 * Will activate any abilities that are right bound on
	 * the quester's item in hand.
	 * 
	 * @param block Block selected for Casting
	 */
	public void callAbilityR(Block block) {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, block.getLocation(), 0, null);
	}

	/**
	 * Will activate any abilities that are right bound on
	 * the quester's item in hand.
	 * 
	 * @param entity Entity selected for Casting
	 */
	public void callAbilityR(Entity entity) {
		if (entity instanceof LivingEntity) {
			getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, entity.getLocation(), 0, (LivingEntity)entity);
		}
	}

	/**
	 * Determines if the Quester is high enough level in
	 * this class to use a specific item. if the item
	 * does not relate to this class then it will return
	 * true.
	 * 
	 * @param itemStack
	 * @return Boolean False if Quester cannot use item. True otherwise
	 */
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
	
	/**
	 * Checks if the Quester is wearing any equipment that
	 * they are not high enough level to use.
	 * 
	 * @param equip Equipment the Quester is Wearing
	 */
	public void checkEquip(PlayerInventory equip) {
		int item_ids[];
		int i;
		Player player = quester.getPlayer();
		
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
	
	/**
	 * Called whenever the Quester that has this class is
	 * being attacked.
	 * 
	 * @param entity Entity that is attacker
	 * @param amount Amount of damage being dealt
	 * @return The amount of damage negated by this class
	 */
	public int defend(LivingEntity entity, int amount) {
		int i;
		int armor[] = getClassArmorIds();
		boolean flag = true;
		int sum = 0;
		
		if (armor == null) return 0;
		
		for (i = 0; i < armor.length; i++) {
			if (isWearing(armor[i])) {
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

	/**
	 * Disables a specific Ability.
	 * 
	 * @param abil_name Name of ability to disable
	 */
	public void disableAbility(String abil_name) {
		Ability abil = getAbility(abil_name);
		if (abil != null) {
			abil.disable();
		}
	}

	/**
	 * Displays the status of this class
	 * 
	 */
	public void display() {
		int num = 400;
		if (!isCombatClass()) {
			num *= 2;
		}
		quester.getPlayer().sendMessage(" " + type + ": " + level + " - " + exp + "/" + (num*(level+1)));
		
		return;
	}

	/**
	 * Enables a specific Ability.
	 * 
	 * @param abil_name Name of Ability to enable
	 */
	public void enableAbility(String abil_name) {
		Ability abil = getAbility(abil_name);
		if (abil != null) {
			abil.enable(quester);
		}
	}
	
	/**
	 * Adds experience to the class and checks for a level
	 * up.
	 * 
	 * @param expNum Experience to be added
	 */
	public void expAdd(int expNum) {
		int num = 400;
		if (!isCombatClass()) {
			num *= 2;
		}
		exp += expNum;
		if (exp >= num * (level + 1)) {
			levelUp();
		}
	}

	/**
	 * Gets an ability based on an item that it is
	 * bound to.
	 * 
	 * @param itemInHand Item that ability is bound to
	 * @return Ability that is bound
	 */
	public Ability getAbility(ItemStack itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemInHand)) {
				return ability_list[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Gets an ability based on name.
	 * 
	 * @param name Name of Ability
	 * @return Ability requested
	 */
	public Ability getAbility(String name) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].getName().equalsIgnoreCase(name)) {
				return ability_list[i];
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the effective caster level for Mage classes.
	 * 
	 * @return Effective Caster Level
	 */
	public int getCasterLevel() {
		return (level + 1) / 2;
	}
	
	/**
	 * Gets a List of Armor IDs that are related
	 * to the current class.
	 * 
	 * @return List of Armor IDs
	 */
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

	/**
	 * Gets the Critical Hit chance for this specific
	 * class.
	 * 
	 * @return Critical Hit Chance
	 */
	private double getCritChance() {
		if ((getAbility("Deathblow") != null) && (getAbility("Deathblow").isEnabled())) {
			return 0.1;
		}
		
		return 0.05;
	}
	
	/**
	 * Gets the amount of damage that this class would do
	 * to a specific entity.
	 * 
	 * @param defend Defending Entity
	 * @return Damage to be dealt
	 */
	private int getDamage(LivingEntity defend) {
		int damage = 2;
		damage += (quester.getLevel() / 10);
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
	
	/**
	 * Get the experience that should be added to a class
	 * for damage to a given Entity.
	 * 
	 * @param defend Entity being damaged.
	 * @return Amount of experience
	 */
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
	
	/**
	 * Get the random number generator for this class.
	 * 
	 * @return Random number generator
	 */
	public Random getGenerator() {
		return generator;
	}

	/**
	 * Get Item stack that should be produced by a given
	 * block type being destroyed.
	 * 
	 * @param type2 Block type destroyed
	 * @return Item stack to go in inventory
	 */
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

	/**
	 * Get the level of this class
	 * 
	 * @return Current Level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Get the name of the Quester that owns
	 * this class.
	 * 
	 * @return Name of Quester
	 */
	public String getName() {
		return quester.getPlayer().getName();
	}

	/**
	 * Get the name of this class.
	 * 
	 * @return Name of Class
	 */
	public String getType() {
		return type;
	}

	/**
	 * Check if the given itemstack is bound to an 
	 * ability that this class is holding.
	 * 
	 * @param itemStack Itemstack to check
	 * @return Boolean True if bound to an Ability.
	 */
	public boolean isAbilityItem(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Check if the given itemstack is left bound to an 
	 * ability that this class is holding.
	 * 
	 * @param itemStack Itemstack to check
	 * @return Boolean True if bound to an Ability.
	 */
	public boolean isAbilityItemL(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBoundL(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Check if the given itemstack is right bound to an 
	 * ability that this class is holding.
	 * 
	 * @param itemStack Itemstack to check
	 * @return Boolean True if bound to an Ability.
	 */
	public boolean isAbilityItemR(ItemStack itemStack) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBoundR(itemStack)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if a block type is a type that cannot be
	 * destroyed without better tools.
	 * 
	 * @param i Block Type
	 * @return True for gold, diamond, and redstone
	 */
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

	/**
	 * Determines if an item is a tool specific to this
	 * class. 
	 * 
	 * @param item Item to Check
	 * @return True if item is a class item.
	 */
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

	/**
	 * Determines if an item is a tool specific to this
	 * class. 
	 * 
	 * @param type2 Item to Check
	 * @return True if item is a class item.
	 */
	private boolean isClassItem(Material type2) {
		return isClassItem(new ItemStack(type2.getId()));
	}

	/**
	 * Determines if the class is a combat class.
	 * 
	 * @deprecated
	 * @return
	 */
	private boolean isCombatClass() {
		return type.equals("Warrior") || type.equals("Archer")
				|| type.equals("War Mage") || type.equals("Peace Mage");
	}

	/**
	 * Determines if the item is a stone tool or
	 * a wooden tool.
	 * 
	 * @param itemInHand Item to check.
	 * @return True if wooden or stone
	 */
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

	/**
	 * Determines if the Quester is wearing the item as
	 * a piece of armor.
	 * 
	 * @param i ItemId of Item to check
	 * @return True if wearing the Item
	 */
	private boolean isWearing(int i) {
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

	/**
	 * Called when a Quester left clicks on something.
	 * 
	 * @param itemInHand Item that the quester clicked with
	 * @return True if left clicked parsed and is complete
	 */
	public boolean leftClick(ItemStack itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(quester.getPlayer().getItemInHand())) {
				ability_list[i].parseLeftClick(quester, null);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Called when a Quester left clicks on something.
	 * 
	 * @param itemInHand Item that the quester clicked with
	 * @param block Block that was clicked
	 * @return True if left clicked parsed and is complete
	 */
	public boolean leftClick(Block block, int itemInHand) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBound(quester.getPlayer().getItemInHand())) {
				ability_list[i].parseLeftClick(quester, block);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Called when a class gains enough experience to level up.
	 * Handles adding abilities and modification of health.
	 * 
	 * @param quester
	 */
	public void levelUp() {
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

	/**
	 * Called to display a list of abilities that this class
	 * holds to the player.
	 * 
	 */
	public void listAbil() {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			quester.sendMessage(ability_list[i].getName());
		}
	}

	/**
	 * Called when a block is right clicked by a Quester.
	 * 
	 * @param blockClicked Block that was Clicked
	 * @param item Item used to click block.
	 * @return True if click has been parsed and is complete
	 */
	public boolean rightClick(Block blockClicked, ItemStack item) {
		int i;
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].isBoundR(quester.getPlayer().getItemInHand())) {
				ability_list[i].parseRightClick(quester.getPlayer(), blockClicked, quester);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Save any changes in this class to the MySQL Database.
	 */
	public void save() {
		MineQuest.getSQLServer().update("UPDATE classes SET exp='" + exp + "', level='" + level + 
								"' WHERE name='" + quester.getPlayer().getName() + "' AND class='" + type + "'");
	}

	/***
	 * Unbind an item from any abilities it might be bound to.
	 * 
	 * @param itemInHand Item to unbind
	 * @param l Left or Right bound
	 */
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

	/**
	 * Load class data from database overwriting any unsaved
	 * changes.
	 */
	public void update() {
		ResultSet results;
		
		results = MineQuest.getSQLServer().query("SELECT * FROM classes WHERE name='" + quester.getPlayer().getName() + "' AND class='" + type + "'");
		
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
