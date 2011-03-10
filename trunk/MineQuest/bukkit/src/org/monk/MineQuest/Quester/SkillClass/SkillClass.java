package org.monk.MineQuest.Quester.SkillClass;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.Combat.Archer;
import org.monk.MineQuest.Quester.SkillClass.Combat.PeaceMage;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;
import org.monk.MineQuest.Quester.SkillClass.Combat.Warrior;
import org.monk.MineQuest.Quester.SkillClass.Resource.Digger;
import org.monk.MineQuest.Quester.SkillClass.Resource.Farmer;
import org.monk.MineQuest.Quester.SkillClass.Resource.Lumberjack;
import org.monk.MineQuest.Quester.SkillClass.Resource.Miner;

/**
 * Holds information referring to a Questers specific class
 * such as Warrior. Handles the list of abilities associated
 * with the class and any damage/experience required.
 * 
 * @author jmonk
 */
public class SkillClass {
	private int abil_list_id;
	protected Ability ability_list[];
	private int exp;
	protected Random generator;
	protected int level;
	protected Quester quester;
	protected String type;
	
	public static SkillClass newClass(Quester quester, String type) {
		
		if (type.equals("Warrior")) {
			return new Warrior(quester, type);
		} else if (type.equals("Archer")) {
			return new Archer(quester, type);
		} else if (type.equals("WarMage")) {
			return new WarMage(quester, type);
		} else if (type.equals("PeaceMage")) {
			return new PeaceMage(quester, type);
		} else if (type.equals("Digger")) {
			return new Digger(quester, type);
		} else if (type.equals("Farmer")) {
			return new Farmer(quester, type);
		} else if (type.equals("Lumberjack")) {
			return new Lumberjack(quester, type);
		} else if (type.equals("Miner")) {
			return new Miner(quester, type);
		}
		
		MineQuest.log("Warning: SkillClass " + type + " could not be found");
		
		return new SkillClass(quester, type);
	}
	
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
	
	public SkillClass() {
		//Shell
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

	public void addAbility(Ability ability) {
		Ability[] new_abil_list = new Ability[ability_list.length + 1];
		int i;
		for (i = 0; i < ability_list.length; i++) {
			new_abil_list[i] = ability_list[i];
		}
		new_abil_list[i] = ability;
		
		ability_list = new_abil_list;
	}
	
	public void remAbility(Ability ability) {
		Ability[] new_list = new Ability[ability_list.length - 1];
		int i = 0;
		for (Ability abil : ability_list) {
			if (abil != null) {
				if (!abil.getName().equals(ability.getName())) {
					new_list[i++] = abil;
				}
			}
		}
		
		ability_list = new_list;
	}

	/**
	 * Adds an ability to the class in the DB. Then
	 * reloads the ability list for the class.
	 * 
	 * @param string Name of ability to be added.
	 */
	protected void addAbility(String string) {
		try {
			ability_list = abilListSQL(abil_list_id);
			if (ability_list.length == 10) return;
			MineQuest.getSQLServer().update("UPDATE abilities SET abil" + ability_list.length + "='" + string
					+ "' WHERE abil_list_id='" + abil_list_id + "'");
			ability_list = abilListSQL(abil_list_id);
			quester.sendMessage("You gained the ability " + string);
			quester.updateBinds();
		} catch (SQLException e) {
			System.out.println("Failed to add ability " + string + " to mysql server");
			e.printStackTrace();
		}
		
		quester.sendMessage("Gained ability " + string);
		
		return;
	}

	/**
	 * Called any time a player is destroying a block of this
	 * classes type or if the block has no class type then when
	 * the player is using this classes weapon/tool. Handles
	 * parsing of activated abilities.
	 * 
	 * @param event.getBlock() Block being destroyed
	 */
	public void blockDestroy(BlockDamageEvent event) {
		for (Ability abil : ability_list) {
			if (abil.isBound(quester.getPlayer().getItemInHand())) {
				abil.parseClick(quester, event.getBlock());
				return;
			}
		}
	}

	/**
	 * Will activate any abilities that are left bound on
	 * the quester's item in hand.
	 * 
	 * @param block Block selected for Casting
	 */
	public void callAbility(Block block) {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, block.getLocation(), null);
	}

	/**
	 * Will activate any abilities that are left bound on
	 * the quester's item in hand.
	 * 
	 * @param entity Entity selected for Casting
	 */
	public void callAbility(Entity entity) {
		if (entity instanceof LivingEntity) {
			getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, entity.getLocation(), (LivingEntity)entity);
		}
	}

	public void callAbility() {
		getAbility(quester.getPlayer().getItemInHand()).useAbility(quester, quester.getPlayer().getLocation(), null);
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
		if (this instanceof WarMage) {
			if (quester.getClass("PeaceMage").getLevel() >= 20) {
				return;
			}
		}
		if (this instanceof PeaceMage) {
			if (quester.getClass("WarMage").getLevel() >= 20) {
				return;
			}
		}
		
		item_ids = getClassArmorIds();
		if (item_ids == null) {
			return;
		}
		if (this instanceof ResourceClass) {
			if (level > 1) return;
		}
		
		for (i = 0; i < item_ids.length; i++) {
			for (ItemStack itemStack : equip.getArmorContents()) {
				if (itemStack.getTypeId() == item_ids[i]) {
					if (equip.firstEmpty() != -1) {
						equip.addItem(new ItemStack(item_ids[i]));
					} else {
						player.getWorld().dropItem(player.getLocation(), new ItemStack(item_ids[i]));
					}
					itemStack = null;
					player.sendMessage("You are not high enough level to use those leggings");
				}
			}
		}
		
		return;
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
		if (this instanceof ResourceClass) {
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
		if (this instanceof ResourceClass) {
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
			if (ability_list[i] != null) {
				if (ability_list[i].getName().equalsIgnoreCase(name)) {
					return ability_list[i];
				}
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
		return null;
	}
	
	/**
	 * Get the experience that should be added to a class
	 * for damage to a given Entity.
	 * 
	 * @param defend Entity being damaged.
	 * @return Amount of experience
	 */
	protected int getExpMob(LivingEntity defend) {
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
			if (ability_list[i] != null) {
				if (ability_list[i].isBound(itemStack)) {
					return true;
				}
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
	protected boolean isBlockGiveType(int i) {
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
		for (Ability abil : ability_list) {
			if (abil.isBound(item)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Determines if an item is a tool specific to this
	 * class. 
	 * 
	 * @param type Item to Check
	 * @return True if item is a class item.
	 */
	protected boolean isClassItem(Material type) {
		return isClassItem(new ItemStack(type.getId()));
	}

	/**
	 * Determines if the Quester is wearing the item as
	 * a piece of armor.
	 * 
	 * @param i ItemId of Item to check
	 * @return True if wearing the Item
	 */
	protected boolean isWearing(int i) {
		PlayerInventory equip = quester.getPlayer().getInventory();
		
		for (ItemStack itemStack : equip.getArmorContents()) {
			if (itemStack.getTypeId() == i) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when a Quester left clicks on something.
	 * 
	 * @param itemInHand Item that the quester clicked with
	 * @return True if left clicked parsed and is complete
	 */
	public boolean click(ItemStack itemInHand) {
		for (Ability ability : ability_list) {
			if (ability.isBound(quester.getPlayer().getItemInHand())) {
				ability.parseClick(quester, null);
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
	public boolean click(Block block, int itemInHand) {
		for (Ability ability : ability_list) {
			if (ability.isBound(quester.getPlayer().getItemInHand())) {
				ability.parseClick(quester, block);
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
		int num = 400;
		if (this instanceof ResourceClass) {
			num *= 2;
		}
		level++;
		exp -= num * (level);
		quester.getPlayer().sendMessage("Congratulations on becoming a level " + level + " " + type);
		
		fillAbilities();
	}

	private void fillAbilities() {
		try {
			ability_list = abilListSQL(abil_list_id);
			
			List<Ability> available = Ability.newAbilities(this);
			
			for (Ability ability : available) {
				if (ability_list.length < 10) {
					if (getAbility(ability.getName()) == null) {
						if (level >= ability.getReqLevel()) {
							addAbility(ability.getName());
							ability_list = abilListSQL(abil_list_id);
						}
					}
				}
			}
			
		} catch (SQLException e) {
			
		}
		quester.updateBinds();
	}

	/**
	 * Called to display a list of abilities that this class
	 * holds to the player.
	 * 
	 */
	public void listAbil() {
		for (Ability ability : ability_list) {
			if (ability != null) {
				quester.sendMessage(ability.getName());
			}
		}
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
	 * @param itemInHand 
	 */
	public void unBind(ItemStack itemInHand) {
		for (Ability ability : ability_list) {
			if (ability != null) {
				if (ability.isBound(itemInHand)) {
					ability.unBind(quester);
				}
			}
		}
	}

	/**
	 * Load class data from database overwriting any unsaved
	 * changes.
	 */
	public void update() {
		ResultSet results;
		
		results = MineQuest.getSQLServer().query("SELECT * FROM classes WHERE name='" + quester.getName() + "' AND class='" + type + "'");
		
		try {
			results.next();
			exp = results.getInt("exp");
			level = results.getInt("level");
			abil_list_id = results.getInt("abil_list_id");
			ability_list = abilListSQL(abil_list_id);
			quester.updateBinds();
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

	public void silentUnBind(ItemStack itemStack) {
		for (Ability ability : ability_list) {
			if (ability != null) {
				if (ability.isBound(itemStack)) {
					ability.silentUnBind(quester);
				}
			}
		}
	}

	public void replaceAbil(String old, String new_abil) {
		int i;
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i].getName().equals(old)) {
				break;
			}
		}
		
		Ability new_ability = Ability.newAbility(new_abil, this);
		
		if (new_ability == null) {
			quester.sendMessage(new_abil + " is not a valid ability");
			return;
		}
		
		if (level < new_ability.getReqLevel()) {
			quester.sendMessage("You are not high enough level to cast " + new_abil);
			return;
		}
		
		ability_list[i] = new_ability;
		MineQuest.getSQLServer().update("UPDATE abilities SET abil" + i + "='" + new_ability.getName()
				+ "' WHERE abil_list_id='" + abil_list_id + "'");
		quester.sendMessage(old + " Ability has been replaced with " + new_ability.getName() + " Ability in your spellbook");
	}

	public void rightClick(Block block) {
		expAdd(5);
	}
	

}
