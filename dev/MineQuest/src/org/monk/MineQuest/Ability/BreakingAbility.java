package org.monk.MineQuest.Ability;

import org.bukkit.block.Block;
import org.monk.MineQuest.Quester.Quester;

public interface BreakingAbility {
	public void blockBreak(Quester quester, Block block);
}
