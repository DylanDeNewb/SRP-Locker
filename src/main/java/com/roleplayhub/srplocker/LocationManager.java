package com.roleplayhub.srplocker;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LocationManager {

    private SRPLocker core;
    private static int count = 0;
    private Particle particle = null;
    private int particleCount = 100;
    private Sound sound = null;
    private SoundCategory soundCategory = null;
    private float volume = 1;
    private float pitch = 1;
    private int cost = 0;
    private List<LocationManaged> managed;
    private List<LocationManaged> inactive;

    public LocationManager(SRPLocker core){
        this.core = core;
        this.managed = new ArrayList<>();
    }

    public LocationManaged create(Block block){
        if(block.getType() != Material.CARVED_PUMPKIN){ return null; }

        LocationManaged object = new LocationManaged(
                null,
                block.getLocation(),
                next()
        );

        this.managed.add(object);
        object.save();

        count+=1;

        return object;
    }

    public boolean isManaged(Block block){
        if(block.getType() == Material.AIR) { return false; }

        for(LocationManaged loc : this.getManaged()){
            if(loc.getLocation().equals(block.getLocation())){
                return true;
            }
        }
        return false;
    }

    public boolean load(){
        FileConfiguration config = core.getBlockConfig();
        if(config == null){ return false; }

        loadConfigurable(config);

        ConfigurationSection configSection = config.getConfigurationSection("blocks");
        if(configSection == null){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Section is null!");
            return false;
        }

        Set<String> section = configSection.getKeys(false);
        if(section.isEmpty()) {
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Section is empty!");
            return true;
        }

        for(String path : section){
            String player = config.getString("blocks."+path+".owner");
            UUID uuid;
            if(player.equals("")){
                uuid = null;
            }else{
                uuid = UUID.fromString(player);
            }
            ItemStack[] content = ((List<ItemStack>) config.get("blocks."+path+".inventory")).toArray(new ItemStack[0]);
            boolean active = config.getBoolean("blocks."+path+".active");
            String w = config.getString("blocks."+path+".world");
            double x = config.getDouble("blocks."+path+".x");
            double y = config.getDouble("blocks."+path+".y");
            double z = config.getDouble("blocks."+path+".z");
            int slot = Integer.parseInt(path);

            World world = Bukkit.getServer().getWorld(w);
            Location loc = new Location(world, x, y, z);

            LocationManaged object = new LocationManaged(
                    uuid,
                    loc,
                    active,
                    slot
            );

            object.getInventory().setContents(content);

            if(!active){
                this.inactive.add(object);
            }else {
                this.managed.add(object);
            }

            count+=1;
        }

        return true;
    }

    public void loadConfigurable(FileConfiguration config){

        int cost = config.getInt("general.cost");
        if(cost == 0){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Cost is null!");
        }else{
            setCost(cost);
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Cost loaded " + cost);
        }

        String sound = config.getString("sounds.type");
        if(sound == null){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound type is null!");
        }else{
            Sound sou = Sound.valueOf(sound);
            setSound(sou);
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound loaded " + sou.toString());
        }

        String soundCategory = config.getString("sounds.category");
        if(soundCategory == null){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound category is null!");
        }else{
            SoundCategory souc = SoundCategory.valueOf(soundCategory);
            setSoundCategory(souc);
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound category loaded " + souc.toString());
        }

        float volume = config.getInt("sounds.volume");
        if(volume == 0){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound volume is null!");
        }else{
            setVolume(volume);
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound volume loaded " + volume);
        }

        float pitch = config.getInt("sounds.pitch");
        if(pitch == 0){
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound pitch is null!");
        }else{
            setPitch(pitch);
            Bukkit.getConsoleSender().sendMessage(core.getPrefix() + "Sound pitch loaded " + pitch);
        }
    }

    public static int next(){
        return count + 1;
    }

    public List<LocationManaged> getManaged() {
        return managed;
    }

    public LocationManaged getManaged(Block block){

        for(LocationManaged loc : managed){
            if(loc.getLocation().equals(block.getLocation())){
                return loc;
            }
        }

        return null;
    }

    public List<LocationManaged> getInactive() {
        return inactive;
    }

    public int getClaimedOfPlayer(Player player){
        int count = 0;
        for(LocationManaged loc : managed){
            Player t = Bukkit.getPlayer(loc.getUUID());
            if(t != null){
                if(t.getName().equals(player.getName())){
                    count++;
                }
            }
        }
        return count;
    }

    public LocationManaged getLockerOfPlayer(Player player){
        for(LocationManaged loc : managed){
            if(loc.getUUID().equals(player.getUniqueId())){
                return loc;
            }
        }
        return null;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public void setParticleCount(int particleCount) {
        this.particleCount = particleCount;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public SoundCategory getSoundCategory() {
        return soundCategory;
    }

    public float getPitch() {
        return pitch;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public void setSoundCategory(SoundCategory soundCategory) {
        this.soundCategory = soundCategory;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}
