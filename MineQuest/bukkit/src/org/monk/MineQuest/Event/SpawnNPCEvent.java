package org.monk.MineQuest.Event;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.NPCQuester;

import redecouverte.npcspawner.NpcSpawner;

public class SpawnNPCEvent extends NormalEvent {

	private NPCQuester npcQuester;
	private String world;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;

	public SpawnNPCEvent(long delay, NPCQuester npcQuester,
			String world, double x, double y, double z, float pitch, float yaw) {
		super(delay);
		this.npcQuester = npcQuester;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		if (MineQuest.getSServer().getWorld(world) == null) {
			eventParser.setComplete(false);
		} else {
			npcQuester.setEntity(NpcSpawner.SpawnBasicHumanNpc(npcQuester.getName(), 
					npcQuester.getName(), MineQuest.getSServer().getWorld(world), 
					x, y, z, yaw, pitch));
		}
	}

}
