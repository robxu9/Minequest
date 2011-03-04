package org.monk.MineQuest.Ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityBinder extends Ability {

	private Ability ability;
	private int bind;
	private int l;

	public AbilityBinder(String name, SkillClass myclass, Ability ability, int bind, int l) {
		super(name, myclass);
		this.ability = ability;
		this.bind = bind;
		this.l = l;
	}

	@Override
	public void useAbility(Quester quester, Location location, int l, LivingEntity entity) {
		if (l > 0) {
			ability.bindl(quester.getPlayer(), new ItemStack(bind, 1));
		} else {
			ability.bindr(quester.getPlayer(), new ItemStack(bind, 1));
		}
	}
}
