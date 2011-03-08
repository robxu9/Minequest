package org.monk.MineQuest.Ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityBinder extends Ability {

	private String ability;
	private int bind_to;

	public AbilityBinder(SkillClass myclass, String ability, int bind) {
		super("Should not Matter", myclass);
		this.ability = ability;
		this.bind_to = bind;
	}
	
	@Override
	public void bind(Quester quester, ItemStack item) {
		if (bind != item.getTypeId()) {
			bind = item.getTypeId();
			MineQuest.getSQLServer().update("INSERT INTO " + quester.getName() + " (abil, bind, bind_2) VALUES('" + getName() + "', '" + bind + "', '" + bind_to + "')");
			quester.sendMessage(getName() + " is now bound to " + item.getTypeId());
		}
	}
	
	@Override
	public String getName() {
		return "Binder:" + ability;
	}
	
	@Override
	public void unBind(Quester quester) {
		super.unBind(quester);
		myclass.remAbility(this);
		quester.sendMessage(getName() + " removed");
	}
	
	@Override
	public void useAbility(Quester quester, Location location,
			LivingEntity entity) {
		quester.bind(ability, new ItemStack(bind_to, 1));
	}
	
	@Override
	public void silentBind(Quester quester, ItemStack itemStack) {
		bind = itemStack.getTypeId();
	}
}
