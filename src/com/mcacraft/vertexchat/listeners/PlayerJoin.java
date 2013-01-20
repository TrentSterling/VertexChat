package com.mcacraft.vertexchat.listeners;

import com.mcacraft.vertexchat.VertexChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Kenny
 */
public class PlayerJoin implements Listener
{
    private VertexChat plugin;
    
    public PlayerJoin(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        plugin.getChatManager().setFocusedChannel(player.getName(), plugin.getConfig().getString("default-channel"));
        plugin.getChatManager().addChannelToPlayer(player.getName(), plugin.getConfig().getString("default-channel"));
        plugin.getChatManager().setSilentChat(player.getName(), Boolean.FALSE);
    }
    
}
