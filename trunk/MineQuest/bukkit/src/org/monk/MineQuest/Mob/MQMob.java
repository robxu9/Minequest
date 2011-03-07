package org.monk.MineQuest.Mob;

import org.bukkit.entity.LivingEntity;

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
		return damage;
	}

	public int attack(int amount, LivingEntity player) {
		return amount;
	}

	public int getHealth() {
		return entity.getHealth();
	}

	public void setHealth(int i) {
		entity.setHealth(i);
	}

	public void damage(int i) {
		setHealth(entity.getHealth() - i);
	}

}
