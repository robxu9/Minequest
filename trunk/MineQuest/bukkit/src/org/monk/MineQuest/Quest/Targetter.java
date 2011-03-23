package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.Event.TargetEvent;
import org.monk.MineQuest.Quester.Quester;

public class Targetter extends Target {
	private TargetEvent[] events;

	public Targetter(TargetEvent events[]) {
		this.events = events;
	}
	
	@Override
	public Quester[] getTargets() {
		List<Quester> questers = new ArrayList<Quester>();
		
		for (TargetEvent event : events) {
			if (event.getTarget() != null) {
				questers.add(event.getTarget());
			}
		}
		return (Quester[])questers.toArray();
	}
}
