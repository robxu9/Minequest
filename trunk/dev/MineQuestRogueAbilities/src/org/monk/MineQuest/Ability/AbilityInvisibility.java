package org.monk.MineQuest.Ability;

// Many parts of this ability were taken and adapted from code that was found on
// Spyer github repository.


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.Event.AbilityEvent;
import org.monk.MineQuest.Quester.Quester;

public class AbilityInvisibility extends Ability {
	private int total_time;
	private Quester caster;
	public List<String> names;
	private boolean active;
	
	public AbilityInvisibility() {
		super();
		config = new int[] {30000, 0};
		active = false;
	}

	@Override
	public void castAbility(Quester quester, Location location,
			LivingEntity entity) {
		if (quester == null) return;
		total_time = config[0] + config[1] * myclass.getCasterLevel();
		caster = quester;
		names = new ArrayList<String>();
		active = true;
		MineQuest.getEventParser().addEvent(new AbilityEvent(300, this));
	}
	
	@Override
	protected boolean canCast() {
		if (active) {
			myclass.getQuester().sendMessage("You are already have temporary invisibility active");
			return false;
		}
		return super.canCast();
	}
	
	@Override
	public void eventActivate() {
		super.eventActivate();
		
		checkInvisible();
		
		total_time -= 300;
		if (total_time > 0) {
			MineQuest.getEventParser().addEvent(new AbilityEvent(300, this));
		} else {
			complete();
			active = false;
		}
	}

	public boolean outsideSight(Location first, Location second) {
		World first_world = first.getWorld();
		World second_world = second.getWorld();
		if (!first_world.getName().equals(second_world.getName())) {
			return false;
		}
		Chunk source = second_world.getChunkAt(second.getBlock());
		Chunk dest = first_world.getChunkAt(first.getBlock());
		int maxX = source.getX() + 16;
		int minX = source.getX() - 16;
		int maxZ = source.getZ() + 16;
		int minZ = source.getZ() - 16;
		if (dest.getX() <= maxX) return false;
		if (dest.getX() >= minX) return false;
		if (dest.getZ() <= maxZ) return false;
		if (dest.getZ() >= minZ) return false;
	
		return true;
	}

	public void invisible(Player source, Player player) {
		if (outsideSight(source.getLocation(), player.getLocation())) {
			return;
		}
		CraftPlayer hide = (CraftPlayer) source;
		CraftPlayer hideFrom = (CraftPlayer) player;
		if (!this.names.contains(player.getName())) {
			if (source != player) {
				hideFrom.getHandle().netServerHandler
						.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
				names.add(player.getName());
			}
		}
	}

	private void uninvisible(Player source, Player player) {
		CraftPlayer unHide = (CraftPlayer) source;
		CraftPlayer unHideFrom = (CraftPlayer) player;
		if (source != player) {
			if (names.contains(player.getName())) {
				unHideFrom.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHide.getHandle()));
				names.remove(player.getName());
			}
		}
	}

	private void checkInvisible() {
		
	}

	private void complete() {
		
	}

	@Override
	public List<ItemStack> getManaCost() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		list.add(new ItemStack(Material.SAPLING, 1));
		list.add(new ItemStack(Material.SAPLING, 1));
		list.add(new ItemStack(Material.LEATHER, 1));
		list.add(new ItemStack(Material.LEATHER, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		list.add(new ItemStack(Material.WOOL, 1));
		
		return list;
	}

	@Override
	public String getName() {
		return "Temporary Invisibility";
	}

	@Override
	public int getReqLevel() {
		return 20;
	}

	@Override
	public String getSkillClass() {
		return "Rogue";
	}

}
