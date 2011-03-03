package org.monk.MineQuest.Quester;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.block.CraftChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Quester.SkillClass.Resource.Miner;
import org.monk.MineQuest.World.Town;

/**
 * This is a wrapper around bukkit's player class.
 * It manages the health for the player as well as
 * any other Player specific MineQuest data.
 * 
 * @author jmonk
 */
public class Quester {
	private ChestSet chests;
	private SkillClass classes[];
	private double cubes;
	private double distance;
	private boolean enabled;
	private int exp;
	private int health;
	private String last;
	private boolean respawn_flag;
	private int level;
	private int max_health;
	private String name;
	private Player player;
	private int poison_timer;
	private int rep;
	private long damage_timer;
	List<Integer> ids = new ArrayList<Integer>();
	List<Long> times = new ArrayList<Long>();
	private ItemStack[] spare_inven;
	private ItemStack[] spare_inven_2;
	private Quest quest;
	
	/**
	 * Load player from MySQL Database.
	 * @param player
	 */
	public Quester(Player player) {
		this(player.getName());
		this.player = player;
	}
	
	/**
	 * Create a new Player in the MySQL Database.
	 * 
	 * @param player
	 * @param x
	 */
	public Quester(Player player, int x) {
		this(player.getName(), 0);
		this.player = player;
	}
	
	/**
	 * Load player from MySQL Database.
	 * @param name
	 */
	public Quester(String name) {
		this.name = name;
		update();
		spare_inven = null;
		spare_inven_2 = null;
	}

	/**
	 * Create a new Player in the MySQL Database
	 * 
	 * @param name
	 * @param x
	 */
	public Quester(String name, int x) {
		this.name = name;
		create();
		update();
		distance = 0;
		spare_inven = null;
		spare_inven_2 = null;
	}

	/**
	 * Adds to both health and maximum health of quester.
	 * Should be used on level up of character of 
	 * SkillClasses.
	 * 
	 * @param addition
	 */
	public void addHealth(int addition) {
		health += addition;
		max_health += addition;
	}

	/**
	 * Called whenever a Quester attacks any other entity.
	 * 
	 * @param entity
	 * @param event
	 */
	public void attackEntity(Entity entity, EntityDamageByEntityEvent event) {
		if (checkItemInHand()) return;

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				if (entity instanceof LivingEntity) {
					if (skill instanceof CombatClass) {
						((CombatClass)skill).attack((LivingEntity)entity, event);
						expGain(5);
					}
					return;
				}
			}
		}

		if (entity instanceof LivingEntity) {
			for (SkillClass skill : classes) {
				if (skill.isClassItem(player.getItemInHand())) {
					if (skill instanceof CombatClass) {
						((CombatClass)skill).attack((LivingEntity)entity, event);
						expGain(5);
						return;
					} else {
						((CombatClass)getClass("Warrior")).attack((LivingEntity)entity, event);
						expGain(3);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Binds an Ability to left of right click of the item
	 * in the players hand.
	 * 
	 * @param player Player
	 * @param name Name of Ability
	 * @param lr 1 for left, 0 for right
	 */
	public void bind(Player player, String name, String lr) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].unBind(player.getItemInHand(), lr.equals("l"));
		}
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getAbility(name) != null) {
				if (lr.equals("l")) {
					classes[i].getAbility(name).bindl(player, player.getItemInHand());
				} else {
					classes[i].getAbility(name).bindr(player, player.getItemInHand());
				}
			}
		}
	}

	/**
	 * Determines which resource class a given block belongs to.
	 * 
	 * @param block
	 * @return
	 */
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
			if (block.getType().getId() == miner[i]) {
				return 0;
			}
		}
		for (i = 0; i < lumber.length; i++) {
			if (block.getType().getId() == lumber[i]) {
				return 1;
			}
		}
		for (i = 0; i < digger.length; i++) {
			if (block.getType().getId() == digger[i]) {
				return 2;
			}
		}
		for (i = 0; i < farmer.length; i++) {
			if (block.getType().getId() == farmer[i]) {
				return 3;
			}
		}
		return 4;
	}
	
	/**
	 * Casts a left click bound ability on the given block.
	 * 
	 * @param block
	 */
	public void callAbilityL(Block block) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())){
				classes[i].callAbilityL(block);
				return;
			}
		}
	}
	
	/**
	 * Casts a left click bound ability on the given entity
	 * 
	 * @param entity
	 */
	public void callAbilityL(Entity entity) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())){
				classes[i].callAbilityL(entity);
				return;
			}
		}
	}

	/**
	 * Casts a right click bound ability on a given block.
	 * 
	 * @param block
	 */
	public void callAbilityR(Block block) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())){
				classes[i].callAbilityR(block);
				return;
			}
		}
	}

	/**
	 * Casts a right click bound ability on a given entity.
	 * @param entity
	 */
	public void callAbilityR(Entity entity) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())){
				classes[i].callAbilityR(entity);
				return;
			}
		}
	}
	
	/**
	 * Checks if a Quester has the required spell components.
	 * to cast an ability. If the Quester has the components
	 * they are removed from the inventory.
	 * 
	 * @param list List of Components
	 * @return true if cost paid
	 */
	public boolean canCast(List<ItemStack> list) {
		int i;
		PlayerInventory inven = player.getInventory();
		
		for (i = 0; i < list.size(); i++) {
			if (inven.contains(list.get(i).getType())) {
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
	
	/**
	 * Checks if a Quester can use all of the equipment
	 * that they are wearing. It checks with each class
	 * individually, and they all handle their own removal.
	 * 
	 * @param player
	 */
	public void checkEquip(Player player) {
		if (!enabled) return;
		
		PlayerInventory inven = player.getInventory();
		
		int i;
		
		for (SkillClass skill : classes) {
			skill.checkEquip(inven);
		}
	}
	
	/**
	 * Checks if a Quester is allowed to use the item
	 * in hand. The item is checked with each class
	 * but the removal is handled here.
	 * 
	 * @return
	 */
	public boolean checkItemInHand() {
		int i;
		PlayerInventory inven = player.getInventory();
		ItemStack item = player.getItemInHand();
		
		for (i = 0; i < classes.length; i++) {
			if (!classes[i].canUse(item)) {
				if (inven.firstEmpty() != -1) {
					inven.addItem(item);
				} else {
					player.getWorld().dropItem(player.getLocation(), item);
				}
				
				inven.setItemInHand(null);
				player.sendMessage("You are not high enough level to use that weapon");
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Checks if the item in the Questers hand is bound
	 * to any abilities. 
	 * @return true if bound to an ability
	 */
	public boolean checkItemInHandAbil() {
		int i;
		
		if ((player.getItemInHand().getTypeId() == 261) || (player.getItemInHand().getTypeId() == 332)) {
			return false;
		}

		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItem(player.getItemInHand())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if the item in the Questers hand is left bound
	 * to any abilities. 
	 * @return true if left bound to an ability
	 */
	public boolean checkItemInHandAbilL() {
		int i;
		
		if ((player.getItemInHand().getTypeId() == 261) || (player.getItemInHand().getTypeId() == 332)) {
			return false;
		}

		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItemL(player.getItemInHand())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks if the item in the Questers hand is right bound
	 * to any abilities. 
	 * @return true if right bound to an ability
	 */
	public boolean checkItemInHandAbilR() {
		int i;
		
		if ((player.getItemInHand().getTypeId() == 261) || (player.getItemInHand().getTypeId() == 332)) {
			return false;
		}

		for (i = 0; i < classes.length; i++) {
			if (classes[i].isAbilityItemR(player.getItemInHand())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Creates database entry for this quester with starting
	 * classes and health.
	 */
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
		
		String update_string = "INSERT INTO questers (name, selected_chest, cubes, exp, level, last_town, classes, health, max_health) VALUES('"
			+ name + "', '" + name + "', '500000', '0', '0', 'Bitville', '";
		update_string = update_string + class_names[0];
		for (i = 1; i < class_names.length; i++) {
			update_string = update_string + ", " + class_names[i];
		}
		update_string = update_string + "', '10', '10')";

		MineQuest.getSQLServer().update(update_string);

		num = 0;
		try {
			results = MineQuest.getSQLServer().query("SELECT * FROM abilities");
			while (results.next()) {
				num++;
			}
		} catch (SQLException e) {
			System.out.println("Unable to get max ability id");
		}
		
		for (i = 0; i < class_names.length; i++) {
			update_string = "INSERT INTO classes (name, class, exp, level, abil_list_id) VALUES('"
								+ name + "', '" + class_names[i] + "', '0', '0', '" + (num + i) + "')";

			MineQuest.getSQLServer().update(update_string);
			MineQuest.getSQLServer().update("INSERT INTO abilities (abil_list_id) VALUES('" + (num + i) + "')");
		}
	}
	
	/**
	 * Cures all poison for the Quester.
	 */
	public void curePoison() {
		poison_timer = 0;
	}

	/**
	 * Called any time there is a generic damage event on 
	 * the Quester.
	 * 
	 * @param event
	 */
	public void defend(EntityDamageEvent event) {
		healthChange(event.getDamage(), event);
	}

	/**
	 * Called any time there is a damaged by block event
	 * on the Quester.
	 * @param event
	 */
	public void defendBlock(EntityDamageByBlockEvent event) {
		healthChange(event.getDamage(), event);
	}

	/**
	 * Called any time the Quester is defending against an
	 * attack from another entity.
	 * 
	 * @param entity Attacker
	 * @param event
	 */
	public void defendEntity(Entity entity, EntityDamageByEntityEvent event) {
		int amount = classes[0].getGenerator().nextInt(10);
		int levelAdj = MineQuest.getAdjustment();
		if (levelAdj == 0) {
			levelAdj = 1;
		} else {
			amount *= levelAdj * 3;
		}
		amount /= 4;
		
		if (entity instanceof LivingEntity) {
			if (MineQuest.getMob((LivingEntity)entity) != null) {
				amount = MineQuest.getMob((LivingEntity)entity).attack(amount, player);
			}
		}
		
//		if (MineQuest.isSpecial((LivingEntity)attacker)) {
//			amount = MineQuest.getSpecial((LivingEntity)attacker).attack(this, player, amount);
//		}
		
		MineQuest.log("[INFO] Damage to " + name + " is " + amount);
		if (!enabled) return;
		
		int i, sum = 0;
		
		for (SkillClass sclass : classes) {
			if (entity instanceof LivingEntity) {
				if (sclass instanceof CombatClass) {
					sum += ((CombatClass)sclass).defend((LivingEntity)entity, amount);
				} else if (sclass instanceof Miner) {
					sum += ((Miner)sclass).defend((LivingEntity)entity, amount);
				}
			}
		}
		
		amount -= sum;

		if (amount < 0) {
			amount = 0;
		}
		
		healthChange(amount, event);
		
		return;
	}
	
	/**
	 * Called anytime a Player is destroying a block. Checks to
	 * see if it is bound to any abilities then attributes experience
	 * to given class if required.
	 * 
	 * @param event being destroyed
	 * @return false
	 */
	public boolean destroyBlock(BlockDamageEvent event) {
		if (!enabled) return false;

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				skill.blockDestroy(event);
				expGain(5);
				return false;
			}
		}
		
		for (SkillClass skill : classes) {
			if (skill.isClassItem(player.getItemInHand())) {
				skill.blockDestroy(event);
				expGain(1);
				return false;
			}
		}
		
		switch (blockToClass(event.getBlock())) {
		case 0: // Miner
			getClass("Miner").blockDestroy(event);
			return false;
		case 1: // Lumberjack
			getClass("Lumberjack").blockDestroy(event);
			return false;
		case 2: // Digger
			getClass("Digger").blockDestroy(event);
			return false;
		case 3: // Farmer
			getClass("Farmer").blockDestroy(event);
			return false;
		default:
			break;
		}
		
		return false;
	}
	
	/**
	 * Disable an ability with the given name.
	 * 
	 * @param string Name of Ability
	 */
	public void disableabil(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getAbility(string) != null) {
				classes[i].getAbility(string).disable();
			}
		}
	}

	/**
	 * Drops a Questers reputation by i.
	 * 
	 * @param i
	 */
	public void dropRep(int i) {
		rep -= i;
	}

    /**
	 * Enable an ability with the given name.
	 * 
	 * @param string Name of Ability
	 */
	public void enableabil(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getAbility(string) != null) {
				classes[i].getAbility(string).enable(this);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Quester) {
			return name.equals(((Quester)obj).getName());
		}
		if (obj instanceof String) {
			return name.equals(obj);
		}
		return super.equals(obj);
	}

	/**
	 * Adds i experience to Quester and checks for level
	 * up.
	 * 
	 * @param i Experience to Add
	 */
	private void expGain(int i) {
		exp += i;
		if (exp > 400 * (level + 1)) {
			levelUp();
		}
	}

	/**
	 * Gets the ChestSet for given player
	 * 
	 * @param player
	 * @return ChestSet
	 */
	public ChestSet getChestSet(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the SkillClass with given name for this
	 * Quester. Returns NULL if no class exists with
	 * given name.
	 * 
	 * @param string Name of SkillClass
	 * @return SkillClass
	 */
	public SkillClass getClass(String string) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			if (classes[i].getType().equalsIgnoreCase(string)) {
				return classes[i];
			}
		}
		
		return null;
	}

	/**
	 * Gets a list of SkillClasses that this Quester has.
	 * @return
	 */
	public SkillClass[] getClasses() {
		return classes;
	}

	/**
	 * Returns amount of cubes the Quester has.
	 * 
	 * @return
	 */
	public double getCubes() {
		return cubes;
	}

	/**
	 * Returns amount of experience for Quester.
	 * @return
	 */
	public int getExp() {
		return exp;
	}
	
	/**
	 * Returns health of Quester.
	 * @return
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Returns level of Quester.
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns maximum health of Quester.
	 * 
	 * @return
	 */
	public int getMaxHealth() {
		return max_health;
	}

	/**
	 * Returns name of Quester.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns player that the Quester wraps.
	 * 
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Returns last town player was near.
	 * 
	 * @return
	 */
	public Town getTown() {
		return MineQuest.getTown(last);
	}
	
	public void giveSpareInventory() {
		if (spare_inven != null) {
			Location loc = player.getLocation();
			World world = player.getWorld();
			
			Block block = world.getBlockAt((int)loc.getX() + 1,
					(int)loc.getY(), (int)loc.getZ());
			
			block.setType(Material.CHEST);
			
			Chest chest = new CraftChest(block);
			
			chest.getInventory().setContents(spare_inven);
			
			block = world.getBlockAt((int)loc.getX() + 2,
					(int)loc.getY(), (int)loc.getZ());
			
			block.setType(Material.CHEST);
			
			chest = new CraftChest(block);
			
			chest.getInventory().setContents(spare_inven_2);
			
			spare_inven = null;
		}
	}

	/**
	 * Called any time a Quester is taking damage of any time
	 * it adjusts the Quester's health accordingly and sets
	 * the damage of the event as required.
	 * 
	 * @param change Amount of Damage Done
	 * @param event Event causing Damage
	 * @return false
	 */
	public boolean healthChange(int change, EntityDamageEvent event) {
		int newHealth;
		
        if ((event instanceof EntityDamageByEntityEvent) && checkDamage(((EntityDamageByEntityEvent)event).getDamager().getEntityId())) {
        	event.setCancelled(true);
        	return false;
        }
        if (((event.getCause() == DamageCause.FIRE) || 
        		(event.getCause() == DamageCause.FIRE_TICK) || 
        		(event.getCause() == DamageCause.SUFFOCATION) || 
        		(event.getCause() == DamageCause.DROWNING) || 
        		(event.getCause() == DamageCause.LAVA)) && checkDamage(42024)) {
        	event.setCancelled(true);
        	return false;
        }

    	MineQuest.log(change + " damage to " + name);
        health -= change;
        
        newHealth = 20 * health / max_health;
        
        if ((newHealth == 0) && (health > 0)) {
        	newHealth++;
        }
        
        if (health > max_health) {
        	health = max_health;
        }
        
        if (health <= 0) {
        	MineQuest.log("Clearing Inventory!!");
        	
        	spare_inven = new ItemStack[27];
        	int i;
        	for (i = 0; i < 27; i++) {
        		spare_inven[i] = player.getInventory().getItem(i);
        	}
        	spare_inven_2 = new ItemStack[27];
        	for (i = 0; i < (player.getInventory().getSize() - 27); i++) {
        		spare_inven_2[i] = player.getInventory().getItem(i + 27);
        	}
        	while ((i - (player.getInventory().getSize() - 27)) < player.getInventory().getArmorContents().length) {
        		spare_inven_2[i] = player.getInventory().getArmorContents()[i - (player.getInventory().getSize() - 27)];
        		i++;
        	}
        	
        	player.getInventory().clear();
        	
        }
        
        if (player.getHealth() >= newHealth) {
        	event.setDamage(player.getHealth() - newHealth);
        } else {
        	if (health < 20) {
        		player.setHealth(health + 1);
        		event.setDamage(1);
        	} else {
        		event.setDamage(0);
                event.setCancelled(true);
        	}
        }

        MineQuest.log("[INFO] " + name + " - " + health + "/" + max_health);

        return false;
    }

	/**
	 * This function will check if the damager in the event passed
	 * has damaged the quester too recently to damage again.
	 * This implements individual cool down for every entity.
	 * 
	 * @param event Event that holds the attacker
	 * @return True if damage should be cancelled
	 */
	private boolean checkDamage(int id) {
		Calendar now = Calendar.getInstance();
		int i;
		
		for (i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				if ((now.getTimeInMillis() - times.get(i)) > 500) {
					times.set(i, now.getTimeInMillis());
					return false;
				} else {
					return true;
				}
			}
		}
		
		ids.add(id);
		times.add(now.getTimeInMillis());
		
		return false;
	}

	/**
	 * Returns true if MineQuest is enabled for this
	 * Quester.
	 * 
	 * @deprecated
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Returns true if Quester is poisoned currently.
	 * 
	 * @return
	 */
	public boolean isPoisoned() {
		return (poison_timer > 0);
	}

	/**
	 * Called whenever a Quester's character goes up
	 * a level. Handles experience changes and health
	 * additions.
	 */
	private void levelUp() {
		Random generator = new Random();
		int add_health = generator.nextInt(3) + 1;
		level++;
		exp -= (400 * level);
		max_health += add_health;
		health += add_health;
		
		getPlayer().sendMessage("Congratulations on reaching character level " + level);
	}

	/**
	 * Display list of abilities that Quester has.
	 */
	public void listAbil() {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].listAbil();
		}
	}

	/**
	 * Called every time a player moves. It makes sure that players
	 * respawn in the proper town. Handles any poison damage required
	 * and updates the health to fix any inconsistencies that may have
	 * arisen.
	 * 
	 * @param from Quester's old Location
	 * @param to Quester's new Location
	 */
	public void move(Location from, Location to) {
		if ((respawn_flag) || ((health <= 0) && (player.getHealth() == 20))) {
			health = max_health;
//			updateHealth(player);
			player.teleportTo(MineQuest.getTown(last).getSpawn());
			respawn_flag = false;
			return;
		}
		checkEquip(player);
		
		updateHealth(player);
		
		Town last_town = MineQuest.getNearestTown(to);
		if (last_town != null) {
			last = last_town.getName();
		} else {
			last = null;
		}
		if (last != null) {
			MineQuest.getSQLServer().update("UPDATE questers SET last_town='" + last + "'");
		}
		
		if (poison_timer > 0) {
			distance += MineQuest.distance(from, to);
		}
		
		while ((distance > 5) && (poison_timer > 0)) {
			distance -= 5;
			poison_timer -= 1;
			setHealth(getHealth() - 1);
		}
	}

	/**
	 * Called each time the player is poisoned.
	 * Updates the poison counter appropriately.
	 */
	public void poison() {
		poison_timer += 10;
	}

	/**
	 * Saves any changes in the Quester to the MySQL
	 * Database.
	 */
	public void save() {
			int i;
			
		if (MineQuest.getSQLServer().update("UPDATE questers SET exp='" + exp + "', level='" + level + "', health='" 
				+ health + "', max_health='" + max_health + "', enabled='" + (enabled?1:0) 
				+ "' WHERE name='" + name + "'") == -1) {
			player.sendMessage("May not have saved properly, please try again");
		}
		for (i = 0; i < classes.length; i++) {
			classes[i].save();
		}
		enabled = false;
	}

	/**
	 * Sends a message to the Quester's Player.
	 * 
	 * @param string Message
	 */
	public void sendMessage(String string) {
		if (player != null) {
			player.sendMessage(string);
		} else {
			MineQuest.log("[WARNING] Quester " + name + " doesn't have player, not logged in?");
		}
	}

	/**
	 * Sets the Cubes of the Quester.
	 * 
	 * @param d New Cubes
	 */
	public void setCubes(double d) {
		cubes = d;
	}

	/**
	 * Sets the Health of the Quester.
	 * 
	 * @param i New Health
	 */
	public void setHealth(int i) {
		int newValue;

		if (i > max_health) {
			i = max_health;
		}
		health = i;
		
		updateHealth(player);
	}

	/**
	 * Sets the reference to the Player.
	 * 
	 * @param player New Reference
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setRespawn(boolean enabled) {
		respawn_flag = enabled;
	}

	/**
	 * Sets the last town that the Quester was near.
	 * 
	 * @param town Last Town
	 */
	public void setTown(Town town) {
		last = town.getName();
		MineQuest.getSQLServer().update("UPDATE players SET town='" + town.getName() + "' WHERE name='" + name + "'");
	}

	/**
	 * Called whenever a Quester is teleported to check for
	 * respawning.
	 * 
	 * @param event Teleport Event
	 */
	public void teleport(PlayerMoveEvent event) {
//		if ((health <= 0) && (player.getHealth() == 20)) {
//			event.setTo(MineQuest.getTown(last).getSpawn());
//			setRespawn(true);
//		}
	}

	/**
	 * Unbinds the Item that the Quester is holding
	 * from all abilities.
	 * 
	 * @param itemInHand
	 */
	public void unBind(ItemStack itemInHand) {
		int i;
		
		for (i = 0; i < classes.length; i++) {
			classes[i].unBind(itemInHand, true);
			classes[i].unBind(itemInHand, false);
		}
	}

	/**
	 * Loads all of the Quester's status from the MySQL
	 * database.
	 */
	public void update() {
		ResultSet results;
		String split[];
		int i;
		
		try {
			results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + name + "'");
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
			max_health = results.getInt("max_health");
			enabled = results.getInt("enabled") > 0;
			
			cubes = results.getDouble("cubes");
			chests = new ChestSet();
			last = results.getString("last_town");
		} catch (SQLException e) {
			System.out.println("Issue getting parameters");
			e.printStackTrace();
			return;
		}
		
		classes = new SkillClass[split.length];
		for (i = 0; i < split.length; i++) {
			classes[i] = SkillClass.newClass(this, split[i]);
		}
		damage_timer = 0;
	}

	/**
	 * Sets the reference to the Player and updates
	 * the Quester's status information from the db.
	 * @param player
	 */
	public void update(Player player) {
		this.player = player;
		update();
		updateHealth(player);
	}

	/**
	 * Updates the Players health to show a percentage of
	 * the Questers health.
	 * 
	 * @param player
	 */
	public void updateHealth(Player player) {
		int newValue;
		
		newValue = 20 * health / max_health;
		
		if ((newValue == 0) && (health > 0)) {
			newValue++;
		}

		if (newValue < 0) {
			newValue = 0;
		}
		
		player.setHealth(newValue);
	}
	
	public void setQuest(Quest quest) {
		this.quest = quest;
	}
	
	public void clearQuest() {
		this.quest = null;
	}
	
	public boolean inQuest() {
		return quest != null;
	}
}
