package org.monk.MineQuest.Quest.CanEdit;

import org.bukkit.Location;
import org.monk.MineQuest.Quester.Quester;


public class CanEditTypes extends CanEditBlock {
	private int[] ids;

	public CanEditTypes(int index, int ids[]) {
		super(null, index);
		this.ids = ids;
	}
	
	@Override
	public boolean canEdit(Quester quester, Location loc) {
		for (int id : ids) {
			if (loc.getBlock().getTypeId() == id) {
				return true;
			}
		}
		
		return false;
	}

}
