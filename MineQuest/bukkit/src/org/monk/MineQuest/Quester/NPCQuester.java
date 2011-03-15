package org.monk.MineQuest.Quester;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.World;
import org.monk.MineQuest.MineQuest;

import redecouverte.npcspawner.BasicHumanNpc;
import redecouverte.npcspawner.NpcSpawner;

public class NPCQuester extends Quester {
	public BasicHumanNpc entity;
	
	public NPCQuester(String name) {
		super(name);
		this.entity = null;
	}
	
	public NPCQuester(String name, NPCMode mode, World world, int x, int y, int z) {
		this.name = name;
		create(mode, world, x, y, z);
		update();
		distance = 0;
		entity = null;
	}
	
	public void create(NPCMode mode, World world, int x, int y, int z) {
		super.create();

		MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
				x + "', y='" + 
				y + "', z='" + 
				z + "', mode='" + 
				mode + "', world='" + 
				world + "' WHERE name='"
				+ name + "'");
	}
	
	@Override
	public void save() {
		super.save();
		
		if (entity == null) return;
		MineQuest.getSQLServer().update("UPDATE questers SET x='" + 
				entity.getBukkitEntity().getLocation().getX() + "', y='" + 
				entity.getBukkitEntity().getLocation().getY() + "', z='" + 
				entity.getBukkitEntity().getLocation().getZ() + "', world='" + 
				entity.getBukkitEntity().getWorld().getName() + "' WHERE name='"
				+ name + "'");
	}

	public void update() {
		super.update();

		ResultSet results = MineQuest.getSQLServer().query("SELECT * FROM questers WHERE name='" + name + "'");

		try {
			if (!results.next()) return;
			int x = results.getInt("x");
			int y = results.getInt("y");
			int z = results.getInt("z");
			World world = MineQuest.getSServer().getWorld(results.getString("world"));
			entity = NpcSpawner.SpawnBasicHumanNpc(name, name, world, x, y, z, 0, 0);
		} catch (SQLException e) {
			MineQuest.log("Unable to add NPCQuester");
			e.printStackTrace();
		}
	}
}