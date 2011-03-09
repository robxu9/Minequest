package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityBinder extends Ability {

	private String ability;
	private int bind_to;

	public AbilityBinder(String ability, int bind) {
		this.ability = ability;
		this.bind_to = bind;
	}
	
	public AbilityBinder() {
		// Shell
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

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SkillClass getClassType() {
		return null;
	}

	@Override
	public List<ItemStack> getManaCost() {
		return new ArrayList<ItemStack>();
	}
}
