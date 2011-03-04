package org.monk.MineQuest.Event;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class EntitySpawnerCompleteNMEvent extends PeriodicEvent {
	private Quest quest;
	private boolean first;
	private LivingEntity entities[];
	private int index;
	private EntitySpawnerEvent[] events;

	public EntitySpawnerCompleteNMEvent(Quest quest, long delay, int index, EntitySpawnerEvent[] events) {
		super(delay);
		this.quest = quest;
		this.index = index;
		first = true;
		this.events = events;
		this.entities = new LivingEntity[events.length];
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		if (first) {
			int i = 0;
			for (EntitySpawnerEvent e : events) {
				entities[i++] = e.getEntity();
				e.setComplete(true);
			}
		
			first = false;
		} else {
			boolean flag = true;
			int i;
			for (i = 0; i < entities.length; i++) {
				if ((entities[i] != null) && (entities[i].getHealth() > 0)) {
					flag = false;
				} else if (entities[i] != null) {
					entities[i] = null;
				}
			}
			
			if (flag) {
				eventComplete();
				eventParser.setComplete(true);
			}
		}
	}
	
	public void eventComplete() {
		Event[] events = quest.getNextEvents(index);
		if (events != null) {
			for (Event event : events) {
				MineQuest.getEventParser().addEvent(event);
			}
		}
	}
	
	@Override
	public String getName() {
		return "Entity Spawner No Move Destruction Event";
	}

}
