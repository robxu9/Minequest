package org.monk.MineQuest.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.monk.MineQuest.MineQuest;

public class EventQueue {
	private Plugin minequest;

	public EventQueue(Plugin plugin) {
		minequest = plugin;
	}
	
	public void addEvent(Event event) {
		EventParser newEventParser = new EventParser(event);
		newEventParser.setId(MineQuest.getSServer().getScheduler().scheduleSyncRepeatingTask(minequest, newEventParser, 1, 1));
	}
}
