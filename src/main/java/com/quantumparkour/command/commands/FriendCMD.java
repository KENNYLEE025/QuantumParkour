package com.quantumparkour.command.commands;

import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class FriendCMD implements QuantumCommand
{
    private final String[] SUB_ARGS = {"help", "add", "accept", "reject", "block"};

    //------------------------------------------------------------------------------------------------------------
    @Override
    public String getName()
    {
        return "friend";
    }

    //------------------------------------------------------------------------------------------------------------
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player))
        {
            return;
        }

        Player player = (Player)sender;

        if (args[0].equalsIgnoreCase("help"))
        {
            showFriendCommandUsage(player);
            return;
        }

        if (args.length != 2)
        {
            showFriendCommandUsage(player);
            return;
        }

        if (!isSubCommandValid(args[0]))
        {
            showInvalidSubCommand(player);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (!isTargetValid(target))
        {
            showInvalidUsername(player);
            return;
        }

        if (args[0].equalsIgnoreCase("accept"))
        {
            onFriendAccept(player, target);
        }
        else if (args[0].equalsIgnoreCase("reject"))
        {
            onFriendReject(player, target);
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            onFriendAdd(player, target);
        }
        else if (args[0].equalsIgnoreCase("block"))
        {
            onFriendBlock(player, target);
        }
        else
        {
            showFriendCommandUsage(player);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    private boolean isSubCommandValid(String subCommand)
    {
        for (String subCommandIndex : SUB_ARGS)
        {
            if (subCommand.equalsIgnoreCase(subCommandIndex))
            {
                return true;
            }
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------------
    private boolean isTargetValid(Player target)
    {
        return (target != null);
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendAccept(Player player, Player target)
    {
        player.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + target));
        target.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + target));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendReject(Player player, Player target)
    {
        player.sendMessage(MessageColorUtils.translate("&2You have rejected a friend requests from &a" + target));
        target.sendMessage(MessageColorUtils.translate("&2Your friend request to &a" + player + "&2has been rejected"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendAdd(Player player, Player target)
    {
        player.sendMessage(MessageColorUtils.translate("&2Friend request sent to &a" + target));
        target.sendMessage(MessageColorUtils.translate("&a" + player + " &2has sent you a friend request"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendBlock(Player player, Player target)
    {
        player.sendMessage(MessageColorUtils.translate("&a" + target + " &2has been blocked"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void showFriendCommandUsage(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&2&l[Friends] &2/friend usage"));
        player.sendMessage(MessageColorUtils.translate("&a/friend add <username>. "));
        player.sendMessage(MessageColorUtils.translate("&a/friend accept <username>. "));
        player.sendMessage(MessageColorUtils.translate("&a/friend reject <username>. "));
        player.sendMessage(MessageColorUtils.translate("&a/friend remove <username>. "));
        player.sendMessage(MessageColorUtils.translate("&a/friend block <username>. "));
    }

    //------------------------------------------------------------------------------------------------------------
    private void showInvalidUsername(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&cThat player is not online."));
    }

    //------------------------------------------------------------------------------------------------------------
    private void showInvalidSubCommand(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&cThat command is not valid. Type /friend help for the list of commands for friends."));
    }

    private void showIncompleteCommand(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&cThat command is in progress. Please try again later"));
    }
}