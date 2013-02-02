package com.mcacraft.vertexchat.listeners;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.chat.ChatManager;
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
        ChatManager.setFocusedChannel(player.getName(), plugin.getConfig().getString("default-channel"));
        
        if(!ChatManager.getListeningChannelsMap().containsKey(player.getName()))
        {
            ChatManager.addChannelToPlayer(player.getName(), plugin.getConfig().getString("default-channel"));
            ChatManager.setSilentChat(player.getName(), Boolean.FALSE);
            return;
        }
        
        for(String s : ChatManager.getListeningChannelsMap().get(player.getName()))
        {
            ChatManager.getChannel(s).addPlayer(player.getName());
        }
        ChatManager.setSilentChat(player.getName(), Boolean.FALSE);
    }
    
}
