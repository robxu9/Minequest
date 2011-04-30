package org.monk.MineQuest.Ability;

import java.util.HashMap;
import java.util.Map;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.PropertiesFile;

public class AbilityConfigManager {
	private Map<String, Integer> casting_times;
	private Map<String, Integer> required_levels;
	private Map<String, Integer> experience;
	private PropertiesFile cast;
	private PropertiesFile required;
	private PropertiesFile exper;
	
	public AbilityConfigManager() {
		casting_times = new HashMap<String, Integer>();
		required_levels = new HashMap<String, Integer>();
		experience = new HashMap<String, Integer>();
		
		for (Ability ability : Ability.newAbilities(null)) {
			casting_times.put(ability.getName(), ability.getCastTime());
			required_levels.put(ability.getName(), ability.getReqLevel());
			experience.put(ability.getName(), ability.getExp());
		}

		cast = new PropertiesFile("MineQuest/casting_times.properties");
		required = new PropertiesFile("MineQuest/required_levels.properties");
		exper = new PropertiesFile("MineQuest/experience_given.properties");
		
		for (String abil : casting_times.keySet()) {
			casting_times.put(abil, cast.getInt(abil, casting_times.get(abil)));
			required_levels.put(abil, required.getInt(abil, required_levels.get(abil)));
			experience.put(abil, exper.getInt(abil, experience.get(abil)));
		}
	}

	public int getCastingTime(String ability) {
		return casting_times.get(ability);
	}

	public int getRequiredLevel(String ability) {
		return required_levels.get(ability);
	}

	public int getExperience(String ability) {
		return experience.get(ability);
	}
}
