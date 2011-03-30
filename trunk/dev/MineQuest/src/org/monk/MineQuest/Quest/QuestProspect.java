package org.monk.MineQuest.Quest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.monk.MineQuest.MineQuest;

public class QuestProspect {
	private String name;
	private String file;
	private boolean repeatable;

	public QuestProspect(String name, String file, boolean repeatable) {
		this.name = name;
		this.file = file;
		this.repeatable = repeatable;
	}
	
	public QuestProspect(String filename) {
		BufferedReader bis;
		this.name = filename;
		this.file = filename;
		this.repeatable = false;
		try {
			bis = new BufferedReader(new FileReader(filename + ".quest"));
			String line = "";
			int number = 0;
			try {
				while ((line = bis.readLine()) != null) {
					number++;
					String split[] = line.split(":");
					parseLine(split);
				}
			} catch (Exception e) {
				MineQuest.log("Unable to load Quest Problem on Line " + number);
				MineQuest.log("  " + line);
				return;
			}
		} catch (FileNotFoundException e1) {
			MineQuest.log("Unable to load quest file " + filename + ".quest");
			this.name = "";
			this.file = filename;
			this.repeatable = false;
		}
	}
	
	private void parseLine(String[] split) {
		if (split[0].equals("Name")) {
			name = split[1];
		} else if (split[0].equals("Repeatable")) {
			repeatable = Boolean.parseBoolean(split[1]);
		}
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFile() {
		return file;
	}

	public boolean isRepeatable() {
		return repeatable;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof QuestProspect) {
			return ((QuestProspect)obj).getFile().equals(file);
		}
		return super.equals(obj);
	}
}
