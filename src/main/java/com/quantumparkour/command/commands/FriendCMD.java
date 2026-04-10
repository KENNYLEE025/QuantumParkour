package com.quantumparkour.command.commands;

import com.quantumparkour.command.QuantumCommand;
import com.quantumparkour.database.FriendDBManager;
import com.quantumparkour.friend.FriendRequestNotification;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

//----------------------------------------------------------------------------------------------------------------------
public class FriendCMD implements QuantumCommand
{
    private final String[] SUB_ARGS = {"help", "add", "accept", "reject", "block", "unblock", "remove", "list"};
    private final FriendRequestNotification m_friendRequestNotification;

    //------------------------------------------------------------------------------------------------------------
    public FriendCMD(FriendRequestNotification friendRequestNotification)
    {
        m_friendRequestNotification = friendRequestNotification;
    }

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

        Player player = (Player) sender;

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
        if (args[0].equalsIgnoreCase("list"))
        {
            showFriendsList(player);
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

        if (target == null)
        {
            showInvalidUsername(player);
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId()))
        {
            showSameUserFriendRequest(player);
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
        else if (args[0].equalsIgnoreCase("unblock"))
        {
            onRfriendUnblock(player, target);
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
    private void showFriendsList(Player player)
    {
        List<UUID> friends = FriendDBManager.getFriendUUIDs(player.getUniqueId());

        if (friends.isEmpty())
        {
            player.sendMessage(MessageColorUtils.translate("&cYou have no friends."));
            return;
        }

        player.sendMessage(MessageColorUtils.translate("&2&lYour Friends:"));

        for (UUID uuid : friends)
        {
            Player friend = Bukkit.getPlayer(uuid);

            if (friend != null)
            {
                player.sendMessage(MessageColorUtils.translate("&a- " + friend.getName() + " &7(Online)"));
            } else
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String offlinePlayerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : uuid.toString();
                player.sendMessage(MessageColorUtils.translate("&7- " + offlinePlayerName + " (Offline)"));
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendAccept(Player player, Player target)
    {
        boolean isFriendAcceptSuccessful = FriendDBManager.acceptFriendRequest(player.getUniqueId(), target.getUniqueId());
        if (!isFriendAcceptSuccessful)
        {
            player.sendMessage(MessageColorUtils.translate("&cNo valid friend request from &a" + target.getName()));
            return;
        }

        player.sendMessage(MessageColorUtils.translate("&2You accepted &a" + target.getName() + "'s &2friend request"));
        target.sendMessage(MessageColorUtils.translate(player.getName() + " &2has accepted your friend request"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendReject(Player player, Player target)
    {
        boolean isFriendRejectSuccessful = FriendDBManager.rejectFriendRequest(player.getUniqueId(), target.getUniqueId());
        if (!isFriendRejectSuccessful)
        {
            player.sendMessage(MessageColorUtils.translate("&cNo pending request from &a" + target.getName()));
            return;
        }

        player.sendMessage(MessageColorUtils.translate("&2You have rejected a friend request from &a" + target.getName()));
        target.sendMessage(MessageColorUtils.translate("&2Your friend request to &a" + player.getName() + " &2has been rejected"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendAdd(Player player, Player target)
    {
        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (FriendDBManager.isBlocked(playerUUID, targetUUID))
        {
            player.sendMessage(MessageColorUtils.translate("&cYou have blocked &a" + target.getName()));
            return;
        }

        if (FriendDBManager.isBlocked(targetUUID, playerUUID))
        {
            player.sendMessage(MessageColorUtils.translate("&cYou cannot send a friend request to &a" + target.getName()));
            return;
        }

        if (FriendDBManager.areFriends(playerUUID, targetUUID))
        {
            player.sendMessage(MessageColorUtils.translate("&cYou are already friends with &a" + target.getName()));
            return;
        }

        if (FriendDBManager.hasValidFriendRequest(targetUUID, playerUUID))
        {
            boolean isFriendRequestSuccessful = FriendDBManager.acceptFriendRequest(playerUUID, targetUUID);
            if (!isFriendRequestSuccessful)
            {
                player.sendMessage(MessageColorUtils.translate("&cFailed to accept friend request."));
                return;
            }

            player.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + target.getName()));
            target.sendMessage(MessageColorUtils.translate("&2You are now friends with &a" + player.getName()));
            return;
        }

        if (FriendDBManager.hasValidFriendRequest(playerUUID, targetUUID))
        {
            player.sendMessage(MessageColorUtils.translate("&cYou already sent a friend request to &a" + target.getName()));
            return;
        }

        boolean canSendFriendRequest = FriendDBManager.sendFriendRequest(playerUUID, targetUUID);
        if (!canSendFriendRequest)
        {
            player.sendMessage(MessageColorUtils.translate("&cFailed to send friend request."));
            return;
        }

        m_friendRequestNotification.scheduleExpiryNotification(playerUUID, targetUUID);
        player.sendMessage(MessageColorUtils.translate("&2Friend request sent to &a" + target.getName() + " &7(Expires in 60s)"));
        target.sendMessage(MessageColorUtils.translate("&a" + player.getName() + " &2has sent you a friend request"));
    }

    private void onRfriendUnblock(Player player, Player target)
    {
        boolean isFriendUnblockSuccessful = FriendDBManager.unblockPlayer(player.getUniqueId(), target.getUniqueId());
        if (!isFriendUnblockSuccessful)
        {
            player.sendMessage("&cFailed to unblock player: &a" + target.getName());
            return;
        }

        player.sendMessage(MessageColorUtils.translate("&2You have unblocked &a" + target.getName()));

    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendBlock(Player player, Player target)
    {
        boolean isFriendBlockedSuccessful = FriendDBManager.blockPlayer(player.getUniqueId(), target.getUniqueId());
        if (!isFriendBlockedSuccessful)
        {
            player.sendMessage("&cFailed to block player.");
            return;
        }

        player.sendMessage(MessageColorUtils.translate("&a" + target.getName() + " &2has been blocked"));
    }

    //------------------------------------------------------------------------------------------------------------
    private void onFriendRemove(Player player, Player target)
    {
        boolean isFriendRemoveSuccessful = FriendDBManager.removeFriend(player.getUniqueId(), target.getUniqueId());
        if (!isFriendRemoveSuccessful)
        {
            player.sendMessage(MessageColorUtils.translate("&cYou are not friends with &a" + target.getName()));
            return;
        }

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
        player.sendMessage(MessageColorUtils.translate("&a/friend unblock <username>. "));
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