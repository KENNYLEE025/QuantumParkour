package com.quantumparkour.command.commands;

import com.quantumparkour.QuantumParkour;
import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.player.VanishManager;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//------------------------------------------------------------------------------------------------------------------
public class VanishCMD implements QuantumCommand
{

    //---------------------------------------------------------------------------------------------
    private final VanishManager m_vanishManager;

    //---------------------------------------------------------------------------------------------
    public VanishCMD(VanishManager vanishManager)
    {
        m_vanishManager = vanishManager;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public String getName()
    {
        return "vanish";
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public String getDescription()
    {
        return "Toggles vanish mode";
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(MessageColorUtils.translate("&4Only players can use this command."));
            return;
        }

        Player player = (Player)sender;

        if (args.length != 0)
        {
            showUsage(player);
            return;
        }

        boolean wasVanished = m_vanishManager.isVanished(player);
        m_vanishManager.toggleVanish(player);

        if (!wasVanished)
        {
            player.sendMessage(MessageColorUtils.translate("&6Vanish for " + player.getName() + " : enabled"));
            player.sendMessage(MessageColorUtils.translate("&6You are now completely invisible to normal users, and hidden from in-game commands."));
        }
        else
        {
            player.sendMessage(MessageColorUtils.translate("&6Vanish for " + player.getName() + ": disabled"));
        }
    }

    //---------------------------------------------------------------------------------------------
    private void showUsage(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&4Usage: /vanish"));
    }
}
