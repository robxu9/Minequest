package org.monk.MineQuest.Quester;

import java.util.HashMap;
import java.util.Map;

public enum NPCMode {
	STATIONARY, 
	GOD;
	
    private static final Map<String, NPCMode> lookupName = new HashMap<String, NPCMode>();

	public static NPCMode getNPCMode(String name) {
        return lookupName.get(name);
	}

    static {
        for (NPCMode mode : values()) {
            lookupName.put(mode.name(), mode);
        }
    }
}
