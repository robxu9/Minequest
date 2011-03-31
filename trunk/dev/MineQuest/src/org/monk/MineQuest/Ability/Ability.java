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
package org.monk.MineQuest.Ability;

import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftAnimals;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

/**
 * This is the base class for all abilities in MineQuest.
 * 
 * @author jmonk
 *
 */
public abstract class Ability {
	// http://www.devx.com/tips/Tip/38975
	public static Class<?> getClass(String the_class) throws Exception {
		URL url = new URL("file:abilities.jar");
		URLClassLoader ucl = new URLClassLoader(new URL[] {url}, (new AbilityBinder()).getClass().getClassLoader());
		return Class.forName(the_class.replaceAll(".class", ""), true, ucl);
	}
	
	//following code came from http://snippets.dzone.com/posts/show/4831
	public static List<String> getClasseNamesInPackage(String jarName,
			String packageName) {
		boolean debug = false;
		ArrayList<String> classes = new ArrayList<String>();

		packageName = packageName.replaceAll("\\.", "/");
		if (debug)
			System.out
					.println("Jar " + jarName + " looking for " + packageName);
		try {
			JarInputStream jarFile = new JarInputStream(new FileInputStream(
					jarName));
			JarEntry jarEntry;

			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if ((jarEntry.getName().startsWith(packageName))
						&& (jarEntry.getName().endsWith(".class"))) {
					if (debug)
						System.out.println("Found "
								+ jarEntry.getName().replaceAll("/", "\\."));
					classes.add((String)jarEntry.getName().replaceAll("/", "\\."));
				}
			}
		} catch (Exception e) {
			MineQuest.log("Couldn't get Ability Classes - Missing abilities.jar?");
		}
		return classes;
	}
	
	/**
	 * Returns the nearest empty block to location. Location is not
	 * Guaranteed to be closest to location. If the block is solid
	 * it looks upward until it finds air. If the block is air it
	 * looks downward until it finds ground.
	 * 
	 * @param x X Location
	 * @param y Y Location
	 * @param z Z Location
	 * @return Nearest empty height above ground to Location
	 */
	static public int getNearestY(World world, int x, int y, int z) {
		int i = y;
		
		if (world.getBlockAt(x, y, z).getTypeId() != 0) {
			do {
				i++;
			} while (((world.getBlockAt(x, i, z).getType() != Material.SNOW) 
					&& (world.getBlockAt(x, i, z).getType() != Material.FIRE) 
					&& (world.getBlockAt(x, i, z).getType() != Material.AIR)) && (i < 1000));
			if (i == 1000) i = 0;
		} else {
			do {
				i--;
			} while (((world.getBlockAt(x, i, z).getType() == Material.SNOW) 
					|| (world.getBlockAt(x, i, z).getType() == Material.FIRE) 
					|| (world.getBlockAt(x, i, z).getType() == Material.AIR)) && (i > -100));
			if (i == -100) i = 0;
			i++;
		}
		
		return i;
	}
	
	static public List<Ability> newAbilities(SkillClass myclass) {
		List<String> classes = new ArrayList<String>();
		List<Ability> abilities = new ArrayList<Ability>();
		
		try {
			classes = getClasseNamesInPackage("abilities.jar", "org.monk.MineQuest.Ability");
		} catch (Exception e) {
			MineQuest.log("Unable to get Abilities");
		}
		for (String this_class : classes) {
			try {
				Ability ability = (Ability)getClass(this_class).newInstance();
				
				ability.setSkillClass(myclass);
				
				if (myclass == null) {
					abilities.add(ability);
				} else if (myclass.getClass().equals(ability.getClassType().getClass())) {
					abilities.add(ability);
				}
			} catch (Exception e) {
				MineQuest.log("Could not load Ability: " + this_class);
			}
		}

		return abilities;
	}
	
	/**
	 * Creates an instance of the proper ability based on name
	 * and returns it as an Ability.
	 * 
	 * @param name Name of Ability
	 * @param myclass Instance of SkillClass holding the Ability
	 * @return new Ability created
	 */
	static public Ability newAbility(String name, SkillClass myclass) {
		for (Ability ability : newAbilities(myclass)) {
			if (name.equalsIgnoreCase(ability.getName())) {
				return ability;
			}
		}
		MineQuest.log("Warning: Could not find ability " + name);
		
		return null;
	}
	
	protected int bind;
	protected int cast_time;
	protected int count;
	protected boolean enabled;
	protected long last_msg;
	protected SkillClass myclass;
	protected long time;
	/**
	 * Creates an Ability
	 * 
	 * @param name Name of Ability
	 * @param myclass SkillClass that holds Ability
	 */
	public Ability() {
		Calendar now = Calendar.getInstance();
		enabled = true;
		if (this instanceof PassiveAbility) enabled = false;
		count = 0;
		cast_time = getCastTime();
		bind = -1;
		time = now.getTimeInMillis();
		last_msg = 0;
	}
	
	/**
	 * Bind to left click of item.
	 * 
	 * @param player Player binding Ability
	 * @param item Item to be bound
	 */
	public void bind(Quester quester, ItemStack item) {
		if (bind != item.getTypeId()) {
			silentUnBind(quester);
			bind = item.getTypeId();
			MineQuest.getSQLServer().update("INSERT INTO " + quester.getName() + " (abil, bind, bind_2) VALUES('" + getName() + "', '" + bind + "', '" + bind + "')");
			quester.sendMessage(getName() + " is now bound to " + item.getTypeId());
		}
	}
	
	/**
	 * Checks if the Ability has been cast too recently based on
	 * casting time.
	 * 
	 * @return Boolean true if can cast now
	 */
	protected boolean canCast() {
		Calendar now = Calendar.getInstance();
		if ((now.getTimeInMillis() - time) > getCastTime()) {
			time = now.getTimeInMillis();
			return true;
		}
		return false;
	}
	
	/**
	 * This is called when non-passive abilities are activated.
	 * Must be overloaded for all non-passive abilities. Binding
	 * and casting cost have already been checked.
	 * 
	 * @param quester Caster
	 * @param location Location of Target
	 * @param entity Target
	 */
	public abstract void castAbility(Quester quester, Location location,
			LivingEntity entity);
	
	/**
	 * Disable the ability
	 */
	public void disable() {
		enabled = false;
	}
	
	/**
	 * Enable the ability.
	 * 
	 * @param quester Quester enabling the ability
	 */
	public void enable(Quester quester) {
		if (quester.canCast(getRealManaCost())) {
			enabled = true;
			quester.sendMessage(getName() + " enabled");
		} else {
			quester.sendMessage("Not Enough Mana");
		}
	}
	
	public boolean equals(String name) {
		return name.equals(getName());
	}
	
	/**
	 * Get the casting time of the spell that restricts
	 * how often it can be cast.
	 * @return
	 */
	public int getCastTime() {
		return 0;
	}

	public abstract SkillClass getClassType();

	/**
	 * Gets the distance between a Player and the entity.
	 * 
	 * @param player
	 * @param entity
	 * @return distance between player and entity
	 */
	protected int getDistance(Player player, LivingEntity entity) {
		return (int)MineQuest.distance(player.getLocation(), entity.getLocation());
	}
	
	/**
	 * Gets the entities within a area of a player. name
	 * 
	 * Not Implemented in bukkit yet!
	 * 
	 * @param player
	 * @param radius
	 * @return List of Entities within the area
	 */
	public static List<LivingEntity> getEntities(LivingEntity entity, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList = entity.getWorld().getLivingEntities();
		int i;
		
		for (i = 0; i < serverList.size(); i++) {
			if ((MineQuest.distance(entity.getLocation(), serverList.get(i).getLocation()) <= radius) 
				&& (serverList.get(i).getEntityId() != entity.getEntityId())) {
				entities.add(serverList.get(i));
			}
		}
		
		return entities;
	}
	
	/**
	 * Gets the entities within a area of a player. name
	 * 
	 * Not Implemented in bukkit yet!
	 * 
	 * @param player
	 * @param radius
	 * @return List of Entities within the area
	 */
	public static List<LivingEntity> getEntities(Location location, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList = location.getWorld().getLivingEntities();
		int i;
		
		for (i = 0; i < serverList.size(); i++) {
			if ((MineQuest.distance(location, serverList.get(i).getLocation()) <= radius)) {
				entities.add(serverList.get(i));
			}
		}
		
		return entities;
	}
	
	/**
	 * Get the experience gained from using this ability.
	 * 
	 * @return
	 */
	private int getExp() {
		// TODO: Fix getExp in Ability.java
		return 30;
	}
	
	/**
	 * Get the spell components of casting the ability.
	 * Must be overloaded by all abilities that have 
	 * components.
	 * 
	 * @return
	 */
	public abstract List<ItemStack> getManaCost();
	
	/**
	 * Get the name of the Ability
	 * 
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Gets a random entity within the radius of the entity
	 * 
	 * @param entity
	 * @param radius
	 * @return
	 */
	protected LivingEntity getRandomEntity(LivingEntity entity, int radius) {
		List<LivingEntity> entities = getEntities(entity, radius);
		int i = myclass.getGenerator().nextInt(entities.size());
		
		return entities.get(i);
	}
	
	public List<ItemStack> getRealManaCost() {
		List<ItemStack> cost = getManaCost();

		int i;
		for (i = 0; i < (getReqLevel() / 4); i++) {
			cost.add(new ItemStack(Material.REDSTONE, 1));
		}

		return cost;
	}
	
	public abstract int getReqLevel();
	
	/**
	 * Gives the casting cost back to the player.
	 * 
	 * @param player
	 */
	@SuppressWarnings("deprecation")
	protected void giveManaCost(Player player) {
		List<ItemStack> cost = getRealManaCost();
		int i;
		
		for (i = 0; i < cost.size(); i++) {
			player.getInventory().addItem(cost.get(i));
		}
		player.updateInventory();
	}
	
	/**
	 * Checks if the itemStack is bound to this ability.
	 * 
	 * @param itemStack
	 * @return true if bound
	 */
	public boolean isBound(ItemStack itemStack) {
		return (bind == itemStack.getTypeId());
	}
	
	/**
	 * Checks if ability is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * This was used to determine if entities are part of type
	 * for purge spells.
	 * 
	 * @param livingEntity
	 * @param type
	 * @return
	 */
	private boolean isType(LivingEntity livingEntity, PurgeType type) {
		switch (type) {
		case ZOMBIE:
			return livingEntity instanceof CraftZombie;
		case SPIDER:
			return livingEntity instanceof CraftSpider;
		case SKELETON:
			return livingEntity instanceof CraftSkeleton;
		case CREEPER:
			return livingEntity instanceof CraftCreeper;
		case ANIMAL:
			return livingEntity instanceof CraftAnimals;
		default:
			return true;
		}
	}
	
	/**
	 * Determines if player and baseEntity are within radius distance
	 * of each other.
	 * 
	 * @param player
	 * @param baseEntity
	 * @param radius
	 * @return true if within radius
	 */
	protected boolean isWithin(LivingEntity player, LivingEntity baseEntity, int radius) {
		return MineQuest.distance(player.getLocation(), baseEntity.getLocation()) < radius;
	}

	/**
	 * Moves the entity other so that it is not within the distance
	 * specified of the player. It keeps the direction of location
	 * other with respect to player.
	 * 
	 * @param player
	 * @param other
	 * @param distance
	 */
	private void moveOut(LivingEntity player, LivingEntity other,
			int distance) {
		double x, z;
		double unit_fix;
		
		x = other.getLocation().getX() - player.getLocation().getX();
		z = other.getLocation().getZ() - player.getLocation().getZ();
		unit_fix = Math.sqrt(x*x + z*z);
		
		x *= distance / unit_fix;
		z *= distance / unit_fix;
		
		other.teleport(new Location(other.getWorld(), x + player.getLocation().getX(), 
				(double)getNearestY(player.getWorld(), (int)(x + player.getLocation().getX()), 
				(int)other.getLocation().getY(), (int)(z + player.getLocation().getZ())), 
				z + player.getLocation().getZ()));
		
		return;
	}
	
	public void notify(Quester quester, String message) {
		Calendar now = Calendar.getInstance();
		
		if ((now.getTimeInMillis() - last_msg) > 2000) {
			last_msg = now.getTimeInMillis();
			quester.sendMessage(message);
		}		
	}

	/**
	 * Parse any affects of the ability being activated as part
	 * of an attack motion.
	 * 
	 * @param quester
	 * @param defend
	 * @return true if attack damage should be negated
	 */
	public boolean parseAttack(Quester quester, LivingEntity defend) {
		useAbility(quester, defend.getLocation(), defend);
		
		if ((getName().equals("Fire Arrow") || getName().equals("PowerStrike"))) {
			return false;
		}
		
		return true;
	}

	/**
	 * Parse any affects of the ability being activated
	 * as part of a left click.
	 * 
	 * @param quester
	 * @param block
	 */
	public void parseClick(Quester quester, Block block) {
		useAbility(quester, block.getLocation(), null);
	}

	/**
	 * Moves all entities of given type outside of the distance specified
	 * from the entity passed. 
	 * 
	 * @param player
	 * @param distance
	 * @param type
	 */
	protected void purgeEntities(LivingEntity player, int distance, PurgeType type) {
		List<LivingEntity> entities = getEntities(player, distance);
		
		int i;
		
		for (i = 0; i < entities.size(); i++) {
			if (isType(entities.get(i), type)) {
				moveOut(player, entities.get(i), distance);
				MineQuest.getMob(entities.get(i)).damage(1, MineQuest.getQuester(player));
			}
		}
		
	}

	public void setSkillClass(SkillClass skillclass) {
		this.myclass = skillclass;
	}

	public void silentBind(Quester quester, ItemStack itemStack) {
		bind = itemStack.getTypeId();
	}

	/**
	 * Clears bindings for this ability.
	 * @param player
	 */
	public void silentUnBind(Quester quester) {
		bind = -1;
		MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + " WHERE abil='" + getName() + "'");
	}

	/**
	 * Clears bindings for this ability.
	 * @param player
	 */
	public void unBind(Quester quester) {
		bind = -1;
		MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + " WHERE abil='" + getName() + "'");
		quester.sendMessage(getName() + " is now unbound");
	}
	
	/**
	 * This activates non-passive abilities. First makes sure that
	 * the ability is enabled, can be cast, and is bound. Then will
	 * call castAbility.
	 * 
	 * @param quester Caster
	 * @param location Location of Target
	 * @param l 1 for left click, 0 for right click
	 * @param entity Target
	 */
	public void useAbility(Quester quester, Location location, LivingEntity entity) {
		Player player = quester.getPlayer();
		
		if (this instanceof PassiveAbility) {
			notify(quester, getName() + " is a passive ability");
			return;
		}
		
		if (!enabled) {
			notify(quester, getName() + " is not enabled");
			return;
		}
		
		if ((quester == null) || quester.canCast(getRealManaCost())) {
			if (canCast() || (player == null)) {
				notify(quester, "Casting " + getName());
				castAbility(quester, location, entity);
				if (myclass != null) {
					myclass.expAdd(getExp());
				}
			} else {
				if (player != null) {
					giveManaCost(player);
					notify(quester, "You cast that too recently");
				}
			}
		} else {
			if (player != null) {
				notify(quester, "You do not have the materials to cast that - try /spellcomp " + getName());
			}
		}
	}

	public void eventActivate() {
		
	}
}
