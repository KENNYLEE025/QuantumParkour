package com.quantumparkour.friend;

import com.quantumparkour.database.FriendDBManager;
import com.quantumparkour.util.MessageColorUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
    public void sendIncomingFriendRequestNotification(Player sender, Player target)
    {
        if (sender == null || target == null)
        {
            return;
        }

        target.sendMessage(MessageColorUtils.translate("&a" + sender.getName() + " &2has sent you a friend request."));

        HoverEvent.Action showTextAction = net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT;

        TextComponent acceptText = new TextComponent(MessageColorUtils.translate("&a&l[ACCEPT]"));
        String acceptTextOnHover = MessageColorUtils.translate("&2Click to accept &a" + sender.getName() + "'s &2friend request.");
        acceptText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + sender.getName()));
        acceptText.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(showTextAction, new ComponentBuilder(acceptTextOnHover).create()));

        TextComponent rejectText = new TextComponent(MessageColorUtils.translate(" &c&l[REJECT]"));
        String rejectTextOnHover = MessageColorUtils.translate("&2Click to reject &a" + sender.getName() + "'s &2friend request.");
        rejectText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend reject " + sender.getName()));
        rejectText.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(showTextAction, new ComponentBuilder(rejectTextOnHover).create()));

        target.spigot().sendMessage(acceptText, rejectText);
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