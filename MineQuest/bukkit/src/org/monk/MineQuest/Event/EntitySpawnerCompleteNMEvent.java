package org.monk.MineQuest.Event;

import org.bukkit.entity.LivingEntity;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quest.Quest;

public class EntitySpawnerCompleteNMEvent extends EntitySpawnerCompleteEvent {
	private Quest quest;
	private boolean first;
	private LivingEntity entity;

	public EntitySpawnerCompleteNMEvent(Quest quest, long delay, EntitySpawnerEvent event) {
		super(delay, event);
		this.quest = quest;
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
		Event[] events = quest.getNextEvents();
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
