package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.AuraEvent;
import org.monk.MineQuest.Quester.Quester;
import org.monk.MineQuest.Quester.SkillClass.SkillClass;

public class AbilityDamageAura extends Ability {

	public AbilityDamageAura(String name, SkillClass myclass) {
		super(name, myclass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(259, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 7;
	}
	
	@Override
	public String getName() {
		return "Damage Aura";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		super.castAbility(quester, location, entity);
		
		MineQuest.getEventParser().addEvent(new AuraEvent(this, quester, 10000, 300000, -1, false));
	}
}
