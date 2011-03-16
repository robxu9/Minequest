package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class AreaTarget extends Target {
	private Location location;
	private double radius;

	public AreaTarget(Location location, double radius) {
		this.location = location;
		this.radius = radius;
	}

	@Override
	public Quester[] getTargets() {
		List<Quester> questers = new ArrayList<Quester>();
		
		for (Quester q : MineQuest.getQuesters()) {
			if (MineQuest.distance(q.getPlayer().getLocation(), location) < radius) {
				questers.add(q);
			}
		}
		
		return (Quester[])questers.toArray();
	}

}
