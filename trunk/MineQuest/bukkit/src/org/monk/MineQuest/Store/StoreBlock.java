/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.monk.MineQuest.Store;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class StoreBlock {
	private double price;
	private double new_price;
	private String type;
	private int quantity;
	private Store my_store;
	private int id;
	
	public StoreBlock(Store store, String stype, int squantity, double d, int sid) {
		type = stype;
		price = d;
		quantity = squantity;
		id = sid;
		my_store = store;
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
		
		cubes = blocksToCubes(block_quantity, b);
		
    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}
		
		quester.getPlayer().sendMessage("You could " + buy + block_quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	public void buy(Quester quester, int block_quantity) {
		int cubes;
		int multis;
		int lefts;
		Player player = quester.getPlayer();
		
		if (quantity < block_quantity) {
			player.sendMessage("There are only " + quantity + " " + type + " blocks available in the store");
			return;
		}
		if (block_quantity < 0) {
			player.sendMessage("You entered an invalid quantity");
			return;
		}
		
		cubes = blocksToCubes(block_quantity, true);
		
		if (cubes > quester.getCubes()) {
			player.sendMessage("Insufficient Funds");
			return;
		}
		
		price = new_price;
		
		multis = block_quantity / 64;
		lefts = block_quantity % 64;
		try {
			while (multis-- > 0) {
				player.getInventory().addItem(new ItemStack(id, 64));
			}
			if (lefts != 0) {
				//System.out.println("Giviing " + lefts + " of " + id);
				player.getInventory().addItem(new ItemStack(id, lefts));
			}
		} catch (Exception e) {
			System.out.println("Strange problem " + e);
		}
		
		quantity -= block_quantity;
		
		quester.setCubes(quester.getCubes() - cubes);
		update();
		

    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}

		player.sendMessage("You bought " + block_quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	public void sell(Quester quester, int block_quantity) {
		int cubes;
		Player player = quester.getPlayer();
		
		cubes = blocksToCubes(block_quantity, false);
		
		if (!playerRemove(player, block_quantity)) {
			player.sendMessage("Insufficient Materials");
			return;
		}
		
		price = new_price;
		
		quantity += block_quantity;

		quester.setCubes(quester.getCubes() + cubes);
		update();
		
    	String cubes_string;
		if (cubes > 1000000) {
    		cubes_string = (((int)((double)cubes) / 1000.0)/1000) + "MC";
    	} else if (cubes > 1000) {
    		cubes_string = ((double)cubes / 1000.0) + "KC";
    	} else {
    		cubes_string = cubes + "C";
    	}
		
		player.sendMessage("You sold " + block_quantity + " " + type + " for " + cubes_string);
		
		return;
	}
	
	private boolean playerRemove(Player player, int quantity) {
		// TODO: test this function - bukkit has inventory now
		/* old way!!*/
		PlayerInventory inventory = player.getInventory();
		int mod = 1;
		int multis = quantity / mod;
		int lefts = 0;//quantity % mod;

		
		while (multis-- > 0) {
			if (inventory.contains(id)) {
				inventory.removeItem(new ItemStack(id, mod));
			} else {
				multis++;
				while (multis++ < (quantity / mod)) {
					inventory.addItem(new ItemStack(id, mod));
				}
				return false;
			}
		}
		if (lefts > 0) {
			if (inventory.contains(id)) {
				inventory.removeItem(new ItemStack(id, lefts));
			} else {
				for (multis = 64; multis < quantity; multis += mod) {
					inventory.addItem(new ItemStack(id, mod));
				}
				return false;
			}
		}
		
		return true;
	}
	
	private void update() {
		MineQuest.getSQLServer().update("UPDATE " + my_store.getName() + " SET price='" + price + "', quantity='" + quantity + "' WHERE type='" + type + "'");
	}
	
	private int blocksToCubes(int blocks, boolean buy) {
        int change = blocks;
        double cost = 0;
        new_price = price;
        
        while (change-- > 0) {
            if (buy) {
                cost += (new_price);
                new_price *= 1.0005;
            } else {
                cost += (new_price);
                new_price /= 1.0005;
            }
        }

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
                	new_price *= 1.0005;
                } else {
                	new_price /= 1.0005;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StoreBlock) {
			return id == ((StoreBlock)obj).getId();
		} else if (obj instanceof String) {
			return type.equals(obj);
		}
		return super.equals(obj);
	}
}
