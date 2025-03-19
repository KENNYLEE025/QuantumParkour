package com.quantumparkour.listener;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.quantumparkour.util.SafeConfirm;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerTickEndListener implements Listener {

    @EventHandler
    public void onServerTick(ServerTickEndEvent event) {
        SafeConfirm.tickAll();
    }
}
