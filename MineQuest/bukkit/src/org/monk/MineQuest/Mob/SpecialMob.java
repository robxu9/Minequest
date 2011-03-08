package org.monk.MineQuest.Mob;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Event.SpecialMobHandler;
import org.monk.MineQuest.Quester.SkillClass.Combat.WarMage;

public class SpecialMob extends MQMob {
	protected boolean half;
	protected Random generator;
	protected int id;

	public SpecialMob(Monster entity) {
		super(entity);
		half = false;
		generator = new Random();
		
		id = MineQuest.getEventParser().addEvent(new SpecialMobHandler(1, this));
	}

	@Override
	public double dodgeChance() {
		return .05;
	}

	@Override
	public void dropLoot() {
		if (entity instanceof Zombie) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(39, 2));
		} else if (entity instanceof Spider) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(89, 6));
		} else if (entity instanceof Skeleton) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(331, 6));
		} else if (entity instanceof Creeper) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(46));
		}

	}
	
	@Override
	public void cancel() {
		MineQuest.getEventParser().cancel(id);
	}

	@Override
	public int attack(int amount, LivingEntity player) {
		if (entity instanceof Zombie) {
			if (generator.nextDouble() < .8) {
				MineQuest.getQuester((Player)player).poison();
			}
		} else if (entity instanceof Spider) {
			if (generator.nextDouble() < .2) {
				Ability trap = Ability.newAbility("Trap", null);
				trap.castAbility(null, player.getLocation(), player);
			}
		} else if (entity instanceof Skeleton) {
			double num = generator.nextDouble();
			WarMage warmage = new WarMage();
			List<Ability> abilities = Ability.newAbilities("", warmage);
			int index = (int)(num * abilities.size());
			
			abilities.get(index).castAbility(null, player.getLocation(), player);
		} else if (entity instanceof Creeper) {
			Location location = new Location(entity.getWorld(), (int) entity.getLocation().getX(), 
					Ability.getNearestY(entity.getWorld(), (int) entity.getLocation().getX(), (int) entity.getLocation().getY(),
					(int) entity.getLocation().getZ()), (int) entity.getLocation().getZ());
			Block nblock = entity.getWorld().getBlockAt(location);
			nblock.setType(Material.TNT);
		}
		
		amount *= 2;
		
		return amount;
	}

	@Override
	public int defend(int damage, LivingEntity player) {
		if (entity instanceof Zombie) {
			if (generator.nextDouble() < .10) {
				double rot = entity.getLocation().getYaw();
				while (rot < 0)	rot += 360;

				if ((rot < 45) || (rot > 315)) {
					((Player)player).sendMessage("Zombie Dodged");
					entity.teleportTo(new Location(entity.getWorld(),
							entity.getLocation().getX() - 1,
							Ability.getNearestY(entity.getWorld(), (int) entity.getLocation().getX(),
									(int) entity.getLocation().getY(), (int) entity.getLocation().getZ()),
									entity.getLocation().getZ()));
				} else if ((rot > 45) && (rot < 135)) {
					((Player)player).sendMessage("Zombie Dodged");
					entity.teleportTo(new Location(entity.getWorld(),
							entity.getLocation().getX(),
							Ability.getNearestY(entity.getWorld(), (int) entity.getLocation().getX(),
									(int) entity.getLocation().getY(), (int) entity.getLocation().getZ()),
									entity.getLocation().getZ() - 1));
				} else if ((rot > 135) && (rot < 225)) {
					((Player)player).sendMessage("Zombie Dodged");
					entity.teleportTo(new Location(entity.getWorld(),
							entity.getLocation().getX() + 1,
							Ability.getNearestY(entity.getWorld(), (int) entity.getLocation().getX(),
									(int) entity.getLocation().getY(), (int) entity.getLocation().getZ()),
									entity.getLocation().getZ()));
				} else {
					((Player)player).sendMessage("Zombie Dodged");
					entity.teleportTo(new Location(entity.getWorld(),
							entity.getLocation().getX(),
							Ability.getNearestY(entity.getWorld(), (int) entity.getLocation().getX(),
									(int) entity.getLocation().getY(), (int) entity.getLocation().getZ()),
									entity.getLocation().getZ() + 1));
				}
				return 0;
			}
		}

		if (damage == 1) {
			if (!half) {
				half = true;
			} else {
				half = false;
				return 1;
			}
		}
		return damage / 2;
	}
	
	@Override
	public void damage(int i) {
		if (i == 1) {
			if (!half) {
				half = true;
			} else {
				half = false;
				super.damage(1);
			}
		}
		super.damage(i / 2);
	}

}
