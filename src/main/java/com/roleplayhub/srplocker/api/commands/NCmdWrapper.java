package com.roleplayhub.srplocker.api.commands;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class NCmdWrapper {

    private final CommandMap map;

    public NCmdWrapper() throws NoSuchFieldException, IllegalAccessException {
        Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        this.map = (CommandMap) field.get(Bukkit.getServer());
    }

    public void load(NCommand command){
        Validate.notNull(map, "commandMap null");
        map.register("newbs", new createCommand(command));
    }

    class createCommand extends Command {

        private final NCommand command;

        createCommand(NCommand command){
            super(command.getCommand(), "", "/" + command.getCommand(), command.getAliases());
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {
            return command.onCommand(sender, this, s, args);
        }

        @Override
        public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            return Objects.requireNonNull(command.onTabComplete(sender, this, alias, args));
        }
    }
}
