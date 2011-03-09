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

public class AbilityHealAura extends Ability {

	public AbilityHealAura(SkillClass myclass) {
		super(myclass);
	}
	
	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(297, 1));
		list.add(new ItemStack(297, 1));
		
		return list;
	}
	
	@Override
	public int getReqLevel() {
		return 8;
	}
	
	@Override
	public String getName() {
		return "Heal Aura";
	}
	
	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		super.castAbility(quester, location, entity);
		
		MineQuest.getEventParser().addEvent(new AuraEvent(this, quester, 10000, 150000, myclass.getCasterLevel() / 4, true));
	}
}
