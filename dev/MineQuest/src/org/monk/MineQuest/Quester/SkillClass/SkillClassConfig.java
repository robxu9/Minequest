package org.monk.MineQuest.Quester.SkillClass;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.monk.MineQuest.MineQuest;
import org.monk.MineQuest.PropertiesFile;

public class SkillClassConfig {
	protected List<String> names;
	protected List<int[]> types;
	protected List<int[]> levels;
	protected List<int[]> armors;
	protected List<int[]> armor_levels;
	protected List<double[]> armor_defends;
	protected List<int[]> armor_blocks;
	protected PropertiesFile properties;
	private List<Integer> level_health;
	
	public SkillClassConfig() {
		setupProperties();

		names = new ArrayList<String>();
		types = new ArrayList<int[]>();
		levels = new ArrayList<int[]>();
		armors = new ArrayList<int[]>();
		armor_levels = new ArrayList<int[]>();
		armor_defends = new ArrayList<double[]>();
		armor_blocks = new ArrayList<int[]>();
		level_health = new ArrayList<Integer>();

		String[] name_list = properties.getString("names", "").split(",");

		for (String name : name_list) {
			if (name.replaceAll(" ", "").length() > 0) {
				names.add(name.replaceAll(" ", ""));
			}
		}
		
		for (String name : names) {
			parseConfig(name);
		}
	}
	
	protected void parseConfig(String name) {
		types.add(intList(properties.getString(name + "_types", "")));

		levels.add(intList(properties.getString(name + "_levels", "")));

		armors.add(intList(properties.getString(name + "_armor", "")));

		armor_levels.add(intList(properties.getString(name + "_armor_levels", "")));

		armor_defends.add(doubleList(properties.getString(name + "_armor_defend", "")));

		armor_blocks.add(intList(properties.getString(name + "_armor_blocks", "")));
		
		level_health.add(properties.getInt(name + "_level_health"));
	}
	
	public List<String> getClassNames() {
		return names;
	}

	protected void createFile(String string) {
		MineQuest.log("Cannot find " + string + " - downloading template!");
		try {
			MineQuest.downloadFile("http://www.theminequest.com/download/" + string, string);
			MineQuest.log("Download Successful!");
		} catch (Exception e) {
			MineQuest.log("(Error) Download Failed!");
		}
	}
	
	public int[] intList(String list) {
		if (list.length() == 0) return null;
		if (!list.contains(",")) return new int[] {Integer.parseInt(list)};
		String[] strings = list.split(",");
		int[] ints = new int[strings.length];
		int i = 0;
		for (String armor_level_string : strings) {
			ints[i] = Integer.parseInt(armor_level_string);
		}

		return ints;
	}
	
	public double[] doubleList(String list) {
		if (list.length() == 0) return null;
		if (!list.contains(",")) return new double[] {Double.parseDouble(list)};
		String[] strings = list.split(",");
		double[] doubles = new double[strings.length];
		int i = 0;
		for (String armor_level_string : strings) {
			doubles[i] = Double.parseDouble(armor_level_string);
		}

		return doubles;
	}

	protected void setupProperties() {
		properties = new PropertiesFile("MineQuest/generic_classes.properties");
	}
	
	public int[] getTypes(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return types.get(i);
			}
		}
		
		return null;
	}
	
	public int[] getLevels(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return levels.get(i);
			}
		}
		
		return null;
	}
	
	public int[] getArmor(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return armors.get(i);
			}
		}
		
		return null;
	}
	
	public int[] getArmorLevels(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return armor_levels.get(i);
			}
		}
		
		return null;
	}
	
	public double[] getArmorDefends(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return armor_defends.get(i);
			}
		}
		
		return null;
	}
	
	public int[] getArmorBlocks(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				return armor_blocks.get(i);
			}
		}
		
		return null;
	}
	
	public int getLevelHealth(String name) {
		int i;
		
		for (i = 0; i < names.size(); i++) {
			if (names.get(i).equals(name)) {
				if (level_health.get(i) == null) {
					return 0;
				}
				return level_health.get(i);
			}
		}
		
		return 0;
	}
}
