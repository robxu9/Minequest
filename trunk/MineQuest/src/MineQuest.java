/*
 * MineQuest - Hey0 Plugin for adding RPG characteristics to minecraft
 * Copyright (C) 2010  Jason Monk
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
import java.util.logging.Logger;

public class MineQuest extends Plugin {
    static final MineQuestListener listener = new MineQuestListener();
    private Logger log;
    private String name = "MineQuest";
    private String version = "0.11d";

    public void enable() {
    }
    
    public void disable() {
    }

    public void initialize() {
        log = Logger.getLogger("Minecraft");
        listener.setup();
        log.info(name + " " + version + " initialized");
        etc.getLoader().addListener(
                PluginLoader.Hook.LOGIN,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.COMMAND,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.DISCONNECT,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.DAMAGE,
                listener,
                this,
                PluginListener.Priority.HIGH);
        /*
        etc.getLoader().addListener(
                PluginLoader.Hook.EQUIPMENT_CHANGE,
                listener,
                this,
                PluginListener.Priority.MEDIUM);*/
        etc.getLoader().addListener(
                PluginLoader.Hook.ARM_SWING,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.BLOCK_DESTROYED,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.HEALTH_CHANGE,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.BLOCK_RIGHTCLICKED,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.BLOCK_PLACE,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(
                PluginLoader.Hook.PLAYER_MOVE,
                listener,
                this,
                PluginListener.Priority.MEDIUM);
    }
}