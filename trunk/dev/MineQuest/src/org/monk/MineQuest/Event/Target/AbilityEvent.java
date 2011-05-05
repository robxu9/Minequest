package org.monk.MineQuest.Event.Target;

import org.monk.MineQuest.Ability.Ability;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityEvent extends TargetedEvent {

	private String abil_name;
	private String type;

	public AbilityEvent(long delay, Target target, String abil_name, 
			String type) {
		super(delay, target);
		this.abil_name = abil_name;
		this.type = type;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		SkillClass skill = SkillClass.newClass(null, type);
		Ability ability = Ability.newAbility(abil_name, skill);
		
		for (Quester quester : target.getTargets()) {
			ability.castAbility(null, quester.getPlayer().getLocation(), 
					quester.getPlayer());
		}
	}

}
