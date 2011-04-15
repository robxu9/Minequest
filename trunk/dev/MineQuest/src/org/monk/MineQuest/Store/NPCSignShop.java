package org.monk.MineQuest.Store;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.UpdateSignEvent;
import org.monk.MineQuest.Quester.NPCMode;
import org.monk.MineQuest.Quester.NPCQuester;
import org.monk.MineQuest.Quester.Quester;

public class NPCSignShop extends Store {
	private Sign next;
	private Sign last;
	private Sign buy_64;
	private Sign buy_1;
	private Sign sell_64;
	private Sign sell_1;
	private Sign display_1;
	private Sign display_2;
	private NPCQuester keeper;
	private int selected;
	private int initialization = 0;

	public NPCSignShop(String storeName, Location start, Location end) {
		super(storeName, start, end); 
		selected = 0;
	}
	
	public NPCSignShop(String name, String town) {
		super(name, town);
		selected = 0;
	}
	
	public void setKeep(NPCQuester keeper) {
		this.keeper = keeper;
		keeper.setMode(NPCMode.STORE);
	}
	
	public boolean parseClick(Quester quester, Block clicked) {
		if (initialization == -1) {
			MineQuest.log("Parse Click");
			if (equals(next.getBlock(), clicked)) {
				next(quester, clicked);
				return true;
			}
			if (equals(last.getBlock(), clicked)) {
				last(quester, clicked);
				return true;
			}
			if (equals(buy_64.getBlock(), clicked)) {
				buy_64(quester, clicked);
				updateDisplay();
				return true;
			}
			if (equals(buy_1.getBlock(), clicked)) {
				buy_1(quester, clicked);
				updateDisplay();
				return true;
			}
			if (equals(sell_64.getBlock(), clicked)) {
				sell_64(quester, clicked);
				updateDisplay();
				return true;
			}
			if (equals(sell_1.getBlock(), clicked)) {
				sell_1(quester, clicked);
				updateDisplay();
				return true;
			}
			if (equals(sell_1.getBlock(), clicked)) {
				sell_1(quester, clicked);
				updateDisplay();
				return true;
			}
		} else {
			if ((clicked.getType() != Material.SIGN) && (clicked.getType() != Material.WALL_SIGN)) {
				return false;
			}
			switch (initialization) {
			case 0:
				next = (Sign)clicked.getState();
				MineQuest.log("last");
				break;
			case 1:
				last = (Sign)clicked.getState();
				MineQuest.log("buy_1");
				break;
			case 2:
				buy_1 = (Sign)clicked.getState();
				MineQuest.log("buy_64");
				break;
			case 3:
				buy_64 = (Sign)clicked.getState();
				MineQuest.log("sell_1");
				break;
			case 4:
				sell_1 = (Sign)clicked.getState();
				MineQuest.log("sell_64");
				break;
			case 5:
				sell_64 = (Sign)clicked.getState();
				MineQuest.log("display_1");
				break;
			case 6:
				display_1 = (Sign)clicked.getState();
				MineQuest.log("display_2");
				break;
			case 7:
				display_2 = (Sign)clicked.getState();
				MineQuest.log("done");
				initialization = -1;
				updateDisplay();
				return true;
			default:
				return false;
			}
			initialization++;
			return true;
		}
		
		return false;
	}
	
	public void updateDisplay() {
		StoreBlock block = blocks.get(selected);
		String lines[] = new String [] {
				"Material Type:",
				block.getType(),
				"Material Id:",
				"" + block.getId()
		};
		
		MineQuest.getEventParser().addEvent(new UpdateSignEvent(100, display_1, lines));
		MineQuest.log(block.getType());
		
		lines = new String [] {
				"Quantity:",
				block.getQuantity() + "",
				"Price:",
				block.getPrice() + ""
		};
		
		MineQuest.getEventParser().addEvent(new UpdateSignEvent(200, display_2, lines));
		
//		if (!display_2.update()) {
//			display_2.update(true);
//		}
		
		
//		display_2.update(true);
	}

	private void sell_1(Quester quester, Block clicked) {
		StoreBlock block = blocks.get(selected);
		
		block.sell(quester, 1);
	}

	private void sell_64(Quester quester, Block clicked) {
		StoreBlock block = blocks.get(selected);
		
		block.sell(quester, 64);
	}

	private void buy_1(Quester quester, Block clicked) {
		StoreBlock block = blocks.get(selected);
		
		block.buy(quester, 1);
	}

	private void buy_64(Quester quester, Block clicked) {
		StoreBlock block = blocks.get(selected);
		
		block.buy(quester, 64);
	}

	private void last(Quester quester, Block clicked) {
		selected--;
		
		if (selected < 0) {
			selected = blocks.size() - 1;
		}
		
		updateDisplay();
	}

	private void next(Quester quester, Block clicked) {
		selected++;
		
		if (selected == blocks.size()) {
			selected = 0;
		}
		
		updateDisplay();
	}

	private boolean equals(Block block, Block clicked) {
		if (block.getX() != clicked.getX()) {
			return false;
		}
		if (block.getY() != clicked.getY()) {
			return false;
		}
		if (block.getZ() != clicked.getZ()) {
			return false;
		}
		return true;
	}

}
