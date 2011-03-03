package org.monk.MineQuest.Event;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockCDEvent extends BlockEvent {
	private long second_delay;

	public BlockCDEvent(long delay, long second_delay, Block block, Material newType) {
		super(delay, block, newType);
		this.second_delay = second_delay;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		super.activate(eventParser);
		
		eventParser.addEvent(new BlockEvent(second_delay, block, Material.AIR));
	}
	
	@Override
	public String getName() {
		return "Block Create-Destroy Event";
	}

}
