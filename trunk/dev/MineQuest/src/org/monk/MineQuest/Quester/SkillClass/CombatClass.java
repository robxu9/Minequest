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
package org.monk.MineQuest.Quester.SkillClass;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Ability.DefendingAbility;
import org.monk.MineQuest.Quester.Quester;

public class CombatClass extends SkillClass implements DefendingClass {

	public CombatClass(Quester quester, String type) {
		super(quester, type);
	}
	
	public CombatClass() {
		// Shell
	}

	/**
	 * Called whenever an attack is made by a Quester. Checks for
	 * bound abilities and if none are found sets the damage of the
	 * event as required.
	 * 
	 * @param defend Entity that is defending the attack
	 * @param event Event created for this attack
	 * @return boolean true
	 */
	public boolean attack(LivingEntity defend, EntityDamageByEntityEvent event) {
		event.setDamage(getDamage(defend));

		expAdd(getExpMob(defend) + MineQuest.getAdjustment());
		
		return true;
	}
	
	/**
	 * Gets the amount of damage that this class would do
	 * to a specific entity.
	 * 
	 * @param defend Defending Entity
	 * @return Damage to be dealt
	 */
	protected int getDamage(LivingEntity defend) {
		int damage = 2;
		damage += (quester.getLevel() / 10);
		damage += (level / 5);
		
		if (generator.nextDouble() < getCritChance()) {
			damage *= 2;
			quester.sendMessage("Critical Hit!");
		}
		if (!isClassItem(quester.getPlayer().getItemInHand())) {
			damage /= 2;
		}
		
//		if (MineQuest.getMob(defend) != null) {
//			damage = MineQuest.getMob(defend).defend(damage, quester.getPlayer());
//		}
		
		return damage;
	}

	/**
	 * Gets the Critical Hit chance for this specific
	 * class.
	 * 
	 * @return Critical Hit Chance
	 */
	private double getCritChance() {
		if ((getAbility("Deathblow") != null) && (getAbility("Deathblow").isEnabled())) {
			return 0.1;
		}
		
		return 0.05;
	}
	
	/**
	 * Called whenever the Quester that has this class is
	 * being attacked.
	 * 
	 * @param entity Entity that is attacker
	 * @param amount Amount of damage being dealt
	 * @return The amount of damage negated by this class
	 */
	public int defend(LivingEntity entity, int amount) {
		int i;
		int armor[] = getClassArmorIds();
		boolean flag = true;
		int sum = 0;
		
		if (armor == null) return 0;
		
		for (i = 0; i < armor.length; i++) {
			if (isWearing(armor[i])) {
				if (generator.nextDouble() < (.05 * i)) {
					sum++;
				}
			} else {
				flag = false;
			}
		}
		
		if (flag) {
			if (generator.nextDouble() < .4) {
				sum++;
			}
			
			if (generator.nextDouble() < armorBlockChance(armor)) {
				return amount;
			}
		}
		
		for (i = 0; i < ability_list.length; i++) {
			if (ability_list[i] instanceof DefendingAbility) {
				sum += ((DefendingAbility)ability_list[i]).parseDefend(quester, entity, amount - sum);
			}
		}
		
		return sum;
	}

	/**
	 * Gets the chance for an armor set to block an
	 * attack.
	 * 
	 * @param armor Set of armor to check.
	 * @return Chance of armor blocking attack.
	 */
	private double armorBlockChance(int[] armor) {
		switch (armor[0]) {
		case 302:
			return .25;
		case 310:
			return .20;
		case 306:
			return .15;
		case 314:
			return .10;
		case 298: 
			return .05;
		}
		
		return 0;
	}
	
	@Override
	public void levelUp() {
		super.levelUp();
		int add_health;
		int size;
		
		size = getSize();
		
		add_health = generator.nextInt(size) + 1;
		
		quester.addHealth(add_health);
	}

	private int getSize() {
		//TODO: deal with this
		if (type.equals("Warrior")) {
			return 10;
		} else if (type.equals("Archer") || type.equals("PeaceMage")) {
			return 6;
		} else {
			return 4;
		}
	}

}
