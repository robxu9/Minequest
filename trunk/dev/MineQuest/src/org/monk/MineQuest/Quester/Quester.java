/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
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
package org.monk.MineQuest.Quester;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Ability.AbilityBinder;
import org.monk.MineQuest.Ability.PassiveAbility;
import org.monk.MineQuest.Event.Absolute.EntityTeleportEvent;
import org.monk.MineQuest.Mob.MQMob;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quester.SkillClass.CombatClass;
import org.monk.MineQuest.Quester.SkillClass.DefendingClass;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Store.NPCSignShop;
import org.monk.MineQuest.World.Property;
import org.monk.MineQuest.World.Town;

/**
 * This is a wrapper around bukkit's player class.
 * It manages the health for the player as well as
 * any other Player specific MineQuest data.
 * 
 * @author jmonk
 */
public class Quester {
	protected List<QuestProspect> available;
	protected Location before_quest;
	protected ChestSet chests;
	protected int class_exp;
	protected List<SkillClass> classes;
	protected List<QuestProspect> completed;
	protected double cubes;
	protected boolean debug;
	protected double distance;
	protected boolean enabled;
	protected int exp;
	protected int health;
	protected CreatureType[] kills;
	protected String last;
	protected int level;
	protected int max_health;
	protected String name;
	protected Party npcParty;
	protected Party party;
	protected HumanEntity player;
	protected int poison_timer;
	protected Quest quest;
	protected int rep;
	protected Map<Material, Integer> destroyed;
	private List<Long> times = new ArrayList<Long>();
	private List<Integer> ids = new ArrayList<Integer>();

	public Quester() {
	}

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
		npcParty = new Party();
		update();
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
		npcParty = new Party();
		update();
		distance = 0;
	}

	/**
	 * Binds an Ability to left of right click of the item
	 * in the players hand.
	 * 
	 * @param player Player
	 * @param name Name of Ability
	 * @param lr 1 for left, 0 for right
	 */
	public void addBinder(String ability, int item) {
		SkillClass my_class = getClassFromAbil(ability);
		if (my_class == null) {
			sendMessage("You do not have an ability named " + ability);
			return;
		}
		
		for (SkillClass skill : classes) {
			skill.unBind(player.getItemInHand());
		}
		
		Ability abil = new AbilityBinder(ability, item);
		abil.bind(this, player.getItemInHand());
		my_class.addAbility(abil);
		
		sendMessage("Binder added");
	}

	protected void addBinder(String ability, int item, ItemStack item_hand) {
		SkillClass my_class = getClassFromAbil(ability);
		if (my_class == null) {
			return;
		}

		for (SkillClass skill : classes) {
			if (skill != null) {
				skill.unBind(item_hand);
			}
		}
		
		Ability abil = new AbilityBinder(ability, item);
		my_class.addAbility(abil);
		
		abil.silentBind(this, item_hand);
	}

	public void addClass(String name) {
		if (getClass(name) != null) {
			sendMessage("You already have the class " + name);
			return;
		}
		if (getCombatClasses().size() == MineQuest.getMaxClasses()) {
			sendMessage("You do not have space for any more combat classes");
			return;
		}
		String names[] = new String[] {"WarMage", "PeaceMage", "Archer", "Warrior"};
		boolean flag = false;
		for (String clazz : names) {
			if (name.equals(clazz)) {
				flag = true;
			}
		}
		if (!flag) {
			sendMessage(name + " is not a valid class currently");
		}
		
		save();
		try {
			ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + this.name + "'");
			results.next();
			String classes = results.getString("classes");
			
			classes = classes + ", " + name;
			
			MineQuest.getSQLServer().update("UPDATE questers SET classes='" + classes + "' WHERE name='" + this.name + "'");
			
			createClass(name, MineQuest.getNextAbilId());
			sendMessage(name + " class added!");
		} catch (SQLException e) {
			MineQuest.log("Unable to update class list");
		}
		update();
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
	
	private void addKill(CreatureType kill) {
		CreatureType[] new_kills = new CreatureType[kills.length + 1];
		int i;
		
		for (i = 0; i < kills.length; i++) {
			new_kills[i] = kills[i];
		}
		new_kills[i] = kill;
		
		kills = new_kills;
	}
	
	public void addKill(MQMob mqMob) {
		LivingEntity monster = mqMob.getMonster();
		
		String name = monster.getClass().getName();
		name = name.replace('.', '/');
		if (name.split("/").length > 0) {
			String type = name.split("/")[name.split("/").length - 1];
			type = type.replace("Craft", "");
			if (CreatureType.fromName(type) != null) {
				addKill(CreatureType.fromName(type));
			}
		}
	}

	public void addNPC(NPCQuester quester) {
		npcParty.addQuester(quester);
		if (!equals(quester.getFollow())) {
			quester.setFollow(this);
		}
	}

	public void addQuestAvailable(QuestProspect quest) {
		if (isAvailable(quest)) return;
		if (quest.equals("")) {
			return;
		}
		available.add(quest);
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_quests (type, file) VALUES('A', '" 
				+ quest.getFile() + "')");
		sendMessage("You now have access to the quest " + quest.getName());
	}

	public void addQuestCompleted(QuestProspect quest) {
		if (isCompleted(quest)) return;
		completed.add(quest);
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_quests (type, file) VALUES('C', '" 
				+ quest.getFile() + "')");
	}

	/**
	 * Called whenever a Quester attacks any other entity.
	 * 
	 * @param entity
	 * @param event
	 */
	public void attackEntity(Entity entity, EntityDamageByEntityEvent event) {
		if (checkItemInHand()) return;
		if (!(entity instanceof LivingEntity)) return;

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				if (entity instanceof LivingEntity) {
					if (skill instanceof CombatClass) {
						skill.callAbility(entity);
						expGain(MineQuest.getCastAbilityExp());
					} else {
						skill.callAbility(entity);
					}
					break;
				}
			}
		}
		
		if (npcParty != null) {
			boolean flag = true;
			if (entity instanceof Player) {
				for (Quester quester : npcParty.getQuesters()) {
					if (quester.getName().equals(((Player)entity).getName())) {
						flag = false;
						break;
					}
				}
			}
			if (flag) {
				for (Quester quester : npcParty.getQuesterArray()) {
					NPCQuester npc = (NPCQuester)quester;
					
					npc.questerAttack((LivingEntity)entity);
				}
			}
		}

		if (entity instanceof LivingEntity) {
			for (SkillClass skill : classes) {
				if (skill.isClassItem(player.getItemInHand())) {
					if (skill instanceof CombatClass) {
						((CombatClass)skill).attack((LivingEntity)entity, event);
						expGain(MineQuest.getExpClassDamage());
						return;
					} else {
						if (getClass("Warrior") != null) {
							((CombatClass)getClass("Warrior")).attack((LivingEntity)entity, event);
							expGain(MineQuest.getExpClassDamage() / 2);
						}
						return;
					}
				}
			}
			event.setDamage(event.getDamage() / 2);
			if (getClass("Warrior") != null) {
				((CombatClass)getClass("Warrior")).attack((LivingEntity)entity, event);
				expGain(MineQuest.getExpClassDamage() / 3);
			}
			return;
		}
	}

	/**
	 * Binds an Ability to click of the item
	 * in the players hand.
	 * 
	 * @param player Player
	 * @param name Name of Ability
	 */
	public void bind(String name) {
		for (SkillClass skill : classes) {
			skill.unBind(player.getItemInHand());
		}

		for (SkillClass skill : classes) {
			if (skill.getAbility(name) != null) {
				if (skill.getAbility(name) instanceof PassiveAbility) {
					sendMessage("Passive Abilities cannot be bound, must be enabled");
				} else {
					skill.getAbility(name).bind(this, player.getItemInHand());
				}
				return;
			}
		}
		sendMessage(name + " is not a valid ability");
		return;
	}
	
	public void bind(String ability, ItemStack itemStack) {
		for (SkillClass skill : classes) {
			skill.silentUnBind(itemStack);
		}

		for (SkillClass skill : classes) {
			if (skill.getAbility(ability) != null) {
				skill.getAbility(ability).bind(this, itemStack);
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
			1,4,14,15,16,
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
			if (block.getTypeId() == miner[i]) {
				return 0;
			}
		}
		for (i = 0; i < lumber.length; i++) {
			if (block.getTypeId() == lumber[i]) {
				return 1;
			}
		}
		for (i = 0; i < digger.length; i++) {
			if (block.getTypeId() == digger[i]) {
				return 2;
			}
		}
		for (i = 0; i < farmer.length; i++) {
			if (block.getTypeId() == farmer[i]) {
				return 3;
			}
		}
		return 4;
	}
	
	public void callAbility() {
		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())){
				skill.callAbility();
				return;
			}
		}
	}
	
	/**
	 * Casts a left click bound ability on the given block.
	 * 
	 * @param block
	 */
	public void callAbility(Block block) {
		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())){
				skill.callAbility(block);
				return;
			}
		}
	}
	
	/**
	 * Casts a left click bound ability on the given entity
	 * 
	 * @param entity
	 */
	public void callAbility(Entity entity) {
		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())){
				skill.callAbility(entity);
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

	public boolean canEdit(Block block) {
		Town town = null;
		if (block != null) {
			town = MineQuest.getTown(block.getLocation());
		} else {
			return true;
		}

		if (inQuest()) {
			if (!quest.canEdit(this, block)) {
				return false;
			}
			if (!player.getWorld().getName().equals(MineQuest.getSServer().getWorlds().get(0).getName())) {
				return true;
			}
		}
		
		if (!MineQuest.isTownEnabled()) {
			return true;
		}
		
		if (!MineQuest.isTownProtect()) {
			return true;
		}

		if (town != null) {
			Property prop = town.getProperty(block.getLocation());
			
			for (NPCSignShop shop : town.getStores()) {
				if (shop.parseClick(this, block)) {
					return false;
				}
			}
			
			if (prop != null) {
				if (prop.canEdit(this)) {
					return true;
				} else {
					sendMessage("You are not authorized to modify this property - please get the proper authorization");
					dropRep(20);
					return false;
				}
			} else {
				prop = town.getTownProperty();
				if (prop.canEdit(this)) {
					return true;
				} else {
					sendMessage("You are not authorized to modify town - please get the proper authorization");
					dropRep(10);
					return false;
				}
			}	
		}

		return true;
	}
	
	private boolean checkDamage(DamageCause cause) {
        if (cause == DamageCause.FIRE) {
        	return checkDamage(24040);
        } else if (cause == DamageCause.FIRE_TICK) {
        	return checkDamage(24041);
        } else if (cause == DamageCause.SUFFOCATION) {
        	return checkDamage(24042);
        } else if (cause == DamageCause.DROWNING) {
        	return checkDamage(24043);
        } else if (cause == DamageCause.LAVA) {
        	return checkDamage(24044);
        } else {
        	return checkDamage(24045);
        }
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
	    int delay = 500;

    	if ((id == 24040) || (id == 24041)) {
    		if ((getAbility("Fire Resistance") != null) && getAbility("Fire Resistance").isEnabled()) {
	    		delay = 1000;
	    	}
	    }
	    
		for (i = 0; i < ids.size(); i++) {
			if (ids.get(i) == id) {
				if ((now.getTimeInMillis() - times.get(i)) > delay) {
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
	 * Checks if a Quester can use all of the equipment
	 * that they are wearing. It checks with each class
	 * individually, and they all handle their own removal.
	 * 
	 * @param player
	 */
	public void checkEquip() {
		if (!enabled) return;
		
		PlayerInventory inven = player.getInventory();
		
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
		PlayerInventory inven = player.getInventory();
		ItemStack item = player.getItemInHand();

		for (SkillClass skill : classes) {
			if (!skill.canUse(item)) {
				if (inven.firstEmpty() != -1) {
					inven.addItem(item);
				} else {
					player.getWorld().dropItem(player.getLocation(), item);
				}
				
				inven.setItemInHand(null);
				if (player instanceof Player) {
					((Player)player).sendMessage("You are not high enough level to use that weapon");
				}
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
//		if ((player.getItemInHand().getTypeId() == 261) || (player.getItemInHand().getTypeId() == 332)) {
//			return false;
//		}

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clearKills() {
		if (MineQuest.isTrackingKills()) {
			Map<CreatureType, Integer> kill_map = new HashMap<CreatureType, Integer>();
			Map<Material, Integer> destroyed = new HashMap<Material, Integer>();
			
			ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_kills");
			
			try {
				while (results.next()) {
					if (CreatureType.fromName(results.getString("name")) != null) {
						kill_map.put(CreatureType.fromName(results.getString("name")), results.getInt("count"));
					}
					if (Material.getMaterial(results.getString("name")) != null) {
						if (destroyed.get(results.getString("name")) == null) {
							destroyed.put(Material.getMaterial(results.getString("name")), results.getInt("count"));
						} else {
							destroyed.put(Material.getMaterial(results.getString("name")), results.getInt("count") + destroyed.get(results.getString("name")));
						}
					}
				}
			} catch (Exception e) {
				MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + "_kills" + "(name VARCHAR(30), count INT)");
			}
			
			for (CreatureType creature : kills) {
				if (creature != null) {
					if (kill_map.get(creature) == null) {
						kill_map.put(creature, 1);
					} else {
						kill_map.put(creature, kill_map.get(creature) + 1);
					}
				}
			}
			
			MineQuest.getSQLServer().update("DELETE FROM " + name + "_kills");
			
			for (CreatureType creature : kill_map.keySet()) {
				MineQuest.getSQLServer().update(
						"INSERT INTO " + name + "_kills (name, count) VALUES('"
								+ creature.getName() + "', '"
								+ kill_map.get(creature) + "')");
			}
			
			for (Material material : destroyed.keySet()) {
				MineQuest.getSQLServer().update(
						"INSERT INTO " + name + "_kills (name, count) VALUES('"
								+ material.name() + "', '"
								+ destroyed.get(material) + "')");
			}
		}
		
		kills = new CreatureType[0];
	}
	
	public void clearDestroyed() {
		if (MineQuest.isTrackingDestroy()) {
			Map<CreatureType, Integer> kill_map = new HashMap<CreatureType, Integer>();
			Map<Material, Integer> destroyed = new HashMap<Material, Integer>();
			
			ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_kills");
			
			try {
				while (results.next()) {
					if (CreatureType.fromName(results.getString("name")) != null) {
						kill_map.put(CreatureType.fromName(results.getString("name")), results.getInt("count"));
					}
					if (Material.getMaterial(results.getString("name")) != null) {
						if (destroyed.get(results.getString("name")) == null) {
							destroyed.put(Material.getMaterial(results.getString("name")), results.getInt("count"));
						} else {
							destroyed.put(Material.getMaterial(results.getString("name")), results.getInt("count") + destroyed.get(results.getString("name")));
						}
					}
				}
			} catch (Exception e) {
				MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + "_kills" + "(name VARCHAR(30), count INT)");
			}
			
			MineQuest.getSQLServer().update("DELETE FROM " + name + "_kills");
			
			for (CreatureType creature : kill_map.keySet()) {
				MineQuest.getSQLServer().update(
						"INSERT INTO " + name + "_kills (name, count) VALUES('"
								+ creature.getName() + "', '"
								+ kill_map.get(creature) + "')");
			}
			
			for (Material material : destroyed.keySet()) {
				MineQuest.getSQLServer().update(
						"INSERT INTO " + name + "_kills (name, count) VALUES('"
								+ material.name() + "', '"
								+ destroyed.get(material) + "')");
			}
		}
		
		destroyed.clear();
	}
	
	public void clearQuest(boolean reset) {
		this.quest = null;
		poison_timer = 0;
		if (reset) {
			MineQuest.getEventParser().addEvent(new EntityTeleportEvent(5000, this, before_quest.getWorld().getSpawnLocation()));
			MineQuest.getEventParser().addEvent(new EntityTeleportEvent(6000, this, before_quest));
		}
		kills = new CreatureType[0];
	}

	public void completeQuest(QuestProspect quest) {
		addQuestCompleted(quest);
		if (!quest.isRepeatable()) {
			remQuestAvailable(quest);
		}
	}

    /**
	 * Creates database entry for this quester with starting
	 * classes and health.
	 */
	public void create() {
		int num;
		List<String> class_names = new ArrayList<String>();
		class_names.add("Miner");
		class_names.add("Lumberjack");
		class_names.add("Digger");
		class_names.add("Farmer");
		if (MineQuest.getMaxClasses() >= 4) {
			class_names.add("Warrior");
			class_names.add("Archer");
			class_names.add("WarMage");
			class_names.add("PeaceMage");
			MineQuest.log("Adding all classes");
		}
		
		String update_string = "INSERT INTO questers (name, selected_chest, cubes, exp, level, last_town, classes, health, max_health) VALUES('"
			+ name + "', '" + name + "', '500000', '0', '0', 'Bitville', '";
		update_string = update_string + class_names.get(0);
		for (String name : class_names) {
			if (!name.equals(class_names.get(0))) {
				update_string = update_string + ", " + name;
			}
		}
		update_string = update_string + "', '" + MineQuest.getStartingHealth() + "', '" + MineQuest.getStartingHealth() + "')";

		MineQuest.getSQLServer().update(update_string);
		
		num = MineQuest.getNextAbilId();
		
		for (String clazz : class_names) {
			createClass(clazz, num++);
		}
		
		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + " (abil VARCHAR(30), bind int, bind_2 int)");
	}

	public void createClass(String clazz, int abil_list_id) {
		String update_string = "INSERT INTO classes (name, class, exp, level, abil_list_id) VALUES('"
			+ name + "', '" + clazz + "', '0', '0', '" + abil_list_id + "')";

		MineQuest.getSQLServer().update(update_string);
		MineQuest.getSQLServer().update("INSERT INTO abilities (abil_list_id) VALUES('" + abil_list_id + "')");
	}

	public void createParty() {
		party = new Party();
		party.addQuester(this);
	}

	/**
	 * Cures all poison for the Quester.
	 */
	public void curePoison() {
		poison_timer = 0;
	}

	public void damage(int i) {
		setHealth(getHealth() - i);
	}

	/**
	 * Called anytime a Player is destroying a block. Checks to
	 * see if it is bound to any abilities then attributes experience
	 * to given class if required.
	 * 
	 * @param event being destroyed
	 * @return false
	 */
	public void damageBlock(BlockDamageEvent event) {
		if (!enabled) return;

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				skill.blockDamage(event);
				return;
			}
		}
		
		return;
	}

	public void debug() {
		debug = !debug;
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
		int amount;
		if ((classes != null) && (classes.get(0) != null) && (classes.get(0).getGenerator() != null)) {
			amount = classes.get(0).getGenerator().nextInt(10);
		} else {
			amount = 5;
		}
		int levelAdj = MineQuest.getAdjustment();
		if (levelAdj == 0) {
			levelAdj = 1;
		} else {
			amount *= levelAdj * 1;
		}
		
		amount /= 2;
		
		if ((event.getDamager() != null) && 
				(!(event.getDamager() instanceof Player) && 
				 checkDamage(event.getDamager().getEntityId()))) {
            event.setCancelled(true);
            return;
        }
		
		if (entity instanceof LivingEntity) {
			if (entity != null) {
				if (MineQuest.getMob((LivingEntity)entity) != null) {
					amount = MineQuest.getMob((LivingEntity)entity).attack(amount, player);
				}
			}
		}
		
		if (npcParty != null) {
			for (Quester quester : npcParty.getQuesterArray()) {
				NPCQuester npc = (NPCQuester)quester;
				
				npc.questerAttack((LivingEntity)entity);
			}
		}
		
		int sum = 0;
		
		if (classes != null) {
			for (SkillClass sclass : classes) {
				if (entity instanceof LivingEntity) {
					if (sclass instanceof DefendingClass) {
						sum += ((DefendingClass)sclass).defend((LivingEntity)entity, amount);
					}
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

	public void destroyBlock(BlockBreakEvent event) {
		if (!enabled) return;

		for (SkillClass skill : classes) {
			if (skill.isAbilityItem(player.getItemInHand())) {
				skill.blockBreak(event);
				return;
			}
		}
		
		for (SkillClass skill : classes) {
			if (skill.isClassItem(player.getItemInHand())) {
				skill.blockBreak(event);
				expGain(MineQuest.getDestroyBlockExp());
				return;
			}
		}
		if (destroyed.get(event.getBlock().getType()) == null) {
			destroyed.put(event.getBlock().getType(), 1);
		} else {
			destroyed.put(event.getBlock().getType(), destroyed.get(event.getBlock().getType()) + 1);
		}
		
		switch (blockToClass(event.getBlock())) {
		case 0: // Miner
			getClass("Miner").blockBreak(event);
			expGain(MineQuest.getDestroyBlockExp());
			return;
		case 1: // Lumberjack
			getClass("Lumberjack").blockBreak(event);
			expGain(MineQuest.getDestroyBlockExp());
			return;
		case 2: // Digger
			getClass("Digger").blockBreak(event);
			expGain(MineQuest.getDestroyBlockExp());
			return;
		case 3: // Farmer
			getClass("Farmer").blockBreak(event);
			expGain(MineQuest.getDestroyBlockExp());
			return;
		}
		
		return;
	}

	/**
	 * Disable an ability with the given name.
	 * 
	 * @param string Name of Ability
	 */
	public void disableabil(String string) {
		for (SkillClass skill : classes) {
			if (skill.getAbility(string) != null) {
				skill.getAbility(string).disable();
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
		for (SkillClass skill : classes) {
			if (skill.getAbility(string) != null) {
				if (!skill.getAbility(string).isEnabled()) {
					skill.getAbility(string).enable(this);
				} else {
					sendMessage(string + " already enabled!");
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Quester) {
			return name.equals(((Quester)obj).getName());
		}
		if (obj instanceof HumanEntity) {
			return name.equals(((HumanEntity)obj).getName());
		}
		if (obj instanceof String) {
			return name.equals(obj);
		}
		return super.equals(obj);
	}
	
	public void expClassGain(int class_exp) {
		this.class_exp = class_exp;
	}

	/**
	 * Adds i experience to Quester and checks for level
	 * up.
	 * 
	 * @param i Experience to Add
	 */
	public void expGain(int i) {
		exp += i;
		if (exp > 400 * (level + 1)) {
			levelUp();
		}
	}

    public Ability getAbility(String ability) {
		for (SkillClass skill : classes) {
			if (skill != null) {
				if (skill.getAbility(ability) != null) {
					return skill.getAbility(ability);
				}
			}
		}
		return null;
	}

	public List<QuestProspect> getAvailableQuests() {
		return available;
	}

	/**
	 * Gets the ChestSet for given player
	 * 
	 * @return ChestSet
	 */
	public ChestSet getChestSet() {
		return chests;
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
		for (SkillClass skill : classes) {
			if (skill.getType().equalsIgnoreCase(string)) {
				return skill;
			}
		}
		
		return null;
	}

	/**
	 * Gets a list of SkillClasses that this Quester has.
	 * @return
	 */
	public List<SkillClass> getClasses() {
		return classes;
	}

	public int getClassExp() {
		return class_exp;
	}

	public SkillClass getClassFromAbil(String ability) {
		for (SkillClass skill : classes) {
			if (skill != null) {
				if (skill.getAbility(ability) != null) {
					return skill;
				}
			}
		}
		return null;
	}

	public List<SkillClass> getCombatClasses() {
		List<SkillClass> ret = new ArrayList<SkillClass>();
		for (SkillClass skill : classes) {
			if (skill instanceof CombatClass) {
				ret.add(skill);
			}
		}
		
		return ret;
	}

	public List<QuestProspect> getCompletedQuests() {
		return completed;
	}

	/**
	 * Returns amount of cubes the Quester has.
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public double getCubes() {
		if (MineQuest.getIsConomyOn()) {
			return MineQuest.getIConomy().getBank().getAccount(name).getBalance();
		}
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

	public CreatureType[] getKills() {
		return kills;
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

	public Party getParty() {
		return party;
	}

	/**
	 * Returns player that the Quester wraps.
	 * 
	 * @return
	 */
	public Player getPlayer() {
		if (player instanceof Player) {
			return (Player)player;
		} else {
			return null;
		}
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	private QuestProspect getQuestProspect(String string) {
		for (QuestProspect qp : available) {
			if (qp.equals(string)) {
				return qp;
			}
		}
		for (QuestProspect qp : completed) {
			if (qp.equals(string)) {
				return qp;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns last town player was near.
	 * 
	 * @return
	 */
	public Town getTown() {
		return MineQuest.getTown(last);
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
		if (event.isCancelled()) return false;
		if (player == null) return false;
		int newHealth;
                
        if (checkDamage(event.getCause())) {
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
        
        if (player.getHealth() >= newHealth) {
        	event.setDamage(player.getHealth() - newHealth);
        } else {
        	if (player.getHealth() < 20) {
        		player.setHealth(health + 1);
        		event.setDamage(1);
        		if (MineQuest.everyHitSignal()) {
	        		Random random = new Random();
	        		((CraftPlayer)player).getHandle().world.makeSound(
	        				((CraftPlayer)player).getHandle(), "random.hurt", 1.0F, 
	        				(random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        		}
        	} else {
        		if (MineQuest.everyHitSignal()) {
	        		Random random = new Random();
	        		((CraftPlayer)player).getHandle().world.makeSound(
	        				((CraftPlayer)player).getHandle(), "random.hurt", 1.0F, 
	        				(random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        		}
        		event.setDamage(0);
        	}
        }

        MineQuest.log("[INFO] " + name + " - " + health + "/" + max_health);

        return false;
    }
	
	public boolean healthIncrease(PlayerInteractEvent event) {
		if (event.getItem() == null) return false;
		Material type = event.getItem().getType();
		
		switch (type) {
		case GRILLED_PORK:
			health += 8;
			break;
		case PORK:
			health += 3;
			break;
		case MUSHROOM_SOUP:
			health += 10;
			break;
		case BREAD:
			health += 5;
			break;
		case CAKE:
			health += 3;
			break;
		case GOLDEN_APPLE:
			health = max_health;
			break;
		case APPLE:
			health += (int)(.15 * max_health);
			break;
		case RAW_FISH:
			health += 2;
			break;
		case COOKED_FISH:
			health += 5;
			break;
		default:
			return false;
		}
		
		event.setCancelled(true);
		if (!(this instanceof NPCQuester)) {
			if (getPlayer().getItemInHand().getAmount() == 1) {
				getPlayer().setItemInHand(null);
			} else {
				getPlayer().getItemInHand().setAmount(
						getPlayer().getItemInHand().getAmount() - 1);
			}
		}
		
		if (health > max_health) health = max_health;
		
		updateHealth();
		
		return true;
	}
	
	public boolean inQuest() {
		return quest != null;
	}

	public boolean isAvailable(QuestProspect quest) {
		for (QuestProspect qp : available) {
			if (qp.equals(quest)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAvailable(String quest) {
		for (QuestProspect qp : available) {
			if (qp.equals(quest)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCompleted(QuestProspect quest) {
		for (QuestProspect qp : completed) {
			if (qp.equals(quest)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCompleted(String quest) {
		for (QuestProspect qp : completed) {
			if (qp.equals(quest)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDebug() {
		return debug;
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
		for(SkillClass skill : classes) {
			skill.listAbil();
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
		checkEquip();
		
		updateHealth();
		
		if (MineQuest.isTownEnabled()) {
			Town last_town = MineQuest.getNearestTown(to);
			if (last_town != null) {
				if (!last_town.getName().equals(last)) {
					last = last_town.getName();
					MineQuest.getSQLServer().update("UPDATE questers SET last_town='" + last + "'");
				}
			} else {
				last = null;
			}
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
		sendMessage("Poisoned!");
		poison_timer += 10;
	}

	public void regroup() {
		for (Quester quester : npcParty.getQuesterArray()) {
			((NPCQuester)quester).setTarget((LivingEntity)null);
		}

		sendMessage("Regrouping Mercenaries!");
	}

	public void remNPC(NPCQuester quester) {
		npcParty.remQuester(quester);
		quester.setMode(NPCMode.GENERIC);
		quester.setFollow(null);
	}

	public void remQuestAvailable(QuestProspect quest) {
		available.remove(quest);
		MineQuest.getSQLServer().update("DELETE FROM " + name + 
				"_quests WHERE type='A' AND file='" + quest.getFile() + "'");
	}
	
	public void remQuestComplete(QuestProspect quest) {
		completed.remove(quest);
		MineQuest.getSQLServer().update("DELETE FROM " + name + 
				"_quests WHERE type='C' AND file='" + quest.getFile() + "'");
	}
	
	public void respawn(PlayerRespawnEvent event) {
		health = max_health;
		poison_timer = 0;
		if (quest != null) {
			event.setRespawnLocation(quest.getSpawn());
		} else {
			if (MineQuest.getTown(last) != null) {
				event.setRespawnLocation(MineQuest.getTown(last).getSpawn());
			}
		}
		return;
	}
	
	public void rightClick(Block block) {
		for (SkillClass skill : classes) {
			if (skill.isClassItem(player.getItemInHand())) {
				skill.rightClick(block);
				expGain(3);
				return;
			}
		}
		
		switch (blockToClass(block)) {
		case 0: // Miner
			getClass("Miner").rightClick(block);
			return;
		case 1: // Lumberjack
			getClass("Lumberjack").rightClick(block);
			return;
		case 2: // Digger
			getClass("Digger").rightClick(block);
			return;
		case 3: // Farmer
			getClass("Farmer").rightClick(block);
			return;
		default:
			break;
		}
		
		return;
	}
	
	/**
	 * Saves any changes in the Quester to the MySQL
	 * Database.
	 */
	public void save() {
		if (MineQuest.getSQLServer().update("UPDATE questers SET exp='" + exp + "', level='" + level + "', health='" 
				+ health + "', max_health='" + max_health + "', enabled='" + 1
				+ "', cubes='" + (long)cubes + "' WHERE name='" + name + "'") == -1) {
			if (player instanceof Player) {
				((Player)player).sendMessage("May not have saved properly, please try again");
			} else {
				MineQuest.log("May not have saved properly, please try again");
			}
		}
		for(SkillClass skill : classes) {
			skill.save();
		}
		enabled = false;
		
		if (npcParty != null) {
			for (Quester quester : npcParty.getQuesters()) {
				((NPCQuester)quester).setMode(NPCMode.PARTY_STAND);
			}
		}
		
		clearKills();
		clearDestroyed();
	}
	
	/**
	 * Sends a message to the Quester's Player.
	 * 
	 * @param string Message
	 */
	public void sendMessage(String string) {
		if ((player != null) && (player instanceof Player)) {
			((Player)player).sendMessage(string);
		} else {
			MineQuest.log("[WARNING] Quester " + name + " doesn't have player, not logged in?");
		}
	}
	
	/**
	 * Sets the Cubes of the Quester.
	 * 
	 * @param d New Cubes
	 */
	@SuppressWarnings("static-access")
	public void setCubes(double d) {
		cubes = d;
		if (MineQuest.getIsConomyOn()) {
			MineQuest.getIConomy().getBank().getAccount(name).setBalance(d);
		}
	}
	
	/**
	 * Sets the Health of the Quester.
	 * 
	 * @param i New Health
	 */
	public void setHealth(int i) {
		if (i < health) {
    		if (MineQuest.everyHitSignal()) {
        		Random random = new Random();
        		((CraftPlayer)player).getHandle().world.makeSound(
        				((CraftPlayer)player).getHandle(), "random.hurt", 1.0F, 
        				(random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    		}
		}
		if (i > max_health) {
			i = max_health;
		}
		health = i;
		
		updateHealth();
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
	
	/**
	 * Sets the reference to the Player.
	 * 
	 * @param player New Reference
	 */
	public void setPlayer(HumanEntity player) {
		if (player == null) {
			if (party != null) party.remQuester(this);
		}
		this.player = player;
	}
	
	public void setQuest(Quest quest, World world) {
		this.quest = quest;
		
		before_quest = player.getLocation();
		
		if (!world.getName().equals(player.getWorld().getName())) {
			player.teleport(world.getSpawnLocation());
		}
		clearKills();
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
	
	public void spendClassExp(String type, int amount) {
		if (amount > class_exp) {
			sendMessage("You only have " + class_exp + " available");
			return;
		}
		if (getClass(type) == null) {
			sendMessage(type + " is not a valid class for you");
			return;
		}
		
		class_exp -= amount;
		
		getClass(type).expAdd(amount);
		sendMessage(amount + " experience spent on " + type);
	}
	
	public void startQuest(String string) {
		if (!isAvailable(string)) {
			sendMessage("You have no quest named " + string + " available");
			return;
		}

		if (!getQuestProspect(string).isRepeatable()) {
			for (Quester quester : party.getQuesterArray()) {
				if (quester.isCompleted(string)) {
					sendMessage(quester.getName() + " has already completed " + string);
					quester.sendMessage("You have already completed " + string);
					return;
				}
			}
		}

		MineQuest.addQuest(new Quest(getQuestProspect(string).getFile(), party));
	}
	
	/**
	 * Called whenever a Quester is teleported to check for
	 * respawning.
	 * 
	 * @deprecated
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
		for(SkillClass skill : classes) {
			skill.unBind(itemInHand);
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

		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + " (abil VARCHAR(30), bind int, bind_2 int)");
		
		try {
			results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + name + "'");
			results.next();
		} catch (SQLException e) {
			System.out.println("Issue querying name");
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
			last = results.getString("last_town");
			chests = new ChestSet(this, results.getString("selected_chest"));
		} catch (SQLException e) {
			System.out.println("Issue getting parameters");
			return;
		}
		
		classes = new ArrayList<SkillClass>();
		for (i = 0; i < split.length; i++) {
			classes.add(SkillClass.newClass(this, split[i]));
		}
		
		class_exp = 0;
		
		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + 
				"_quests (type VARCHAR(1), file VARCHAR(30))");
		
		results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_quests WHERE type='C'");
		completed = new ArrayList<QuestProspect>();
		
		try {
			while (results.next()) {
				completed.add(new QuestProspect(results.getString("file")));
			}
		} catch (SQLException e) {
			MineQuest.log("Unable to load completed quests for " + name);
		}
		
		results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_quests WHERE type='A'");
		available = new ArrayList<QuestProspect>();
		
		try {
			while (results.next()) {
				available.add(new QuestProspect(results.getString("file")));
			}
		} catch (SQLException e) {
			MineQuest.log("Unable to load completed quests for " + name);
		}
		
		updateBinds();
		kills = new CreatureType[0];
		destroyed = new HashMap<Material, Integer>();
		
//		if (npcParty != null) {
//			for (Quester quester : npcParty.getQuesterArray()) {
//			}
//		}
		
		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " + name + "_kills" + "(name VARCHAR(30), count INT)");
	}

	/**
	 * Sets the reference to the Player and updates
	 * the Quester's status information from the db.
	 * @param player
	 */
	public void update(Player player) {
		this.player = player;
		update();
		updateHealth();
	}

	public void updateBinds() {
		ResultSet results;
		try {
			results = MineQuest.getSQLServer().query("SELECT * FROM " + name);
			while (results.next()) {
				String name = results.getString("abil");
				if (name.contains(":") && name.split(":")[0].equals("Binder")) {
					if (getAbility(name) == null) {
						addBinder(name.split(":")[1], results.getInt("bind_2"), new ItemStack(results.getInt("bind")));
					}
				} else {
					Ability ability = getAbility(name);
					if (ability != null) {
						ability.silentBind(this, new ItemStack(results.getInt("bind")));
					}
				}
			}
		} catch (SQLException e) {
			MineQuest.log("Could not update binds for quester " + name);
		}
	}

	/**
	 * Updates the Players health to show a percentage of
	 * the Questers health.
	 * 
	 * @param player
	 */
	public void updateHealth() {
		int newValue;
		
		newValue = (int)((20 * (double)health) / max_health);
		
		if ((newValue == 0) && (health > 0)) {
			newValue++;
		}

		if (newValue < 0) {
			newValue = 0;
		}
		
		if (player != null) {
			if (newValue < player.getHealth()) {
				player.damage(player.getHealth() - newValue);
			} else if (newValue != player.getHealth()) {
				player.setHealth(newValue);
			}
		}
	}
	
	public int recalculateHealth() {
		health = MineQuest.getStartingHealth();
		Random generator = new Random();
		
		for (int i = 0; i < level; i++) {
			int add_health = generator.nextInt(3) + 1;
			health += add_health;
		}
		
		for (SkillClass skill : classes) {
			if (skill instanceof CombatClass) {
				CombatClass cclass = (CombatClass)skill;
				for (int i = 0; i < cclass.getLevel(); i++) {
					int size = cclass.getSize();
					int add_health = generator.nextInt(size) + 1;
	
					health += add_health;
				}
			}
		}
		
		max_health = health;
		
		return health;
	}

	public boolean hasQuester(Quester quester) {
		return npcParty.getQuesters().contains(quester);
	}

	public void listMercs() {
		if (npcParty.getQuesters().size() == 0) {
			sendMessage("You have no mercenaries");
		} else {
			sendMessage("Your mercenaries are:");
			for (Quester quester : npcParty.getQuesters()) {
				sendMessage(quester.getName());
			}
		}
	}
}
