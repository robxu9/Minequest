package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class MessageEvent extends NormalEvent {
	private Quester[] questers;
	private String message;

	public MessageEvent(long delay, Quester questers[], String message) {
		super(delay);
		this.questers = questers;
		this.message = message;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		MineQuest.log("Sending Message: " + message);
		for (Quester quester : questers) {
			quester.sendMessage(message);
		}
	}

}
