package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.AbilityEvent;
import org.monk.MineQuest.Quester.Quester;

public class AbilityMoveSilently extends Ability implements TargetDefendAbility {
	private boolean activated;
	
	public AbilityMoveSilently() {
		super();
		config = new int[] {30000, 0};
		activated = false;
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		activated = true;
		int delay = config[0] + myclass.getCasterLevel() * config[1];
		MineQuest.getEventParser().addEvent(new AbilityEvent(delay, this));
	}
	
	@Override
	public void eventActivate() {
		activated = false;
		super.eventActivate();
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(Material.FEATHER, 1));
		list.add(new ItemStack(Material.FEATHER, 1));
		list.add(new ItemStack(Material.LEATHER, 1));
		list.add(new ItemStack(Material.LEATHER, 1));
		
		return list;
	}

	@Override
	public String getName() {
		return "Move Silently";
	}

	@Override
	public int getReqLevel() {
		return 10;
	}

	@Override
	public String getSkillClass() {
		return "Rogue";
	}

	@Override
	public void targeted(EntityTargetEvent event) {
		Quester quester = myclass.getQuester();
		if (activated && quester.getPlayer().isSneaking()) {
			event.setCancelled(true);
			if (event.getEntity() instanceof Creature) {
				((Creature)event.getEntity()).setTarget(null);
			}
		}
	}

}
