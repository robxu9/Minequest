package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.BlockEvent;
import org.monk.MineQuest.Event.AreaEvent;
import org.monk.MineQuest.Event.EntitySpawnerCompleteEvent;
import org.monk.MineQuest.Event.EntitySpawnerEvent;
import org.monk.MineQuest.Event.Event;
import org.monk.MineQuest.Event.QuestEvent;
import org.monk.MineQuest.Quester.Quester;

public class Quest {
	private Quester questers[];
	private List<QuestTask> tasks;
	
	public Quest(Quester questers[]) {
		this.questers = questers;
		tasks = new ArrayList<QuestTask>();
		
		Event[] events = new Event[1];
		LivingEntity entities[] = new LivingEntity[questers.length];
		int i = 0;
		for (Quester quester : questers) {
			entities[i++] = quester.getPlayer();
			quester.setQuest(this);
		}
		events[0] = new AreaEvent(this, 500, entities, new Location(entities[0].getWorld(), -116, 68, -179), 2);
		
		tasks.add(new QuestTask(events));
		events = new Event[4];
		
		Location location = new Location(entities[0].getWorld(), -117, 67, -181);
		events[0] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		location = new Location(entities[0].getWorld(), -118, 67, -181);
		events[1] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		location = new Location(entities[0].getWorld(), -119, 67, -181);
		events[2] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		events[3] = new QuestEvent(this, 1000);
		
		tasks.add(new QuestTask(events));
		
		events = new Event[4];
		location = new Location(entities[0].getWorld(), -97, 71, -178);
		events[0] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.SKELETON);
		location = new Location(entities[0].getWorld(), -97, 71, -185);
		events[1] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.ZOMBIE);
		events[2] = new EntitySpawnerCompleteEvent(120000, (EntitySpawnerEvent)events[0]);
		events[3] = new EntitySpawnerCompleteEvent(120000, (EntitySpawnerEvent)events[1]);
		
		tasks.add(new QuestTask(events));
		
		MineQuest.getEventParser().addEvent(tasks.get(0).getEvents()[0]);
	}
	
	public Event[] getNextEvents() {
		tasks.remove(0);
		if (tasks.size() == 1) {
			for (Quester quester : questers) {
				quester.clearQuest();
			}
		}
		MineQuest.log("Getting the Next " + tasks.get(0).getEvents().length + " Events");
		return tasks.get(0).getEvents();
	}
}
