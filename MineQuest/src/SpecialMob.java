import java.util.Random;


public class SpecialMob {
	private LivingEntity mob;
	static Random generator = new Random();

	public SpecialMob(LivingEntity livingEntity) {
		mob = livingEntity;
	}

	public LivingEntity getMob() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updatePos() {
		Block nblock = etc.getServer().getBlockAt((int)mob.getX(), Ability.getNearestY((int)mob.getX(), (int)mob.getY() - 5, (int)mob.getZ()) - 1, (int)mob.getZ());
		if (etc.getServer().getBlockAt(nblock.getX(), nblock.getY() + 1, nblock.getZ()).getType() == 78) {
			nblock = etc.getServer().getBlockAt(nblock.getX(), nblock.getY() + 1, nblock.getZ());
			nblock.setType(0);
			nblock.update();
			nblock = etc.getServer().getBlockAt(nblock.getX(), nblock.getY() - 1, nblock.getZ());
		}
		if ((nblock.getType() != 9) && (nblock.getType() != 8) && (nblock.getType() != 10)) { // can't walk across liquid
			nblock.setType(3);
			nblock.update();
		}
	}

	public void dropLoot() {
		// TODO Auto-generated method stub
		
	}

	public boolean is(LivingEntity defend) {
		return (mob.getId() == defend.getId());
	}

	public int defend(Player player, int damage) {
		if (mob.getName().contains("Zombie")) {
			if (generator.nextDouble() < .10) {
				double rot = mob.getRotation();
				while (rot < 0) rot += 360;
				
				if ((rot  < 45) && (rot > 315)) {
					player.sendMessage("Zombie Dodged");
					mob.teleportTo(mob.getX() - 1, 
							Ability.getNearestY((int)mob.getX(), (int)mob.getY(), (int)mob.getZ()),
							mob.getZ(), mob.getRotation(), mob.getPitch());
				} else if ((rot > 45) && (rot < 135)) {
					player.sendMessage("Zombie Dodged");
					mob.teleportTo(mob.getX(), 
							Ability.getNearestY((int)mob.getX(), (int)mob.getY(), (int)mob.getZ()),
							mob.getZ() - 1, mob.getRotation(), mob.getPitch());
				} else if ((rot > 135) && (rot < 225)) {
					player.sendMessage("Zombie Dodged");
					mob.teleportTo(mob.getX() + 1, 
							Ability.getNearestY((int)mob.getX(), (int)mob.getY(), (int)mob.getZ()), 
							mob.getZ(), mob.getRotation(), mob.getPitch());
				} else {
					player.sendMessage("Zombie Dodged");
					mob.teleportTo(mob.getX(), 
							Ability.getNearestY((int)mob.getX(), (int)mob.getY(), (int)mob.getZ()),
							mob.getZ() + 1, mob.getRotation(), mob.getPitch());
				}
				return 0;
			}
		}
		
		damage /= 2;
		
		return damage;
	}

	public int attack(Quester quester, Player player, int amount) {
		if (mob.getName().contains("Zombie")) {
			if (generator.nextDouble() < .5) {
				MineQuestListener.getQuester(player.getName()).poison();
			}
		}
		amount *= 2;
		
		return amount;
	}
}
