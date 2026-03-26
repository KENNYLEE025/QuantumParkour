package com.quantumparkour.command.commands;

import com.quantumparkour.command.QuantumCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GListCMD implements QuantumCommand
{
    //------------------------------------------------------------------------------------------------------------
    @Override
    public String getName()
    {
        return "glist";
    }

    //------------------------------------------------------------------------------------------------------------
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {

    }

    private void ShowGListCommandList(Player player)
    {

    }
}
