package org.monk.MineQuest.Quester.SkillClass;

import org.bukkit.block.BlockDamageLevel;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.Quester.Quester;

public class ResourceClass extends SkillClass {

	public ResourceClass(Quester quester, String type) {
		super(quester, type);
	}
	
	@Override
	public void blockDestroy(BlockDamageEvent event) {
		super.blockDestroy(event);

		// TODO: Deal with this
		if (level > 5) {
			if (isStoneWoodenTool(quester.getPlayer().getItemInHand())) {
				if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
					if (isBlockGiveType(event.getBlock().getTypeId())) {
						quester.getPlayer().getInventory().addItem(getItemGive(event.getBlock().getTypeId()));
					}
				}
			}
		}
		
		if (isClassItem(event.getBlock().getType())) {
			expAdd(2);
		} else {
			expAdd(1);
		}
	}

	/**
	 * Determines if the item is a stone tool or
	 * a wooden tool.
	 * 
	 * @param itemStack Item to check.
	 * @return True if wooden or stone
	 */
	protected boolean isStoneWoodenTool(ItemStack itemStack) {

		switch (itemStack.getTypeId()) {
		case 272:
		case 268:
		case 274:
		case 270:
		case 275:
		case 271:
		case 273:
		case 269:
		case 292:
		case 290:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Get Item stack that should be produced by a given
	 * block type being destroyed.
	 * 
	 * @param type2 Block type destroyed
	 * @return Item stack to go in inventory
	 */
	private ItemStack getItemGive(int type2) {
		switch (type2) {
		case 14: //gold
			return new ItemStack(266, 1);
		case 56: //diamond
			return new ItemStack(264, 1);
		case 73: //red stone
			return new ItemStack(331, 2);
		case 74: //more red stone
			return new ItemStack(331, 2);
		default:
			return null;
		}
	}

}
