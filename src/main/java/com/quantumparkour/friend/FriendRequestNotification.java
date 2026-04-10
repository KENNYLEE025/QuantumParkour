package com.quantumparkour.friend;

import com.quantumparkour.database.FriendDBManager;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

//----------------------------------------------------------------------------------------------------------------------
public class FriendRequestNotification
{
    private final JavaPlugin plugin;

    //------------------------------------------------------------------------------------------------------------------
    public FriendRequestNotification(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void scheduleExpiryNotification(UUID senderUUID, UUID targetUUID)
    {
        long expiresAt = FriendDBManager.getFriendRequestExpirationMillis(senderUUID, targetUUID);
        if (expiresAt <= 0L)    return;

        long delayMillis = expiresAt - System.currentTimeMillis();
        if (delayMillis <= 0L)  return;

        // Prevent roundoff error (could maybe change to original)
        long delayTicks = Math.max(1L, (delayMillis + 49L) / 50L + 1L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
        {
            boolean stillPending = FriendDBManager.hasValidFriendRequest(senderUUID, targetUUID);
            boolean alreadyFriends = FriendDBManager.areFriends(senderUUID, targetUUID);

            if (stillPending || alreadyFriends)  return;

            Player sender = Bukkit.getPlayer(senderUUID);
            Player target = Bukkit.getPlayer(targetUUID);

            String targetName = target != null ? target.getName() : Bukkit.getOfflinePlayer(targetUUID).getName();
            String senderName = sender != null ? sender.getName() : Bukkit.getOfflinePlayer(senderUUID).getName();

            if (sender != null && sender.isOnline())
            {
                sender.sendMessage(MessageColorUtils.translate("&cYour friend request to &a" + (targetName != null ? targetName : "that player") + " &chas expired."));
            }

            if (target != null && target.isOnline())
            {
                target.sendMessage(MessageColorUtils.translate("&cThe friend request from &a" + (senderName != null ? senderName : "that player") + " &chas expired."));
            }
        }, delayTicks);
    }
}