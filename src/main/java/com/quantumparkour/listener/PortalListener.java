package com.quantumparkour.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

//----------------------------------------------------------------------------------------------------------------------
public class PortalListener implements Listener
{
    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        TeleportCause cause = event.getCause();
        if (cause == TeleportCause.NETHER_PORTAL || cause == TeleportCause.END_PORTAL)
        {
            event.setCancelled(true); // Cancel the event to prevent teleportation
        }
    }
}