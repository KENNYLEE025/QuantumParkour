package com.quantumparkour.command.commands;

import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.database.FriendDBManager;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class FriendCMD implements QuantumCommand
{
    private final String[] SUB_ARGS = {"help", "add", "accept", "reject", "block", "remove"};

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

        if (args.length == 0)
        {
            showFriendCommandUsage(player);
            return;
        }


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

        if (player.getName().equals(target.getName()))
        {
            showSameUserFriendRequest(player);
            return;
        }

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
        else if (args[0].equalsIgnoreCase("remove"))
        {
            onFriendRemove(player, target);
        }
        else
        {
            showIncompleteCommand(player);
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
        FriendDBManager.acceptFriendRequest(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + target.getName()));
        target.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + target.getName()));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendReject(Player player, Player target)
    {
        FriendDBManager.rejectFriendRequest(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(MessageColorUtils.translate("&2You have rejected a friend requests from &a" + target.getName()));
        target.sendMessage(MessageColorUtils.translate("&2Your friend request to &a" + player.getName() + "&2has been rejected"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendAdd(Player player, Player target)
    {
        FriendDBManager.sendFriendRequest(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(MessageColorUtils.translate("&2Friend request sent to &a" + target.getName()));
        target.sendMessage(MessageColorUtils.translate("&a" + player.getName() + " &2has sent you a friend request"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendBlock(Player player, Player target)
    {
        FriendDBManager.blockPlayer(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(MessageColorUtils.translate("&a" + target.getName() + " &2has been blocked"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendRemove(Player player, Player target)
    {
        FriendDBManager.removeFriend(player.getUniqueId(), target.getUniqueId());

        player.sendMessage(MessageColorUtils.translate("&2You have removed &a" + target.getName() + " &2from your friends list"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void showSameUserFriendRequest(Player player)
    {
        player.sendMessage((MessageColorUtils.translate("&2You cannot add yourself as a friend")));
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

    //------------------------------------------------------------------------------------------------------------
    private void showIncompleteCommand(Player player)
    {
        player.sendMessage(MessageColorUtils.translate("&cThat command is in progress. Please try again later"));
    }
}