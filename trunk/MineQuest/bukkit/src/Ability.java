
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
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Ability {
	static public int getNearestY(int x, int y, int z) {
		int i = y;
		Server server = MineQuest.getSServer();
		
		if (server.getWorlds().length > 1) {
			MineQuest.log("[CONFUSED] Multiple worlds found...");
		}
		
		World world = server.getWorlds()[0];
		
		if (world.getBlockAt(x, y, z).getTypeId() != 0) {
			do {
				i++;
			} while (((world.getBlockAt(x, i, z).getTypeId() != 78) && (world.getBlockAt(x, i, z).getTypeId() != 0)) && (i < 1000));
			if (i == 1000) i = 0;
		} else {
			do {
				i--;
			} while (((world.getBlockAt(x, i, z).getTypeId() == 78) || (world.getBlockAt(x, i, z).getTypeId() == 0)) && (i > -100));
			if (i == -100) i = 0;
			i++;
		}
		
		return i;
	}
	private int bindl;
	private int bindr;
	private int cast_time;
	private int count;
	private boolean enabled;
	private SkillClass myclass;
	private String name;
	private long time;
	
	public Ability(String name, SkillClass myclass) {
		this.name = name;
		enabled = true;
		this.myclass = myclass;
		count = 0;
		if (name.equals("Dodge")) {
			enabled = false;
		}
		cast_time = getCastTime();
		bindl = -1;
		bindr = -1;
		//time = MineQuest.getSServer().getTime();
	}
	
	public void bindl(Player player, ItemStack item) {
		bindl = item.getTypeId();
		player.sendMessage(name + " is now bound to Left " + item.getTypeId());
	}

	public void bindr(Player player, ItemStack item) {
		bindr = item.getTypeId();
		player.sendMessage(name + " is now bound to Right " + item.getTypeId());
	}
	
	private boolean canCast() {
		if ((MineQuest.getSServer().getTime() - time) > getCastTime()) {
			time = MineQuest.getSServer().getTime();
			return true;
		}
		return false;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void enable(Quester quester) {
		if (quester.canCast(null)) {
			enabled = true;
			quester.sendMessage(name + " enabled");
		} else {
			quester.sendMessage("Not Enough Mana");
		}
	}

	public boolean equals(String name) {
		return name.equals(this.name);
	}

	public int getCastTime() {
		if (name.equals("Heal") || name.equals("Heal Other")) {
			return 600;
		} else if (name.equals("Cure Poison") || name.equals("Cure Poison Other") || name.equals("Trap") || name.equals("Trape")) {
			return 300;
		}
		return 0;
	}
	
	private int getDistance(Player player, LivingEntity entity) {
		double x, y, z;
		x = player.getLocation().getX() - entity.getLocation().getX();
		y = player.getLocation().getY() - entity.getLocation().getY();
		z = player.getLocation().getZ() - entity.getLocation().getZ();
		
		return (int)Math.sqrt(x*x + y*y + z*z);
	}
	
	public List<LivingEntity> getEntities(LivingEntity player, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList;
		int i;
		
//		for (i = 0; i < serverList.size(); i++) {
//			if ((isWithin(player, serverList.get(i), radius)) && (serverList.get(i).getId() != player.getId())) {
//				entities.add(serverList.get(i));
//			}
//		}
		
		return entities;
	}
	
	private int getExp() {
		// TODO: Fix getExp in Ability.java
		return 30;
	}
	
	public List<ItemStack> getManaCost(Player player, LivingEntity entity) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		if (name.equals("Dodge")) {
			list.add(new ItemStack(288, 1));
			list.add(new ItemStack(288, 1));
			list.add(new ItemStack(288, 1));
			list.add(new ItemStack(288, 1));
			list.add(new ItemStack(288, 1));
		} else if (name.equals("Fireball")) {
			list.add(new ItemStack(263, 1));
		} else if (name.equals("Deathblow")) {
			list.add(new ItemStack(265, 1));
			list.add(new ItemStack(265, 1));
		} else if (name.equals("Sprint")) {
			list.add(new ItemStack(288, 1));
		} else if (name.equals("PowerStrike")) {
			list.add(new ItemStack(268, 1));
		} else if (name.equals("Hail of Arrows")) {
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
			list.add(new ItemStack(262, 1));
		} else if (name.equals("Fire Arrow")) {
			list.add(new ItemStack(263, 1));
		} else if (name.equals("Repulsion")) {
			list.add(new ItemStack(50, 1));
			list.add(new ItemStack(81, 1));
		} else if (name.equals("FireChain")) {
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
		} else if (name.equals("Wall of Fire")) {
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(263, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
		} else if (name.equals("IceSphere")) {
			list.add(new ItemStack(332, 1));
			list.add(new ItemStack(332, 1));
			list.add(new ItemStack(332, 1));
			list.add(new ItemStack(332, 1));
			list.add(new ItemStack(332, 1));
		} else if (name.equals("Drain Life")) {
			list.add(new ItemStack(89, 1));
		} else if (name.equals("Fire Resistance")) {
			list.add(new ItemStack(87, 1));
		} else if (name.equals("Trap") || name.equals("Trape")) {
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(269, 1));
		} else if (name.equals("Heal")) {
			list.add(new ItemStack(326, 1));
		} else if (name.equals("Heal Other")) {
			list.add(new ItemStack(326, 1));
		} else if (name.equals("Wall of Water")) {
			list.add(new ItemStack(326, 1));
			list.add(new ItemStack(326, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
			list.add(new ItemStack(3, 1));
		} else if (name.equals("Heal Aura")) {
			list.add(new ItemStack(297, 1));
			list.add(new ItemStack(297, 1));
		} else if (name.equals("Damage Aura")) {
			list.add(new ItemStack(259, 1));
		} else if (name.equals("Cure Poison")) {
			list.add(new ItemStack(39, 1));
		} else if (name.equals("Cure Poison Other")) {
			list.add(new ItemStack(39, 1));
		}
		
		return list;
	}
	
	public String getName() {
		return name;
	}
	
	private LivingEntity getRandomEntity(LivingEntity entity, int radius) {
		List<LivingEntity> entities = getEntities(entity, radius);
		int i = myclass.getGenerator().nextInt(entities.size());
		
		return entities.get(i);
	}
	
	private void giveManaCost(Player player) {
		List<ItemStack> cost = getManaCost(player, null);
		int i;
		
		for (i = 0; i < cost.size(); i++) {
			player.getInventory().addItem(cost.get(i));
		}
	}

	public boolean isBound(ItemStack itemStack) {
		return (bindl == itemStack.getTypeId()) || (bindr == itemStack.getTypeId());
	}
	
	public boolean isBoundL(ItemStack itemInHand) {
		return (bindl == itemInHand.getTypeId());
	}
	
	public boolean isBoundR(ItemStack itemStack) {
		return (bindr == itemStack.getTypeId());
	}
	
	public boolean isDefending(Quester quester, LivingEntity mob) {
		if (name.equals("Dodge")) {
			return true;
		}
		return false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private boolean isType(LivingEntity livingEntity, int type) {
		/*if (type == 1) {
			return livingEntity.getName().equals("Zombie");
		} else if (type == 2) {
			return livingEntity.getName().equals("Skeleton");
		} else if (type == 3) {
			return livingEntity.getName().equals("Creeper");
		} else if (type == 4) {
			return livingEntity.getName().equals("Spider");
		} else {
			return true;
		}*/
		return false;
	}


	private boolean isWithin(LivingEntity player, LivingEntity baseEntity, int radius) {
		double x, y, z;
		
		x = player.getLocation().getX() - baseEntity.getLocation().getX();
		y = player.getLocation().getY() - baseEntity.getLocation().getY();
		z = player.getLocation().getZ() - baseEntity.getLocation().getZ();
		
		return Math.sqrt(x*x + y*y + z*z) < radius;
	}
	
	private void moveOut(LivingEntity player, LivingEntity other,
			int distance) {
		double x, y, z;
		double unit_fix;
		
		x = other.getLocation().getX() - player.getLocation().getX();
		z = other.getLocation().getZ() - player.getLocation().getZ();
		unit_fix = Math.sqrt(x*x + z*z);
		
		x *= distance / unit_fix;
		z *= distance / unit_fix;
		
		y = MineQuest.getSServer().getWorlds()[0].getHighestBlockYAt((int)x, (int)z) + 1;

		other.teleportTo(new Location(other.getWorld(), x + player.getLocation().getX(), 
				(double)getNearestY((int)(x + player.getLocation().getX()), 
				(int)other.getLocation().getY(), (int)(z + player.getLocation().getZ())), 
				z + player.getLocation().getZ()));
		
		return;
	}

	public boolean parseAttack(Quester quester, LivingEntity defend) {
		Player player = quester.getPlayer();
		if ((player.getItemInHand().getTypeId() == 261) || (player.getItemInHand().getTypeId() == 332)) {
			useAbility(quester, defend.getLocation(), 0, defend);
		} else {
			useAbility(quester, defend.getLocation(), 1, defend);
		}
		if ((name.equals("Fire Arrow") || name.equals("PowerStrike"))) {
			return false;
		}
		
		return true;
	}

	public int parseDefend(Quester quester, LivingEntity mob, int amount) {
		if (!enabled) return 0;
		Player player = quester.getPlayer();
		
		if (name.equals("Dodge")) {
			if (myclass.getGenerator().nextDouble() < (.01 + (.0025 * myclass.getLevel()))) {
				double rot = player.getLocation().getYaw() % 360;
				while (rot < 0) rot += 360;
				
				if ((rot  < 45) && (rot > 315)) {
					player.sendMessage("Dodging1");
					player.teleportTo(new Location(player.getWorld(), 
							(int)player.getLocation().getX() - 1, 
							getNearestY((int)player.getLocation().getX(), 
									(int)player.getLocation().getY(), 
									(int)player.getLocation().getZ()),
							player.getLocation().getZ(), player.getLocation().getYaw(), 
							player.getLocation().getPitch()));
				} else if ((rot > 45) && (rot < 135)) {
					player.sendMessage("Dodging2");
					player.teleportTo(new Location(player.getWorld(), 
							player.getLocation().getX(), 
							getNearestY((int)player.getLocation().getX(), 
									(int)player.getLocation().getY(), 
									(int)player.getLocation().getZ()),
							player.getLocation().getZ() - 1, player.getLocation().getYaw(), 
							player.getLocation().getPitch()));
				} else if ((rot > 135) && (rot < 225)) {
					player.sendMessage("Dodging3");
					player.teleportTo(new Location(player.getWorld(), 
							(int)player.getLocation().getX() + 1, 
							getNearestY((int)player.getLocation().getX(), 
									(int)player.getLocation().getY(), 
									(int)player.getLocation().getZ()), 
							player.getLocation().getZ(), player.getLocation().getYaw(), 
							player.getLocation().getPitch()));
				} else {
					player.sendMessage("Dodging4");
					player.teleportTo(new Location(player.getWorld(), 
							player.getLocation().getX(), 
							getNearestY((int)player.getLocation().getX(), 
									(int)player.getLocation().getY(), 
									(int)player.getLocation().getZ()),
							player.getLocation().getZ() + 1, player.getLocation().getYaw(), 
							player.getLocation().getYaw()));
				}
				return amount;
			}
		}
		
		return 0;
	}

	public void parseLeftClick(Quester quester, Block block) {
		useAbility(quester, block.getLocation(), 1, null);
	}

	public void parseRightClick(Player player, Block block, Quester quester) {
		useAbility(quester, block.getLocation(), 0, null);
	}

	private void purgeEntities(LivingEntity player, int distance, int type) {
		List<LivingEntity> entities = getEntities(player, distance);
		
		int i;
		
		for (i = 0; i < entities.size(); i++) {
			if (isType(entities.get(i), type)) {
				moveOut(player, entities.get(i), distance);
//				MineQuestListener.damageEntity(entities.get(i), 1);
			}
		}
		
	}

	public void unbind(Player player) {
		bindl = 0;
		bindr = 0;
		player.sendMessage(name + " is now unbound");
	}

	public void unBindL() {
		bindl = 0;
	}

	public void unBindR() {
		bindr = 0;
	}

	public void useAbility(Quester quester, Location location, int l, LivingEntity entity) {
		Player player = quester.getPlayer();
		
		if (!enabled) {
			player.sendMessage(name + " is not enabled");
			return;
		}
		// TODO: add DrainLife
		// TODO: add DamageAura
		// TODO: add HealAura
		// TODO: fix Repulsion 
		// TODO: fix FireChain
		
		if ((quester == null) || quester.canCast(getManaCost(player, entity))) {
			if (canCast() || (player == null)) {
				if ((player == null) || player.getItemInHand().getTypeId() == ((l == 1)?bindl:bindr)) {
					player.sendMessage("Casting " + name);
					if (name.equals("Fireball")) {
						double leftx, leftz;
						int x, z;
						if ((location.getX() == 0) && (location.getY() == 0) && (location.getZ() == 0)) {
							giveManaCost(player);
							return;
						}
						leftx = location.getX() % 1;
						leftz = location.getZ() % 1;
						x = (leftx < .5)?-1:1;
						z = (leftz < .5)?-1:1;
						
						Block nblock = player.getWorld().getBlockAt((int)location.getX(), 
								getNearestY((int)location.getX(), (int)location.getY(), (int)location.getZ()), 
								(int)location.getZ());
						nblock.setTypeId(51);
						
						nblock = player.getWorld().getBlockAt((int)location.getX() + x, 
								getNearestY((int)location.getX() + x, (int)location.getY(), (int)location.getZ()), 
								(int)location.getZ());
						nblock.setTypeId(51);
						
						nblock = player.getWorld().getBlockAt((int)location.getX() + x, 
								getNearestY((int)location.getX() + x, (int)location.getY(), (int)location.getZ() + z), 
								(int)location.getZ() + z);
						nblock.setTypeId(51);
						
						nblock = player.getWorld().getBlockAt((int)location.getX(), 
								getNearestY((int)location.getX(), (int)location.getY(), (int)location.getZ() + z), 
								(int)location.getZ() + z);
						nblock.setTypeId(51);
					} else if (name.equals("PowerStrike")) {
						if (entity != null) {
							entity.setHealth(entity.getHealth() - 10);
						} else {
							giveManaCost(player);
							player.sendMessage("PowerStrike must be bound to an attack");
							return;
						}
					} else if (name.equals("Sprint")) {
						Location loc = player.getLocation();
						double rot = loc.getYaw() % 360 - 90;
						while (rot < 0) rot += 360;
						
						if ((rot  < 45) || (rot > 315)) {
							loc.setX(loc.getX() - 1);
						} else if ((rot > 45) && (rot < 135)) {
							loc.setZ(loc.getZ() - 1);
						} else if ((rot > 135) && (rot < 225)) {
							loc.setX(loc.getX() + 1);
						} else {
							loc.setZ(loc.getZ() + 1);
						}
						player.teleportTo(loc);
					} else if (name.equals("Hail of Arrows")) {
						if (entity != null) {
							Location start = entity.getLocation();
							start.setZ(start.getZ() - 3);
							start.setY(start.getY() + 5);
							Vector vel = new Vector(0, -5, 0);
							int i, j;
							
							for (i = 0; i < 3; i++) {
								start.setX(entity.getLocation().getX() - 3);
								for (j = 0; j < 3; j++) {
									entity.getWorld().spawnArrow(start, vel, (float).8, (float)12.0);
									start.setX(start.getX() + 3);
								}
								start.setZ(start.getZ() + 3);
							}
						} else {
							giveManaCost(player);
							player.sendMessage("Hail of Arrows must be bound to an attack - Recommended that it is ranged");
							return;
						}
					} else if (name.equals("Fire Arrow")) {
						if ((entity != null) && (player.getInventory().getItemInHand().getTypeId() == 261)) {
							Location loc = entity.getLocation();
							Block block = player.getWorld().getBlockAt((int)loc.getX(), 
									getNearestY((int)location.getX(), (int)location.getY(), (int)location.getZ()), 
									(int)loc.getZ());
							block.setTypeId(51);
							
						} else {
							giveManaCost(player);
							player.sendMessage("Fire Arrow must be bound to a bow attack - Recommended that it is ranged");
							return;
						}
//					} else if (name.equals("Repulsion")) {
//						purgeEntities(player, 10, 0);
//					} else if (name.equals("FireChain")) {
//						if (entity != null) {
//							Ability fireball = new Ability("Fireball", myclass);
//							LivingEntity this_entity;
//							int i;
//							
//							fireball.enable(quester);
//							fireball.bindl(player, new ItemStack(player.getItemStackInHand(), 1));
//							
//							this_entity = getRandomEntity(entity, 10);
//							for (i = 0; i < 3 + myclass.getCasterLevel(); i++) {
//								fireball.useAbility(null, new Block(1, (int)entity.getX(), (int)entity.getY(), (int)entity.getZ()), 
//										null, 1, entity);
//								this_entity = getRandomEntity(this_entity, 10);
//								if (myclass.getGenerator().nextDouble() < .1) {
//									break;
//								}
//							}
//						} else {
//							giveManaCost(player);
//							player.sendMessage("FireChain must be bound to an attack");
//							return;
//						}		
					} else if (name.equals("Wall of Fire")) {
						double rot = player.getLocation().getYaw() % 360 - 90;
						int x_change, z_change;
						int x, z;
						int i;
						while (rot < 0) rot += 360;
						
						
						if ((rot  < 45) || (rot > 315)) {
							x_change = 0;
							z_change = 1;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() - 4;
						} else if ((rot > 45) && (rot < 135)) {
							x_change = 1;
							z_change = 0;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() - 3;
						} else if ((rot > 135) && (rot < 225)) {
							x_change = 0;
							z_change = 1;
							x = (int)player.getLocation().getX() + 1;
							z = (int)player.getLocation().getZ() - 4;
						} else {
							x_change = 1;
							z_change = 0;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() + 1;
						}
						
						World world = player.getWorld();
						for (i = 0; i < 7; i++) {
							Block nblock = world.getBlockAt(x, getNearestY(x, (int)player.getLocation().getY(), z), z);
							nblock.setTypeId(51);
							x += x_change;
							z += z_change;
						}
					} else if (name.equals("Wall of Water")) {
						double rot = player.getLocation().getYaw() % 360 - 90;
						int x_change, z_change;
						int x, z;
						int i;
						
						player.getInventory().addItem(new ItemStack(325, 1));
						player.getInventory().addItem(new ItemStack(325, 1));
						
						while (rot < 0) rot += 360;
						
						
						if ((rot  < 45) || (rot > 315)) {
							x_change = 0;
							z_change = 1;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() - 4;
						} else if ((rot > 45) && (rot < 135)) {
							x_change = 1;
							z_change = 0;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() - 3;
						} else if ((rot > 135) && (rot < 225)) {
							x_change = 0;
							z_change = 1;
							x = (int)player.getLocation().getX() + 1;
							z = (int)player.getLocation().getZ() - 4;
						} else {
							x_change = 1;
							z_change = 0;
							x = (int)player.getLocation().getX() - 4;
							z = (int)player.getLocation().getZ() + 1;
						}

						World world = player.getWorld();
						for (i = 0; i < 7; i++) {
							Block nblock = world.getBlockAt(x, getNearestY(x, (int)player.getLocation().getY(), z), z);
							nblock.setTypeId(8);
							x += x_change;
							z += z_change;
						}
					} else if (name.equals("Cure Poison")) {
						if (quester.isPoisoned()) {
							quester.curePoison();
						} else {
							player.sendMessage("Quester must not be poisoned to cure poison");
							return;
						}
					} else if (name.equals("Heal")) {
						player.getInventory().addItem(new ItemStack(325, 1));
						if (quester.getHealth() < quester.getMaxHealth()) {
							quester.setHealth(player.getHealth() + myclass.getCasterLevel() + myclass.getGenerator().nextInt(8) + 1);
						} else {
							player.sendMessage("Quester must not be at full health to heal");
							return;
						}
					} else if (name.equals("Drain Life")) {
						int drain = myclass.getGenerator().nextInt(3 + myclass.getCasterLevel()) + 1;
						if (entity != null) {
							entity.setHealth(entity.getHealth() - drain);
							quester.setHealth(player.getHealth() + drain);
						} else {
							player.sendMessage("Must be called on an Entity");
						}
					} else if (name.equals("Trap") || name.equals("Trape")) {
						int i, j, k;
						int x, y, z;
						if (entity == null) {
							giveManaCost(player);
							player.sendMessage("Trap must be used on a living entity");
							return;
						}
						World world = entity.getWorld();
						x = (int)entity.getLocation().getX();
						y = (int)entity.getLocation().getY();
						z = (int)entity.getLocation().getZ();
						
						for (i = 1; i < 3; i++) {
							for (j = -1; j < 2; j++) {
								for (k = -1; k < 2; k++) {
									Block nblock = world.getBlockAt(x + j, y - i, z + k);
									
									nblock.setTypeId(0);
								}
							}
						}
					} else if (name.equals("Cure Poison Other")) {
						if (entity instanceof Player) {
							Quester other = MineQuest.getQuester((Player)entity);
							if (other != null) {
								if (other.isPoisoned()) {
									other.curePoison();
								} else {
									player.sendMessage("Quester must be poisoned to Cure Poison");
									return;
								}
							} else {
								giveManaCost(player);
								player.sendMessage("entity is not a Quester");
								return;
							}
						} else {
							giveManaCost(player);
							player.sendMessage(name + " must be cast on another player");
						}
					} else if (name.equals("Heal Other")) {
						if (entity instanceof Player) {
							Quester other = MineQuest.getQuester((Player)entity);
							if (other != null) {
								player.getInventory().addItem(new ItemStack(325, 1));
								if (other.getHealth() < other.getMaxHealth()) {
									other.setHealth(other.getPlayer().getHealth() + myclass.getCasterLevel() + myclass.getGenerator().nextInt(8) + 1);
								} else {
									player.sendMessage("Quester must not be at full health to heal");
									return;
								}
							} else {
								giveManaCost(player);
								player.sendMessage("entity is not a Quester");
								return;
							}
						} else {
							player.sendMessage(name + " must be cast on another player");
						}
					} else if (name.equals("IceSphere")) {
						int j,k;
						if (entity == null) {
							player.sendMessage("Cannot cast on null entity");
							giveManaCost(player);
							return;
						}
						
						World world = entity.getWorld();
						for (j = -1; j < 2; j++) {
							for (k = -1; k < 2; k++) {
								Block nblock = world.getBlockAt((int)location.getX() + j, 
										getNearestY((int)location.getX() + j, (int)location.getY() + ((entity == null)?1:0), (int)location.getZ() + k), 
										(int)location.getZ() + k);
								nblock.setTypeId(78);
							}
						}
						
						entity.setHealth(entity.getHealth() - 3 - (myclass.getCasterLevel() / 2));
					} else {
						return;
					}
					if (myclass != null) {
						myclass.expAdd(getExp(), quester);
					}
				}
			} else {
				if (player != null) {
					giveManaCost(player);
					player.sendMessage("You cast that too recently");
				}
			}
		} else {
			if (player != null) {
				player.sendMessage("You do not have the materials to cast that - try /spellcomp " + name);
			}
		}
	}
}
