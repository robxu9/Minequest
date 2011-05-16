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
package org.monk.MineQuest.World;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Quester.Quester;

public class Property {
	private int x, max_x;
	private int z, max_z;
	private int height, y;
	private String owner;
	private List<Quester> editors;
	private long price;
	
	public Property(String owner, Location start, Location end, boolean height, long price) {
		if (start.getX() < end.getX()) {
			x = (int)start.getX();
			max_x = (int)end.getX();
		} else {
			x = (int)end.getX();
			max_x = (int)start.getX();
		}
		if (start.getZ() < end.getZ()) {
			z = (int)start.getZ();
			max_z = (int)end.getZ();
		} else {
			z = (int)end.getZ();
			max_z = (int)start.getZ();
		}
		y = (int)start.getY();
		if (height) {
			this.height = (int)(start.getY() - end.getY());
		}
		
		this.price = price;
		
		this.owner = owner;
		
		editors = new ArrayList<Quester>();
	}
	
	public boolean inProperty(Location loc) {
		if (loc.getX() < x) {
			return false;
		}
		if (loc.getX() > max_x) {
			return false;
		}
		if (loc.getZ() < z) {
			return false;
		}
		if (loc.getZ() > max_z) {
			return false;
		}
		if (height > 0) {
			if (loc.getY() < y) {
				return false;
			}
			if (loc.getY() > (y + height)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean inProperty(Player player) {
		return inProperty(player.getLocation());
	}
	
	public boolean inProperty(Quester quester) {
		return inProperty(quester.getPlayer());
	}
	
	public boolean inProperty(Block block) {
		return inProperty(new Location(null, block.getX(), block.getY(), block.getZ()));
	}
	
	
	public Quester getOwner() {
		return MineQuest.getQuester(owner);
	}
	
	public void setOwner(Quester quester) {
		owner = quester.getName();
	}
	
	public boolean canEdit(Quester quester) {
		int i;
		
		if (quester.equals(owner)) {
			return true;
		}
		
		for (i = 0; i < editors.size(); i++) {
			if (editors.get(i).equals(quester)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addEdit(Quester quester) {
		editors.add(quester);
	}
	
	public void remEdit(Quester quester) {
		editors.remove(quester);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			Property other = (Property)obj;
			if (other.getX() != getX()) {
				return false;
			}
			if (other.getMaxX() != getMaxX()) {
				return false;
			}
			if (other.getZ() != getZ()) {
				return false;
			}
			if (other.getMaxZ() != getMaxZ()) {
				return false;
			}
			if (other.getY() != getY()) {
				return false;
			}
			
			return true;
		}
		return super.equals(obj);
	}

	public int getY() {
		return y;
	}

	public int getMaxZ() {
		return max_z;
	}

	public int getZ() {
		return z;
	}

	public int getMaxX() {
		return max_x;
	}

	public int getX() {
		return x;
	}

	public Location getLocation() {
		return new Location(null, (x + max_x) / 2, y, (z + max_z) / 2);
	}

	public int getCenterX() {
		return (x + max_x) / 2;
	}

	public int getCenterZ() {
		return (z + max_z) / 2;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public Location getEdge(Location location) {
		Location ret = new Location(location.getWorld(), location.getX(),
				location.getY(), location.getZ());
		
		if (Math.abs(ret.getX() - getCenterX()) > Math.abs(ret.getZ() - getCenterZ())) {
			if (ret.getX() > getCenterX()) {
				ret.setX(getMaxX());
			} else {
				ret.setX(getX());
			}
		} else {
			if (ret.getZ() > getCenterZ()) {
				ret.setZ(getMaxZ());
			} else {
				ret.setZ(getZ());
			}
		}
		
		return ret;
	}

	public long getPrice() {
		return price;
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setX(int x) {
		this.x = x;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET x='" + x + " WHERE name='" + town.getName() + "'");
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setMaxX(int x) {
		this.max_x = x;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET max_x='" + max_x + " WHERE name='" + town.getName() + "'");
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setZ(int z) {
		this.z = z;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET z='" + z + " WHERE name='" + town.getName() + "'");
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setMaxZ(int z) {
		this.max_z = z;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET max_z='" + max_z + " WHERE name='" + town.getName() + "'");
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setY(int y) {
		this.y = y;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET y='" + y + " WHERE name='" + town.getName() + "'");
	}
	
	/**
	 * FOR USE WITH TOWN PROPERTY ONLY!!
	 */
	public void setHeight(int h) {
		this.height = h;
		Town town = MineQuest.getTown(new Location(null, x + 1, y, z + 1));
		if (town == null) {
			MineQuest.log("[ERROR] Property outside of town...");
			return;
		}
		
		MineQuest.getSQLServer().update("UPDATE towns SET height='" + h + " WHERE name='" + town.getName() + "'");
	}
}
