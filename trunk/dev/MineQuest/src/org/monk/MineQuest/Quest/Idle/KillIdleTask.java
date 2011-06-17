package org.monk.MineQuest.Quest.Idle;

import org.bukkit.entity.CreatureType;
import org.monk.MineQuest.Quest.QuestProspect;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClassConfig;

public class KillIdleTask extends IdleTask {

	private CreatureType[] creatures;
	private int[] counts;
	private int[] targets;

	public KillIdleTask(QuestProspect quest, int event_id, int id, Quester quester, 
			CreatureType[] creatures, int[] counts, int[] targets) {
		super(quest, event_id, id, quester);
		this.creatures = creatures;
		this.counts = counts;
		this.targets = targets;
	}

	@Override
	public boolean isComplete() {
		boolean flag = true;
		boolean print_flag = false;
		int i;
		String last = quester.getKills()[quester.getKills().length - 1].getName();
		
		for (i = 0; i < creatures.length; i++) {
			if (quester.getKills(creatures[i]) < targets[i]) {
				flag = false;
			}
			if (creatures[i].getName().equals(last)) {
				print_flag = true;
			}
		}
		if (print_flag) {
			printStatus();
		}
		
		return flag;
	}

	@Override
	public String getTarget() {
		String ret = targets[0] + "";
		int i;
		
		for (i = 1; i < targets.length; i++) {
			ret = ret + "," + targets[i];
		}
		
		return ret;
	}

	@Override
	public IdleType getType() {
		return IdleType.KILL;
	}

	@Override
	public int getTypeId() {
		return IdleType.KILL.getId();
	}

	@Override
	public void printStatus() {
		int i = 0;;
		quester.sendMessage(quest.getName() + ": waiting for kills");
		for (CreatureType creature : creatures) {
			quester.sendMessage("    "  + creature.getName() + " - " + (quester.getKills(creature) - targets[i] + counts[i]) + "/" + counts[i]);
			i++;
		}
	}

	@Override
	public void setTarget(String target) {
		targets = SkillClassConfig.intList(target);
	}
}
