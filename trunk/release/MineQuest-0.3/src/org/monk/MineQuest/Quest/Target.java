package org.monk.MineQuest.Quest;

import org.bukkit.Location;
import org.monk.MineQuest.Quester.Quester;

public abstract class Target {
	private int id;
	
	public static Target newTarget(String split[], Quest quest) throws Exception{
		int id = Integer.parseInt(split[1]);
		Target target = null;
		
		if (split[2].equals("AreaTarget")) {
			Location location = new Location(null,
					Double.parseDouble(split[3]),
					Double.parseDouble(split[4]),
					Double.parseDouble(split[5]));
			double radius = Double.parseDouble(split[6]);
			target = new AreaTarget(location, radius);
		} else if (split[2].equals("AreaTargetQuester")) {
			Target t = quest.getTarget(Integer.parseInt(split[3]));
			double radius = Double.parseDouble(split[4]);
			
			target = new AreaTargetQuester(t, radius);
		} else if (split[2].equals("PartyTarget")) {
			target = new PartyTarget(quest.getParty());
		} else if (split[2].equals("Targetter")) {
			String[] event_ids = split[3].split(",");
			TargetEvent[] events = new TargetEvent[event_ids.length];
			int i = 0;
			
			for (String eid : event_ids) {
				events[i++] = (TargetEvent)quest.getEvent(Integer.parseInt(eid));
			}
			
			target = new Targetter(events);
		} else {
			throw new Exception();
		}
		target.setId(id);
		
		return target;
	}

	public abstract Quester[] getTargets();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
