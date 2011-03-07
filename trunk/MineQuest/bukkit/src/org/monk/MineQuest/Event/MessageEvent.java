package org.monk.MineQuest.Event;

import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quester.Quester;

public class MessageEvent extends NormalEvent {
	private String message;
	private Party party;

	public MessageEvent(long delay, Party party, String message) {
		super(delay);
		this.party = party;
		this.message = message;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		for (Quester quester : party.getQuesterArray()) {
			quester.sendMessage(message);
		}
	}
	
	@Override
	public String getName() {
		return "Message Event";
	}

}
