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
package org.monksanctum.MineQuest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.martin.bukkit.npclib.NPCManager;
import org.monksanctum.MineQuest.Ability.Ability;
import org.monksanctum.MineQuest.Configuration.AbilityConfigManager;
import org.monksanctum.MineQuest.Configuration.CombatClassConfig;
import org.monksanctum.MineQuest.Configuration.HealItemConfig;
import org.monksanctum.MineQuest.Configuration.ResourceClassConfig;
import org.monksanctum.MineQuest.Configuration.SkillClassConfig;
import org.monksanctum.MineQuest.Event.DelayedSQLEvent;
import org.monksanctum.MineQuest.Event.EventQueue;
import org.monksanctum.MineQuest.Event.NoMobs;
import org.monksanctum.MineQuest.Event.Absolute.HealEvent;
import org.monksanctum.MineQuest.Event.Absolute.ManaEvent;
import org.monksanctum.MineQuest.Listener.MineQuestBlockListener;
import org.monksanctum.MineQuest.Listener.MineQuestEntityListener;
import org.monksanctum.MineQuest.Listener.MineQuestPlayerListener;
import org.monksanctum.MineQuest.Listener.MineQuestServerListener;
import org.monksanctum.MineQuest.Listener.MineQuestWorldListener;
import org.monksanctum.MineQuest.Mob.MQMob;
import org.monksanctum.MineQuest.Mob.SpecialMob;
import org.monksanctum.MineQuest.Quest.FullParty;
import org.monksanctum.MineQuest.Quest.Quest;
import org.monksanctum.MineQuest.Quester.NPCQuester;
import org.monksanctum.MineQuest.Quester.Quester;
import org.monksanctum.MineQuest.Quester.SkillClass.SkillClass;
import org.monksanctum.MineQuest.Store.NPCStringConfig;
import org.monksanctum.MineQuest.World.Claim;
import org.monksanctum.MineQuest.World.Town;
import org.monksanctum.MineQuest.World.Village;

import com.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * This is the main class of MineQuest. It holds static lists of players in the server,
 * Towns in the server, Properties owned by players, and in the future will hold lists
 * of quest status information. It has public static methods to access all of these
 * including the bukkit server.
 * 
 * @author jmonk
 *
 */
public class MineQuest extends JavaPlugin {
	private static AbilityConfigManager ability_config;
	private static int adjustment_multiplier;
	private static int cast_ability_exp;
	private static CombatClassConfig combat_config;
	private static boolean cubonomy_enable = true;
	private static boolean debug_enable = true;
	private static boolean deny_non_class;
	private static int destroy_block_exp;
	private static int destroy_class_exp;
	private static int destroy_materials_level;
	private static int destroy_non_class_exp;
	private static String[] disable_worlds;
	private static EventQueue eventQueue;
	private static int exp_class_damage;
	private static int exp_damage;
	private static boolean half_damage;
	private static boolean health_spawn_enable;
	private static iConomy IConomy = null;
	private static boolean log_health_change;
	private static int maxClass;
	private static MQMob mobs[];
	private static long[] money_amounts;
	private static String[] money_names;
	private static boolean mq_damage_system;
	private static List<String> noMobs;
    private static String npc_attack_type;
    private static int npc_cost;
    private static int npc_cost_class;
	private static boolean npc_enabled;
	private static NPCManager npc_m;
	private static NPCStringConfig npc_strings = new NPCStringConfig();
	private static PermissionHandler permissionHandler;
	private static double price_change;
	private static List<Quester> questers = new ArrayList<Quester>(); 
	private static Quest[] quests;
	private static ResourceClassConfig resource_config;
	private static double sell_percent;
	private static Server server;
//	private MineQuestVehicleListener vl;
//	private MineQuestWorldListener wl;
	private static String server_owner;
	private static String skeleton_type;
	private static MysqlInterface sql_server;
	private static String[] starting_classes;
	private static int starting_health;
	private static boolean town_enable = true;
	private static int[] town_exceptions;
	private static boolean town_no_mobs;
	private static boolean town_protect;
	private static boolean town_respawn;
	private static List<Town> towns = new ArrayList<Town>();
	private static List<Claim> claims = new ArrayList<Claim>();
	private static List<Village> villages = new ArrayList<Village>();
	private static boolean track_destroy;
	private static boolean track_kills;
	private static boolean mana;
	private static Map<String, Location> start_locations = new HashMap<String, Location>();
	private static boolean op_restricted;
	private static boolean mayor_restricted;
	private static boolean is_village_restricted;
	private static boolean is_claim_restricted;
	private static int claim_cost;
	private static int town_cost;
	private static int village_cost;
	
	/**
	 * This adds a minequest wrapper to an existing mob.
	 * This function should not be called outside of MineQuest.
	 * Instead setMQMob should be used. This function handles
	 * the random control of whether a mob is a special mob or
	 * not.
	 * 
	 * @param entity Living entity to add
	 */
	public static void addMob(LivingEntity entity) {
		Random generator = new Random();
		MQMob newMob;
		
		if (getMob(entity) != null) return;
		
		if (generator.nextDouble() < (getAdjustment() / 100.0)) {
			newMob = new SpecialMob(entity);
		} else {
			newMob = new MQMob(entity);
		}
		
		addMQMob(newMob);
	}
	
	/**
	 * This inserts a already created mob warpper into the list
	 * of other mobs. This function should not be called outside of MineQuest.
	 * Instead setMQMob should be used.
	 * 
	 * @param newMob
	 */
	public static void addMQMob(MQMob newMob) {
		int i;
		for (i = 0; i < mobs.length; i++) {
			if (mobs[i] == null) {
				mobs[i] = newMob;
				return;
			}
		}
		
		MQMob newList[] = new MQMob[mobs.length*2];
		i = 0;
		for (MQMob mob : mobs) {
			newList[i++] = mob;
		}
		newList[i++] = newMob;
		while (i < newList.length){
			newList[i++] = null;
		}
		
		mobs = newList;
	}
	
	/**
	 * This function adds a quest to the list of quests that MineQuest knows
	 * about. Any quest started from an external source should be added using
	 * this function.
	 * 
	 * @param quest Quest to add
	 */
	public static void addQuest(Quest quest) {
		Quest[] new_quests = new Quest[quests.length + 1];
		int i = 0;
		for (Quest qst : quests) {
			new_quests[i++] = qst;
		}
		
		new_quests[i] = quest;
		
		quests = new_quests;
	}
	
	/**
	 * Adds a Quester to the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param quester Quester to be added
	 */
	static public void addQuester(Quester quester) {
		questers.add(quester);
	}

	/**
	 * Adds a town to the MineQuest Server. 
	 * Does not modify mysql database.
	 * 
	 * @param town Town to be added
	 */
	static public void addTown(Town town) {
		towns.add(town);
	}

	/**
	 * Adds a claim to the MineQuest Server. 
	 * Does not modify mysql database.
	 * 
	 * @param claim Claim to be added
	 */
	static public void addClaim(Claim claim) {
		claims.add(claim);
	}

	/**
	 * Adds a village to the MineQuest Server. 
	 * Does not modify mysql database.
	 * 
	 * @param village Village to be added
	 */
	static public void addVillage(Village village) {
		villages.add(village);
	}

	/**
	 * This is used in the mob control system to determine if a mob should be
	 * allowed to spawn in any given world.
	 * 
	 * @param entity Mob contained in world of question
	 * @return true if mobs are allowed in the entities world, false if not.
	 */
	public static boolean canCreate(Entity entity) {
		String name = entity.getWorld().getName();
		
		if (spawning) return true;
		
		if (noMobs.contains(name)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Forces a check of all of the mobs in every world to check for a mob that
	 * exists in MC but is not known about by MQ.
	 */
	public static void checkAllMobs() {
    	for (World world : getSServer().getWorlds()) {
    		for (LivingEntity entity : world.getLivingEntities()) {
    			if ((entity instanceof Monster) || (entity instanceof PigZombie) || (entity instanceof Ghast)) {
    				if (getMob(entity) == null) {
    					addMob(entity);
    				}
    			}
    		}
    	}
	}
	
	/**
	 * This checks all MQ related mobs for death to see if any death actions 
	 * should be taken. Used for kill tracking.
	 */
	public static void checkMobs() {
    	int i;
    	
    	for (i = 0; i < mobs.length; i++) {
    		if ((mobs[i] != null) && ((mobs[i].getHealth() <= 0) || (mobs[i].isDead()))) {
    			mobs[i].dropLoot();
    			if (mobs[i].getLastAttacker() != null) {
    				mobs[i].getLastAttacker().addKill(mobs[i]);
    			}
    			mobs[i] = null;
    		}
    	}
    }
	
	/**
	 * Starts the creation of town based on Player
	 * Location.
	 * 
	 * @param player Player Creating the Town
	 */
	public static void startCreate(Player player) {
		start_locations.put(player.getName(), player.getLocation());
	}
	
	/**
	 * This function should be used any time any other plugin wants to damage
	 * any living entity. This function will automatically determine whether the
	 * entity is a quester or a mob or not tracked by MQ at all and assign
	 * damage as needed.
	 * 
	 * @param entity Entity to take the damage
	 * @param i Amount of damage
	 */
	public static void damage(LivingEntity entity, int i) {
		if (entity instanceof HumanEntity) {
			Quester quester = getQuester((HumanEntity)entity);
			quester.setHealth(quester.getHealth() - i);
		} else if (getMob(entity) != null) {
			getMob(entity).damage(i);
		} else {
			int newHealth = entity.getHealth() - i;
			
			if (newHealth <= 0) newHealth = 0;
			
			entity.setHealth(newHealth);
		}
	}

	/**
	 * This function should be used any time any other plugin wants to damage
	 * any living entity. This function will automatically determine whether the
	 * entity is a quester or a mob or not tracked by MQ at all and assign
	 * damage as needed. When possible this call should be used over the other
	 * damage function, as this will cause mobs to retailiate properly.
	 * 
	 * @param entity Entity to take the damage
	 * @param i Amount of damage
	 * @param source Cause of damage
	 */
	public static void damage(LivingEntity entity, int i, Quester source) {
		if (entity instanceof HumanEntity) {
			Quester quester = getQuester((HumanEntity)entity);
			quester.setHealth(quester.getHealth() - i);
		} else if (getMob(entity) != null) {
			getMob(entity).damage(i, source);
		} else {
			if (source != null) {
				entity.damage(i, source.getPlayer());
			} else {
				entity.damage(i, null);
			}
		}
	}

	public static boolean denyNonClass() {
		return deny_non_class;
	}

	/**
     * This is a utility for various parts of MineQuest to calculate
     * the distance between two locations.
     * 
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return Distance between first and second locations
     */
	static public double distance(Location loc1, Location loc2) {
		double x, y, z;
		if ((loc1.getWorld() != null) && (loc2.getWorld() != null)) {
			if (!loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
				return 10000;
			}
		}
		
		x = loc1.getX() - loc2.getX();
		y = loc1.getY() - loc2.getY();
		z = loc1.getZ() - loc2.getZ();
		
		return Math.sqrt(x*x + y*y + z*z);
	}

	/**
	 * This has MineQuest download the file from the specified URL to the
	 * specified location. Used for downloading templates and updated abilities
	 * files.
	 * 
	 * @param url Location of source file
	 * @param file Target of download
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void downloadFile(String url, String file) throws MalformedURLException, IOException {
		BufferedInputStream in = new BufferedInputStream(
				new java.net.URL(url).openStream());
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
		byte data[] = new byte[1024];
		int size;

		while ((size = in.read(data, 0, 1024)) >= 0) {
			bout.write(data, 0, size);
		}

		bout.close();
		in.close();
	}

	/**
	 * Finishes creation of vvillage based on Player
	 * Location.
	 * 
	 * @param player Player Creating Village
	 * @param name Name of Village
	 */
	public static void finishClaim(Player player, String name) {
		if (isClaimRestricted()) {
			if (!isPermissionsEnabled() || !permissionHandler.has(player, "MineQuest.Claim")) {
				if (!player.isOp()) {
					player.sendMessage("You do not have permission to create a claim");
					return;
				}
			}
		}

		if (start_locations.get(player.getName()) == null) {
			player.sendMessage("You have to use /startcreate first...");
			return;
		}

		Location start = start_locations.get(player.getName());
		Location end = player.getLocation();
		int x, z, max_x, max_z;
		if (end.getX() > start.getX()) {
			x = (int)start.getX();
			max_x = (int)end.getX();
		} else {
			x = (int)end.getX();
			max_x = (int)start.getX();
		}
		if (end.getZ() > start.getZ()) {
			z = (int)start.getZ();
			max_z = (int)end.getZ();
		} else {
			z = (int)end.getZ();
			max_z = (int)start.getZ();
		}
		if (MineQuest.getQuester(player).canPay((max_x - x) * (max_z - z) * claim_cost)) {
			sql_server.update("INSERT INTO claims (name, x, z, max_x, max_z, owner, height, y, world) VALUES('"
					+ name + "', '" + x + "', '" + z + "', '" + max_x + "', '" + max_z + "', '" + player.getName() + "', '0', '0', '" + player.getWorld() + "')");
			claims.add(new Claim(name, player.getWorld()));
			player.sendMessage("Claim " + name + " created");
		} else {
			player.sendMessage("You cannot afford to buy a claim of size " + (max_x - x) + " by " + (max_z - z));
			player.sendMessage("It would cost " + ((max_x - x) * (max_z - z) * claim_cost));
		}
	}

	public static boolean isClaimRestricted() {
		return is_claim_restricted;
	}

	/**
	 * Finishes creation of vvillage based on Player
	 * Location.
	 * 
	 * @param player Player Creating Village
	 * @param name Name of Village
	 */
	public static void finishVillage(Player player, String name) {
		if (isVillageRestricted()) {
			if (!isPermissionsEnabled() || !permissionHandler.has(player, "MineQuest.Village")) {
				if (!player.isOp()) {
					player.sendMessage("You do not have permission to create a village");
					return;
				}
			}
		}

		if (start_locations.get(player.getName()) == null) {
			player.sendMessage("You have to use /startcreate first...");
			return;
		}

		Location start = start_locations.get(player.getName());
		Location end = player.getLocation();
		int x, z, max_x, max_z;
		if (end.getX() > start.getX()) {
			x = (int)start.getX();
			max_x = (int)end.getX();
		} else {
			x = (int)end.getX();
			max_x = (int)start.getX();
		}
		if (end.getZ() > start.getZ()) {
			z = (int)start.getZ();
			max_z = (int)end.getZ();
		} else {
			z = (int)end.getZ();
			max_z = (int)start.getZ();
		}
		if (MineQuest.getQuester(player).canPay((max_x - x) * (max_z - z) * village_cost)) {
			sql_server.update("INSERT INTO villages (name, x, z, max_x, max_z, owner, height, y, world) VALUES('"
					+ name + "', '" + x + "', '" + z + "', '" + max_x + "', '" + max_z + "', '" + player.getName() + "', '0', '0', '" + player.getWorld() + "')");
			villages.add(new Village(name, player.getWorld()));
			player.sendMessage("Village " + name + " created");
		} else {
			player.sendMessage("You cannot afford to buy a village of size " + (max_x - x) + " by " + (max_z - z));
			player.sendMessage("It would cost " + ((max_x - x) * (max_z - z) * village_cost));
		}
	}

	public static boolean isVillageRestricted() {
		return is_village_restricted;
	}

	/**
	 * Finishes creation of town based on Player
	 * Location.
	 * 
	 * @param player Player Creating Town
	 * @param name Name of Town
	 */
	public static void finishTown(Player player, String name) {
		if (isMayorRestricted()) {
			if (!isMayor(getQuester(player))) {
				player.sendMessage("Only mayors are allowed to create towns");
				return;
			}
		} else if (isOpRestricted()) {
			if (!player.isOp()) {
				player.sendMessage("Only ops are allowed to create towns");
				return;
			}
		}

		if (start_locations.get(player.getName()) == null) {
			player.sendMessage("You have to use /startcreate first...");
			return;
		}

		Location start = start_locations.get(player.getName());
		Location end = player.getLocation();
		int x, z, max_x, max_z;
		int spawn_x, spawn_y, spawn_z;
		if (end.getX() > start.getX()) {
			x = (int)start.getX();
			max_x = (int)end.getX();
		} else {
			x = (int)end.getX();
			max_x = (int)start.getX();
		}
		if (end.getZ() > start.getZ()) {
			z = (int)start.getZ();
			max_z = (int)end.getZ();
		} else {
			z = (int)end.getZ();
			max_z = (int)start.getZ();
		}
		spawn_x = (x + max_x) / 2;
		spawn_y = (int)(start.getY() + end.getY()) / 2;
		spawn_z = (z + max_z) / 2;
		if (MineQuest.getQuester(player).canPay((max_x - x) * (max_z - z) * town_cost)) {
			sql_server.update("INSERT INTO towns (name, x, z, max_x, max_z, spawn_x, spawn_y, spawn_z, owner, height, y, world) VALUES('"
					+ name + "', '" + x + "', '" + z + "', '" + max_x + "', '" + max_z + "', '" + spawn_x + "', '"
					+ spawn_y + "', '" + spawn_z + "', '" + player.getName() + "', '0', '0', '" + player.getWorld() + "')");
			sql_server.update("CREATE TABLE IF NOT EXISTS " + name + 
					"(height INT, x INT, y INT, z INT, max_x INT, max_z INT, price INT, name VARCHAR(30), store_prop BOOLEAN)");
			towns.add(new Town(name, player.getWorld()));
			player.sendMessage("Town " + name + " created");
		} else {
			player.sendMessage("You cannot afford to buy a town of size " + (max_x - x) + " by " + (max_z - z));
			player.sendMessage("It would cost " + ((max_x - x) * (max_z - z) * town_cost));
		}
	}

	public static boolean isOpRestricted() {
		return op_restricted;
	}

	public static boolean isMayorRestricted() {
		return mayor_restricted;
	}

	public static AbilityConfigManager getAbilityConfiguration() {
		return ability_config;
	}
	
	/**
	 * This gets a list of all of the questers in the server presently. Meaning
	 * they have not null players.
	 * 
	 * @return List of Active Questers
	 */
	public static Quester[] getActiveQuesters() {
		List<Quester> active = new ArrayList<Quester>();
		
		for (Quester quester : questers) {
			if (quester.getPlayer() != null) {
				active.add(quester);
			}
		}
		
		Quester[] questers = new Quester[active.size()];
		int i;
		for (i = 0; i < active.size(); i++) {
			questers[i] = active.get(i);
		}
		
		return questers;
	}
	/**
	 * Gets the difficulty adjustement of the MineQuest Server.
	 * As the level of players goes up the natural encounter
	 * of monsters gets harder to compensate.
	 * 
	 * @return Adjustment Factor to be used
	 */
	public static int getAdjustment() {
        int avgLevel = 0;
        int size = 0;
        for (Quester quester : questers) {
            if (quester.getPlayer() != null) {
	            avgLevel += quester.getLevel();
	            size++;
            }
        }
        if (size == 0) return 0;
        avgLevel /= size;
        
        return (avgLevel / 10);
	}
	public static int getAdjustmentMultiplier() {
		return adjustment_multiplier;
	}
	public static int getCastAbilityExp() {
		return cast_ability_exp;
	}
	public static String[] getClassNames() {
		return starting_classes;
	}
	public static CombatClassConfig getCombatConfig() {
		return combat_config;
	}
	public static int getDestroyBlockExp() {
		return destroy_block_exp;
	}
	public static int getDestroyClassExp() {
		return destroy_class_exp;
	}
	public static int getDestroyMaterialsLevel() {
		return destroy_materials_level;
	}
	public static int getDestroyNonClassExp() {
		return destroy_non_class_exp;
	}
	/**
     * Gets the EventParser being used by MineQuest.
     * 
     * @return EventParser
     */
    static public EventQueue getEventQueue() {
    	return eventQueue;
    }
	public static int getExpClassDamage() {
		return exp_class_damage;
	}
	public static int getExpMob() {
		return exp_damage;
	}
	/**
	 * This returns a list of all of the names of classes in the server, both
	 * combat and resource.
	 * 
	 * @return list of names
	 */
	public static List<String> getFullClassNames() {
		List<String> names = new ArrayList<String>();
		
		for (String name : combat_config.getClassNames()) {
			names.add(name);
		}
		for (String name : resource_config.getClassNames()) {
			names.add(name);
		}
		return names;
	}
	public static iConomy getIConomy() {
		return IConomy;
	}
	public static boolean getIsConomyOn() {
		return IConomy != null;
	}
	public static int getMaxClasses() {
		return maxClass;
	}
	
	/**
	 * Gets the wrapper for a specific mob.
	 * 
	 * @param entity Living entity of the mob
	 * @return MQ Wrapper for Mob
	 */
	public static MQMob getMob(LivingEntity entity) {
		for (MQMob mob : mobs) {
			if (mob != null) {
				if (mob.getId() == entity.getEntityId()) {
					return mob;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Determines the number of Mobs that MQ is tracking right now.
	 * @return
	 */
	public static int getMobSize() {
		int i = 0;
		
		for (MQMob mob : mobs) {
			if (mob != null) i++;
		}
		
		return i;
	}

	public static long[] getMoneyAmounts() {
		return money_amounts;
	}

	public static String[] getMoneyNames() {
		return money_names;
	}
	
	/**
	 * Returns whatever town has the closest spawn point to
	 * the Location.
	 * 
	 * @param to Location
	 * @return Closest Town
	 */
	public static Town getNearestTown(Location to) {
		if (towns.size() == 0) return null;
		
		Town town = towns.get(0);
		int i;
		
		for (i = 1; i < towns.size(); i++) {
			if (distance(to, town.getLocation()) > distance(to, towns.get(i).getLocation())) {
				town = towns.get(i);
			}
		}
		
		return town;
	}
	
	/**
	 * Determines the next available ability id in the abilities SQL table.
	 * 
	 * @return next available id
	 */
	public static int getNextAbilId() {
		int num = 0;
		try {
			ResultSet results = sql_server.query("SELECT * FROM abilities");
			while (results.next()) {
				num++;
			}
		} catch (SQLException e) {
			log("Unable to get max ability id");
		}
		
		return num;
	}
	
	public static String getNPCAttackType() {
		return npc_attack_type;
	}
	
	public static int getNPCCostArcher() {
		return npc_cost_class;
	}
	
	public static int getNPCCostLevel() {
		return npc_cost;
	}

	public static int getNPCCostPeaceMage() {
		return npc_cost_class;
	}
	
	public static int getNPCCostWarMage() {
		return npc_cost_class;
	}
	
	public static int getNPCCostWarrior() {
		return npc_cost_class;
	}

	public static NPCManager getNPCManager() {
		return npc_m;
	}
	
	public static NPCStringConfig getNPCStringConfiguration() {
		return npc_strings;
	}
	
	public static PermissionHandler getPermissions() {
		return permissionHandler;
	}
	
	public static double getPriceChange() {
		return price_change;
	}
	/**
	 * Gets a Quester of a specific Player
	 * 
	 * @param player Player that is a Quester
	 * @return Quester or NULL if none found
	 */
	static public Quester getQuester(LivingEntity player) {
		if (!(player instanceof HumanEntity)) return null;
		
		return getQuester(((HumanEntity)player).getName());
	}
    
    /**
	 * Gets a Quester with a specific player name
	 * 
	 * @param name Name of Quester
	 * @return Quester with Name name or NULL
	 */
	static public Quester getQuester(String name) {
		int i;
		
		for (i = 0; i < questers.size(); i++) {
			if (questers.get(i).equals(name)) {
				return questers.get(i);
			}
		}

		log("[WARNING] Cannot find quester " + name);
		return null;
	}

	/**
	 * Returns lists of Questers within server.
	 * 
	 * @return List of Questers
	 */
	static public List<Quester> getQuesters() {
		return questers;
	}
	
	public static Quest[] getQuests() {
		return quests;
	}
	
	public static ResourceClassConfig getResourceConfig() {
		return resource_config;
	}
	
	public static double getSellPercent() {
		return sell_percent;
	}
	
	public static String getSkeletonType() {
		return skeleton_type;
	}
	
	/**
	 * Gets an interface to the mysql server being used by
	 * MineQuest.
	 * 
	 * @return mysql_interface of MineQuest DB
	 */
	public static MysqlInterface getSQLServer() {
		return sql_server;
	}
	
	/**
	 * Returns the Bukkit Server.
	 * 
	 * @return Bukkit Server
	 */
	public static Server getSServer() {
		return server;
	}
	
	public static int getStartingHealth() {
		return starting_health;
	}
	
	/**
	 * Gets a town that a specific player is within.
	 * 
	 * @param player Player within town
	 * @return Town that player is in or NULL if none exists
	 */
	static public Town getTown(HumanEntity player) {
		return getTown(player.getLocation());
	}
	
	/**
	 * Gets a town that a specific location is within.
	 * 
	 * @param loc Location within town.
	 * @return Town that location is in or NULL is none exists
	 */
	static public Town getTown(Location loc) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).isWithin(loc)) {
				return towns.get(i);
			}
		}
		
		return null;
	}

	/**
	 * Gets a town based on name of the town.
	 * 
	 * @param name Name of the town
	 * @return Town with Name name or NULL is none exists
	 */
	static public Town getTown(String name) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).equals(name)) {
				return towns.get(i);
			}
		}
		
		return null;
	}

	public static int[] getTownExceptions() {
		return town_exceptions;
	}
	
	/**
	 * Gets the list of towns in the server.
	 * 
	 * @return List of towns
	 */
	static public List<Town> getTowns() {
		return towns;
	}
	
	/**
	 * Gets the list of Claims in the server.
	 * 
	 * @return List of Claims
	 */
	static public List<Claim> getClaims() {
		return claims;
	}
	
	/**
	 * Gets the list of villages in the server.
	 * 
	 * @return List of villages
	 */
	static public List<Village> getVillages() {
		return villages;
	}
	
	/**
	 * Determines if all three axis of loc have higher value
	 * than loc2.
	 * 
	 * @param loc Larger Location
	 * @param loc2 Smaller Location
	 * @return Boolean true if loc is greater
	 */
	public static boolean greaterLoc(Location loc, Location loc2) {
		if (loc.getX() < loc2.getX()) {
			return false;
		}
		if (loc.getY() < loc2.getY()) {
			return false;
		}
		if (loc.getZ() < loc2.getZ()) {
			return false;
		}
		return true;
	}
	
	public static boolean halfDamageOn() {
		return half_damage;
	}
	
	public static boolean healSpawnEnable() {
		return health_spawn_enable;
	}
	
	public static boolean isCubonomyEnabled() {
		return cubonomy_enable;
	}
	
	public static boolean isDebugEnabled() {
		return debug_enable;
	}
	
	/**
	 * Determines if a Quester is a Mayor of any town.
	 * Used to determine permissions for creation of towns.
	 * 
	 * @param quester Quester to Test if Mayor
	 * @return Boolean true if Quester is a Mayor
	 */
	public static boolean isMayor(Quester quester) {
		if (quester.equals(server_owner)) {
			return true;
		}
		if (quester.getPlayer() != null) {
			if (isPermissionsEnabled()) {
				if (permissionHandler.has(quester.getPlayer(), "MineQuest.Mayor")) {
					return true;
				}
			}
			
			if (quester.getPlayer().isOp()) {
				return true;
			}
		}
		
		for (Town t : towns) {
			if (t.getTownProperty().getOwner().equals(quester)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isManaEnabled() {
		return mana;
	}
	
	public static boolean isSpellCompEnabled() {
		return spell_comp;
	}
	
	public static boolean isMercEnabled() {
		return npc_enabled;
	}
	
	/**
	 * Uses Permissions and MQ Config to determine if a Player should have MQ
	 * enabled.
	 * 
	 * @param player Player in question
	 * @return true if enabled
	 */
	public static boolean isMQEnabled(Player player) {
		if (!isWorldEnabled(player.getWorld())) {
			return false;
		}
		
		if (isPermissionsEnabled()) {
			if (!permissionHandler.has(player, "MineQuest.Quester")) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determines if a material is considered "open" or meaning a NPC should 
	 * through the block.
	 * 
	 * @param type Material to check
	 * @return true for a "open" material
	 */
	public static boolean isOpen(Material type) {
		if (type == Material.AIR) {
			return true;
		}
		if (type == Material.TORCH) {
			return true;
		}
		if (type == Material.SNOW) {
			return true;
		}
		if (type == Material.FIRE) {
			return true;
		}
		if (type == Material.SIGN) {
			return true;
		}
		if (type == Material.WALL_SIGN) {
			return true;
		}
		if (type == Material.SIGN_POST) {
			return true;
		}
		if (type == Material.FENCE) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isPermissionsEnabled() {
		return permissionHandler != null;
	}
	
	public static boolean isTownEnabled() {
		return town_enable;
	}
	
	public static boolean isTownNoMobs() {
		return town_no_mobs;
	}
	
	public static boolean isTownProtect() {
		return town_protect;
	}
	
	public static boolean isTrackingDestroy() {
		return track_destroy;
	}
	
	public static boolean isTrackingKills() {
		return track_kills;
	}
	
	/**
	 * Checks MQ Config to determine whether MQ should be enabled on this world
	 * and should effect events.
	 * 
	 * @param world World in question
	 * @return true if enabled
	 */
	public static boolean isWorldEnabled(World world) {
		for (String name : disable_worlds) {
			if (world.getName().equals(name)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets a string containing the spell components for a given ability.
	 * 
	 * @param string Ability Name
	 * @return String of Components
	 */
	public static String listSpellComps(String string) {
		Ability ability = Ability.newAbility(string, null);
		String ret = new String();
		
		if (ability == null) {
			ret = string + " is not a valid ability";
			return ret;
		}
		ability.setSkillClass(SkillClass.newShell(MineQuest.getAbilityConfiguration().getSkillClass(string)));
		
		if (isSpellCompEnabled()) {
			for (ItemStack item : reduce(ability.getConfigSpellComps())) {
				ret = ret + item.getAmount() + " " + item.getType().toString() + " ";
			}
		}
		if (isManaEnabled()) {
			ret = ret + " " + ability.getRealManaCost() + " Mana";
		}
		
		return ret;
	}
	
	/**
	 * Prints to screen the message preceded by [MineQuest].
	 * 
	 * @param string Message to Print
	 */
	public static void log(String string) {
		//log.info("[MineQuest] " + string);
		System.out.println("[MineQuest] " + string);
	}

	public static boolean logHealthChange() {
		return log_health_change;
	}

	/**
	 * Uses MQ config and permissions to determine if MQ damage should be 
	 * enabled for a specific quester.
	 * 
	 * @param quester Quester in Question
	 * @return true if enabled
	 */
	public static boolean mqDamageEnabled(Quester quester) {
		if (!mq_damage_system) {
			return false;
		}
		
		if (isPermissionsEnabled() && (quester.getPlayer() != null)) {
			if (permissionHandler.has(quester.getPlayer(), "MineQuest.NormalHealth")) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Removes all mobs from the designated world and adds it to the MQ no spawn
	 * list.
	 * 
	 * @param world World to remove Mobs from.
	 */
	public static void noMobs(World world) {
		for (LivingEntity entity : world.getLivingEntities()) {
			if (!(entity instanceof HumanEntity)) {
				entity.setHealth(0);
			}
		}

		noMobs.add(world.getName());
	}

	/**
	 * Does a reduction on a list of materials for spell components to add
	 * together materials of the same type.
	 * 
	 * @param manaCost Spell Components
	 * @return Reduced Spell Components
	 */
	public static List<ItemStack> reduce(List<ItemStack> manaCost) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		boolean flag;
		
		for (ItemStack itm : manaCost) {
			flag = false;
			for (ItemStack item : ret) {
				if (item.getTypeId() == itm.getTypeId()) {
					flag = true;
					item.setAmount(item.getAmount() + itm.getAmount());
					break;
				}
			}
			if (!flag) {
				ret.add(itm);
			}
		}
		
		return ret;
	}

	/**
	 * This function removes the quest from MQs list of active quests. This
	 * should be called automatically by any quests that complete on their own.
	 * This should only be called in the result of trying to force remove a
	 * quest.
	 * 
	 * @param quest Quest to remove.
	 */
	public static void remQuest(Quest quest) {
		Quest[] new_quests = new Quest[quests.length - 1];
		int i = 0;
		for (Quest qst : quests) {
			if (!qst.equals(quest)) {
				new_quests[i++] = qst;
			}
		}
		
		quests = new_quests;
	}

	/**
	 * Removes a Quester from the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param quester Quester to be removed
	 */
	static public void remQuester(Quester quester) {
		if (quester.getPlayer() != null) {
			for (Quester npc : questers) {
				if (npc instanceof NPCQuester) {
					((NPCQuester)npc).clearTarget(quester.getPlayer());
				}
			}
		}
		questers.remove(quester);
	}

	/**
	 * Removes a Quester from the MineQuest Server.
	 * Does not modify mysql database.
	 * 
	 * @param name Name of Quester to be removed
	 */
	static public void remQuester(String name) {
		questers.remove(getQuester(name));
	}

	/**
	 * Removes a Town from the MineQuester Server.
	 * Does not modify mysql database.
	 * 
	 * @param name Name of Town to remove
	 */
	static public void remTown(String name) {
		towns.remove(getTown(name));
	}

	/**
	 * Removes a Town from the MineQuester Server.
	 * Does not modify mysql database.
	 * 
	 * @param town Town to remove
	 */
	static public void remTown(Town town) {
		towns.remove(town);
	}

	/**
	 * Respawn all NPCs in case of them disappearing.
	 * 
	 */
	public static void respawnNPCs() {
		for (Quester quester : questers) {
			if (quester instanceof NPCQuester) {
				((NPCQuester)quester).redo();
			}
		}
	}

	/**
	 * This sets the health of an entity based on a percent of its max health.
	 * Similar to the damage functions this will handle all types of living
	 * entities correctly.
	 * 
	 * @param entity Living entity to affect.
	 * @param percent Percent of max health.
	 */
	public static void setHealth(LivingEntity entity, double percent) {
		if (entity instanceof HumanEntity) {
			getQuester((HumanEntity)entity).setHealth((int)(percent * getQuester((HumanEntity)entity).getHealth()));
		} else if (getMob(entity) != null) {
			getMob(entity).setHealth((int)(getMob(entity).getHealth() * percent));
		} else {
			entity.setHealth((int)(entity.getHealth() * percent));
		}
	}

	/**
	 * This sets the health of an entity based on an integer number specified.
	 * Similar to the damage functions this will handle all types of living
	 * entities correctly.
	 * 
	 * @param entity Living entity to affect.
	 * @param health New health for entity.
	 */
	public static void setHealth(LivingEntity entity, int health) {
		if (entity instanceof HumanEntity) {
			getQuester((HumanEntity)entity).setHealth(health);
		} else if (getMob(entity) != null) {
			getMob(entity).setHealth(health);
		} else {
			if (health > 20) health = 20;
			if (health < 0) health = 0;
			entity.setHealth(health);
		}
	}

	public static void setIConomy(Plugin object) {
		MineQuest.IConomy = ((iConomy)object);
	}

	/**
	 * This can be used to set the wrapper for a specific. This can be used to
	 * change a mob from special to normal or from any type to any other type.
	 * It will replace the old wrapper based on id of the mob it is wrapping.
	 * If the mob does not already have a wrapper, it will simply be added to 
	 * the list.
	 * 
	 * @param newMob New wrapper to set.
	 */
	public static void setMQMob(MQMob newMob) {
		int i;
		
		for (i = 0; i < mobs.length; i++) {
			if (mobs[i] != null) {
				if (mobs[i].getId() == newMob.getId()) {
					mobs[i].cancel();
					mobs[i] = newMob;
				}
			}
		}
		
		addMQMob(newMob);
	}
	public static boolean townRespawn() {
		return town_respawn;
	}

	/**
	 * Removes a mob spawn restriction from the specified world.
	 * @param world
	 */
	public static void yesMobs(World world) {
		noMobs.remove(world.getName());
		
		return;
	}
	
	private MineQuestBlockListener bl;
	private MineQuestEntityListener el;
	private MineQuestPlayerListener pl;
	private MineQuestServerListener sl;
	private MineQuestWorldListener wl;
	private String version;
	private static int heal_event;
	private static boolean spell_comp;
	private static int starting_mana;
	private static int level_health;
	private static int level_mana;
	private static int mana_event;
	private static boolean spawning;
	private static HealItemConfig heal_item_config;
	
	public MineQuest() {
		heal_event = 0;
	}
	
	private void addColumns(String db, String cols[], String types[]) {
		int i;
		for (i = 0; i < cols.length; i++) {
			try {
				if (!column_exists(db, cols[i])) {
					sql_server.update("ALTER TABLE " + db + " ADD COLUMN " + cols[i] + " " + types[i], false);
				}
			} catch (SQLException e) {
			}
		}
	}
	
	private boolean column_exists(String db, String column) throws SQLException {
		ResultSet results = sql_server.query("SELECT * FROM " + db);
		if (results == null) return false;
		ResultSetMetaData meta = results.getMetaData();
		
		int i;
		for (i = 0; i < meta.getColumnCount(); i++) {
			if (meta.getColumnName(i + 1).equals(column)) {
				return true;
			}
		}
		
		return false;
	}
	private void createDB() throws Exception {
		MineQuest.log("Your DB is too old to determine version");
		MineQuest.log("Upgrading DB to 0.50");
		
		upgradeDB(0, 5);
	}
	
	private void downloadAbilities() throws MalformedURLException, IOException {
		downloadFile("http://www.theminequest.com/download/abilities.jar", "MineQuest/abilities.jar");
	}

	@Override
	public void onDisable() {
		for (Quest quest : quests) {
			quest.issueNextEvents(-1);
		}
		for (Quester quester : questers) {
			if (quester.getPlayer() != null) {
				quester.save();
			}
		}
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
	}

	/**
	 * Sets up an instance of MineQuest. There should never be more than
	 * one instance of MineQuest required. If enabled this method will load all of the
	 * static variables with required information and creating a second instance
	 * will reset all of those, and eventually create inconsistancies in the server.
	 * 
	 * This loads all adjustable parameters from minequest.properties, including
	 * database location and login parameters.
	 */
	@Override
	public void onEnable() {	
        
        PluginDescriptionFile pdfFile = this.getDescription();
        version = pdfFile.getVersion();		
		server = getServer();
        
		mobs = new MQMob[1];
		
        eventQueue = new EventQueue(this);
        
        quests = new Quest[0];
        
        npc_m = new NPCManager(this);
        
        (new File("MineQuest/")).mkdir();
        
        if ((!(new File("MineQuest/abilities.jar")).exists()) || (Ability.getVersion() < 3)) {
        	log("MineQuest/abilities.jar not found or too old: Downloading...");
        	try {
				downloadAbilities();
	        	log("MineQuest/abilities.jar download complete");
			} catch (Exception e) {
				log("Failed to download abilities.jar");
			}
        }
        
//        getEventQueue().addEvent(new RespawnEvent(300000));
        
        noMobs = new ArrayList<String>();
        
        try {
			setupMainProperties();
			setupExperienceProperties();
			setupGeneralProperties();
			setupNPCProperties();
			setupEconomoyProperties();
			setupPropertyProperties();
	        
	        combat_config = new CombatClassConfig();
	        resource_config = new ResourceClassConfig();
	        
	        ability_config = new AbilityConfigManager();
			
			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " npc (name VARCHAR(30), property VARCHAR(30), value VARCHAR(300))");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " binds (name VARCHAR(30), abil VARCHAR(30), bind INT, bind_2 INT)");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " chests (name VARCHAR(30), town VARCHAR(30), x INT, y INT, z INT)");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
					+ " kills (name VARCHAR(30), type VARCHAR(30), count INT)");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " quests (name VARCHAR(30), type VARCHAR(1), file VARCHAR(30))");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " reps (name VARCHAR(30), type VARCHAR(30), amount INT)");

			sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " questers (name VARCHAR(30), health INT, "
							+ "max_health INT, cubes DOUBLE, exp INT, "
							+ "last_town VARCHAR(30), level INT, "
							+ "enabled INT, selected_chest VARCHAR(33), "
							+ "classes VARCHAR(150), mode VARCHAR(30) DEFAULT 'Quester', "
							+ "world VARCHAR(30) DEFAULT 'world', x DOUBLE DEFAULT '0', "
							+ "y DOUBLE DEFAULT '0', z DOUBLE DEFAULT '0', "
							+ "pitch DOUBLE DEFAULT '0', yaw DOUBLE DEFAULT '0')");
			sql_server.update("CREATE TABLE IF NOT EXISTS classes (name VARCHAR(30), "
							+ "class VARCHAR(30), exp INT, level INT, abil_list_id INT)");
			sql_server.update("CREATE TABLE IF NOT EXISTS abilities (abil_list_id INT, "
							+ "abil0 VARCHAR(30) DEFAULT '0', abil1 VARCHAR(30) DEFAULT '0', "
							+ "abil2 VARCHAR(30) DEFAULT '0',"
							+ "abil3 VARCHAR(30) DEFAULT '0', abil4 VARCHAR(30) DEFAULT '0', "
							+ "abil5 VARCHAR(30) DEFAULT '0', abil6 VARCHAR(30) DEFAULT '0', "
							+ "abil7 VARCHAR(30) DEFAULT '0', abil8 VARCHAR(30) DEFAULT '0', "
							+ "abil9 VARCHAR(30) DEFAULT '0')");
			
			sql_server.update("CREATE TABLE IF NOT EXISTS idle (name VARCHAR(30), file VARCHAR(30), type INT, event_id INT, target VARCHAR(180))");

			sql_server.update("CREATE TABLE IF NOT EXISTS towns (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, spawn_x INT, spawn_y INT, spawn_z INT, " +
					"owner VARCHAR(30), height INT, y INT, merc_x DOUBLE, merc_y DOUBLE, merc_z DOUBLE, world VARCHAR(30))");
			
			sql_server.update("CREATE TABLE IF NOT EXISTS claims (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, " +
					"owner VARCHAR(30), height INT, y INT, world VARCHAR(30))");
			
			sql_server.update("CREATE TABLE IF NOT EXISTS villages (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, " +
					"owner VARCHAR(30), height INT, y INT, world VARCHAR(30))");

		} catch (Exception e) {
			MineQuest.log("Unable to initialize configuration");
        	MineQuest.log("Check configuration in MineQuest directory");
        	setEnabled(false);
        	return;
        }
        
//        getEventParser().addEvent(new CheckMQMobs(10000));
		ResultSet results = sql_server.query("SELECT * FROM version");
		
		try {
			if ((results == null) || (!results.next())) {
				createDB();
			} else {
				if (!results.getString("version").equals(version)) {
					upgradeDB(results.getString("version"));
				}
				results = sql_server.query("SELECT * FROM version");
				results.next();
				MineQuest.log("DB Version: " + results.getString("version"));
			}
		} catch (SQLException e) {
			try {
				createDB();
			} catch (Exception e1) {
				log("Unable to upgrade DB1! - Disabling MineQuest");
//				e.printStackTrace();
//				e1.printStackTrace();
				onDisable();
				return;
			}
		} catch (Exception e) {
			log("Unable to upgrade DB - Disabling MineQuest");
			e.printStackTrace();
			onDisable();
			return;
		}
        
        checkAllMobs();

		results = sql_server.query("SELECT * FROM questers");
		List<String> names = new ArrayList<String>();
		List<String> npcs = new ArrayList<String>();
		
		try {
			while (results.next()) {
				if (results.getString("mode").equals("Quester")) {
					names.add(results.getString("name"));
				} else {
					if (npc_enabled) {
						npcs.add(results.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			log("Error: Couldn't get list of questers");
		}
		
		for (String name : names) {
			questers.add(new Quester(name));
		}
		
		for (String name : npcs) {
			questers.add(new NPCQuester(name));
		}
		
		names.clear();
		results = sql_server.query("SELECT * FROM towns");
		List<String> worlds = new ArrayList<String>();
		
		try {
			while (results.next()) {
				names.add(results.getString("name"));
				worlds.add(results.getString("world"));
			}
		} catch (SQLException e) {
			log("Unable to get list of towns");
		}
		
		int i = 0;
		for (String name : names) {
			towns.add(new Town(name, getServer().getWorld(worlds.get(i++))));
		}

		bl = new MineQuestBlockListener();
		el = new MineQuestEntityListener();
		pl = new MineQuestPlayerListener();
		sl = new MineQuestServerListener();
		wl = new MineQuestWorldListener();
//		vl = new MineQuestVehicleListener();
		
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ANIMATION, pl, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST, el, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, el, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, el, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, el, Priority.Highest, this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, el, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, bl, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, bl, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, bl, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLUGIN_DISABLE, sl, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, sl, Priority.Monitor, this);
        pm.registerEvent(Event.Type.CHUNK_LOAD, wl, Priority.Monitor, this);
        pm.registerEvent(Event.Type.CHUNK_UNLOAD, wl, Priority.Monitor, this);
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

		setupIConomy();
		setupPermissions();
		
		if (new File("MineQuest/main.script").exists()) {
			MineQuest.addQuest(new Quest("MineQuest/main.script", new FullParty()));
		}
	}
	
	private static void setupEconomoyProperties() {
		PropertiesFile economy = new PropertiesFile("MineQuest/economy.properties");
		
		sell_percent = economy.getDouble("sell_return", .92);
		price_change = economy.getDouble("price_change", .009);
		
		money_names = economy.getString("money_names", "GC,MC,KC,C").split(",");
		money_amounts = SkillClassConfig.longList(economy.getString("money_amounts", "1000000000,1000000,1000,0"));

		cubonomy_enable = economy.getBoolean("cubonomy_enable", true);
	}

	private static void setupNPCProperties() {
		PropertiesFile npc = new PropertiesFile("MineQuest/npc.properties");

		npc_cost = npc.getInt("npc_cost_level", 1000);
		npc_cost_class = npc.getInt("npc_cost_class", 1000);
		npc_attack_type = npc.getString("npc_attack_type", "Warrior");
	}

	private static void setupPropertyProperties() {
		PropertiesFile property = new PropertiesFile("MineQuest/property.properties");
		mayor_restricted = property.getBoolean("town_mayor_restricted", true);
		op_restricted = property.getBoolean("town_op_restricted", false);
		is_claim_restricted = property.getBoolean("is_claim_restricted", false);
		is_village_restricted = property.getBoolean("is_village_restricted", false);
		town_cost = property.getInt("town_cost", 0);
		village_cost = property.getInt("village_cost", 0);
		claim_cost = property.getInt("claim_cost", 0);
	}

	private static void setupGeneralProperties() {
		PropertiesFile general = new PropertiesFile("MineQuest/general.properties");
		
		town_enable = general.getBoolean("town_enable", true);
		npc_enabled = general.getBoolean("npc_enable", true);
		track_kills = general.getBoolean("track_kills", true);
		track_destroy = general.getBoolean("track_destroy", true);
		town_protect = general.getBoolean("town_protect", true);
		log_health_change = general.getBoolean("log_health_change", true);
		starting_classes = general.getString("starting_classes", "Warrior,Archer,WarMage,PeaceMage,Miner,Digger,Lumberjack,Farmer").split(",");
		skeleton_type = general.getString("skeleton_type", "WarMage");
		half_damage = general.getBoolean("half_damage", true);
		deny_non_class = general.getBoolean("deny_non_class", true);
		debug_enable = general.getBoolean("debug_enable", true);
		health_spawn_enable = general.getBoolean("health_spawn_enable", false);
		mana = general.getBoolean("mana_enable", false);
		spell_comp = general.getBoolean("spell_comp_enable", true);
		boolean slow_heal = general.getBoolean("slow_heal", false);
		if (slow_heal) {
			int amount = general.getInt("slow_heal_amount", 1);
			int delay = general.getInt("slow_heal_delay_ms", 1500);
			heal_event = getEventQueue().addEvent(new HealEvent(delay, amount));
		} else {
			if (heal_event != 0) {
				MineQuest.getEventQueue().cancel(heal_event);
			}
		}
		boolean slow_mana = general.getBoolean("slow_mana", true);
		if (slow_mana) {
			int amount = general.getInt("slow_mana_amount", 1);
			int delay = general.getInt("slow_mana_delay_ms", 1500);
			mana_event = getEventQueue().addEvent(new ManaEvent(delay, amount));
		} else {
			if (mana_event != 0) {
				MineQuest.getEventQueue().cancel(mana_event);
			}
		}
		server_owner = general.getString("mayor", "jmonk");
		disable_worlds = general.getString("disable_worlds", "").split(",");
		starting_health = general.getInt("starting_health", 10);
		level_health = general.getInt("level_health", 4);
		level_mana = general.getInt("level_mana", 4);
		starting_mana = general.getInt("starting_mana", 10);
		town_respawn = general.getBoolean("town_respawn", true);
		String exceptions = general.getString("town_edit_exception", "64,77");
		if (exceptions.contains(",")) {
			String[] split = exceptions.split(",");
			town_exceptions = new int[split.length];
			int i;
			for (i = 0; i < split.length; i++) {
				town_exceptions[i] = Integer.parseInt(split[i]);
			}
		} else {
			try {
				town_exceptions = new int[] {Integer.parseInt(exceptions)};
			} catch (Exception e) {
				town_exceptions = new int[0];
			}
		}
		mq_damage_system = general.getBoolean("mq_health_system", true);
		
		heal_item_config = new HealItemConfig();
	}

	private static void setupExperienceProperties() {
		PropertiesFile experience = new PropertiesFile("MineQuest/experience.properties");

		destroy_class_exp = experience.getInt("destroy_class", 5);
		destroy_non_class_exp = experience.getInt("destroy_non_class", 2);
		destroy_block_exp = experience.getInt("destroy_block", 2);
		adjustment_multiplier = experience.getInt("adjustment_multiplier",
				1);
		exp_damage = experience.getInt("damage", 3);
		cast_ability_exp = experience.getInt("cast_ability", 5);
		exp_class_damage = experience.getInt("class_damage", 5);
	}

	private static void setupMainProperties() throws Exception {
		PropertiesFile minequest = new PropertiesFile("MineQuest/main.properties");
		String url, port, db, user, pass;
		
		url = minequest.getString("url", "localhost");
		port = minequest.getString("port", "3306");
		db = minequest.getString("db", "MineQuest/minequest");
		user = minequest.getString("user", "root");
		pass = minequest.getString("pass", "1234");
		maxClass = minequest.getInt("max_classes", 4);
		boolean real = minequest.getBoolean("mysql", false);
		boolean nomobs_main = minequest.getBoolean("no_mobs_main_world", false);
		if (nomobs_main) {
			eventQueue.addEvent(new NoMobs(5000, "world"));
			noMobs.add("world");
		} else {
			if (noMobs.contains("world")) {
				noMobs.remove("world");
			}
		}
		town_no_mobs = minequest.getBoolean("town_no_mobs", true);
		sql_server = new MysqlInterface(url, port, db, user, pass, minequest.getInt("silent", 1), real);
	}
	private void setupIConomy() {
		Plugin test = this.getServer().getPluginManager().getPlugin(
				"iConomy");

		if (MineQuest.IConomy == null) {
			if (test != null) {
			} else {
				log("iConomy system not detected, defaulting to MineQuest Storage");
			}
		}
	}
	
	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (permissionHandler == null) {
			if (permissionsPlugin != null) {
				permissionHandler = ((Permissions) permissionsPlugin)
						.getHandler();
			} else {
				permissionHandler = null;
				log("Permission system not detected");
			}
		}
	}

	private void upgradeDB(int oldVersion, int newVersion) throws Exception {
		String cols[] = null;
		String types[] = null;
		if (oldVersion == 0) {
			cols = new String[] {
					"world",
					"x",
					"y",
					"z",
					"mode",
					"pitch",
					"yaw"
			};
			types = new String[] {
					"varchar(30) DEFAULT 'world'",
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"varchar(30) DEFAULT 'Quester'",
					"double DEFAULT '0'",
					"double DEFAULT '0'"
			};
			
			addColumns("questers", cols, types);
			
			cols = new String[] {
					"merc_x",
					"merc_y",
					"merc_z"
			};

			types = new String[] {
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"double DEFAULT '0'"
			};

			addColumns("towns", cols, types);
		}
		if (oldVersion < 6) {
			cols = new String[] {
					"mana",
					"max_mana"
			};
			types = new String[] {
					"int DEFAULT '10'",
					"int DEFAULT '10'"
			};
			addColumns("questers", cols, types);
			
			cols = new String[] {
					"world",
			};
			types = new String[] {
					"VARCHAR(30) DEFAULT '" + server.getWorlds().get(0).getName() + "'"
			};
			addColumns("towns", cols, types);
		}
		if (oldVersion < 5) {
			ResultSet results = sql_server.query("SELECT * FROM questers");
			List<String> questers = new ArrayList<String>();
			List<Boolean> npc_flag = new ArrayList<Boolean>();
			
			try {
				while (results.next()) {
					questers.add(results.getString("name"));
					if (results.getString("mode").equals("Quester")) {
						npc_flag.add(false);
					} else {
						npc_flag.add(true);
					}
				}
			} catch (SQLException e) {
				log("DB Upgrading failed - Aborting!!");
				onDisable();
				throw new Exception();
			}
			
			int index = 0;
			for (String name : questers) {
				try {
					results = sql_server.query("SELECT * FROM " + name);
					List<String> abil = new ArrayList<String>();
					List<Integer> bind = new ArrayList<Integer>();
					List<Integer> bind_2 = new ArrayList<Integer>();
					
					while (results.next()) {
						abil.add(results.getString("abil"));
						bind.add(results.getInt("bind"));
						bind_2.add(results.getInt("bind_2"));
					}
					int i;
					for (i = 0; i < abil.size(); i++) {
						sql_server
								.update("INSERT INTO binds (name, abil, bind, bind_2) VALUES('"
										+ name
										+ "', '"
										+ abil.get(i)
										+ "', '"
										+ bind.get(i)
										+ "', '"
										+ bind_2.get(i)
										+ "')");
					}
					sql_server.update("DROP TABLE " + name);
				} catch (Exception e) {
				}
				
				try {
					results = sql_server.query("SELECT * FROM " + name + "_chests");
					List<String> town = new ArrayList<String>();
					List<Integer> x = new ArrayList<Integer>();
					List<Integer> y = new ArrayList<Integer>();
					List<Integer> z = new ArrayList<Integer>();
					
					while (results.next()) {
						town.add(results.getString("town"));
						x.add(results.getInt("x"));
						y.add(results.getInt("y"));
						z.add(results.getInt("z"));
					}
					int i;
					for (i = 0; i < town.size(); i++) {
						sql_server
								.update("INSERT INTO chests (name, town, x, y, z) VALUES('"
										+ name
										+ "', '"
										+ town.get(i)
										+ "', '"
										+ x.get(i)
										+ "', '"
										+ y.get(i)
										+ "', '"
										+ z.get(i)
										+ "')");
					}
					sql_server.update("DROP TABLE " + name + "_chests");
				} catch (Exception e) {
				}
				
				try {
					results = sql_server.query("SELECT * FROM " + name + "_kills");
					List<String> type = new ArrayList<String>();
					List<Integer> count = new ArrayList<Integer>();
					
					while (results.next()) {
						type.add(results.getString("name"));
						count.add(results.getInt("count"));
					}
					int i;
					for (i = 0; i < type.size(); i++) {
						sql_server
								.update("INSERT INTO kills (name, type, count) VALUES('"
										+ name
										+ "', '"
										+ type.get(i)
										+ "', '"
										+ count.get(i)
										+ "')");
					}
					sql_server.update("DROP TABLE " + name + "_kills");
				} catch (Exception e) {
				}
				
				try {
					results = sql_server.query("SELECT * FROM " + name + "_quests");
					List<String> type = new ArrayList<String>();
					List<String> file = new ArrayList<String>();
					
					while (results.next()) {
						type.add(results.getString("type"));
						file.add(results.getString("file"));
					}
					int i;
					for (i = 0; i < type.size(); i++) {
						sql_server
								.update("INSERT INTO quests (name, type, file) VALUES('"
										+ name
										+ "', '"
										+ type.get(i)
										+ "', '"
										+ file.get(i)
										+ "')");
					}
					sql_server.update("DROP TABLE " + name + "_quests");
				} catch (Exception e) {
				}
				
				if (npc_flag.get(index++)) {
					try {
						results = sql_server.query("SELECT * FROM " + name + "_npc");
						List<String> property = new ArrayList<String>();
						List<String> value = new ArrayList<String>();
						
						while (results.next()) {
							property.add(results.getString("property"));
							value.add(results.getString("value"));
						}
						int i;
						for (i = 0; i < property.size(); i++) {
							sql_server
									.update("INSERT INTO npc (name, property, value) VALUES('"
											+ name
											+ "', '"
											+ property.get(i)
											+ "', '"
											+ value.get(i)
											+ "')");
						}
						sql_server.update("DROP TABLE " + name + "_npc");
					} catch (Exception e) {
					}
				}
			}
		}
		
		sql_server.update("CREATE TABLE IF NOT EXISTS version (version VARCHAR(30))");
		sql_server.update("DELETE FROM version");
		sql_server.update("INSERT INTO version (version) VALUES('" + version + "')");
		
	}

	private void upgradeDB(String string) throws Exception {
		int oldVersion = 0;
		try {
			oldVersion = (int)(Double.parseDouble(string) * 10);
		} catch (Exception e) {
			MineQuest.log("Could not detect version - Previously running dev?");
		}

		upgradeDB(oldVersion, 5);
	}

	public static void reloadConfig() {
		MineQuest.log("Loading Economy Properties");
		setupEconomoyProperties();
		MineQuest.log("Loading Experience Properties");
		setupExperienceProperties();
		MineQuest.log("Loading General Properties");
		setupGeneralProperties();
		MineQuest.log("Loading Property Properties");
		setupPropertyProperties();
		MineQuest.log("Loading NPC Properties");
		setupNPCProperties();

		MineQuest.log("Loading Combat Class Config");
        combat_config = new CombatClassConfig();
		MineQuest.log("Loading Resource Class Config");
        resource_config = new ResourceClassConfig();
		MineQuest.log("Loading Ability Config - Warning: will not affect loaded abilities!!");
        ability_config = new AbilityConfigManager();
	}

	public static void delayUpdate(String string) {
		eventQueue.addEvent(new DelayedSQLEvent(50, string));
	}

	public static List<Quester> getRealQuesters() {
		List<Quester> questers = new ArrayList<Quester>();
		
		for (Quester quester : MineQuest.questers) {
			if ((!(quester instanceof NPCQuester)) && (quester.getPlayer() != null)) {
				questers.add(quester);
			}
		}
		
		return questers;
	}

	public static Quest getMainQuest() {
		for (Quest quest : quests) {
			if (quest.isMainQuest()) {
				return quest;
			}
		}
		return null;
	}

	public static int getStartingMana() {
		return starting_mana;
	}

	public static int getLevelHealth() {
		return level_health - 1;
	}

	public static int getLevelMana() {
		return level_mana - 1;
	}
	
	/**
	 * Gets a village that a specific player is within.
	 * 
	 * @param player Player within village
	 * @return village that player is in or NULL if none exists
	 */
	static public Village getVillage(HumanEntity player) {
		return getVillage(player.getLocation());
	}
	
	/**
	 * Gets a village that a specific location is within.
	 * 
	 * @param loc Location within village.
	 * @return village that location is in or NULL is none exists
	 */
	static public Village getVillage(Location loc) {
		int i;
		
		for (i = 0; i < villages.size(); i++) {
			if (villages.get(i).isWithin(loc)) {
				return villages.get(i);
			}
		}
		
		return null;
	}

	/**
	 * Gets a village based on name of the village.
	 * 
	 * @param name Name of the village
	 * @return village with Name name or NULL is none exists
	 */
	static public Village getVillage(String name) {
		int i;
		
		for (i = 0; i < towns.size(); i++) {
			if (towns.get(i).equals(name)) {
				return towns.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a claim that a specific player is within.
	 * 
	 * @param player Player within claim
	 * @return claim that player is in or NULL if none exists
	 */
	static public Claim getClaim(HumanEntity player) {
		return getClaim(player.getLocation());
	}
	
	/**
	 * Gets a claim that a specific location is within.
	 * 
	 * @param loc Location within claim.
	 * @return claim that location is in or NULL is none exists
	 */
	static public Claim getClaim(Location loc) {
		int i;
		
		for (i = 0; i < claims.size(); i++) {
			if (claims.get(i).isWithin(loc)) {
				return claims.get(i);
			}
		}
		
		return null;
	}

	/**
	 * Gets a claim based on name of the claim.
	 * 
	 * @param name Name of the claim
	 * @return claim with Name name or NULL is none exists
	 */
	static public Claim getClaim(String name) {
		int i;
		
		for (i = 0; i < claims.size(); i++) {
			if (claims.get(i).equals(name)) {
				return claims.get(i);
			}
		}
		
		return null;
	}

	public static void setSpawning(boolean b) {
		spawning = b;
	}

	public static HealItemConfig getHealthConfiguration() {
		return heal_item_config;
	}
}
