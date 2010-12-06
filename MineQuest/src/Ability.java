import java.util.ArrayList;
import java.util.List;


public class Ability {
	private String name;
	private boolean enabled;
	private int bindl;
	private int bindr;
	private SkillClass myclass;
	
	public Ability(String name, SkillClass myclass) {
		this.name = name;
		enabled = false;
		this.myclass = myclass;
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
	
	public int getManaCost(Player player, LivingEntity entity) {
		int adj = 0;
		
		if (entity != null) {
			adj = getDistance(player, entity);
		}
		
		if (name.equals("Dodge")) {
			return 10 + adj;
		} else if (name.equals("Fireball")) {
			return 10 + adj;
		} else if (name.equals("Deathblow")) {
			return 30 + adj;
		} else if (name.equals("Sprint")) {
			return 3 + adj;
		} else if (name.equals("PowerStrike")) {
			return 7 + adj;
		} else if (name.equals("Hail of Arrows")) {
			return 15 + adj;
		} else if (name.equals("Fire Arrow")) {
			return 10 + adj;
		} else if (name.equals("Repulsion")) {
			return 12;
		} else if (name.equals("FireChain")) {
			return 40 + adj;
		} else if (name.equals("Wall of Fire")) {
			return 20;
		}
		
		return 10 + adj;
	}

	private int getDistance(Player player, LivingEntity entity) {
		double x, y, z;
		x = player.getX() - entity.getX();
		y = player.getY() - entity.getY();
		z = player.getZ() - entity.getZ();
		
		return (int)Math.sqrt(x*x + y*y + z*z);
	}

	public boolean equals(String name) {
		return name.equals(this.name);
	}
	
	public List<LivingEntity> getEntities(LivingEntity player, int radius) {
		List<LivingEntity> entities = new ArrayList<LivingEntity>(0);
		List<LivingEntity> serverList = etc.getServer().getLivingEntityList();
		int i;
		
		for (i = 0; i < serverList.size(); i++) {
			if (isWithin(player, serverList.get(i), radius)) {
				entities.add(serverList.get(i));
			}
		}
		
		return entities;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isBound(int id) {
		return (bindl == id) || (bindr == id);
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
	
	private boolean isWithin(LivingEntity player, LivingEntity baseEntity, int radius) {
		double x, y, z;
		
		x = player.getX() - baseEntity.getX();
		y = player.getY() - baseEntity.getY();
		z = player.getZ() - baseEntity.getZ();
		
		return Math.sqrt(x*x + y*y + z*z) < radius;
	}
	
	public void parseAttack(Player player, LivingEntity defend, Quester quester) {
		useAbility(player, new Block(1, (int)defend.getX(), (int)defend.getY() - 1, (int)defend.getZ()), quester, 1, defend);
	}
	
	public int parseDefend(Player player, BaseEntity mob, int amount) {
		if (!enabled) return 0;
		
		if (name.equals("Dodge")) {
			if (myclass.getGenerator().nextDouble() < (.01 + (.0025 * myclass.getLevel()))) {
				double rot = player.getRotation() % 360;
				if (rot < 0) rot += 360;
				
				if ((rot  < 45) && (rot < 315)) {
					player.sendMessage("Dodging1");
					player.teleportTo(player.getX() - 1, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
				} else if ((rot > 45) && (rot < 135)) {
					player.sendMessage("Dodging2");
					player.teleportTo(player.getX(), player.getY(), player.getZ() - 1, player.getRotation(), player.getPitch());
				} else if ((rot > 135) && (rot < 225)) {
					player.sendMessage("Dodging3");
					player.teleportTo(player.getX() + 1, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
				} else {
					player.sendMessage("Dodging4");
					player.teleportTo(player.getX(), player.getY(), player.getZ() + 1, player.getRotation(), player.getPitch());
				}
				return amount;
			}
			player.sendMessage("Not Dodging");
		}
		
		return 0;
	}
	
	public void parseLeftClick(Player player, Block block, Quester quester) {
		player.sendMessage("Left click on " + name);
		useAbility(player, block, quester, 1, null);
	}
	
	public void parseRightClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 0, null);
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
		// TODO: add WallofFire
		// TODO: add IceSphere
		// TODO: add DrainLife
		// TODO: add DrainMana - hmm...
		// TODO: add FireResistance
		// TODO: add Trap
		// TODO: add Heal
		// TODO: add HealOther
		// TODO: add WallofWater
		// TODO: add DamageAura
		// TODO: add HealAura
		player.sendMessage("Call to " + name);
		if (quester.canCast(getManaCost(player, entity))) {
			if (player.getItemInHand() == ((l == 1)?bindl:bindr)) {
				if (name.equals("Fireball")) {
						Block nblock = new Block(51);
						nblock.setX(block.getX());
						nblock.setY(block.getY() + 1);
						nblock.setZ(block.getZ());
						nblock.update();
						player.sendMessage("Fireball " + block.getX() + " " + block.getY() + " " + block.getZ());
				} else if (name.equals("PowerStrike")) {
					if (entity != null) {
						MineQuestListener.damageEntity(entity, 5);
					} else {
						player.sendMessage("PowerStrike must be bound to an attack");
						return;
					}
				} else if (name.equals("Sprint")) {
					double rot = player.getRotation() % 360 - 90;
					if (rot < 0) rot += 360;
					
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
						player.sendMessage("Hail of Arrows must be bound to an attack - Recommended that it is ranged");
						return;
					}
				} else if (name.equals("Fire Arrow")) {
					if (entity != null) {
						Block nblock = new Block(51);
						nblock.setX((int)entity.getX());
						nblock.setY((int)entity.getY());
						nblock.setZ((int)entity.getZ());
						nblock.update();
					} else {
						player.sendMessage("Fire Arrow must be bound to an attack - Recommended that it is ranged");
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
							fireball.useAbility(player, new Block(1, (int)entity.getX(), (int)entity.getY() - 1, (int)entity.getZ()), 
									quester, 1, entity);
							this_entity = getRandomEntity(this_entity, 10);
							if (myclass.getGenerator().nextDouble() < .1) {
								break;
							}
						}
					} else {
						player.sendMessage("FireChain must be bound to an attack");
						return;
					}		
				} else if (name.equals("Wall of Fire")) {
					double rot = player.getRotation() % 360 - 90;
					int x_change, z_change;
					int x, z;
					int i;
					Block nblock[] = new Block[7];
					if (rot < 0) rot += 360;
					
					
					if ((rot  < 45) && (rot < 315)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() - 2;
						z = (int)player.getZ() - 3;
					} else if ((rot > 45) && (rot < 135)) {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() - 2;
					} else if ((rot > 135) && (rot < 225)) {
						x_change = 0;
						z_change = 1;
						x = (int)player.getX() + 2;
						z = (int)player.getZ() - 3;
					} else {
						x_change = 1;
						z_change = 0;
						x = (int)player.getX() - 3;
						z = (int)player.getZ() + 2;
					}
					player.sendMessage("Creating Wall " + x + " " + z);
					
					for (i = 0; i < 7; i++) {
						nblock[i] = new Block(51);
						nblock[i].setX(x);
						nblock[i].setY(etc.getServer().getHighestBlockY(x, z) + 1);
						nblock[i].setZ(z);
						nblock[i].update();
						x += x_change;
						z += z_change;
					}
				} else {
					return;
				}
				myclass.expAdd(getExp(), quester);
			}
		} else {
			player.sendMessage("Not enough mana");
		}
	}

	private LivingEntity getRandomEntity(LivingEntity entity, int radius) {
		List<LivingEntity> entities = getEntities(entity, radius);
		int i = myclass.getGenerator().nextInt(entities.size());
		
		return entities.get(i);
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
		
		other.teleportTo(x, y, z, other.getRotation(), other.getPitch());
		
		return;
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

	private int getExp() {
		// TODO: Fix getExp in Ability.java
		return 15;
	}
}
