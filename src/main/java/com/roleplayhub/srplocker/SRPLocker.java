package com.roleplayhub.srplocker;

import com.roleplayhub.srplocker.api.LMessages;
import com.roleplayhub.srplocker.api.LToggleable;
import com.roleplayhub.srplocker.api.NFile;
import com.roleplayhub.srplocker.api.commands.NCommandLoad;
import com.roleplayhub.srplocker.event.LockerEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public final class SRPLocker extends JavaPlugin {

    public boolean outdated = false;

    private static SRPLocker core;
    private Economy econ = null;
    private String prefix;

    private static NFile blockFile;
    private static NFile messageFile;

    private LocationManager manager;
    private NCommandLoad cmdmap;

    private HashMap<UUID, LToggleable> toggles;
    private List<UUID> unconfirmed;
    private List<UUID> confirmBuy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long start = System.currentTimeMillis();

        manager = new LocationManager(this);
        manager.load();

        cmdmap = new NCommandLoad(this);
        cmdmap.load();

        toggles = new HashMap<>();
        unconfirmed = new ArrayList<>();
        confirmBuy = new ArrayList<>();

        events();

        if(!economy()){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.SOFT_DEPEND_NOT_FOUND).replace("%", "Vault"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getConsoleSender().sendMessage("Plugin started in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {

        for(LocationManaged managed : core.getManager().getManaged()){
            managed.save();
        }

        getBlockFile().save();
        getMessageFile().save();
    }

    @Override
    public void onLoad() {
        core = this;

        blockFile = new NFile(this,"blocks.yml");
        messageFile = new NFile(this, "messages.yml");

        LMessages.loadMessages(this);
        prefix = LMessages.getMessage(LMessages.PREFIX);
    }

    private void events(){
        Stream.of(
                new LockerEvents(this)
        ).forEach(event -> Bukkit.getPluginManager().registerEvents(event, this));
    }

    private boolean economy(){
        if(Bukkit.getPluginManager().getPlugin("Vault") != null){
            RegisteredServiceProvider<Economy> economy = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

            if(economy != null){
                econ = economy.getProvider();
            }
            return(econ != null);
        }else{
            return false;
        }

    }

    public String getPrefix() {
        return prefix;
    }

    public static SRPLocker getCore() {
        return core;
    }

    public FileConfiguration getBlockConfig(){
        return blockFile.getAsYaml();
    }

    public NFile getBlockFile() {
        return blockFile;
    }

    public FileConfiguration getMessageConfig(){
        return messageFile.getAsYaml();
    }

    public NFile getMessageFile() {
        return messageFile;
    }

    public LocationManager getManager() {
        return manager;
    }

    public HashMap<UUID, LToggleable> getToggles() {
        return toggles;
    }

    public List<UUID> getUnconfirmed() {
        return unconfirmed;
    }

    public Economy getEconomy() {
        return econ;
    }

    public List<UUID> getConfirmBuy() {
        return confirmBuy;
    }
}
