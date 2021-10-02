package com.roleplayhub.srplocker.event;

import com.roleplayhub.srplocker.LocationManaged;
import com.roleplayhub.srplocker.SRPLocker;
import com.roleplayhub.srplocker.api.LMessages;
import com.roleplayhub.srplocker.api.LToggleable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class LockerEvents implements Listener {

    public SRPLocker core;
    private ConfigurationSection config;

    public LockerEvents(SRPLocker core){
        this.core = core;
        this.config = core.getBlockConfig();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if(core.getManager().isManaged(block)){
            LocationManaged managed = core.getManager().getManaged(block);
            player.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_PLACEMENT));

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e){
        Block block = e.getBlock();
        Player player = e.getPlayer();

        if(core.getManager().isManaged(block)){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        Action action = e.getAction();

        if(block == null) { return; }

        switch (action){
            case LEFT_CLICK_BLOCK:
                if(!core.getToggles().containsKey(player.getUniqueId())){ return; }

                LToggleable toggle = core.getToggles().get(player.getUniqueId());
                if(toggle == LToggleable.CREATE) {
                    if(core.getManager().isManaged(block)) {
                        player.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_DUPLICATE));
                        return;
                    }
                    core.getManager().create(block);
                    player.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CREATE));
                }
                else if(toggle == LToggleable.DELETE) {

                    if(!core.getManager().isManaged(block)) { return; }
                    LocationManaged managed = core.getManager().getManaged(block);

                    managed.setActive(false);
                    managed.setOwner(null);

                    core.getManager().getInactive().add(managed);
                    core.getManager().getManaged().remove(managed);
                    player.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_REMOVE));
                }
                break;
            case RIGHT_CLICK_BLOCK:
                if(!core.getManager().isManaged(block)) { return; }
                LocationManaged managed = core.getManager().getManaged(block);

                Player t = Bukkit.getPlayer(managed.getUUID());
                if(t != null){
                    if(t.getName().equals(player.getName())){
                        player.openInventory(managed.getInventory());
                        player.playSound(player.getLocation(), core.getManager().getSound(),
                                core.getManager().getSoundCategory(), core.getManager().getVolume(), core.getManager().getPitch());
                    }
                }
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        core.getUnconfirmed().remove(player.getUniqueId());
        core.getConfirmBuy().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent e){
        Inventory inv = e.getInventory();

        for(LocationManaged managed : core.getManager().getManaged()){
            if(managed.getInventory() == inv){
                managed.setInventory(inv);
            }
        }
    }
}
