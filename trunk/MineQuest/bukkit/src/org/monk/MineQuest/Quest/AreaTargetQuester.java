package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class AreaTargetQuester extends Target {
	private double radius;
	private Target target;

	public AreaTargetQuester(Target target, double radius) {
		this.target = target;
		this.radius = radius;
	}

	@Override
	public Quester[] getTargets() {
		List<Quester> questers = new ArrayList<Quester>();
		Quester quester = target.getTargets()[0];
		
		for (Quester q : MineQuest.getQuesters()) {
			if (MineQuest.distance(q.getPlayer().getLocation(), quester.getPlayer().getLocation()) < radius) {
				questers.add(q);
			}
		}
		
		return (Quester[])questers.toArray();
	}
}
