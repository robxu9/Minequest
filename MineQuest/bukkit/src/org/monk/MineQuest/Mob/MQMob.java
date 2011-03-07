package org.monk.MineQuest.Mob;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

public class MQMob {
	protected Monster entity;

	public MQMob(Monster entity) {
		this.entity = entity;
	}
	
	public Monster getMonster() {
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

}
