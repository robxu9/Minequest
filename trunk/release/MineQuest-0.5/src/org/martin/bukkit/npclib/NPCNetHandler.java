/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.martin.bukkit.npclib;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet130UpdateSign;
import net.minecraft.server.Packet14BlockDig;
import net.minecraft.server.Packet15Place;
import net.minecraft.server.Packet16BlockItemSwitch;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet19EntityAction;
import net.minecraft.server.Packet255KickDisconnect;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet7UseEntity;
import net.minecraft.server.Packet9Respawn;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.monk.MineQuest.MineQuest;

/**
 *
 * @author martin
 */
public class NPCNetHandler extends NetServerHandler {

    public NPCNetHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    @Override
    public CraftPlayer getPlayer() {
        return null;
    }

    @Override
    public void a() {
    }

    @Override
    public void a(Packet10Flying packet10flying) {
    }

    @Override
    public void sendMessage(String s) {
    }

    @Override
    public void a(double d0, double d1, double d2, float f, float f1) {
    }

    @Override
    public void a(Packet14BlockDig packet14blockdig) {
    }

    @Override
    public void a(Packet15Place packet15place) {
    }

    @Override
    public void a(String s, Object[] aobject) {
    }

    @Override
    public void a(Packet packet) {
    }

    @Override
    public void a(Packet16BlockItemSwitch packet16blockitemswitch) {
    }

    @Override
    public void a(Packet3Chat packet3chat) {
    }

    @Override
    public void a(Packet18ArmAnimation packet18armanimation) {
    }

    @Override
    public void a(Packet19EntityAction packet19entityaction) {
    }

    @Override
    public void a(Packet255KickDisconnect packet255kickdisconnect) {
    }

    @Override
    public void sendPacket(Packet packet) {
    }

    @Override
    public void a(Packet7UseEntity packet7useentity) {
    }

    @Override
    public void a(Packet9Respawn packet9respawn) {
    }

    @Override
    public void a(Packet101CloseWindow packet101closewindow) {
    }

    @Override
    public void a(Packet102WindowClick packet102windowclick) {
    }

    @Override
    public void a(Packet106Transaction packet106transaction) {
    }

    @Override
    public int b() {
        return super.b();
    }

    @Override
    public void a(Packet130UpdateSign packet130updatesign) {
    }
    
    @Override
    public void disconnect(String s) {
    	MineQuest.disconnect(((Player)this.player.getBukkitEntity()).getName());
    }
    
    @Override
    public void teleport(Location dest) {
    }
}
