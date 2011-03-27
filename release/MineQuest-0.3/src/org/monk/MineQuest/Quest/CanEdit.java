package org.monk.MineQuest.Quest;

import org.bukkit.Location;
import org.monk.MineQuest.Quester.Quester;

public interface CanEdit extends TargetEvent {
	public boolean canEdit(Quester quester, Location loc);
	
	public int getQuestIndex();
}
