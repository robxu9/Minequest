package org.monk.MineQuest.Quest;

import org.bukkit.Location;
import org.monk.MineQuest.Quester.Quester;

public class CanEditBlock implements CanEdit {
	public Quester quester;
	private Location location;
	private int index;
	
	public CanEditBlock(Location location, int index) {
		this.location = location;
		this.index = index;
	}

	private boolean equals(Location location, Location location2) {
		if (((int)location.getX()) != ((int)location2.getX())) {
			return false;
		}
		if (((int)location.getY()) != ((int)location2.getY())) {
			return false;
		}
		if (((int)location.getZ()) != ((int)location2.getZ())) {
			return false;
		}
		
		return true;
	}

	@Override
	public Quester getTarget() {
		return quester;
	}

	@Override
	public boolean canEdit(Quester quester, Location loc) {
		if (equals(location, loc)) {
			this.quester = quester;
			return true;
		}
		return false;
	}

	@Override
	public int getQuestIndex() {
		return index;
	}

}
