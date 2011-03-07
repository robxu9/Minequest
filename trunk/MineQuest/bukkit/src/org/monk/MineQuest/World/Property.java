package org.monk.MineQuest.World;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.monk.MineQuest.Quester.Quester;

public class Property {
	private int x, max_x;
	private int z, max_z;
	private int height, y;
	private Quester owner;
	private List<Quester> editors;
	private long price;
	
	public Property(Quester owner, Location start, Location end, boolean height, long price) {
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
		if (height) {
			y = (int)start.getY();
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
		} else {
			y = 0;
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
		return owner;
	}
	
	public void setOwner(Quester quester) {
		owner = quester;
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

	int getY() {
		return y;
	}

	int getMaxZ() {
		return max_z;
	}

	int getZ() {
		return z;
	}

	int getMaxX() {
		return max_x;
	}

	int getX() {
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
}
