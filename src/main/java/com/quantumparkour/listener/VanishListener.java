
package com.quantumparkour.listener;

import com.quantumparkour.player.VanishManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NonNull;

//------------------------------------------------------------------------------------------------------------------
public class VanishListener implements Listener
{
    private final VanishManager m_vanishManager;

    //---------------------------------------------------------------------------------------------
    public VanishListener(VanishManager vanishManager)
    {
        m_vanishManager = vanishManager;
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerJoin(@NonNull PlayerJoinEvent event)
    {
        m_vanishManager.hideVanishedPlayersFrom(event.getPlayer());

        if (m_vanishManager.isVanished(event.getPlayer()))
        {
            event.setJoinMessage(null);
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerLeave(@NonNull PlayerQuitEvent event)
    {
        if (m_vanishManager.isVanished(event.getPlayer()))
        {
            event.setQuitMessage(null);
        }
    }
}