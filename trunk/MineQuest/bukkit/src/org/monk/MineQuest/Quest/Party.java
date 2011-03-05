package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.Quester.Quester;

public class Party {
	private List<Quester> questers;
	
	public Party() {
		questers = new ArrayList<Quester>();
	}
	
	public void addQuester(Quester quester) {
		quester.setParty(this);
		
		questers.add(quester);
	}
	
	public void remQuester(Quester quester) {
		quester.setParty(null);
		
		questers.remove(quester);
	}
	
	public List<Quester> getQuesters() {
		return questers;
	}
	
	public Quester[] getQuesterArray() {
		Quester[] ret = new Quester[questers.size()];
		int i = 0;
		
		for (Quester quester : questers) {
			ret[i++] = quester;
		}
		
		return ret;
	}
}
