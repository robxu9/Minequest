

import org.bukkit.entity.Player;

public class StoreBlock {
	private int price;
	private int new_price;
	private String type;
	private int quantity;
	private Store my_store;
	private int id;
	private mysql_interface sql_server;
	
	public StoreBlock(mysql_interface mysql, Store store, String stype, int squantity, int sprice, int sid) {
		type = stype;
		price = sprice;
		quantity = squantity;
		id = sid;
		my_store = store;
		sql_server = mysql;
	}
	
	public int getId() {
		return id;
	}
	
	public void cost(Quester quester, int block_quantity, boolean b) {
		int cubes;
		String buy;
		if (b) {
			buy = "buy ";
		} else {
			buy = "sell ";
		}
		
		if (b && (quantity < block_quantity)) {
			quantity = block_quantity;
		}
		if (quantity < 0) {
			quantity = 0;
		}
		
		cubes = blocksToCubes(quantity, b);
		
    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}
		
		quester.getPlayer().sendMessage("You could " + buy + quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	public void buy(Quester quester, int block_quantity) {
		int cubes;
		int multis;
		int lefts;
		Player player = quester.getPlayer();
		
		if (quantity > block_quantity) {
			player.sendMessage("There are only " + block_quantity + " " + type + " blocks available in the store");
			return;
		}
		if (quantity < 0) {
			player.sendMessage("You entered an invalid quantity");
			return;
		}
		
		cubes = blocksToCubes(quantity, true);
		
		if (cubes > quester.getCubes()) {
			player.sendMessage("Insufficient Funds");
			return;
		}
		
		price = new_price;
		
		multis = quantity / 64;
		lefts = quantity % 64;
		try {
			while (multis-- > 0) {
				//TODO Item implementation
				//player.giveItem(new Item(id, 64));
			}
			if (lefts != 0) {
				System.out.println("Giviing " + lefts + " of " + id);
				//TODO Item implementation
				//player.giveItem(new Item(id, lefts));
			}
		} catch (Exception e) {
			System.out.println("Strange problem " + e);
		}
		
		block_quantity -= quantity;
		
		quester.setCubes(quester.getCubes() - cubes);
		quester.update();
		update();
		

    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}

		player.sendMessage("You bought " + quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	public void sell(Quester quester, int block_quantity) {
		int cubes;
		Player player = quester.getPlayer();
		
		cubes = blocksToCubes(quantity, false);
		
		if (!playerRemove(player, quantity)) {
			player.sendMessage("Insufficient Materials");
			return;
		}
		
		price = new_price;
		
		block_quantity += quantity;

		quester.setCubes(quester.getCubes() + cubes);
		quester.update();
		update();
		
    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}
		
		player.sendMessage("You sold " + quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	private boolean playerRemove(Player player, int quantity) {
		// TODO: write this function - bukkit needs inventory first
		/* old way!!
		Inventory inventory = player.getInventory();
		int multis = quantity / 64;
		int lefts = quantity % 64;

		
		while (multis-- > 0) {
			if (inventory.hasItem(id, 64, 10000)) {
				inventory.removeItem(new Item(id, 64));
			} else {
				multis++;
				while (multis++ < (quantity / 64)) {
					player.giveItem(new Item(id, 64));
				}
				return false;
			}
		}
		if (lefts > 0) {
			if (inventory.hasItem(id, lefts, 10000)) {
				inventory.removeItem(new Item(id, lefts));
			} else {
				for (multis = 64; multis < quantity; multis += 64) {
					player.giveItem(new Item(id, 64));
				}
				return false;
			}
		}*/
		
		return true;
	}
	
	private void update() {
		sql_server.update("UPDATE " + my_store + " SET price='" + price + "', quantity='" + quantity + "' WHERE type='" + type + "'");
	}
	
	private int blocksToCubes(int blocks, boolean buy) {
        int change = blocks / 64;
        double cost = 0;
        new_price = price;
        
        while (change-- > 0) {
            if (buy) {
                cost += (new_price * 64);
                new_price *= 1.005;
            } else {
                cost += (new_price * 64);
                new_price /= 1.005;
            }
        }
        change = (blocks % 64);
        cost += (new_price * change);

        if (!buy) {
             cost *= .92;
        }
		
		return ((int)cost);
	}
	
	@SuppressWarnings("unused")
	private int cubesToBlocks(int cubes, boolean buy) {
        int blocks = 0;
        if (!buy) {
        	cubes /= .92;
        }
        new_price = price;
        while (cubes > 0) {
            cubes -= new_price;
            blocks++;
            if ((blocks % 64) == 0) {
                if (buy) {
                	new_price *= 1.005;
                } else {
                	new_price /= 1.005;
                }
            }
        }
        if (cubes < 0) {
            blocks--;
        }
        
        return blocks;
	}

	public void display(Player player, int i) {
		int my_price = (int)price;
		
		player.sendMessage("    " + i + ": " + type + " - " + my_price + " - " + quantity);
	}
	
	public void display(Quester quester, int i) {
		display(quester.getPlayer(), i);
	}

}
