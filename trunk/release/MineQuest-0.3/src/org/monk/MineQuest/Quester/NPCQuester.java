package org.monk.MineQuest.Quester;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.Absolute.SpawnNPCEvent;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.NpcSpawner;

public class NPCQuester extends Quester {
	private BasicHumanNpc entity;
	private HashMap<String, Integer> map = new HashMap<String, Integer>();
	private NPCMode mode;
	
	public NPCQuester(String name) {
		super(name);
		this.entity = null;
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
	}
	
	public void setEntity(BasicHumanNpc entity) {
		this.entity = entity;
		setPlayer(entity.getBukkitEntity());
	}
	
	public BasicHumanNpc getEntity() {
		return entity;
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
		}
	}
	
	@Override
	public void save() {
		if (mode == NPCMode.QUEST_NPC) return;
		super.save();
		
		if (entity == null) return;
		MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
				entity.getBukkitEntity().getLocation().getX() + "', y='" + 
				entity.getBukkitEntity().getLocation().getY() + "', z='" + 
				entity.getBukkitEntity().getLocation().getZ() + "', world='" + 
				entity.getBukkitEntity().getWorld().getName() + "' WHERE name='"
				+ name + "'");
	}
	
	@Override
	public boolean healthChange(int change, EntityDamageEvent event) {
		boolean ret = super.healthChange(change, event);
		
		if (mode == NPCMode.GOD) {
			Player player = null;
			if (event instanceof EntityDamageByEntityEvent) {
				Entity entity = ((EntityDamageByEntityEvent)event).getDamager();
				if (entity instanceof Player) {
					player = (Player)entity;
				}
			} else if (event instanceof EntityDamageByProjectileEvent) {
				Entity entity = ((EntityDamageByProjectileEvent)event).getDamager();
				if (entity instanceof Player) {
					player = (Player)entity;
				}
			}
			if (player != null) {
				if (map.get(player.getName()) == null) {
					map.put(player.getName(), 1);
				} else {
					map.put(player.getName(), map.get(player.getName()) + 1);
					if (map.get(player.getName()) == 2) {
						player.sendMessage("<" + getName() + "> Owe!");
					} else if (map.get(player.getName()) == 3) {
						player.sendMessage("<" + getName() + "> Don't do that!");
					} else if (map.get(player.getName()) == 5) {
						player.sendMessage("<" + getName() + "> You won't like me when I'm angry!");
					} else if (map.get(player.getName()) == 9) {
						MineQuest.getQuester(player).damage(200);
						map.put(player.getName(), 1);
					}
				}
			}
		} else {
			if (getHealth() <= 0) {
				setPlayer(null);
				NpcSpawner.RemoveBasicHumanNpc(entity);
				entity = null;
				removeSql();
				MineQuest.remQuester(this);
			}
		}
		
		return ret;
	}
	
	private void removeSql() {
		MineQuest.getSQLServer().update("DELETE FROM questers WHERE name='" + name + "'");
		MineQuest.getSQLServer().update("DROP TABLE " + name);
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_chests");
	}

	@Override
	public void setHealth(int i) {
		super.setHealth(i);
		
		if (getHealth() <= 0) {
			setPlayer(null);
			NpcSpawner.RemoveBasicHumanNpc(entity);
			entity = null;
			removeSql();
		}
	}
	
	@Override
	public void damage(int i) {
		super.damage(i);
	}

	public void update() {
		if (mode == NPCMode.QUEST_NPC) {
			return;
		}
		super.update();

		ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + name + "'");

		try {
			if (!results.next()) return;
			this.mode = NPCMode.getNPCMode(results.getString("mode"));
			double x = results.getDouble("x");
			double y = results.getDouble("y");
			double z = results.getDouble("z");
			double pitch = results.getDouble("pitch");
			double yaw = results.getDouble("yaw");
			String world = results.getString("world");
			makeNPC(world, x, y, z, (float)pitch, (float)yaw);
		} catch (SQLException e) {
			MineQuest.log("Unable to add NPCQuester");
		}
	}

	private void makeNPC(String world, double x, double y, double z,
			float pitch, float yaw) {
		if (entity != null) {
			entity.getBukkitEntity().setHealth(0);
			entity = null;
		}
		MineQuest.getEventParser().addEvent(new SpawnNPCEvent(200, this, world, x, y, z, (float)pitch, (float)yaw));
	}
}