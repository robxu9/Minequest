package org.monk.MineQuest.Event.Target;

import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityEvent extends TargetedEvent {

	private String abil_name;
	private String type;
	private int level;

	public AbilityEvent(long delay, Target target, String abil_name, 
			String type, int level) {
		super(delay, target);
		this.abil_name = abil_name;
		this.type = type;
		this.level = level;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		SkillClass skill = SkillClass.newClass(null, type);
		skill.setLevel(level);
		Ability ability = Ability.newAbility(abil_name, skill);
		
		for (Quester quester : target.getTargets()) {
			ability.castAbility(null, quester.getPlayer().getLocation(), 
					quester.getPlayer());
		}
	}

	@Override
	public String getName() {
		return "Targeted Ability Event";
	}

}
