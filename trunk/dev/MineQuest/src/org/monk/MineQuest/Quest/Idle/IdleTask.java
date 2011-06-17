package org.monk.MineQuest.Quest.Idle;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.IdleTaskEvent;
import org.monk.MineQuest.Event.Idle.IdleEvent;
import org.monk.MineQuest.Quest.Party;
import org.monk.MineQuest.Quest.PartyShell;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quester.Quester;

public abstract class IdleTask {
	protected QuestProspect quest;
	protected int id;
	protected Quester quester;
	protected boolean flag;
	private int event_id;

	public IdleTask(QuestProspect quest, int event_id, int id, Quester quester) {
		this.quest = quest;
		this.event_id = event_id;
		this.id = id;
		this.quester = quester;
		flag = false;
	}
	
	public void checkTask() {
		if (isComplete()) {
			continueQuest();
			quester.remIdle(this);
		}
	}
	
	public int getEventId() {
		return event_id;
	}
	
	public int getId() {
		return id;
	}
	
	public abstract boolean isComplete();
	public abstract String getTarget();
	public abstract void setTarget(String target);
	
	public void continueQuest() {
		if (quester.inQuest()) {
			if (!flag) {
				quester.sendMessage("Idle Task Completed! Will continue when current quest completes.");
			}
			MineQuest.getEventQueue().addEvent(new IdleTaskEvent(10000, this));
			return;
		}
		if (quester.getParty() == null) {
			quester.createParty();
		}
		
		MineQuest.addQuest(new Quest(quest.getFile(), quester.getParty(), id));
	}

	public QuestProspect getQuest() {
		return quest;
	}

	public abstract int getTypeId();
	public abstract void printStatus();

	public abstract IdleType getType();

	public static IdleTask newIdle(Quester quester, String file,
			int event, String target) {
		QuestProspect prospect = new QuestProspect(file);
		String event_line = prospect.getEventLine(event);
		Party party = new PartyShell(quester);
		
		try {
			IdleEvent idle = IdleEvent.newIdleEvent(new Quest(file, party , -1), event_line.split(":"));
			IdleTask idle_task = idle.createEvent();
			
			idle_task.setTarget(target);
			
			return idle_task;
		} catch (Exception e) {
		}
		
		return null;
	}

}
