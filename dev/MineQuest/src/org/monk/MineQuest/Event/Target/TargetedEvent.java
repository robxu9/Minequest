package org.monk.MineQuest.Event.Target;

import org.bukkit.Location;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.NormalEvent;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.Target;

public class TargetedEvent extends NormalEvent {
	protected Target target;

	public TargetedEvent(long delay, Target target) {
		super(delay);
		this.target = target;
	}
	
	public static TargetedEvent newTargeted(String[] split, Quest quest) throws Exception {
		TargetedEvent targetEvent = null;
		if (split[3].equals("DamageEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			int amount = Integer.parseInt(split[6]);
			
			targetEvent = new DamageEvent(delay, target, amount);
		} else if (split[3].equals("ExplosionEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			float radius = Float.parseFloat(split[6]);
			int damage = Integer.parseInt(split[7]);
			
			targetEvent = new ExplosionEvent(delay, target, radius, damage);
		} else if (split[3].equals("HealthEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			double percent = Double.parseDouble(split[6]);
			
			targetEvent = new HealthEvent(delay, target, percent);
		} else if (split[3].equals("TeleportEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Location location = new Location(quest.getWorld(),
					Double.parseDouble(split[6]),
					Double.parseDouble(split[7]),
					Double.parseDouble(split[8]));
			
			targetEvent = new EntityTeleportEvent(delay, target, location);
		} else if (split[3].equals("PoisonEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			int amount = Integer.parseInt(split[6]);
			
			targetEvent = new PoisonEvent(delay, target, amount);
		} else if (split[3].equals("NPCSetTargetEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Location location = new Location(quest.getWorld(),
					Double.parseDouble(split[6]),
					Double.parseDouble(split[7]),
					Double.parseDouble(split[8]));
			
			targetEvent = new NPCSetTargetEvent(delay, target, location);
		} else {
			MineQuest.log("Error: Unknown Targeted Event: " + split[3]);
			throw new Exception();
		}
		
		targetEvent.setId(Integer.parseInt(split[1]));
		
		return targetEvent;
	}
}
