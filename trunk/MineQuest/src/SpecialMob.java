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
			if (nblock.getType() != 52) {
				nblock.setType(0);
			}
			nblock.update();
			nblock = etc.getServer().getBlockAt(nblock.getX(), nblock.getY() - 1, nblock.getZ());
		}
		if ((nblock.getType() != 9) && (nblock.getType() != 8) && (nblock.getType() != 10)) { // can't walk across liquid
			nblock.setType(3);
			nblock.update();
		}
	}

	public void dropLoot() {
		if (mob.getName().contains("Zombie")) {
			etc.getServer().dropItem(mob.getX(), mob.getY(), mob.getZ(), 39, 2);
		} else if (mob.getName().contains("Spider")) {
			etc.getServer().dropItem(mob.getX(), mob.getY(), mob.getZ(), 89, 6);
		} else if (mob.getName().contains("Skeleton")) {
			etc.getServer().dropItem(mob.getX(), mob.getY(), mob.getZ(), 331, 6);
		} else if (mob.getName().contains("Creeper")) {
			etc.getServer().dropItem(new Location(mob.getX(), mob.getY() + 1, mob.getZ()), 46);
		}
	
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
		} else if (mob.getName().contains("Spider")) {
			if (generator.nextDouble() < .2) {
				Ability trap = new Ability("Trap", null);
				trap.useAbility(null, new Block(0, (int)player.getX(), (int)player.getY(), (int)player.getZ()), null, 0, (LivingEntity)player);
			}
		} else if (mob.getName().contains("Skeleton")) {
			double num = generator.nextDouble();
			Ability abil;
			if (num < .2) {
				abil = new Ability("Fireball", null);
			} else if (num < .4) {
				abil = new Ability("FireChain", null);
			} else if (num < .6) {
				abil = new Ability("IceSphere", null);
			} else if (num < .8) {
				abil = new Ability("Hail of Arrows", null);
			} else {
				abil = new Ability("PowerStrike", null);
			}
			abil.useAbility(null, new Block(0, (int)player.getX(), (int)player.getY(), (int)player.getZ()), null, 0, (LivingEntity)player);			
		} else if (mob.getName().contains("Creeper")) {
			Block nblock = new Block(46);
			nblock.setX((int)mob.getX());
			nblock.setY(Ability.getNearestY((int)mob.getX(), (int)mob.getY(), (int)mob.getZ()));
			nblock.setZ((int)mob.getZ());
			nblock.update();
		}
		amount *= 2;
		
		return amount;
	}
}
