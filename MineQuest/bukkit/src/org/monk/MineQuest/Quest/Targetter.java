package org.monk.MineQuest.Quest;

import org.monk.MineQuest.Quester.Quester;

public class Targetter extends Target {
	private TargetEvent[] events;

	public Targetter(TargetEvent events[]) {
		this.events = events;
	}
	
	@Override
	public Quester[] getTargets() {
		Quester[] questers = new Quester[events.length];
		int i = 0;
		
		for (TargetEvent event : events) {
			questers[i++] = event.getTarget();
		}
		return questers;
	}
}
