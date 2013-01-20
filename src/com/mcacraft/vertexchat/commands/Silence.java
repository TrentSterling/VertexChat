package com.mcacraft.vertexchat.commands;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.util.MSG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Kenny
 */
public class Silence implements CommandExecutor
{
    private VertexChat plugin;
    
    public Silence(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String args[])
    {
        Player player;
        if(sender instanceof Player)
        {
            player = (Player) sender;
        }else
        {
            sender.sendMessage("[VertexChat] You must be a player to issue this command.");
            return true;
        }
        
        if(lbl.equalsIgnoreCase("silence"))
        {
            if(!player.hasPermission("vertexchat.silence"))
            {
                MSG.noPermMessage(player, "/silence");
                return true;
            }
            if(plugin.getChatManager().isChatSilenced(player.getName()))
            {
                plugin.getChatManager().setSilentChat(player.getName(), Boolean.FALSE);
                player.sendMessage(ChatColor.BLUE+"Your chat has been "+ChatColor.GREEN+"enabled"+ChatColor.BLUE+" !");
            }else
            {
                plugin.getChatManager().setSilentChat(player.getName(), Boolean.TRUE);
                player.sendMessage(ChatColor.BLUE+"Your chat has been "+ChatColor.RED+"disabled"+ChatColor.BLUE+" !");
            }
        }
        return false;
    }

}
