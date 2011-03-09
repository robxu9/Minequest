package org.monk.MineQuest.Ability;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.monk.MineQuest.Quester.SkillClass.Combat.Archer;
import org.monk.MineQuest.Quester.SkillClass.Combat.PeaceMage;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;
import org.monk.MineQuest.Quester.SkillClass.Combat.Warrior;
import org.monk.MineQuest.Quester.SkillClass.Resource.Miner;

/**
 * This is the base class for all abilities in MineQuest.
 * 
 * @author jmonk
 *
 */
public class Ability {
	/**
	 * Creates an instance of the proper ability based on name
	 * and returns it as an Ability.
	 * 
	 * @param name Name of Ability
	 * @param myclass Instance of SkillClass holding the Ability
	 * @return new Ability created
	 */
	static public Ability newAbility(String name, SkillClass myclass) {
		for (Ability ability : newAbilities(name, myclass)) {
			if (name.equalsIgnoreCase(ability.getName())) {
				return ability;
			}
		}
		MineQuest.log("Warning: Could not find ability " + name);
		
		return new Ability(myclass);
	}
	
	static public List<Ability> newAbilities(String name, SkillClass myclass) {
		List<Ability> abilities = new ArrayList<Ability>();
		
		if ((myclass == null) || (myclass instanceof Warrior)) {
			abilities.add(new AbilityPowerstrike(myclass));
			abilities.add(new AbilityDodge(myclass));
			abilities.add(new AbilityDeathblow(myclass));
			abilities.add(new AbilitySprint(myclass));
		} 
		if ((myclass == null) || (myclass instanceof Archer)) {
			abilities.add(new AbilityDodge(myclass));
			abilities.add(new AbilitySprint(myclass));
			abilities.add(new AbilityFireArrow(myclass));
			abilities.add(new AbilityRepulsion(myclass));
			abilities.add(new AbilityHailofArrows(myclass));
		} 
		if ((myclass == null) || (myclass instanceof WarMage)) {
			abilities.add(new AbilityFireball(myclass));
			abilities.add(new AbilityWallofFire(myclass));
			abilities.add(new AbilityFireChain(myclass));
			abilities.add(new AbilityFireResistance(myclass));
			abilities.add(new AbilityDrainLife(myclass));
			abilities.add(new AbilityIceSphere(myclass));
			abilities.add(new AbilityTrap(myclass));
		}
		if ((myclass == null) || (myclass instanceof PeaceMage)) {
			abilities.add(new AbilityHeal(myclass));
			abilities.add(new AbilityHealOther(myclass));
			abilities.add(new AbilityHealAura(myclass));
			abilities.add(new AbilityDamageAura(myclass));
			abilities.add(new AbilityCurePoison(myclass));
			abilities.add(new AbilityCurePoisonOther(myclass));
			abilities.add(new AbilityTrape(myclass));
			abilities.add(new AbilityWallofWater(myclass));
		}
		if ((myclass == null) || (myclass instanceof Miner)) {
			abilities.add(new AbilityLavaToWater(myclass));
		}
		
		return abilities;
	}
	
	
	
	public int getReqLevel() {
		return 0;
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
			} while (((world.getBlockAt(x, i, z).getType() != Material.SNOW) && (world.getBlockAt(x, i, z).getType() != Material.AIR)) && (i < 1000));
			if (i == 1000) i = 0;
		} else {
			do {
				i--;
			} while (((world.getBlockAt(x, i, z).getType() == Material.SNOW) || (world.getBlockAt(x, i, z).getType() == Material.AIR)) && (i > -100));
			if (i == -100) i = 0;
			i++;
		}
		
		return i;
	}
	protected int bind;
	protected int cast_time;
	protected int count;
	protected boolean enabled;
	protected SkillClass myclass;
	protected long time;
	protected long last_msg;
	
	/**
	 * Creates an Ability
	 * 
	 * @param name Name of Ability
	 * @param myclass SkillClass that holds Ability
	 */
	public Ability(SkillClass myclass) {
		Calendar now = Calendar.getInstance();
		enabled = true;
		if (this instanceof PassiveAbility) enabled = false;
		this.myclass = myclass;
		count = 0;
		cast_time = getCastTime();
		bind = -1;
		time = now.getTimeInMillis();
		last_msg = 0;
	}
	
	public void notify(Quester quester, String message) {
		Calendar now = Calendar.getInstance();
		
		if ((now.getTimeInMillis() - last_msg) > 2000) {
			last_msg = now.getTimeInMillis();
			quester.sendMessage(message);
		}		
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
		if (quester.canCast(getManaCost())) {
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
	public List<LivingEntity> getEntities(LivingEntity player, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList = player.getWorld().getLivingEntities();
		int i;
		
		for (i = 0; i < serverList.size(); i++) {
			if ((MineQuest.distance(player.getLocation(), serverList.get(i).getLocation()) < radius) 
				&& (serverList.get(i).getEntityId() != player.getEntityId())) {
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
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		return list;
	}
	
	/**
	 * Get the name of the Ability
	 * 
	 * @return
	 */
	public String getName() {
		return "Generic Blank Ability";
	}
	
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
	
	/**
	 * Gives the casting cost back to the player.
	 * 
	 * @param player
	 */
	protected void giveManaCost(Player player) {
		List<ItemStack> cost = getManaCost();
		int i;
		
		for (i = 0; i < cost.size(); i++) {
			player.getInventory().addItem(cost.get(i));
		}
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
	 * Checks if this ability affects defending.
	 * 
	 * @return true if defending
	 */
	public boolean isDefending() {
		return false;
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
	private boolean isType(LivingEntity livingEntity, int type) {
		if (type == 1) {
			return livingEntity instanceof CraftZombie;
		} else if (type == 2) {
			return livingEntity instanceof CraftSkeleton;
		} else if (type == 3) {
			return livingEntity instanceof CraftCreeper;
		} else if (type == 4) {
			return livingEntity instanceof CraftSpider;
		} else {
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
		
		other.teleportTo(new Location(other.getWorld(), x + player.getLocation().getX(), 
				(double)getNearestY(player.getWorld(), (int)(x + player.getLocation().getX()), 
				(int)other.getLocation().getY(), (int)(z + player.getLocation().getZ())), 
				z + player.getLocation().getZ()));
		
		return;
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
	 * Parse any affects of the ability being activated as part
	 * of a defense.
	 * 
	 * @param quester Defender
	 * @param mob Attacker
	 * @param amount Damage to be taken
	 * @return amount of damage that should be negated
	 */
	public int parseDefend(Quester quester, LivingEntity mob, int amount) {
		return 0;
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
	protected void purgeEntities(LivingEntity player, int distance, int type) {
		List<LivingEntity> entities = getEntities(player, distance);
		
		int i;
		
		for (i = 0; i < entities.size(); i++) {
			if (isType(entities.get(i), type)) {
				moveOut(player, entities.get(i), distance);
				MineQuest.getMob(entities.get(i)).damage(1);
			}
		}
		
	}

	/**
	 * Clears bindings for this ability.
	 * @param player
	 */
	public void unBind(Quester quester) {
		bind = 0;
		MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + " WHERE abil='" + getName() + "'");
		quester.sendMessage(getName() + " is now unbound");
	}

	/**
	 * Clears bindings for this ability.
	 * @param player
	 */
	public void silentUnBind(Quester quester) {
		bind = 0;
		MineQuest.getSQLServer().update("DELETE FROM " + quester.getName() + " WHERE abil='" + getName() + "'");
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
		
		if (!enabled) {
			notify(quester, getName() + " is not enabled");
			return;
		}
		
		if ((quester == null) || quester.canCast(getManaCost())) {
			if (canCast() || (player == null)) {
				if ((player == null) || player.getItemInHand().getTypeId() == bind) {
					notify(quester, "Casting " + getName());
					castAbility(quester, location, entity);
					if (myclass != null) {
						myclass.expAdd(getExp());
					}
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

	/**
	 * This is called when non-passive abilities are activated.
	 * Must be overloaded for all non-passive abilities. Binding
	 * and casting cost have already been checked.
	 * 
	 * @param quester Caster
	 * @param location Location of Target
	 * @param entity Target
	 */
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		MineQuest.log("Warning: Generic Cast Called");
	}

	public void silentBind(Quester quester, ItemStack itemStack) {
		bind = itemStack.getTypeId();
	}
}
