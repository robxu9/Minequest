package org.monk.MineQuest.Event.Target;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.EventParser;
import org.monk.MineQuest.Event.Absolute.QuestEvent;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.Target;
import org.monk.MineQuest.Quester.Quester;

public class LineOfSightEvent extends QuestEvent {
	protected Target target;
	private Target target_2;
	
	public LineOfSightEvent(Quest quest, long delay, int index, Target target, Target target_2) {
		super(quest, delay, index);
		this.target = target;
		this.target_2 = target_2;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		eventParser.setComplete(true);
		
		boolean flag = true;
		List<Quester> first = target.getTargets();
		List<Quester> second = target_2.getTargets();
		if (first.size() == 0) return;
		if (first.get(0).getPlayer() == null) return;
		Location source = first.get(0).getPlayer().getLocation();
		
		for (Quester q : second) {
			if (q.getPlayer() == null) continue;
			Location target = q.getPlayer().getLocation();
			
			if (!lineOfSight(source, target)) {
				flag = false;
				break;
			}
		}
		
		if (flag) {
			super.activate(eventParser);
		}
	}

	private boolean lineOfSight(Location source, Location target) {
		Location current = new Location(source.getWorld(), source.getX(), source.getY(), source.getZ());
		double distance = MineQuest.distance(source, target);
		Location change = new Location(source.getWorld(),
				(target.getX() - source.getX()) / distance * .5,
				(target.getY() - source.getY()) / distance * .5,
				(target.getZ() - source.getZ()) / distance * .5);
		int total = (int)((distance / .5) + .5);
		int i;
		for (i = 0; i < total; i++) {
			if ((!MineQuest.isOpen(current.getBlock().getType())) && (current.getBlock().getType() != Material.GLASS)) {
				return false;
			}
			current.setX(current.getX() + change.getX());
			current.setY(current.getY() + change.getY());
			current.setZ(current.getZ() + change.getZ());
		}
		
		return true;
	}

}
