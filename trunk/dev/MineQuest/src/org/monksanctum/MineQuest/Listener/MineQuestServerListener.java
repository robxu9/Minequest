/*
 * MineQuest - Bukkit Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2011  Jason Monk
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.monksanctum.MineQuest.Listener;


import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.monksanctum.MineQuest.MineQuest;

public class MineQuestServerListener extends ServerListener {

    @Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (MineQuest.getIsConomyOn()) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                MineQuest.setIConomy(null);
                MineQuest.log("un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (!MineQuest.getIsConomyOn()) {
            Plugin iConomy = MineQuest.getSServer().getPluginManager().getPlugin("iConomy");

            if (iConomy != null) {
                if (iConomy.isEnabled()) {
                	MineQuest.setIConomy(iConomy);
                    MineQuest.log("hooked into iConomy.");
                }
            }
        }
    }

} 
