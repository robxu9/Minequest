import java.util.ArrayList;
import java.util.List;


public class Ability {
	private String name;
	@SuppressWarnings("unused")
	private boolean enabled;
	private int bindl;
	private int bindr;
	private SkillClass myclass;
	
	public Ability(String name, SkillClass myclass) {
		this.name = name;
		enabled = true;;
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
	
	public void enable() {
		enabled = true;
	}
	
	public boolean equals(String name) {
		return name.equals(this.name);
	}
	
	public List<BaseEntity> getEntities(Player player, int radius) {
		List<BaseEntity> entities = new ArrayList<BaseEntity>(0);
		List<BaseEntity> serverList = etc.getServer().getEntityList();
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
	
	private boolean isWithin(Player player, BaseEntity baseEntity, int radius) {
		double x, y, z;
		
		x = player.getX() - baseEntity.getX();
		y = player.getY() - baseEntity.getY();
		z = player.getZ() - baseEntity.getZ();
		
		return Math.sqrt(x*x + y*y + z*z) < radius;
	}
	
	public void parseAttack(Player player, LivingEntity defend, Quester quester) {
		useAbility(player, new Block(1, (int)defend.getX(), (int)defend.getY(), (int)defend.getZ()), quester, 1);
	}
	
	public int parseDefend(Player player, BaseEntity mob, int amount) {
		if (!enabled) return 0;
		
		if (name.equals("Dodge")) {
			if (myclass.getGenerator().nextDouble() < (10 + (.0025 * myclass.getLevel()))) {
				double rot = player.getRotation();
				player.sendMessage("Dodging");
				
				if ((rot > 0) && (rot < 90)) {
					player.sendMessage(rot + "1");
					player.teleportTo(player.getX() - 10, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
				} else if ((rot > 90) && (rot < 180)) {
					player.sendMessage(rot + "2");
					player.teleportTo(player.getX(), player.getY(), player.getZ() - 10, player.getRotation(), player.getPitch());
				} else if ((rot > 180) && (rot < 270)) {
					player.sendMessage(rot + "3");
					player.teleportTo(player.getX() + 10, player.getY(), player.getZ(), player.getRotation(), player.getPitch());
				} else {
					player.sendMessage(rot + "4");
					player.teleportTo(player.getX(), player.getY(), player.getZ() + 10, player.getRotation(), player.getPitch());
				}
				return amount;
			}
			player.sendMessage("Not Dodging");
		}
		
		return 0;
	}
	
	public void parseLeftClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 1);
	}
	
	public void parseRightClick(Player player, Block block, Quester quester) {
		useAbility(player, block, quester, 0);
	}
	
	public void unbind(Player player) {
		bindl = 0;
		bindr = 0;
		player.sendMessage(name + " is now unbound");
	}

	public void useAbility(Player player, Block block, Quester quester, int l) {
		if (!enabled) return;
		// TODO: add PowerStrike
		// TODO: add Dodge
		// TODO: add Sprint
		// TODO: add Hail of Arrows
		// TODO: add Fire Arrow
		// TODO: add Repulsion
		// TODO: add FireChain
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
		if (name.equals("Fireball")) {
			if (player.getItemInHand() == ((l == 1)?bindl:bindr)) {
				if (quester.canCast(50)) {
					Block nblock = new Block(51);
					nblock.setX(block.getX());
					nblock.setY(block.getY());
					nblock.setZ(block.getZ());
					nblock.update();
					player.sendMessage("Created block at " + nblock.getX() + " " + nblock.getY() + " " + nblock.getZ());
					player.sendMessage("Created block at " + block.getX() + " " + block.getY() + " " + block.getZ());
					player.sendMessage("Casting Fireball 1");
					myclass.expAdd(15, quester);
				}
			}
		}
	}
}
