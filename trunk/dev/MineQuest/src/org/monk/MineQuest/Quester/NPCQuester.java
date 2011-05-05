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
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.martin.bukkit.npclib.NPCEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Event.NPCEvent;
import org.monk.MineQuest.Event.Absolute.SpawnNPCEvent;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;
import org.monk.MineQuest.Store.Store;
import org.monk.MineQuest.Store.StoreBlock;
import org.monk.MineQuest.World.Town;


public class NPCQuester extends Quester {
	private Location center;
	private int count = 0;
	private int delay = 15000;
	private NPCEntity entity;
	private Quester follow;
	private String follow_name;
	private String hit_message;
	private List<Integer> ids1 = new ArrayList<Integer>();
	private List<Integer> idsStore;
	private ItemStack item;
	private List<Integer> itemStore;
	private LivingEntity mobTarget;
	private NPCMode mode;
	private String quest_file;
    private double rad;
	private int radius;
	private double speed = .6;
	private Location target = null;
	private List<Long> times1 = new ArrayList<Long>();
	private String town;
	private String walk_message;
	private long last_attack;
	private long last_hit;
	private boolean removed;
	private long respawn;
	private int reach_count;
	private Location last_loc;
	private Random generator;

	
	public NPCQuester(String name) {
		super(name);
		this.entity = null;
		mobTarget = null;
		if (mode == NPCMode.FOLLOW) {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		} else {
			MineQuest.getEventParser().addEvent(new NPCEvent(100, this));
		}
		if (mode == NPCMode.STORE) {
			delay = 2000;
		}
		idsStore = new ArrayList<Integer>();
		itemStore = new ArrayList<Integer>();
		last_attack = 0;
		last_hit = 0;
		removed = false;
		respawn = 0;
		reach_count = 0;
		generator = new Random();
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
		if (mode == NPCMode.STORE) {
			delay = 2000;
		}
		idsStore = new ArrayList<Integer>();
		itemStore = new ArrayList<Integer>();
		last_attack = 0;
		last_hit = 0;
		removed = false;
		respawn = 0;
		reach_count = 0;
		generator = new Random();
	}
	
	public void activate() {
		if (health <= 0) return;
		if (player == null) return;
		
		if (item != null) {
			player.setItemInHand(item);
			item = null;
		}
		if (MineQuest.getQuester(mobTarget) instanceof NPCQuester) {
			NPCQuester npc = (NPCQuester)MineQuest.getQuester(mobTarget);
			if (npc.isMerc()) {
				sendMessage("Its not good for business to attack other mercenaries");
			}
			mobTarget = null;
		}
		
		if (respawn - Calendar.getInstance().getTimeInMillis() > 300000) {
			respawn = Calendar.getInstance().getTimeInMillis();
			Location location = player.getLocation();
			teleport(location);
		}
		
		if (mobTarget == null) {
			if ((follow != null) && (follow.getPlayer() != null)) {
				if (mode != NPCMode.PARTY_STAND) {
					if (MineQuest.distance(follow.getPlayer().getLocation(), entity.getBukkitEntity().getLocation()) > 4) {
						setTarget(follow.getPlayer().getLocation(), 4, 0);
					}
				}
			}
		} else {
			if (MineQuest.distance(player.getLocation(), mobTarget.getLocation()) > 1.3) {
				if (MineQuest.getMob(mobTarget) != null) {
					if (MineQuest.getMob(mobTarget).getHealth() <= 0) {
						mobTarget = null;
						return;
					}
				} else {
					if (mobTarget.getHealth() <= 0) {
						mobTarget = null;
						return;
					}
				}
				setTarget(mobTarget.getLocation(), 1.25, 0);
			}
		}
		if ((follow_name != null) && (!follow_name.equals("null"))) {
			if ((follow == null) && (MineQuest.getQuester(follow_name) != null)) {
				follow = MineQuest.getQuester(follow_name);	
				follow.addNPC(this);
			}
		}

		if (target != null) {
			if (reach_count > 10) {
				teleport(target);
				target = null;
				reach_count = 0;
				return;
			} else {
				if (MineQuest.distance(player.getLocation(), target) < speed) {
					double move_x = (target.getX() - player.getLocation().getX());
					double move_z = (target.getZ() - player.getLocation().getZ());
					float yaw = 0;
					yaw = (float)(-180 * Math.atan2(move_x , move_z) / Math.PI);
					entity.setLocation(target.getX(), target.getY(), target.getZ(), yaw, target.getPitch());
					last_loc = newLocation(player.getLocation());
					reach_count = 0;
	
					target = null;
				} else {
					double distance = MineQuest.distance(player.getLocation(), target);
					double move_x = (speed * (target.getX() - player.getLocation().getX()) / distance);
					double move_y = (speed * (target.getY() - player.getLocation().getY()) / distance);
					double move_z = (speed * (target.getZ() - player.getLocation().getZ()) / distance);
					move_x += (new Random()).nextDouble() * .05;
					move_z += (new Random()).nextDouble() * .05;
					move_y = Ability.getNearestY(player.getWorld(), (int)(player.getLocation().getBlockX() + move_x),
							(int)player.getLocation().getBlockY(), 
							(int)(player.getLocation().getBlockZ() + move_z)) - player.getLocation().getY();
					if ((move_x < .1) && (move_z < .1)) {
						reach_count++;
					}
					if (move_y > 4) {
						move_y = 0;
					}
					float yaw = 0;
					yaw = (float)(-180 * Math.atan2(move_x , move_z) / Math.PI);
					entity.setLocation(
						player.getLocation().getX() + move_x,
						player.getLocation().getY() + move_y,
						player.getLocation().getZ() + move_z,
						yaw, target.getPitch());
				}
			}
		}
		if (player.getLocation() == null) {
			if (last_loc != null) {
				teleport(last_loc);
			}
			return;
		}
		if (player.getLocation().getY() > 128) {
			if ((follow != null) && (follow.getPlayer() != null)) {
				teleport(follow.getPlayer().getLocation());
			}
		}
		
		if ((mobTarget != null) && (MineQuest.distance(mobTarget.getLocation(), player.getLocation()) < 1.2)) {
			attack(mobTarget);
		}
		
		if (rad != 0) {
			count++;
			if (count == 30) {
				setTarget(center, rad, 0);
				MineQuest.log("Setting Target");
				count = 0;
			}
		}
		
		if (walk_message != null) {
			for (Player entity : MineQuest.getSServer().getOnlinePlayers()) {
				if (MineQuest.distance(player.getLocation(), entity.getLocation()) < radius) {
					if ((quest_file == null) || (!MineQuest.getQuester(entity).isCompleted(new QuestProspect(quest_file)))) {
						if (!checkMessage(entity.getEntityId())) {
							if (walk_message.equals("random")) {
								MineQuest.getNPCStringConfiguration().sendRandomHitMessage(this, MineQuest.getQuester(entity));
							} else {
								MineQuest.getQuester(entity).sendMessage("<" + name + "> " + walk_message);
							}
						}
					}
				}
			}
		}
		
		if (mode == NPCMode.STORE) {
			List<Integer> these_ids = new ArrayList<Integer>();
			Store store = MineQuest.getTown(player).getStore(player);
			
			for (Player player : MineQuest.getSServer().getOnlinePlayers()) {
				if ((MineQuest.getTown(player) != null) && store.equals(MineQuest.getTown(player).getStore(player))) {
					if (!checkMessageStore(player.getEntityId(), player.getItemInHand().getTypeId())) {
						MineQuest.getNPCStringConfiguration().sendRandomMessage(this, MineQuest.getQuester(player), store);
					}
					these_ids.add(player.getEntityId());
				}
			}
			
			int i;
			for (i = 0; i < idsStore.size(); i++) {
				if (!these_ids.contains(idsStore.get(i))) {
					itemStore.set(i, -1);
				}
			}
		}
	}

    private boolean isMerc() {
		if ((mode == NPCMode.FOR_SALE) || (mode == NPCMode.PARTY) || (mode == NPCMode.PARTY_STAND)) {
			return true;
		}
		return false;
	}

	private void attack(LivingEntity mobTarget) {
//		entity.attackLivingEntity(mobTarget);
    	long now = Calendar.getInstance().getTimeInMillis();

    	if (now - last_attack > 500) {
    		last_attack = 500;
	    	entity.animateArmSwing();
			((CraftHumanEntity)player).getHandle().d(((CraftLivingEntity)mobTarget).getHandle());
    	}
	}
	
	@Override
	public void bind(String name) {
		if ((getAbility(name) != null) && (getAbility(name).getClassType().getName().equals("Warrior"))) {
			super.bind(name);
		} else {
			sendMessage("I can only cast warrior abilities");
		}
	}
	
	public void buyNPC(Quester quester) {
		if (quester.getCubes() > getCost()) {
			quester.setCubes(quester.getCubes() - getCost());
			quester.addNPC(this);
			setMode(NPCMode.PARTY);
			quester.sendMessage(name + " joined your party!");
		} else {
			quester.sendMessage("You don't have enough cubes");
		}
	}
	
	@Override
	public boolean canCast(List<ItemStack> list) {
		if (follow != null) {
			return follow.canCast(list);
		}
		
		return false;
	}
	
	@Override
	public boolean checkItemInHand() {
		PlayerInventory inven = null;
		if ((follow != null) && (follow.getPlayer() != null)) {
			inven = follow.getPlayer().getInventory();
		}
		ItemStack item = player.getItemInHand();

		for (SkillClass skill : classes) {
			if (!skill.canUse(item)) {
				if (inven.firstEmpty() != -1) {
					if (inven != null) {
						inven.addItem(item);
					}
				} else {
					player.getWorld().dropItem(player.getLocation(), item);
				}
				
				player.setItemInHand(null);
				
				sendMessage("You are not high enough level to use that weapon");
				
				return true;
			}
		}

		return false;
	}
	
	private boolean checkMessage(int id) {
	    Calendar now = Calendar.getInstance();
	    int i;
	    
		for (i = 0; i < ids1.size(); i++) {
			if (ids1.get(i) == id) {
				if ((now.getTimeInMillis() - times1.get(i)) > delay) {
					times1.set(i, now.getTimeInMillis());
					return false;
				} else {
					return true;
				}
			}
		}
	    
	    ids1.add(id);
	    times1.add(now.getTimeInMillis());
	    
	    return false;
    }
	
	private boolean checkMessageStore(int id, int item) {
	    int i;
	    
		for (i = 0; i < idsStore.size(); i++) {
			if (idsStore.get(i) == id) {
				if (itemStore.get(i) != item) {
					itemStore.set(i, item);
					return false;
				} else {
					return true;
				}
			}
		}
	    
	    idsStore.add(id);
	    itemStore.add(item);
	    
	    return false;
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
		}
	}
	
	@Override
	public void expClassGain(int class_exp) {
		if (getClass("Warrior") != null) {
			getClass("Warrior").expAdd(class_exp);
		}
	}
	
	public int getCost() {
		int cost = (level * level + 1) * MineQuest.getNPCCostLevel();
		
		if (getClass("Warrior") != null) {
			cost += (getClass("Warrior").getLevel() * getClass("Warrior").getLevel() + 1) * MineQuest.getNPCCostWarrior();
		}
		if (getClass("Archer") != null) {
			cost += (getClass("Archer").getLevel() * getClass("Archer").getLevel() + 1) * MineQuest.getNPCCostArcher();
		}
		if (getClass("WarMage") != null) {
			cost += (getClass("WarMage").getLevel() * getClass("WarMage").getLevel() + 1) * MineQuest.getNPCCostWarMage();
		}
		if (getClass("PeaceMage") != null) {
			cost += (getClass("PeaceMage").getLevel() * getClass("PeaceMage").getLevel() + 1) * MineQuest.getNPCCostPeaceMage();
		}
		
		return cost;
	}
	
	public NPCEntity getEntity() {
		return entity;
	}
	
	public Quester getFollow() {
		return follow;
	}

	public NPCMode getMode() {
		return mode;
	}
	
	public Town getNPCTown() {
		return MineQuest.getTown(town);
	}

	public LivingEntity getTarget() {
		return mobTarget;
	}

	public void giveItem(Quester quester) {
		ItemStack spare = null;
		if (player.getItemInHand() != null) {
			if (player.getItemInHand().getType() != Material.AIR) {
				spare = new ItemStack(player.getItemInHand().getType(), player.getItemInHand().getAmount());
				if (player.getItemInHand().getData() != null) {
					spare.setData(player.getItemInHand().getData());
				}
				spare.setDurability(player.getItemInHand().getDurability());
			}
		}
		
		ItemStack item = quester.getPlayer().getItemInHand();
		String value = item.getTypeId() + "," + item.getAmount() + "," + item.getDurability();
		if (item.getData() != null) {
			value = value + "," + item.getData().getData();
		}
		setProperty("item", value);
		quester.getPlayer().setItemInHand(null);
		
		if (spare != null) {
			quester.getPlayer().getInventory().addItem(spare);
		}
	}

	public void handleProperty(String property, String value) {
		if (property.equals("radius")) {
			this.radius = Integer.parseInt(value);
		} else if (property.equals("hit_message")) {
			this.hit_message = value;
		} else if (property.equals("walk_message")) {
			this.walk_message = value;
		} else if (property.equals("quest")) {
			if ((value == null) || (value.equals("null"))) {
				this.quest_file = null;
			}
			this.quest_file = value;
		} else if (property.equals("follow")) {
			this.follow_name = value;
			if ((value == null) || (value.equals("null"))) {
				this.follow_name = null;
			}
			if (follow != null) {
				follow.remNPC(this);
				follow = null;
			}
			if (follow_name != null) {
				this.follow = MineQuest.getQuester(value);
				if (follow != null) {
					follow.addNPC(this);
				}
			}
		} else if (property.equals("town")) {
			this.town = value;
		} else if (property.equals("wander_radius")) {
			this.rad = Integer.parseInt(value);
		} else if (property.equals("item")) {
			String split[] = value.split(",");
			item = new ItemStack(Integer.parseInt(split[0]), 
					Integer.parseInt(split[1]));
			
			item.setDurability(Short.parseShort(split[2]));
			if (split.length > 3) {
				MaterialData md = new MaterialData(Integer.parseInt(split[0]));
				md.setData(Byte.parseByte(split[3]));
				item.setData(md);
			}
		}
	}

	@Override
	public boolean healthChange(int change, EntityDamageEvent event) {
		boolean ret = false;
		Calendar now = Calendar.getInstance();
		
		if (now.getTimeInMillis() - last_hit <= 10) {
			return false;
		}
		last_hit = now.getTimeInMillis();
		
		if ((mode != NPCMode.FOR_SALE) && (mode != NPCMode.FOLLOW) && (mode != NPCMode.PARTY) && (mode != NPCMode.PARTY_STAND)) {
			health = max_health;
			event.setDamage(0);

			if (mode == NPCMode.STORE) {
				LivingEntity entity = null;
				if (event instanceof EntityDamageByEntityEvent) {
					entity = (LivingEntity) ((EntityDamageByEntityEvent)event).getDamager();
				}
				if (event instanceof EntityDamageByProjectileEvent) {
					entity = (LivingEntity) ((EntityDamageByProjectileEvent)event).getDamager();
				}
				if (entity instanceof HumanEntity) {
					HumanEntity human = (HumanEntity)entity;
					ItemStack hand = human.getItemInHand();
					MineQuest.getTown(player).getStore(player).setKeeper(this);
					if (checkMessage(entity.getEntityId()) && checkMessageStore(human.getEntityId(), human.getItemInHand().getTypeId())) {
						MineQuest.getTown(player).getStore(player).sell(MineQuest.getQuester(human), hand.getTypeId(), hand.getAmount());
					} else {
						StoreBlock block = MineQuest.getTown(player).getStore(player).getBlock(hand.getTypeId());
						checkMessageStore(human.getEntityId(), hand.getTypeId());
						if (block != null) {
							MineQuest.getNPCStringConfiguration().sendWantMessage(this, MineQuest.getQuester(human), MineQuest.getTown(player).getStore(player));
						} else {
							MineQuest.getNPCStringConfiguration().sendNotWantMessage(this, MineQuest.getQuester(human), MineQuest.getTown(player).getStore(player));
						}
					}
				}
			}
			
			if ((event instanceof EntityDamageByEntityEvent) && 
					(((EntityDamageByEntityEvent)event).getDamager() instanceof Player)) {
				Player player = (Player)((EntityDamageByEntityEvent)event).getDamager();
				if (hit_message != null) {
					if (hit_message.equals("random")) {
						MineQuest.getNPCStringConfiguration().sendRandomHitMessage(this, MineQuest.getQuester(player));
					} else {
						MineQuest.getQuester(player).sendMessage("<" + name + "> " + hit_message);
					}
				}
				if (quest_file != null) {
					if (!MineQuest.getQuester(player).isCompleted(new QuestProspect(quest_file))) {
						MineQuest.getQuester(player).addQuestAvailable(new QuestProspect(quest_file));
					}
				}
			}
		} else {
			LivingEntity entity = null;
			if (event instanceof EntityDamageByEntityEvent) {
				entity = (LivingEntity) ((EntityDamageByEntityEvent)event).getDamager();
			}
			if (event instanceof EntityDamageByProjectileEvent) {
				entity = (LivingEntity) ((EntityDamageByProjectileEvent)event).getDamager();
			}
			if (follow != null) {
				if ((entity instanceof Player) && ((Player)entity).getName().equals(follow.getName())) {
					PlayerInteractEvent pie = new PlayerInteractEvent(getPlayer(), null, ((Player)entity).getItemInHand(), null, null);

					if (!healthIncrease(pie)) {
						if (mobTarget != null) {
							mobTarget = null;
							sendMessage("Regrouping!");
						} else {
							if ((mode == NPCMode.PARTY_STAND)) {
								mode = NPCMode.PARTY;
								sendMessage("I'll follow your lead");
							} else {
								mode = NPCMode.PARTY_STAND;
								sendMessage("I'll stay here");
							}
						}
					} else {
						Player player = (Player)entity;
						if ((player.getItemInHand() != null) &&
								(player.getItemInHand().getAmount() > 1)) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
						sendMessage("Health: " + health + "/" + max_health);
					}
					event.setCancelled(true);
				} else {
					if (mobTarget == null) {
						if (!(entity instanceof Player)
								|| (!((Player) entity).getName().equals(name))) {
							if (NPCMode.FOR_SALE != mode) {
								mobTarget = entity;
							}
						}
					}
				}
			}
			ret = super.healthChange(change, event);

			if (getHealth() <= 0) {
				setPlayer(null);
				if ((mode != NPCMode.PARTY) && (mode != NPCMode.FOR_SALE) && (mode != NPCMode.PARTY_STAND)) {
					removeSql();
					MineQuest.remQuester(this);
					MineQuest.getNPCManager().despawn(name);
					entity = null;
				} else {
					Location location = MineQuest.getTown(town).getNPCSpawn();

					sendMessage("Died!");
					setProperty("follow", null);
					mode = NPCMode.FOR_SALE;
					mobTarget = null;
					target = null;
					makeNPC(location.getWorld().getName(), location.getX(),
							location.getY(), location.getZ(), location
									.getPitch(), location.getYaw());
				}
				health = max_health;
			}
		}
		
		return ret;
	}

	private void makeNPC(String world, double x, double y, double z,
			float pitch, float yaw) {
		if (entity != null) {
			((Player)entity.getBukkitEntity()).setHealth(0);
//			entity.getBukkitEntity().setHealth(0);
			MineQuest.getNPCManager().despawn(name);
//			NpcSpawner.RemoveBasicHumanNpc(this.entity);
			entity = null;
			player = null;
		}
		MineQuest.getEventParser().addEvent(new SpawnNPCEvent(200, this, world, x, y, z, (float)pitch, (float)yaw));
	}

	public void questerAttack(LivingEntity entity) {
		if (NPCMode.PARTY_STAND != mode) {
			if ((mobTarget == null) || (mobTarget.getHealth() <= 0)) {
				mobTarget = entity;
			}
		}
	}

	public void removeSql() {
		removed = true;
		MineQuest.getSQLServer().update("DELETE FROM questers WHERE name='" + name + "'");
		MineQuest.getSQLServer().update("DROP TABLE " + name);
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_chests");
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_kills");
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_npc");
		MineQuest.getSQLServer().update("DROP TABLE " + name + "_quests");
	}

	@Override
	public void save() {
		if (mode == NPCMode.QUEST_NPC) return;
		super.save();
		
		if (entity == null) return;
		if ((mode != NPCMode.FOLLOW) && (mode != NPCMode.PARTY) && (mode != NPCMode.PARTY_STAND)) {
			entity.setLocation(center.getX(), center.getY(), center.getZ(), 
					center.getYaw(), center.getPitch());
		}
		
		MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
				entity.getBukkitEntity().getLocation().getX() + "', y='" + 
				entity.getBukkitEntity().getLocation().getY() + "', z='" + 
				entity.getBukkitEntity().getLocation().getZ() + "', mode='" + 
				mode + "', world='" + 
				entity.getBukkitEntity().getWorld().getName() + "' WHERE name='"
				+ name + "'");
	}
	
	@Override
	public void sendMessage(String string) {
		if ((NPCMode.PARTY == mode) || (mode == NPCMode.PARTY_STAND)) {
			if (follow != null) {
				follow.sendMessage(name + " : " + string);
			}
		}
	}
	
	public void setEntity(NPCEntity entity) {
		if (removed) return;
		this.entity = entity;
		setPlayer((Player)entity.getBukkitEntity());
		last_loc = newLocation(player.getLocation());
	}

	private Location newLocation(Location location) {
		Location loc = new Location(location.getWorld(), location.getX(),
				location.getX(), location.getZ(), location.getYaw(), location
						.getPitch());
		return loc;
	}

	public void setFollow(Quester quester) {
		setProperty("follow", quester == null?null:quester.getName());
		target = null;
	}

	@Override
	public void setHealth(int i) {
		super.setHealth(i);

		if (getHealth() <= 0) {
			setPlayer(null);
			if ((mode != NPCMode.PARTY_STAND) && (mode != NPCMode.PARTY) && (mode != NPCMode.FOR_SALE)) {
				if (mode != NPCMode.QUEST_NPC) {
					removeSql();
				}
				MineQuest.remQuester(this);
				MineQuest.getNPCManager().despawn(name);
//				NpcSpawner.RemoveBasicHumanNpc(this.entity);
				MineQuest.log("NPC Died");
				entity = null;
			} else {
				Location location = MineQuest.getTown(town).getNPCSpawn();

				sendMessage("Died!");
				mode = NPCMode.FOR_SALE;
				setProperty("follow", null);
				mobTarget = null;
				target = null;
				makeNPC(location.getWorld().getName(), location.getX(),
						location.getY(), location.getZ(), location
								.getPitch(), location.getYaw());
			}
			health = max_health;
		}
	}
	
	public void setMode(NPCMode mode) {
		this.mode = mode;
		target = null;
	}
	
	public void setProperty(String property, String value) {
		handleProperty(property, value);

		MineQuest.getSQLServer().update("DELETE FROM " + name + "_npc WHERE property='" + property + "'");
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
				" (property, value) VALUES('" + property + "', '" + value + "')");
		
	}
	
	public void setTarget(LivingEntity entity) {
		mobTarget = entity;
		if (entity == null) {
			if ((follow != null) && (follow.getPlayer() != null) && (follow.getPlayer().getLocation() != null)) {
				if ((mode == NPCMode.PARTY_STAND) || (mode == NPCMode.PARTY)) {
					if ((player != null) || (MineQuest.distance(follow.getPlayer().getLocation(), player.getLocation()) > 100)) {
							Player player = follow.getPlayer();
							teleport(player.getLocation());
					}
				}
			}
		}
	}
	
	public void setTarget(Location location) {
		target = location;
	}
	
	private void setTarget(Location location, double rad, int call) {
//		Location self = entity.getBukkitEntity().getLocation();
//		double distance = MineQuest.distance(location, self);
		double angle = generator.nextDouble() * Math.PI * 2;
		double length = generator.nextDouble() * rad;
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
		if (MineQuest.distance(target, location) > 10) {
			target = null;
		} else {
			if (Math.abs(target.getY() - location.getY()) > 5) {
				target.setY(location.getY());
			}
			if ((target.getWorld().getBlockAt(target).getType() != Material.AIR) && 
					(target.getWorld().getBlockAt(target).getType() != Material.FIRE) &&
					(target.getWorld().getBlockAt(target).getType() != Material.TORCH) &&
					(target.getWorld().getBlockAt(target).getType() != Material.SIGN) &&
					(target.getWorld().getBlockAt(target).getType() != Material.WALL_SIGN) &&
					(target.getWorld().getBlockAt(target).getType() != Material.SNOW)) {
				target = null;
				if (call < 20) {
					setTarget(location, rad, call + 1);
				} else {
					reach_count++;
				}
			}
		}
	}

	public void setTown(String town) {
		this.town = town;
		MineQuest.getSQLServer().update("DELETE FROM " + name + "_npc WHERE property='town'");
		MineQuest.getSQLServer().update("INSERT INTO " + name + "_npc " + 
				" (property, value) VALUES('town', '" + town + "')");
	}

	public void teleport(Location location) {
		makeNPC(location.getWorld().getName(), location.getX(), location.getY(), 
				location.getZ(), location.getPitch(), location.getYaw());
	}
	
	public void redo() {
		teleport(player.getLocation());
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
			center = new Location(MineQuest.getSServer().getWorld(world), x, y, z, yaw, pitch);
			makeNPC(world, x, y, z, (float)pitch, (float)yaw);
		} catch (SQLException e) {
			MineQuest.log("Unable to add NPCQuester");
		}

		MineQuest.getSQLServer().update("CREATE TABLE IF NOT EXISTS " +
				name + "_npc (property VARCHAR(30), value VARCHAR(300))");
		
		results = MineQuest.getSQLServer().query("SELECT * FROM " + name + "_npc");
		
		this.radius = 0;
		this.hit_message = null;
		this.walk_message = null;
		this.quest_file = null;
		this.town = null;
		this.rad = 0;
			
		try {
			while (results.next()) {
				String property = results.getString("property");
				String value = results.getString("value");
				handleProperty(property, value);
			}
		} catch (Exception e) {
			MineQuest.log("Problem getting NPC Properties");
		}
	}
}