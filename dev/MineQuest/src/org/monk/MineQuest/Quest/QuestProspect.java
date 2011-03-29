package org.monk.MineQuest.Quest;

public class QuestProspect {
	private String name;
	private String file;

	public QuestProspect(String name, String file) {
		this.name = name;
		this.file = file;
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
}
