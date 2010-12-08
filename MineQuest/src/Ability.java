import java.util.ArrayList;
import java.util.List;



public class Ability {
	static public int getNearestY(int x, int y, int z) {
		int i = y;
		Server server = etc.getServer();
		
		if (server.getBlockAt(x, y, z).getType() != 0) {
			do {
				i++;
			} while (server.getBlockAt(x, i, z).getType() != 0);
		} else {
			do {
				i--;
			} while (server.getBlockAt(x, i, z).getType() == 0);
			i++;
		}
		
		return i;
	}
	private String name;
	private boolean enabled;
	private int bindl;
	private int bindr;
	private int count;
	
	private SkillClass myclass;
	
	public Ability(String name, SkillClass myclass) {
		this.name = name;
		enabled = true;
		this.myclass = myclass;
		etc.getServer().setTimer("Ability " + name, 300);
		count = 0;
		if (name.equals("Dodge")) {
			enabled = false;
		}
	}
	
	public void bindl(Player player, Item item) {
		bindl = item.getItemId();
		player.sendMessage(name + " is now bound to Left " + item.getItemId());
	}
	
	public void bindr(Player player, Item item) {
		bindr = item.getItemId();
		player.sendMessage(name + " is now bound to Right " + item.getItemId());
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void enable(Quester quester) {
		if (quester.canCast(getManaCost(etc.getServer().getPlayer(quester.getName()), null))) {
			enabled = true;
			etc.getServer().getPlayer(quester.getName()).sendMessage(name + " enabled");
		} else {
			etc.getServer().getPlayer(quester.getName()).sendMessage("Not Enough Mana");
		}
	}

	public boolean equals(String name) {
		return name.equals(this.name);
	}

	private int getDistance(Player player, LivingEntity entity) {
		double x, y, z;
		x = player.getX() - entity.getX();
		y = player.getY() - entity.getY();
		z = player.getZ() - entity.getZ();
		
		return (int)Math.sqrt(x*x + y*y + z*z);
	}
	
	public List<LivingEntity> getEntities(LivingEntity player, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList = etc.getServer().getLivingEntityList();
		int i;
		
		for (i = 0; i < serverList.size(); i++) {
			if ((isWithin(player, serverList.get(i), radius)) && (serverList.get(i).getId() != player.getId())) {
				entities.add(serverList.get(i));
			}
		}
		
		return entities;
	}
	
	private int getExp() {
		// TODO: Fix getExp in Ability.java
		return 15;
	}
	
	public List<Item> getManaCost(Player player, LivingEntity entity) {
		List<Item> list = new ArrayList<Item>();
		
		//if (!name.equals("Fireball")) {
			//list.add(new Item(331, 1));
		//}
		
		if (name.equals("Dodge")) {
			list.add(new Item(288, 5));
		} else if (name.equals("Fireball")) {
			list.add(new Item(263, 1));
		} else if (name.equals("Deathblow")) {
			list.add(new Item(265, 2));
		} else if (name.equals("Sprint")) {
			list.add(new Item(288, 1));
		} else if (name.equals("PowerStrike")) {
			list.add(new Item(268, 1));
		} else if (name.equals("Hail of Arrows")) {
			list.add(new Item(262, 10));
		} else if (name.equals("Fire Arrow")) {
			list.add(new Item(263, 1));
		} else if (name.equals("Repulsion")) {
			list.add(new Item(50, 1));
			list.add(new Item(81, 1));
		} else if (name.equals("FireChain")) {
			list.add(new Item(263, 5));
		} else if (name.equals("Wall of Fire")) {
			list.add(new Item(263, 3));
			list.add(new Item(2, 7));
		} else if (name.equals("IceSphere")) {
			list.add(new Item(332, 5));
		} else if (name.equals("Drain Life")) {
			list.add(new Item(89, 1));
		} else if (name.equals("Fire Resistance")) {
			list.add(new Item(87, 1));
		} else if (name.equals("Trap")) {
			list.add(new Item(3, 6));
			list.add(new Item(269, 1));
		} else if (name.equals("Heal")) {
			list.add(new Item(326, 1));
		} else if (name.equals("Heal Other")) {
			list.add(new Item(326, 1));
		} else if (name.equals("Wall of Water")) {
			list.add(new Item(326, 2));
			list.add(new Item(3, 7));
		} else if (name.equals("Heal Aura")) {
			list.add(new Item(297, 2));
		} else if (name.equals("Damage Aura")) {
			list.add(new Item(259, 1));
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
	
	public boolean isBound(int id) {
		return (bindl == id) || (bindr == id);
	}
	
	public boolean isBoundL(int id) {
		return (bindl == id);
	}

	public boolean isBoundR(int itemInHand) {
		return (bindr == itemInHand);
	}
	
	public boolean isDefending(Player player, BaseEntity mob) {
		if (name.equals("Dodge")) {
			return true;
		}
		return false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private boolean isType(LivingEntity livingEntity, int type) {
		if (type == 1) {
			return livingEntity.getName().equals("Zombie");
		} else if (type == 2) {
			return livingEntity.getName().equals("Skeleton");
		} else if (type == 3) {
			return livingEntity.getName().equals("Creeper");
		} else if (type == 4) {
			return livingEntity.getName().equals("Spider");
		} else {
			return true;
		}
	}
	
	private boolean isWithin(LivingEntity player, LivingEntity baseEntity, int radius) {
		double x, y, z;
		
		x = player.getX() - baseEntity.getX();
		y = player.getY() - baseEntity.getY();
		z = player.getZ() - baseEntity.getZ();
		
		return Math.sqrt(x*x + y*y + z*z) < radius;
	}
	
	private void moveOut(LivingEntity player, LivingEntity other,
			int distance) {
		double x, y, z;
		double unit_fix;
		
		x = other.getX() - player.getX();
		z = other.getZ() - player.getZ();
		unit_fix = Math.sqrt(x*x + z*z);
		
		x *= distance / unit_fix;
		z *= distance / unit_fix;
		
		y = etc.getServer().getHighestBlockY((int)x, (int)z) + 1;

		other.teleportTo(x + player.getX(), getNearestY((int)(x + player.getX()), (int)other.getY(), (int)(z + player.getZ())), 
				z + player.getZ(), other.getRotation(), other.getPitch());
		
		return;
	}


	public boolean parseAttack(Player player, LivingEntity defend, Quester quester) {
		if ((player.getItemInHand() == 261) || (player.getItemInHand() == 332)) {
			useAbility(player, new Block(1, (int)defend.getX(), (int)defend.getY(), (int)defend.getZ()), quester, 0, defend);
		} else {
			useAbility(player, new Block(1, (int)defend.getX(), (int)defend.getY(), (int)defend.getZ()), quester, 1, defend);
		}
		if ((name.equals("Fire Arrow") || name.equals("PowerStrike"))) {
			return false;
		}
		
		return true;
	}
	
	public int parseDefend(Player player, BaseEntity mob, int amount) {
		if (!enabled) return 0;
		
		if (name.equals("Dodge")) {
			if (myclass.getGenerator().nextDouble() < (.01 + (.0025 * myclass.getLevel()))) {
				double rot = player.getRotation() % 360;
				while (rot < 0) rot += 360;
				
				if ((rot  < 45) && (rot < 315)) {
					player.sendMessage("Dodging1");
					player.teleportTo(player.getX() - 1, 
							getNearestY((int)player.getX(), (int)player.getY(), (int)player.getZ()),
							player.getZ(), player.getRotation(), player.getPitch());
				} else if ((rot > 45) && (rot < 135)) {
					player.sendMessage("Dodging2");
					player.teleportTo(player.getX(), 
							getNearestY((int)player.getX(), (int)player.getY(), (int)player.getZ()),
							player.getZ() - 1, player.getRotation(), player.getPitch());
				} else if ((rot > 135) && (rot < 225)) {
					player.sendMessage("Dodging3");
					player.teleportTo(player.getX() + 1, 
							getNearestY((int)player.getX(), (int)player.getY(), (int)player.getZ()), 
							player.getZ(), player.getRotation(), player.getPitch());
				} else {
					player.sendMessage("Dodging4");
					player.teleportTo(player.getX(), 
							getNearestY((int)player.getX(), (int)player.getY(), (int)player.getZ()),
							player.getZ() + 1, player.getRotation(), player.getPitch());
				}
				return amount;
			}
			player.sendMessage("Not Dodging");
		}
		
		return 0;
	}

	public void parseLeftClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 1, null);
	}

	public void parseRightClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 0, null);
	}

	private void purgeEntities(LivingEntity player, int distance, int type) {
		List<LivingEntity> entities = getEntities(player, distance);
		
		int i;
		
		for (i = 0; i < entities.size(); i++) {
			if (isType(entities.get(i), type)) {
				moveOut(player, entities.get(i), distance);
				MineQuestListener.damageEntity(entities.get(i), 1);
			}
		}
		
	}

	public void unbind(Player player) {
		bindl = 0;
		bindr = 0;
		player.sendMessage(name + " is now unbound");
	}

	public void useAbility(Player player, Block block, Quester quester, int l, LivingEntity entity) {
		if (!enabled) {
			player.sendMessage(name + " is not enabled");
			return;
		}
		// TODO: add DrainLife
		// TODO: add DamageAura
		// TODO: add HealAura
		player.sendMessage("Call to " + name);
		if (quester.canCast(getManaCost(player, entity))) {
			if (player.getItemInHand() == ((l == 1)?bindl:bindr)) {
				if (name.equals("Fireball")) {
					if ((block.getX() == 0) && (block.getY() == 0) && (block.getZ() == 0)) {
						giveManaCost(player);
						return;
					}
					
					Block nblock = new Block(51);
					nblock.setX(block.getX());
					nblock.setY(block.getY() + ((entity == null)?1:0));
					nblock.setZ(block.getZ());
					nblock.update();
				
					if (entity == null) {
						player.sendMessage("Null Entity");
					}
					player.sendMessage("Fireball " + nblock.getX() + " " + nblock.getZ() + " " + nblock.getZ());
				} else if (name.equals("PowerStrike")) {
					if (entity != null) {
						MineQuestListener.damageEntity(entity, 5);
					} else {
						giveManaCost(player);
						player.sendMessage("PowerStrike must be bound to an attack");
						return;
					}
				} else if (name.equals("Sprint")) {
					double rot = player.getRotation() % 360 - 90;
					while (rot < 0) rot += 360;
					
					if ((rot  < 45) && (rot < 315)) {
						player.teleportTo(player.getX() - 1, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
					} else if ((rot > 45) && (rot < 135)) {
						player.teleportTo(player.getX(), player.getY(), player.getZ() - 1, player.getRotation(), player.getPitch());
					} else if ((rot > 135) && (rot < 225)) {
						player.teleportTo(player.getX() + 1, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
					} else {
						player.teleportTo(player.getX(), player.getY(), player.getZ() + 1, player.getRotation(), player.getPitch());
					}
				} else if (name.equals("Hail of Arrows")) {
					if (entity != null) {
						List<LivingEntity> entities = getEntities(entity, 10);
						int i;
						
						for (i = 0; i < entities.size(); i++) {
							if (myclass.getGenerator().nextDouble() < .5) {
								MineQuestListener.damageEntity(entities.get(i), 4);
							}
						}
					} else {
						giveManaCost(player);
						player.sendMessage("Hail of Arrows must be bound to an attack - Recommended that it is ranged");
						return;
					}
				} else if (name.equals("Fire Arrow")) {
					Block nblock = new Block(51);
					if ((entity != null) && (player.getItemInHand() == 261)) {
						nblock.setX((int)entity.getX());
						nblock.setY((int)entity.getY());
						nblock.setZ((int)entity.getZ());
						nblock.update();
					} else {
						giveManaCost(player);
						player.sendMessage("Fire Arrow must be bound to a bow attack - Recommended that it is ranged");
						return;
					}
				} else if (name.equals("Repulsion")) {
					purgeEntities(player, 10, 0);
				} else if (name.equals("FireChain")) {
					if (entity != null) {
						Ability fireball = new Ability("Fireball", myclass);
						LivingEntity this_entity;
						int i;
						
						fireball.enable(quester);
						fireball.bindl(player, new Item(player.getItemInHand(), 1));
						
						this_entity = getRandomEntity(entity, 10);
						for (i = 0; i < 3 + myclass.getCasterLevel(); i++) {
							fireball.useAbility(player, new Block(1, (int)entity.getX(), (int)entity.getY(), (int)entity.getZ()), 
									quester, 1, entity);
							this_entity = getRandomEntity(this_entity, 10);
							if (myclass.getGenerator().nextDouble() < .1) {
								break;
							}
						}
					} else {
						giveManaCost(player);
						player.sendMessage("FireChain must be bound to an attack");
						return;
					}		
				} else if (name.equals("Wall of Fire")) {
					double rot = player.getRotation() % 360 - 90;
					int x_change, z_change;
					int x, z;
					int i;
					while (rot < 0) rot += 360;
					
					
					if ((rot  < 45) && (rot < 315)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() - 3;
					} else if ((rot > 45) && (rot < 135)) {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() - 4;
					} else if ((rot > 135) && (rot < 225)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() + 3;
						z = (int)player.getZ() - 3;
					} else {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() + 3;
					}
					
					for (i = 0; i < 7; i++) {
						Block nblock = new Block(51);
						nblock.setX(x);
						nblock.setY(getNearestY(x, (int)player.getY(), z));
						nblock.setZ(z);
						nblock.update();
						x += x_change;
						z += z_change;
					}
				} else if (name.equals("Wall of Water")) {
					double rot = player.getRotation() % 360 - 90;
					int x_change, z_change;
					int x, z;
					int i;
					
					player.giveItem(new Item(326, 2));
					player.getInventory().updateInventory();
					
					while (rot < 0) rot += 360;
					
					
					if ((rot  < 45) && (rot < 315)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() - 3;
					} else if ((rot > 45) && (rot < 135)) {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() - 4;
					} else if ((rot > 135) && (rot < 225)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() + 3;
						z = (int)player.getZ() - 3;
					} else {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() + 3;
					}
					
					for (i = 0; i < 7; i++) {
						Block nblock = new Block(8);
						nblock.setX(x);
						nblock.setY(getNearestY(x, (int)player.getY(), z));
						nblock.setZ(z);
						nblock.update();
						x += x_change;
						z += z_change;
					}
				} else if (name.equals("Heal")) {
					player.giveItem(new Item(325, 1));
					player.getInventory().updateInventory();
					quester.setHealth(quester.getHealth() + myclass.getCasterLevel() + myclass.getGenerator().nextInt(8) + 1);
				} else if (name.equals("Drain Life")) {
					int drain = myclass.getGenerator().nextInt(3 + myclass.getCasterLevel()) + 1;
					if (entity != null) {
						entity.setHealth(entity.getHealth() - drain);
						quester.setHealth(quester.getHealth() + drain);
					} else {
						player.sendMessage("Must be called on an Entity");
					}
				} else if (name.equals("Trap")) {
					int i, j, k;
					int x, y, z;
					if (entity == null) {
						giveManaCost(player);
						player.sendMessage("Trap must be used on a livint entity");
						return;
					}
					x = block.getX();
					y = block.getY();
					z = block.getZ();
					Server server = etc.getServer();
					
					for (i = 1; i < 3; i++) {
						for (j = -1; j < 2; j++) {
							for (k = -1; k < 2; k++) {
								Block nblock = server.getBlockAt(x + j, y - i, z + k);
								
								nblock.setType(0);
								nblock.update();
							}
						}
					}
				} else if (name.equals("Heal Other")) {
					if (entity.isPlayer()) {
						Quester other = MineQuestListener.getQuester(entity.getName());
						if (other != null) {
							player.giveItem(new Item(325, 1));
							player.getInventory().updateInventory();
							other.setHealth(other.getHealth() + myclass.getCasterLevel() + myclass.getGenerator().nextInt(8) + 1);
						} else {
							giveManaCost(player);
							player.sendMessage(entity.getName() + " is not a Quester");
							return;
						}
					} else {
						player.sendMessage(name + " must be cast on another player");
					}
				} else if (name.equals("IceSphere")) {
					int j,k;
					if (entity == null) {
						giveManaCost(player);
						return;
					}
					
					for (j = -1; j < 2; j++) {
						for (k = -1; k < 2; k++) {
							Block nblock = new Block(78, block.getX() + j, 
									getNearestY(block.getX() + j, block.getY() + ((entity == null)?1:0), block.getZ() + k), 
									block.getZ() + k);
							nblock.update();
						}
					}
					
					entity.setHealth(entity.getHealth() - 3 - (myclass.getCasterLevel() / 2));
				} else {
					return;
				}
				myclass.expAdd(getExp(), quester);
			}
		} else {
			player.sendMessage("Not enough mana");
		}
	}

	private void giveManaCost(Player player) {
		List<Item> cost = getManaCost(player, null);
		int i;
		
		for (i = 0; i < cost.size(); i++) {
			player.giveItem(cost.get(i));
		}
	}

	public void unBindL() {
		bindl = 0;
	}

	public void unBindR() {
		bindr = 0;
	}

	public void parseFire(PluginLoader.DamageType type, int amount) {
		amount -= ((count++) & 0x01);
	}
}
