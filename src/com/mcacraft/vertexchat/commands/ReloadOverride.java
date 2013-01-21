package com.mcacraft.vertexchat.commands;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.VertexChatAPI;
import com.mcacraft.vertexchat.util.MSG;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Kenny
 */
public class ReloadOverride implements Listener
{
    private VertexChat plugin;
    
    
    
    public ReloadOverride(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @EventHandler
    public void onReload(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if(args[0].equalsIgnoreCase("/reload"))
        {
            if(!player.hasPermission("bukkit.command.reload"))
            {
                MSG.noPermMessage(player, "/reload");
                event.setCancelled(true);
            }
            if(args.length != 1)
            {
                player.sendMessage(ChatColor.RED+"Usage: /reload");
                event.setCancelled(true);
            }
            plugin.updateChannels = true;
        }
    }
    
}
