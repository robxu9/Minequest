package org.monk.MineQuest.Ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.monksanctum.MineQuest.MineQuest;
import org.monksanctum.MineQuest.Ability.Ability;
import org.monksanctum.MineQuest.Ability.TargetDefendAbility;
import org.monksanctum.MineQuest.Event.AbilityEvent;
import org.monksanctum.MineQuest.Quester.Quester;

public class AbilityMoveSilently extends Ability implements TargetDefendAbility {
	private boolean activated;
	private Quester caster;
	
	public AbilityMoveSilently() {
		super();
		config = new int[] {30000, 0};
		activated = false;
	}
	
	@Override
	protected boolean canCast() {
		if (activated) {
			notify(caster, "You are already have " + getName() + " active");
			return false;
		}
		
		return super.canCast();
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		if (quester == null) return;
		activated = true;
		int delay = config[0] + myclass.getCasterLevel() * config[1];
		this.caster = quester;
		MineQuest.getEventQueue().addEvent(new AbilityEvent(delay, this));
	}
	
	@Override
	public void eventActivate() {
		activated = false;
		caster.sendMessage(getName() + " is complete!");
		super.eventActivate();
	}

	@Override
	public List<ItemStack> getSpellComps() {
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

	@Override
	public int getIconLoc() {
		return 44;
	}

}
