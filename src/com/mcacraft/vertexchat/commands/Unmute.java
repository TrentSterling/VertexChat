package com.mcacraft.vertexchat.commands;

import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.chat.ChatManager;
import com.mcacraft.vertexchat.util.MSG;
import com.mcacraft.vertexchat.util.VConfig;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Kenny
 */
public class Unmute implements CommandExecutor
{

    private VertexChat plugin;
    
    public Unmute(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
    {
        if(lbl.equalsIgnoreCase("unmute"))
        {
            if(!sender.hasPermission("vertexchat.unmute"))
            {
                Player p = (Player) sender;
                MSG.noPermMessage(p, "/unmute");
                return true;
            }
            if(args.length == 0)
            {
                sender.sendMessage(ChatColor.RED+"Usage: /unmute <player>");
                return true;
            }
            
            if(args.length == 1)
            {
                Player p = Bukkit.getPlayer(args[0]);
                if(p != null)
                {
                    if(!ChatManager.getMuted().contains(p.getName()))
                    {
                        sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+p.getName()+ChatColor.RED+" is not muted !");
                        return true;
                    }
                    ChatManager.unmute(p.getName());
                    sender.sendMessage(ChatColor.GREEN+"Successfully unmuted "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+" !");
                    p.sendMessage(ChatColor.GREEN+"You have been unmuted! Celebrate!");
                }else
                {
                    sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+args[0]+ChatColor.RED+" must be online!");
                    return true;
                }
                return true;
            }
            
            if(args.length > 1)
            {
                sender.sendMessage(ChatColor.RED+"Error: Too many arguments.");
                sender.sendMessage(ChatColor.RED+"Usage: /unmute <player>");
                return true;
            }
        }
        return false;
    }
    
}
