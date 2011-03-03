package org.monk.MineQuest.Event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.monk.MineQuest.MineQuest;

public class BlockCDEvent extends BlockEvent {
	private long second_delay;
	private boolean first;

	public BlockCDEvent(long delay, long second_delay, Block block, Material newType) {
		super(delay, block, newType);
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
			Event event = new BlockEvent(second_delay, block, Material.AIR);
			
			event.activate(eventParser);
			eventParser.setComplete(true);
		}
	}
	
	@Override
	public String getName() {
		return "Block Create-Destroy Event";
	}

}
