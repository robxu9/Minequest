package org.monk.MineQuest.Ability;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.Quester.Quester;

public interface DefendingAbility {
	/**
	 * Parse any affects of the ability being activated as part
	 * of a defense.
	 * 
	 * @param quester Defender
	 * @param mob Attacker
	 * @param amount Damage to be taken
	 * @return amount of damage that should be negated
	 */
	public int parseDefend(Quester quester, LivingEntity mob, int amount);
}
