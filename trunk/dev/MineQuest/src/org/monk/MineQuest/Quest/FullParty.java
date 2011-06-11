package org.monk.MineQuest.Quest;

import java.util.List;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class FullParty extends Party {
	public FullParty() {
	}
	
	@Override
	public void addQuester(Quester quester) {
		MineQuest.log("[WARNING] Cannot add quester to Main Party!");
	}
	
	@Override
	public void remQuester(Quester quester) {
		MineQuest.log("[WARNING] Cannot add quester to Main Party!");
	}
	
	@Override
	public Quester[] getQuesterArray() {
		Quester[] ret = new Quester[MineQuest.getRealQuesters().size()];
		int i = 0;
		
		for (Quester quester : MineQuest.getRealQuesters()) {
			ret[i++] = quester;
		}
		
		return ret;
	}
	
	@Override
	public List<Quester> getQuesters() {
		return MineQuest.getRealQuesters();
	}

}
