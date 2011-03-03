package org.monk.MineQuest.Quest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.AreaEvent;
import org.monk.MineQuest.Event.BlockEvent;
import org.monk.MineQuest.Event.EntitySpawnerCompleteEvent;
import org.monk.MineQuest.Event.EntitySpawnerCompleteNMEvent;
import org.monk.MineQuest.Event.EntitySpawnerEvent;
import org.monk.MineQuest.Event.EntitySpawnerNoMove;
import org.monk.MineQuest.Event.Event;
import org.monk.MineQuest.Event.ExperienceAdd;
import org.monk.MineQuest.Event.MessageEvent;
import org.monk.MineQuest.Event.QuestEvent;
import org.monk.MineQuest.Quester.Quester;

public class Quest {
	private Quester questers[];
	private List<QuestTask> tasks;
	
	public Quest(Quester questers[]) {
		this.questers = questers;
		tasks = new ArrayList<QuestTask>();
		
		tasks.add(new QuestTask(null));
		
		Event[] events = new Event[3];
		LivingEntity entities[] = new LivingEntity[questers.length];
		int i = 0;
		for (Quester quester : questers) {
			entities[i++] = quester.getPlayer();
			quester.setQuest(this);
		}
		events[0] = new AreaEvent(this, 500, entities, new Location(entities[0].getWorld(), -116, 68, -179), 2);
		events[1] = new MessageEvent(10, questers, "There has been suspicious activity on the Ship to the East");
		events[2] = new MessageEvent(20, questers, "Go investigate it!");
		
		tasks.add(new QuestTask(events));
		events = new Event[6];
		
		Location location = new Location(entities[0].getWorld(), -117, 67, -181);
		events[0] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		location = new Location(entities[0].getWorld(), -118, 67, -181);
		events[1] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		location = new Location(entities[0].getWorld(), -119, 67, -181);
		events[2] = new BlockEvent(1000, entities[0].getWorld().getBlockAt(location), Material.WOOD);
		events[3] = new QuestEvent(this, 1000);
		events[4] = new MessageEvent(2000, questers, "Its a Trap! Kill the Monsters!");
		events[5] = new MessageEvent(120000, questers, "These monsters just keep respawning, get those on the Shoreline!");
		
		tasks.add(new QuestTask(events));
		
		events = new Event[9];
		//Normals on Ship
		location = new Location(entities[0].getWorld(), -97, 71, -178);
		events[0] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.SKELETON, false);
		location = new Location(entities[0].getWorld(), -97, 71, -185);
		events[1] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.ZOMBIE, false);
		location = new Location(entities[0].getWorld(), -99, 71, -178);
		events[2] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.SPIDER, false);
		location = new Location(entities[0].getWorld(), -99, 71, -185);
		events[3] = new EntitySpawnerEvent(5, entities[0].getWorld(), location, CreatureType.ZOMBIE, false);
		
		//Supers on Land
		location = new Location(entities[0].getWorld(), -87, 67, -142);
		events[4] = new EntitySpawnerNoMove(5, entities[0].getWorld(), location, CreatureType.SKELETON, true);
		location = new Location(entities[0].getWorld(), -87, 67, -143);
		events[5] = new EntitySpawnerNoMove(5, entities[0].getWorld(), location, CreatureType.ZOMBIE, true);
		location = new Location(entities[0].getWorld(), -88, 67, -142);
		events[6] = new EntitySpawnerNoMove(5, entities[0].getWorld(), location, CreatureType.SPIDER, true);
		location = new Location(entities[0].getWorld(), -88, 67, -143);
		events[7] = new EntitySpawnerNoMove(5, entities[0].getWorld(), location, CreatureType.ZOMBIE, true);
		
		events[8] = new AreaEvent(this, 500, entities, new Location(entities[0].getWorld(), -87, 67, -143), 20);
		
		Event[] end_move = new Event[5];
		end_move[0] = new EntitySpawnerCompleteNMEvent(this, 10, (EntitySpawnerNoMove)events[4]);
		end_move[1] = new EntitySpawnerCompleteNMEvent(this, 10, (EntitySpawnerNoMove)events[5]);
		end_move[2] = new EntitySpawnerCompleteNMEvent(this, 10, (EntitySpawnerNoMove)events[6]);
		end_move[3] = new EntitySpawnerCompleteNMEvent(this, 10, (EntitySpawnerNoMove)events[7]);
		end_move[4] = new MessageEvent(120000, questers, "They noticed you! Kill them quickly!");
		
		Event[] end_spawn = new Event[1];
		end_spawn[0] = new EntitySpawnerCompleteEvent(10, (EntitySpawnerEvent)events[0]);
		
		tasks.add(new QuestTask(events));
		tasks.add(new QuestTask(end_move));
		tasks.add(new QuestTask(end_spawn));
		end_spawn = new Event[1];
		end_spawn[0] = new EntitySpawnerCompleteEvent(10, (EntitySpawnerEvent)events[1]);
		tasks.add(new QuestTask(end_spawn));
		end_spawn = new Event[1];
		end_spawn[0] = new EntitySpawnerCompleteEvent(10, (EntitySpawnerEvent)events[2]);
		tasks.add(new QuestTask(end_spawn));
		end_spawn = new Event[3];
		end_spawn[0] = new EntitySpawnerCompleteEvent(10, (EntitySpawnerEvent)events[3]);
		end_spawn[1] = new MessageEvent(1000, questers, "Good job! You got them all!");
		end_spawn[2] = new ExperienceAdd(1000, questers, 1000, 1000);
		tasks.add(new QuestTask(end_spawn));
		
		MineQuest.getEventParser().addEvent(new QuestEvent(this, 0));
	}
	
	public Event[] getNextEvents() {
		tasks.remove(0);
		if (tasks.size() == 1) {
			for (Quester quester : questers) {
				quester.clearQuest();
			}
		}
		if (tasks.get(0).getEvents() != null) {
			MineQuest.log("Getting the Next " + tasks.get(0).getEvents().length + " Events");
		} else {
			MineQuest.log("Getting Next Null Event Set");
		}
		return tasks.get(0).getEvents();
	}
}
