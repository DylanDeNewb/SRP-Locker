package com.roleplayhub.srplocker.command;

import com.roleplayhub.srplocker.LocationManaged;
import com.roleplayhub.srplocker.SRPLocker;
import com.roleplayhub.srplocker.api.LMessages;
import com.roleplayhub.srplocker.api.LToggleable;
import com.roleplayhub.srplocker.api.NUtils;
import com.roleplayhub.srplocker.api.commands.NCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;

import java.util.ArrayList;
import java.util.List;

public class LockerCommand extends NCommand {

    public SRPLocker core;

    public LockerCommand(SRPLocker core){
        this.core = core;
    }

    @Override
    public void command(Player player, String[] args) {
        if(args.length==0){ help(player);}
        else {
            if(args[0].equalsIgnoreCase("create")) { create(player,args); }
            else if(args[0].equalsIgnoreCase("remove")) { remove(player, args); }
            else if(args[0].equalsIgnoreCase("inspect")) { inspect(player); }
            else if(args[0].equalsIgnoreCase("reload")) { reload(player); }
            else if(args[0].equalsIgnoreCase("claim")) { claim(player); }
            else if(args[0].equalsIgnoreCase("unclaim")) { unclaim(player); }
            else if(args[0].equalsIgnoreCase("open")) { open(player, args); }
            else{
                help(player);
            }
        }
    }

    private void create(Player p, String[] args){

        if(!p.hasPermission("srp.locker.admin")){
            return;
        }

        if(args.length == 1){
            p.getLineOfSight(null, 5).stream()
                    .filter(block -> block.getType() == Material.CARVED_PUMPKIN)
                    .forEach(block -> {
                        if (core.getManager().isManaged(block)) {
                            p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_DUPLICATE));
                            return;
                        }

                        if (block.getType() != Material.CARVED_PUMPKIN) {
                            p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID_TYPE));
                            return;
                        }

                        //Create regen block
                        LocationManaged managed = core.getManager().create(block);
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CREATE));
                    });
        }else{
            if(args[1].equalsIgnoreCase("toggle")){
                if(core.getToggles().containsKey(p.getUniqueId())){
                    if(core.getToggles().get(p.getUniqueId()) == LToggleable.CREATE){
                        core.getToggles().remove(p.getUniqueId());
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_OFF, LToggleable.CREATE));
                        return;
                    }
                    core.getToggles().remove(p.getUniqueId());
                    p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_OFF, LToggleable.DELETE));
                }

                core.getToggles().put(p.getUniqueId(), LToggleable.CREATE);
                p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_ON, LToggleable.CREATE));
                return;
            }
        }
    }

    private void remove(Player p, String[] args){

        if(!p.hasPermission("srp.locker.admin"));

        if(args.length == 1){
            p.getLineOfSight(null, 5).stream()
                    .filter(block -> block.getType() == Material.CARVED_PUMPKIN)
                    .forEach(block -> {
                        if (!core.getManager().isManaged(block)) {
                            p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID));
                            return;
                        }

                        //Remove regen block
                        LocationManaged managed = core.getManager().getManaged(block);
                        Location loc = managed.getLocation();

                        managed.setActive(false);
                        managed.setOwner(null);

                        core.getManager().getInactive().add(managed);
                        core.getManager().getManaged().remove(managed);
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_REMOVE));
                    });
        }else{
            if(args[1].equalsIgnoreCase("toggle")) {
                if (core.getToggles().containsKey(p.getUniqueId())) {
                    if (core.getToggles().get(p.getUniqueId()) == LToggleable.DELETE) {
                        core.getToggles().remove(p.getUniqueId());
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_OFF, LToggleable.DELETE));
                        return;
                    }
                    core.getToggles().remove(p.getUniqueId());
                    p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_OFF, LToggleable.CREATE));
                }

                core.getToggles().put(p.getUniqueId(), LToggleable.DELETE);
                p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.TOGGLE_ON, LToggleable.DELETE));
                return;
            }
        }
    }

//    private void edit(Player p, String[] args){
//        p.getLineOfSight(null, 10).stream()
//                .filter(block -> block.getType() != Material.AIR)
//                .forEach(block ->{
//                    if(!core.getManager().isManaged(block)){
//                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID));
//                        return;
//                    }
//
//                    //Edit block regen time
//                    LocationManaged managed = core.getManager().getManaged(block);
//
//                    int time = managed.getRegenTime();
//                    try{
//                        time = Integer.parseInt(args[1]);
//                    }catch(NumberFormatException e){
//                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.INSUFFICIENT_NUMBER));
//                        return;
//                    }catch(Exception e){
//
//                    }
//
//                    managed.setRegenTime(time);
//                    p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.EDIT_TIME).replace("%", String.valueOf(time)).replace("^", managed.getMaterial().toString()));
//                });
//    }

    private void inspect(Player p){

        if(!p.hasPermission("srp.locker.admin"));

        p.getLineOfSight(null ,5).stream()
                .filter(block -> block.getType() != Material.AIR)
                .forEach(block ->{
                    if(!core.getManager().isManaged(block)){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID));
                        return;
                    }

                    LocationManaged managed = core.getManager().getManaged(block);
                    Player player = Bukkit.getPlayer(managed.getUUID());

                    p.sendMessage(NUtils.translate("&7Locker " + managed.getSlot() + ":"));
                    if(player == null) {
                        p.sendMessage(NUtils.translate("  &7Owner: &6None"));
                    }else {
                        p.sendMessage(NUtils.translate("  &7Owner: &6" + player.getName()));
                    }
                });
    }

    public void claim(Player p){
        p.getLineOfSight(null ,5).stream()
                .filter(block -> block.getType() != Material.AIR)
                .forEach(block ->{
                    if(!core.getManager().isManaged(block)){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID));
                        return;
                    }

                    if(core.getManager().getClaimedOfPlayer(p) >= 1){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_LIMIT));
                        return;
                    }

                    LocationManaged managed = core.getManager().getManaged(block);
                    if(managed.getUUID() != null){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CLAIMED).replace("%", String.valueOf(managed.getSlot())));
                        return;
                    }

                    if(!core.getConfirmBuy().contains(p.getUniqueId())){
                        core.getConfirmBuy().add(p.getUniqueId());
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_UNCLAIM_CONFIRM));
                        return;
                    }

                    double rr = core.getEconomy().getBalance(p);
                    if(rr - core.getManager().getCost() < 0){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CLAIM_BALANCE)
                                .replace("%", String.valueOf(core.getManager().getCost()))
                                .replace("^", String.valueOf(rr)));
                        return;
                    }

                    EconomyResponse r = core.getEconomy().withdrawPlayer(p,core.getManager().getCost());
                    if(!r.transactionSuccess()){
                        p.sendMessage("Error with transaction, contact administration.");
                        return;
                    }

                    managed.setOwner(p.getUniqueId());
                    core.getConfirmBuy().remove(p.getUniqueId());
                    p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CLAIM).replace("%", String.valueOf(managed.getSlot())));
                });
    }

    public void unclaim(Player p){
        p.getLineOfSight(null ,5).stream()
                .filter(block -> block.getType() != Material.AIR)
                .forEach(block ->{
                    if(!core.getManager().isManaged(block)){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_INVALID));
                        return;
                    }

                    LocationManaged managed = core.getManager().getManaged(block);
                    if(managed.getUUID() == null){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_CLAIMED_NOT).replace("%", String.valueOf(managed.getSlot())));
                        return;
                    }

                    if(managed.getUUID() != p.getUniqueId() && !p.hasPermission("srp.locker.admin")){
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_UNCLAIM_NOT));
                        return;
                    }

                    if(!core.getUnconfirmed().contains(p.getUniqueId())){
                        core.getUnconfirmed().add(p.getUniqueId());
                        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_UNCLAIM_CONFIRM));
                        return;
                    }

                    managed.setOwner(null);
                    managed.getInventory().clear();
                    core.getUnconfirmed().remove(p.getUniqueId());
                    p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.BLOCK_UNCLAIM).replace("%", String.valueOf(managed.getSlot())));
                });
    }

    public void open(Player p, String[] args){
        if(args.length == 1){
            return;
        }

        if(!p.hasPermission("srp.locker.admin")){ return; }

        try{
            int i = Integer.parseInt(args[1]);

            for(LocationManaged managed : core.getManager().getManaged()){
                if(managed.getSlot() == i){
                    p.openInventory(managed.getInventory());
                    return;
                }
            }
        } catch(NumberFormatException e){
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                return;
            }

            for(LocationManaged managed : core.getManager().getManaged()){
                Player t = Bukkit.getPlayer(managed.getUUID());
                if(t != null){
                    if(t.getName().equals(target.getName())){
                        p.openInventory(managed.getInventory());
                    }
                }
            }
        }

    }

    private void help(Player p){
        p.sendMessage(core.getPrefix() + "/" + getCommand() + " <create|remove|edit|inspect|reload> <integer - create|edit>");
    }

    private void reload(Player p){
        core.getBlockFile().reload(false);
        core.getMessageFile().reload(false);

        core.getManager().loadConfigurable(core.getBlockConfig());
        LMessages.loadMessages(core);

        p.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.RELOAD));
    }

    @Override
    public void command(ConsoleCommandSender console, String[] args) {
        console.sendMessage(core.getPrefix() + LMessages.getMessage(LMessages.INSUFFICIENT_ENTITY));
    }

    @Override
    public List<String> tabcomplete(CommandSender sender, Command cmd, String alias, String[] args) {

        List<String> options = new ArrayList<>();

        if(!cmd.getName().equalsIgnoreCase(getCommand())) { return options; }

        Player p = (Player) sender;
        if(!p.hasPermission("srp.locker.admin")) { return options; }

        if(args.length == 1){
            options.add("create");
            options.add("remove");
            options.add("claim");
            options.add("unclaim");
            options.add("inspect");
            options.add("reload");

            return options;
        }

        return new ArrayList<>(0);
    }

    @Override
    public SRPLocker getCore() {
        return core;
    }

    @Override
    public String getCommand() {
        return "locker";
    }

    @Override
    public String getPermission() {
        return "srp.locker";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(0);
    }
}
