package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class NPCTarget extends Target{
	private String[] names;

	public NPCTarget(String[] names) {
		this.names = names;
	}

	@Override
	public List<Quester> getTargets() {
		List<Quester> questers = new ArrayList<Quester>();
		
		int i;
		for (i = 0; i < names.length; i++) {
			if (MineQuest.getQuester(names[i]) != null) {
				questers.add(MineQuest.getQuester(names[i]));
			}
		}
		
		return questers;
	}
}
