package org.monk.MineQuest.Mob;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;

public class MQMob {
	protected LivingEntity entity;

	public MQMob(LivingEntity entity) {
		this.entity = entity;
	}
	
	public LivingEntity getMonster() {
		return entity;
	}

	public int getId() {
		return entity.getEntityId();
	}
	
	public double dodgeChance() {
		return .01;
	}

	public void dropLoot() {
	}

	public int defend(int damage, LivingEntity player) {
	    MineQuest.log("MQMob Health " + (entity.getHealth() - damage));
		return damage;
	}

	public int attack(int amount, LivingEntity player) {
		return amount;
	}

	public int getHealth() {
		return entity.getHealth();
	}

	public void setHealth(int i) {
		if (i < 0) {
			i = 0;
		} else if (i > 20) {
			i = 20;
		}
		entity.setHealth(i);
	    MineQuest.log("MQMob Health " + (i));
	}

	public void damage(int i) {
		setHealth(entity.getHealth() - i);
	}

}
