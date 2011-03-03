package org.monk.MineQuest.Event;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockEvent extends NormalEvent {
	protected Material newType;
	protected Block block;
	
	public BlockEvent(long delay, World world, int x, int y,
			int z, Material newType) {
		this(delay, world.getBlockAt(x, y, z), newType);
	}

	public BlockEvent(long delay, Block block, Material newType) {
		super(delay);
		this.block = block;
		this.newType = newType;
	}
	
	@Override
	public void activate(EventParser eventParser) {
		block.setType(newType);
	}

	@Override
	public String getName() {
		return "Generic Block Type Event";
	}

}
