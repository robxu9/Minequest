package org.monk.MineQuest.Quester.SkillClass;

import org.bukkit.entity.LivingEntity;

public interface DefendingClass {
	/**
	 * Called whenever the Quester that has this class is
	 * being attacked.
	 * 
	 * @param entity Entity that is attacker
	 * @param amount Amount of damage being dealt
	 * @return The amount of damage negated by this class
	 */
	public int defend(LivingEntity entity, int amount);
}
