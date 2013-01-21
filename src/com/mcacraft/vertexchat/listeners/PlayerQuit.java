package com.mcacraft.vertexchat.listeners;

import com.mcacraft.vertexchat.chat.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Kenny
 */
public class PlayerQuit implements Listener
{
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        for(String s : ChatManager.getListeningChannelsMap().get(player.getName()))
        {
            ChatManager.getChannel(s).removePlayer(player.getName());
        }
    }
    
}
