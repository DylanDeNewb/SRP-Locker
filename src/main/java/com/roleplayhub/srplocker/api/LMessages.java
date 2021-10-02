package com.roleplayhub.srplocker.api;

import com.roleplayhub.srplocker.SRPLocker;
import org.bukkit.Bukkit;

public enum LMessages {

    PREFIX(NUtils.translate("&b&lN&3&lC &7")),
    INSUFFICIENT_PERMS(NUtils.translate("&7Insufficient permissions to access this command.")),
    INSUFFICIENT_ENTITY(NUtils.translate("&7Insufficient entity, player required.")),
    INSUFFICIENT_PLAYER(NUtils.translate("&7Insufficient player, please check the name.")),
    INSUFFICIENT_NUMBER(NUtils.translate("&Insufficient number, please use a valid number.")),
    SOFT_DEPEND_FOUND(NUtils.translate("&7Soft-Dependency &6&l% &7found.")),
    SOFT_DEPEND_NOT_FOUND(NUtils.translate("&7Soft-Dependency &6&l% &7not found, skipping.")),
    RELOAD(NUtils.translate("&7Plugin has been &6&lRELOADED")),
    BLOCK_PLACEMENT(NUtils.translate("&7You can &c&lNOT &7build here!")),
    BLOCK_BREAK(NUtils.translate("&7You are interacting with a &6&lLOCKER &7block!")),
    BLOCK_CREATE(NUtils.translate("&7Locker &a&lCREATED&7")),
    BLOCK_REMOVE(NUtils.translate("&7Locker &c&lREMOVED&7")),
    BLOCK_CLAIMED(NUtils.translate("&7Locker &6&l% &7is already &c&lCLAIMED&7")),
    BLOCK_CLAIMED_NOT(NUtils.translate("&7Locker &6&l% &7is &c&lNOT &7claimed!")),
    BLOCK_CLAIM_CONFIRM(NUtils.translate("&7Please &6&lCONFIRM &7by re-running this command. &c&lCOSTS %")),
    BLOCK_CLAIM(NUtils.translate("&7You &b&lCLAIMED&7 Locker &6&l%")),
    BLOCK_CLAIM_BALANCE(NUtils.translate("&7You need &c&l% &7yen to purchase this!")),
    BLOCK_UNCLAIM_CONFIRM(NUtils.translate("&7Please &6&lCONFIRM &7by re-running this command. &c&lCLEARS LOCKER INVENTORY")),
    BLOCK_UNCLAIM(NUtils.translate("&7You &c&lUN-CLAIMED&7 Locker &6&l%")),
    BLOCK_UNCLAIM_NOT(NUtils.translate("&7You can &c&lNOT&7 un-claim this locker")),
    BLOCK_LIMIT(NUtils.translate("&7You can only &b&lCLAIM&7 a total of: &6&l1")),
    BLOCK_DUPLICATE(NUtils.translate("&7You tried &c&lDUPLICATING &7a &6&lLOCKER &7block!")),
    BLOCK_INVALID(NUtils.translate("&7You tried interacting with a &6&lNORMAL &7block!")),
    BLOCK_INVALID_TYPE(NUtils.translate("&7You tried making a locker with a &6&lNORMAL &7block!")),
    TOGGLE_ON(NUtils.translate("&7You toggled &6&l% &7to &a&lON")),
    TOGGLE_OFF(NUtils.translate("&7You toggled &6&l% &7to &c&lOFF"));

    private String message;

    LMessages(String message){
        this.message=message;
    }

    public void setMessage(String msg){
        this.message = msg;
    }

    public static String getMessage(LMessages message){
        return message.message;
    }

    public static String getMessage(LMessages message, LToggleable toggle){
        return message.message.replace("%", toggle.toString());
    }

    public static void loadMessages(SRPLocker core){
        for(LMessages msg : LMessages.values()){
            String mesg = core.getMessageConfig().getString("messages." + msg.toString());
            if(mesg != null){
                msg.setMessage(NUtils.translate(mesg));
            }
        }
    }

    public static void list(){
        for(LMessages msg : LMessages.values()){
            Bukkit.getConsoleSender().sendMessage(msg.toString());
        }
    }

}
