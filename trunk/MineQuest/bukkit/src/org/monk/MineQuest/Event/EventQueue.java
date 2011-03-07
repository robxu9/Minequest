package org.monk.MineQuest.Event;

import org.bukkit.plugin.Plugin;
import org.monk.MineQuest.MineQuest;

public class EventQueue {
	private Plugin minequest;

	public EventQueue(Plugin plugin) {
		minequest = plugin;
	}
	
	public int addEvent(Event event) {
		EventParser newEventParser = new EventParser(event);
		newEventParser.setId(MineQuest.getSServer().getScheduler().scheduleSyncRepeatingTask(minequest, newEventParser, 1, 1));
		return newEventParser.getId();
	}
}
