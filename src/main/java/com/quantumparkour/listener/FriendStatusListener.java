package com.quantumparkour.listener;

import com.quantumparkour.database.FriendDBManager;
import com.quantumparkour.util.MessageColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

//----------------------------------------------------------------------------------------------------------------------
public class FriendStatusListener implements Listener
{
    //------------------------------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player joinedPlayer = event.getPlayer();
        notifyFriends(joinedPlayer, true);
    }

    //------------------------------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player leftPlayer = event.getPlayer();
        notifyFriends(leftPlayer, false);
    }

    //------------------------------------------------------------------------------------------------------------------
    private void notifyFriends(Player targetPlayer, boolean joined)
    {
        List<UUID> friendUUIDs = FriendDBManager.getFriendUUIDs(targetPlayer.getUniqueId());

        for (UUID friendUUID : friendUUIDs)
        {
            Player friendPlayer = Bukkit.getPlayer(friendUUID);
            if (friendPlayer == null || !friendPlayer.isOnline())
            {
                continue;
            }

            if (joined)
            {
                friendPlayer.sendMessage(MessageColorUtils.translate("&a[Friends] &2Your friend, &a" + targetPlayer.getName() + ", &2has joined the server."));
            }
            else
            {
                friendPlayer.sendMessage(MessageColorUtils.translate("&a[Friends] &2Your friend, &a" + targetPlayer.getName() + ", &chas left the server."));
            }
        }
    }
}