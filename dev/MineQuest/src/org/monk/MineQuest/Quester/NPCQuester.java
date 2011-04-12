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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Event.NPCEvent;
import org.monk.MineQuest.Event.Absolute.SpawnNPCEvent;
import org.monk.MineQuest.Quest.QuestProspect;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.NpcSpawner;

public class NPCQuester extends Quester {
	private Location center;
	private int count = 0;
	private BasicHumanNpc entity;
	private Quester follow;
	private LivingEntity mobTarget;
	private NPCMode mode;
	private double rad;
	private double speed = .6;
	private Location target = null;
	private String walk_message;
	private String hit_message;
	private int radius;
	private String quest_file;
	private String town;
	
	public NPCQuester(String name) {
		super(name);
		this.entity = null;
		mobTarget = null;
		if (mode == NPCMode.FOLLOW) {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		} else {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		}
	}
	
	public NPCQuester(String name, NPCMode mode, World world, Location location) {
		this.name = name;
		this.mode = mode;
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		double pitch = location.getPitch();
		double yaw = location.getYaw();
		if (mode != NPCMode.QUEST_NPC) {
			create(mode, world, x, y, z, pitch, yaw);
			update();
		} else {
			makeNPC(world.getName(), x, y, z, (float)pitch, (float)yaw);
			health = max_health = 2000;
		}
		distance = 0;
		entity = null;
		mobTarget = null;
		if (mode == NPCMode.FOLLOW) {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		} else {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		}
	}
	
	public void activate() {
		if (health <= 0) return;
		if ((mode == NPCMode.FOLLOW) || (mode == NPCMode.PARTY) || (mode == NPCMode.GQUEST_NPC)) {
			if (mobTarget == null) {
				if ((follow != null) && (follow.getPlayer() != null)) {
					if (MineQuest.distance(follow.getPlayer().getLocation(), entity.getBukkitEntity().getLocation()) > 4) {
						setTarget(follow.getPlayer().getLocation(), 4);
					}
				}
			} else {
				if (MineQuest.distance(follow.getPlayer().getLocation(), mobTarget.getLocation()) > 1.3) {
					if (mobTarget.getHealth() <= 0) {
						mobTarget = null;
						return;
					}
					setTarget(mobTarget.getLocation(), 1.25);
				}
			}

			if (target != null) {
				if (MineQuest.distance(player.getLocation(), target) < speed) {
					double move_x = (target.getX() - player.getLocation().getX());
	//				double move_y = (target.getY() - player.getLocation().getY());
					double move_z = (target.getZ() - player.getLocation().getZ());
					float yaw = 0;
					yaw = (float)(-180 * Math.atan2(move_x , move_z) / Math.PI);
					entity.moveTo(target.getX(), target.getY(), target.getZ(), yaw, target.getPitch());
	
					target = null;
				} else {
					double distance = MineQuest.distance(player.getLocation(), target);
					double move_x = (speed * (target.getX() - player.getLocation().getX()) / distance);
					double move_y = (speed * (target.getY() - player.getLocation().getY()) / distance);
					double move_z = (speed * (target.getZ() - player.getLocation().getZ()) / distance);
					move_x += (new Random()).nextDouble() * .05;
					move_z += (new Random()).nextDouble() * .05;
	//				move_y = Ability.getNearestY(player.getWorld(), (int)(player.getLocation().getBlockX() + move_x),
	//						(int)player.getLocation().getBlockY(), 
	//						(int)(player.getLocation().getBlockZ() + move_z)) - player.getLocation().getY();
					float yaw = 0;
					yaw = (float)(-180 * Math.atan2(move_x , move_z) / Math.PI);
					entity.moveTo(
						player.getLocation().getX() + move_x,
						player.getLocation().getY() + move_y,
						player.getLocation().getZ() + move_z,
						yaw, target.getPitch());
				}
			}
			
			if (mode == NPCMode.PARTY) {
				if ((mobTarget != null) && (MineQuest.distance(mobTarget.getLocation(), player.getLocation()) < 1.2)) {
					attack(mobTarget);
				}
			}
			
			if (mode == NPCMode.GQUEST_NPC) {
				count++;
				if (count == 30) {
					setTarget(center, rad);
					count = 0;
				}
				
				if (player != null) {
					List<LivingEntity> entities = Ability.getEntities(player, radius);
					for (LivingEntity lentity : entities) {
						if (lentity instanceof Player) {
							if (!checkMessage(lentity.getEntityId())) {
								MineQuest.getQuester(lentity).sendMessage("<" + name + "> " + walk_message);
							}
						}
					}
				}
			}
		}
	}
	
    private boolean checkMessage(int id) {
	    Calendar now = Calendar.getInstance();
	    int i;
	    int delay = 15000;
	    
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
	
	private void attack(LivingEntity mobTarget) {
		entity.attackLivingEntity(mobTarget);
		((CraftHumanEntity)player).getHandle().d(((CraftLivingEntity)mobTarget).getHandle());
	}
	
	public void create(NPCMode mode, World world, double x, double y, double z, double pitch, double yaw) {
		if (mode != NPCMode.QUEST_NPC) {
			super.create();
	
			MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
					x + "', y='" + 
					y + "', z='" + 
					z + "', pitch='" + 
					pitch + "', yaw='" + 
					yaw + "', mode='" + 
					mode + "', world='" + 
					world.getName() + "' WHERE name='"
					+ name + "'");

			MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " +
					name + "_npc (property VARCHAR(30), value VARCHAR(300))");
			if (mode == NPCMode.GQUEST_NPC) {
				MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
						" (property, value) VALUES('walk_message', 'Set me Admin!!')");
				MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
						" (property, value) VALUES('hit_message', 'Set me Admin!!')");
				MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
						" (property, value) VALUES('radius', '10')");
				MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
						" (property, value) VALUES('quest', '')");
			}
		}
	}
	
	public void setTown(String town) {
		this.town = town;
		MineQuest.getSQLServer().update("DELETE FROM " + name + "_npc WHERE property='town'");
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
				" (property, value) VALUES('town', '" + town + "')");
	}
	
	@Override
	public void damage(int i) {
		super.damage(i);
	}
	
	public BasicHumanNpc getEntity() {
		return entity;
	}
	
	public NPCMode getMode() {
		return mode;
	}
	
	public LivingEntity getTarget() {
		return mobTarget;
	}
	
	@Override
	public boolean healthChange(int change, EntityDamageEvent event) {
		boolean ret = super.healthChange(change, event);
		
		if (mode == NPCMode.GOD) {
			health = max_health;
			event.setDamage(0);
		} else if (mode == NPCMode.GQUEST_NPC) {
			health = max_health;
			event.setDamage(0);
			
			if ((event instanceof EntityDamageByEntityEvent) && 
					(((EntityDamageByEntityEvent)event).getDamager() instanceof Player)) {
				Player player = (Player)((EntityDamageByEntityEvent)event).getDamager();
				MineQuest.getQuester(player).sendMessage("<" + name + "> " + hit_message);
				MineQuest.log("Quest:" + quest_file);
				MineQuest.getQuester(player).addQuestAvailable(new QuestProspect(quest_file));
			}
		} else {
			if (getHealth() <= 0) {
				setPlayer(null);
				if ((mode != NPCMode.PARTY) && (mode != NPCMode.FOR_SALE)) {
					removeSql();
					MineQuest.remQuester(this);
					NpcSpawner.RemoveBasicHumanNpc(entity);
					entity = null;
				} else {
					Location location = MineQuest.getTown(town).getNPCSpawn();

					makeNPC(location.getWorld().getName(), location.getX(),
							location.getY(), location.getZ(), location
									.getPitch(), location.getYaw());
					mode = NPCMode.FOR_SALE;
				}
			}
		}
		
		return ret;
	}
	
	private void makeNPC(String world, double x, double y, double z,
			float pitch, float yaw) {
		if (entity != null) {
			entity.getBukkitEntity().setHealth(0);
			entity = null;
		}
		MineQuest.getEventParser().addEvent(new SpawnNPCEvent(200, this, world, x, y, z, (float)pitch, (float)yaw));
	}
	
	public void questerAttack(LivingEntity entity) {
		if ((mobTarget == null) || (mobTarget.getHealth() <= 0)) {
			mobTarget = entity;
		}
	}

	private void removeSql() {
		MineQuest.getSQLServer().update("DELETE FROM questers WHERE name='" + name + "'");
		MineQuest.getSQLServer().update("DROP TABLE " + name);
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_chests");
	}
	
	@Override
	public void save() {
		if (mode == NPCMode.QUEST_NPC) return;
		if (mode == NPCMode.GQUEST_NPC) return;
		super.save();
		
		if (entity == null) return;
		entity.moveTo(center.getX(), center.getY(), center.getZ(), 
				center.getYaw(), center.getPitch());
		MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
				entity.getBukkitEntity().getLocation().getX() + "', y='" + 
				entity.getBukkitEntity().getLocation().getY() + "', z='" + 
				entity.getBukkitEntity().getLocation().getZ() + "', mode='" + 
				mode + "', world='" + 
				entity.getBukkitEntity().getWorld().getName() + "' WHERE name='"
				+ name + "'");
	}

	public void setEntity(BasicHumanNpc entity) {
		this.entity = entity;
		setPlayer(entity.getBukkitEntity());
	}

	public void setFollow(Quester quester) {
		this.follow = quester;
		target = null;
	}

	@Override
	public void setHealth(int i) {
		super.setHealth(i);

		if (getHealth() <= 0) {
			setPlayer(null);
			if ((mode != NPCMode.PARTY) && (mode != NPCMode.FOR_SALE)) {
				removeSql();
				MineQuest.remQuester(this);
				NpcSpawner.RemoveBasicHumanNpc(entity);
				entity = null;
			} else {
				Location location = MineQuest.getTown(town).getNPCSpawn();

				makeNPC(location.getWorld().getName(), location.getX(),
						location.getY(), location.getZ(), location
								.getPitch(), location.getYaw());
				mode = NPCMode.FOR_SALE;
			}
		}
	}

	public void setMode(NPCMode mode) {
		this.mode = mode;
		target = null;
	}

	public void setTarget(LivingEntity entity) {
		mobTarget = entity;
	}

	private void setTarget(Location location, double rad) {
//		Location self = entity.getBukkitEntity().getLocation();
//		double distance = MineQuest.distance(location, self);
		double angle = (new Random()).nextDouble() * Math.PI * 2;
		double length = (new Random()).nextDouble() * rad;
		double x = length * Math.cos(angle);
		double z = length * Math.sin(angle);
		target = new Location(location.getWorld(), 
				location.getX() + x,
				Ability.getNearestY(location.getWorld(), 
						(int)(location.getX() + x), (int)location.getY(), 
						(int)(location.getZ() + z)),
				location.getZ() + z,
				location.getYaw(),
				location.getPitch());
		if (MineQuest.distance(target, location) > 20) {
			target = null;
		}
	}

	public void update() {
		if (mode == NPCMode.QUEST_NPC) {
			return;
		}
		super.update();

		ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + name + "'");

		try {
			if (!results.next()) return;
			String mode_string = results.getString("mode");
			this.mode = NPCMode.getNPCMode(mode_string);
			double x = results.getDouble("x");
			double y = results.getDouble("y");
			double z = results.getDouble("z");
			float pitch = (float)results.getDouble("pitch");
			float yaw = (float)results.getDouble("yaw");
			String world = results.getString("world");
			MineQuest.log("Setting Center");
			center = new Location(MineQuest.getSServer().getWorld(world), x, y, z, yaw, pitch);
			makeNPC(world, x, y, z, (float)pitch, (float)yaw);
		} catch (SQLException e) {
			MineQuest.log("Unable to add NPCQuester");
		}
		
			results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_npc");
			
		try {
			while (results.next()) {
				String property = results.getString("property");
				if (property.equals("radius")) {
					this.radius = Integer.parseInt(results.getString("value"));
				} else if (property.equals("hit_message")) {
					this.hit_message = results.getString("value");
				} else if (property.equals("walk_message")) {
					this.walk_message = results.getString("value");
				} else if (property.equals("quest")) {
					this.quest_file = results.getString("value");
				} else if (property.equals("town")) {
					this.town = results.getString("value");
				}
			}
		} catch (SQLException e) {
			MineQuest.log("Problem getting NPC Properties");
		}
	}

	public void setProperty(String property, String value) {
		if (property.equals("radius")) {
			this.radius = Integer.parseInt(value);
		} else if (property.equals("hit_message")) {
			this.hit_message = value;
		} else if (property.equals("walk_message")) {
			this.walk_message = value;
		} else if (property.equals("quest")) {
			this.quest_file = value;
		} else {
			return;
		}
		
		MineQuest.getSQLServer().update("DELETE FROM " + name + "_npc WHERE property='" + property + "'");
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
				" (property, value) VALUES('" + property + "', '" + value + "')");
		
	}
	
	public int getCost() {
		int cost = level * MineQuest.getNPCCostLevel();
		
		if (getClass("Warrior") != null) {
			cost += getClass("Warrior").getLevel() * MineQuest.getNPCCostWarrior();
		}
		if (getClass("Archer") != null) {
			cost += getClass("Archer").getLevel() * MineQuest.getNPCCostArcher();
		}
		if (getClass("WarMage") != null) {
			cost += getClass("WarMage").getLevel() * MineQuest.getNPCCostWarMage();
		}
		if (getClass("PeaceMage") != null) {
			cost += getClass("PeaceMage").getLevel() * MineQuest.getNPCCostPeaceMage();
		}
		
		return cost;
	}
}