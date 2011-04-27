package org.monk.MineQuest.Quest;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class NPCTarget extends Target{
	private String[] names;

	public NPCTarget(String[] names) {
		this.names = names;
	}

	@Override
	public Quester[] getTargets() {
		Quester[] questers = new Quester[names.length];
		
		int i;
		for (i = 0; i < names.length; i++) {
			questers[i] = MineQuest.getQuester(names[i]);
		}
		
		return questers;
	}
}
