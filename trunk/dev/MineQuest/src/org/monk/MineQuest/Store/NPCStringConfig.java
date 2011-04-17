package org.monk.MineQuest.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.monk.MineQuest.PropertiesFile;
import org.monk.MineQuest.Quester.NPCQuester;
import org.monk.MineQuest.Quester.Quester;

public class NPCStringConfig {
	private List<String> air;
	private List<String> normal;
	private Random generator;
	
	public NPCStringConfig() {
		PropertiesFile npc_strings = new PropertiesFile(
				"MineQuest/npc_strings.properties");
		
		air = new ArrayList<String>();
		normal = new ArrayList<String>();
		int air_number = npc_strings.getInt("air_string_number", 1);
		int normal_number = npc_strings.getInt("normal_string_number", 1);
		
		int i;
		for (i = 0; i < air_number; i++) {
			air.add(npc_strings.getString("air_string_" + (i + 1), "Unset String!"));
		}
		
		for (i = 0; i < normal_number; i++) {
			normal.add(npc_strings.getString("normal_string_" + (i + 1), "Unset String!"));
		}
		
		generator = new Random();
	}

	public void sendRandomMessage(NPCQuester npcQuester, Quester quester,
			Store store) {
		String message = getRandomMessage(quester, store);
		if (message != null) {
			quester.sendMessage("<" + npcQuester.getName() + "> " + message);
		} else {
			
		}
	}

	private String getRandomMessage(Quester quester, Store store) {
		if (quester.getPlayer().getItemInHand().getType() == Material.AIR) {
			return getRandomAirMessage(quester, store);
		} else {
			return getRandomNormMessage(quester, store);
		}
	}

	private String getRandomAirMessage(Quester quester, Store store) {
		if (air.size() == 0) {
			return null;
		}
		
		int index = generator.nextInt(air.size());
		
		String ret = new String(air.get(index));
		
		ret = ret.replaceAll("%b", store.getBest().getType());
		
		return ret;
	}

	private String getRandomNormMessage(Quester quester, Store store) {
		if (normal.size() == 0) {
			return null;
		}
		
		int index = generator.nextInt(normal.size());
		
		String ret = new String(normal.get(index));

		if (store.getBlock(quester.getPlayer().getItemInHand().getTypeId()) != null) {
			ret = ret.replaceAll("%i", quester.getPlayer().getItemInHand()
					.getType() + "");
		} else {
			return null;
		}
		
		return ret;
	}

}
