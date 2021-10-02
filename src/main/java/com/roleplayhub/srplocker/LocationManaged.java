package com.roleplayhub.srplocker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LocationManaged {

    private UUID player;
    private Location loc;
    private Inventory inv;
    private int slot;
    private boolean active;

    private SRPLocker core;

    public LocationManaged(UUID player, Location loc, int slot){
        this.player = player;
        this.loc = loc;
        this.inv = Bukkit.createInventory(null, 27,"Open Locker");
        this.slot = slot;
        this.active = true;

        this.core = SRPLocker.getCore();
    }

    public LocationManaged(UUID player, Location loc, boolean active, int slot){
        this.player = player;
        this.loc = loc;
        this.inv = Bukkit.createInventory(null, 27,"Open Locker");
        this.slot = slot;
        this.active = active;

        this.core = SRPLocker.getCore();
    }

    public boolean save(){
        //Save loc
        FileConfiguration config = core.getBlockConfig();
        if(config == null) { return false; }

        if(player == null){
            config.set("blocks." + slot + ".owner","");
        }else{
            config.set("blocks." + slot + ".owner",player.toString());
        }
        config.set("blocks." + slot + ".inventory", inv.getContents());
        config.set("blocks." + slot + ".active", true);
        config.set("blocks." + slot + ".world", loc.getWorld().getName());
        config.set("blocks." + slot + ".x", loc.getX());
        config.set("blocks." + slot + ".y", loc.getY());
        config.set("blocks." + slot + ".z", loc.getZ());

        core.getBlockFile().reload(true);

        return true;
    }

    public Inventory getInventory() {
        return inv;
    }

    public UUID getUUID() {
        return player;
    }

    public Location getLocation() {
        return loc;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isActive() {
        return active;
    }

    public void setOwner(UUID player) {
        this.player = player;

        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = SRPLocker.getCore().getBlockConfig();
                if(config == null) { return; }

                if(player == null){
                    config.set("blocks." + slot + ".owner", "");
                }else {
                    config.set("blocks." + slot + ".owner", player.toString());
                }

                core.getBlockFile().reload(true);
            }
        }.runTaskAsynchronously(core);
    }

    public void setInventory(Inventory inv) {
        this.inv = inv;

        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = core.getBlockConfig();
                if(config == null) { return; }

                config.set("blocks." + slot + ".inventory", inv.getContents());

                core.getBlockFile().reload(true);
            }
        }.runTaskAsynchronously(SRPLocker.getCore());
    }

    public void setActive(boolean active) {
        this.active = active;

        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = core.getBlockConfig();
                if(config == null) { return; }

                config.set("blocks." + slot + ".active", active);

                core.getBlockFile().reload(true);
            }
        }.runTaskAsynchronously(core);
    }
}
