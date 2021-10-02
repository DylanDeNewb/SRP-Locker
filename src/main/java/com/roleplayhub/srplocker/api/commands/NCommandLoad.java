package com.roleplayhub.srplocker.api.commands;

import com.roleplayhub.srplocker.SRPLocker;
import com.roleplayhub.srplocker.command.LockerCommand;

import java.util.stream.Stream;

public class NCommandLoad {

    private final SRPLocker core;
    private NCmdWrapper map;

    public NCommandLoad(SRPLocker core){
        this.core = core;
    }

    public void load(){
        try {
            map = new NCmdWrapper();
        } catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }

        Stream.of(
                new LockerCommand(core)
        ).forEach(command -> map.load(command));

    }

}
