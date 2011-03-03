package org.monk.MineQuest.Event;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockDCEvent extends BlockEvent {
	private long second_delay;
	private Material otherType;

	public BlockDCEvent(long delay, long second_delay, Block block, Material newType) {
		super(delay, block, newType);
		this.newType = Material.AIR;
		this.otherType = newType;
		this.second_delay = second_delay;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		eventParser.addEvent(new BlockEvent(second_delay, block, otherType));
	}
	
	@Override
	public String getName() {
		return "Block Create-Destroy Event";
	}

}
