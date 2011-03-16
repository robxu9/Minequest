package org.monk.MineQuest.Quest;

import org.monk.MineQuest.Quester.Quester;

public class PartyTarget extends Target {
	private Party party;

	public PartyTarget(Party party) {
		this.party = party;
	}

	@Override
	public Quester[] getTargets() {
		return party.getQuesterArray();
	}
}
