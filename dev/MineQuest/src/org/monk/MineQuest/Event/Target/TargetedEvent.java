package org.monk.MineQuest.Event.Target;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.Event;
import org.monk.MineQuest.Event.NormalEvent;
import org.monk.MineQuest.Quest.Quest;
import org.monk.MineQuest.Quest.Target;

public abstract class TargetedEvent extends NormalEvent {
	protected Target target;

	public TargetedEvent(long delay, Target target) {
		super(delay);
		this.target = target;
	}
	
	public static Event newTargeted(String[] split, Quest quest) throws Exception {
		Event targetEvent = null;
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
		} else if (split[3].equals("LightningEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			
			targetEvent = new LightningEvent(delay, target);
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
		} else if (split[3].equals("FireballEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Vector location = new Vector(
					Double.parseDouble(split[6]),
					Double.parseDouble(split[7]),
					Double.parseDouble(split[8]));

			targetEvent = new FireballEvent(delay, target, location);
		} else if (split[3].equals("PoisonEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			int amount = Integer.parseInt(split[6]);

			targetEvent = new PoisonEvent(delay, target, amount);
		} else if (split[3].equals("AbilityEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			int level = Integer.parseInt(split[8]);

			targetEvent = new AbilityEvent(delay, target, split[6], split[7], level);
		} else if (split[3].equals("NPCSetTargetEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Location location = new Location(quest.getWorld(),
					Double.parseDouble(split[6]),
					Double.parseDouble(split[7]),
					Double.parseDouble(split[8]));

			targetEvent = new NPCSetTargetEvent(delay, target, location);
		} else if (split[3].equals("NPCPropertyEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			
			targetEvent = new NPCPropertyEvent(delay, target, split[6], split[7]);
		} else if (split[3].equals("StartQuestEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			
			targetEvent = new StartQuestEvent(delay, target, split[6]);
		} else if (split[3].equals("MessageEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			
			targetEvent = new MessageEvent(delay, target, split[6]);
		} else if (split[3].equals("NPCFollowEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Target other = quest.getTarget(Integer.parseInt(split[6]));
			
			targetEvent = new NPCFollowEvent(delay, target, other);
		} else if (split[3].equals("NPCSetAttackTargetEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Target other = quest.getTarget(Integer.parseInt(split[6]));
			
			targetEvent = new NPCSetAttackTargetEvent(delay, target, other);
		} else if (split[3].equals("LineOfSightEvent")) {
			long delay = Long.parseLong(split[4]);
			Target target = quest.getTarget(Integer.parseInt(split[5]));
			Target target2 = quest.getTarget(Integer.parseInt(split[6]));
			int index = Integer.parseInt(split[7]);
			
			targetEvent = new LineOfSightEvent(quest, delay, index, target, target2);
		} else {
			MineQuest.log("Error: Unknown Targeted Event: " + split[3]);
			throw new Exception();
		}
		
		targetEvent.setId(Integer.parseInt(split[1]));
		
		return targetEvent;
	}
}
