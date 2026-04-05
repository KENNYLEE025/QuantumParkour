package com.quantumparkour.player;

import com.quantumparkour.QuantumParkour;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//------------------------------------------------------------------------------------------------------------------
public class VanishManager
{
    private final Set<UUID> m_vanishedPlayers = new HashSet<>();

    //---------------------------------------------------------------------------------------------
    public boolean isVanished(Player player)
    {
        return m_vanishedPlayers.contains(player.getUniqueId());
    }

    //---------------------------------------------------------------------------------------------
    public void toggleVanish(Player player)
    {
        if (isVanished(player))
        {
            disableVanish(player);
        }
        else
        {
            enableVanish(player);
        }
    }

    //---------------------------------------------------------------------------------------------
    public void enableVanish(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (m_vanishedPlayers.contains(uuid))
        {
            return;
        }

        m_vanishedPlayers.add(uuid);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
            if (onlinePlayer.equals(player))
            {
                continue;
            }

            onlinePlayer.hidePlayer(QuantumParkour.getPlugin(), player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2, false, false));
    }

    //---------------------------------------------------------------------------------------------
    public void disableVanish(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (!m_vanishedPlayers.contains(uuid))
        {
            return;
        }

        m_vanishedPlayers.remove(uuid);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
        {
            if (onlinePlayer.equals(player))
            {
                continue;
            }

            onlinePlayer.showPlayer(QuantumParkour.getPlugin(), player);
        }
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    //---------------------------------------------------------------------------------------------
    public void hideVanishedPlayersFrom(Player viewer)
    {
        for (UUID uuid : m_vanishedPlayers)
        {
            Player vanishedPlayer = Bukkit.getPlayer(uuid);
            if (vanishedPlayer == null || !vanishedPlayer.isOnline())
            {
                continue;
            }

            if (viewer.equals(vanishedPlayer))
            {
                continue;
            }

            viewer.hidePlayer(QuantumParkour.getPlugin(), vanishedPlayer);
        }
    }
}