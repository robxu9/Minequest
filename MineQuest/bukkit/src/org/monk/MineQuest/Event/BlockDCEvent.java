package org.monk.MineQuest.Event;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockDCEvent extends BlockEvent {
	private long second_delay;
	private Material otherType;
	private boolean first;

	public BlockDCEvent(long delay, long second_delay, Block block, Material newType) {
		super(delay, block, newType);
		this.newType = Material.AIR;
		this.otherType = newType;
		this.second_delay = second_delay;
		first = false;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		if (!first) {
			super.activate(eventParser);
			
			first = true;
			
			eventParser.setComplete(false);
			delay = second_delay;
		} else {
			Event event = new BlockEvent(second_delay, block, otherType);
			
			event.activate(eventParser);
			
			eventParser.setComplete(true);
		}
	}
	
	@Override
	public String getName() {
		return "Block Create-Destroy Event";
	}

}
