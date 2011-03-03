package org.monk.MineQuest.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.monk.MineQuest.MineQuest;

public class EventQueue implements java.lang.Runnable {
	private ArrayList<EventParser> events;
	private Plugin minequest;

	public EventQueue(Plugin plugin) {
		events = new ArrayList<EventParser>();
		minequest = plugin;
	}
	
	public void addEvent(Event event) {
		EventParser newEventParser = new EventParser(event);
		newEventParser.setId(MineQuest.getSServer().getScheduler().scheduleSyncRepeatingTask(minequest, newEventParser, 1, 1));
		events.add(newEventParser);
		for (EventParser parser : events) {
			if (parser.isDone()) {
				events.remove(parser);
			}
		}
	}
	
	public void clearComplete() {
		for (EventParser parser : events) {
			if (parser.getComplete()) {
				MineQuest.getSServer().getScheduler().cancelTask(parser.getId());
				parser.setDone(true);
			}
		}
	}

	@Override
	public void run() {
		clearComplete();
	}
}
