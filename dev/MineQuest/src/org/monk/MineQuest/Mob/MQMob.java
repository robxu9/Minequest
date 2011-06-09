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
package org.monk.MineQuest.Mob;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class MQMob {
	protected LivingEntity entity;
	private Quester last_attack;
	protected boolean dead;

	public MQMob(LivingEntity entity) {
		this.entity = entity;
		last_attack = null;
		dead = false;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public Quester getLastAttacker() {
		return last_attack;
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
	
	public void cancel() {
	}

	public void dropLoot() {
	}

	public int defend(int damage, LivingEntity player) {
		if ((entity.getHealth() - damage) <= 0) {
			dead = true;
		}
		if (MineQuest.getQuester(player) != null) {
			last_attack = MineQuest.getQuester(player);
		}
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
	}

	public void damage(int i) {
		if ((entity.getHealth() - i) <= 0) {
			dead = true;
		}
		entity.damage(i);
	}

	public void damage(int i, Quester source) {
		if (source != null) {
			this.last_attack = source;
		}
		if ((entity.getHealth() - i) <= 0) {
			dead = true;
		}
		entity.damage(i, source.getPlayer());
	}

}
