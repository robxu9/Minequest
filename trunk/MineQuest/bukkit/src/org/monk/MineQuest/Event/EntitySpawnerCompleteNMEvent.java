package org.monk.MineQuest.Event;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class EntitySpawnerCompleteNMEvent extends EntitySpawnerCompleteEvent {
	private Quest quest;
	private boolean first;
	private LivingEntity entity;
	private int index;

	public EntitySpawnerCompleteNMEvent(Quest quest, long delay, int index, EntitySpawnerEvent event) {
		super(delay, event);
		this.quest = quest;
		this.index = index;
		first = true;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(false);
		if (first) {
			entity = event.getEntity();
			super.activate(eventParser);
		
			first = false;
		} else {
			if (entity.getHealth() <= 0) {
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
