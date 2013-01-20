package com.mcacraft.vertexchat.commands;

import com.mcacraft.vertexchat.VertexChat;
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
public class Mute implements CommandExecutor
{
    private VertexChat plugin;
    
    public Mute(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
    {
        if(lbl.equalsIgnoreCase("mute"))
        {
            if(!sender.hasPermission("vertexchat.mute"))
            {
                Player p = (Player) sender;
                MSG.noPermMessage(p, "/mute");
                return true;
            }
            if(args.length == 0)
            {
                sender.sendMessage(ChatColor.RED+"Usage: /mute <player>");
                return true;
            }
            
            if(args.length == 1)
            {
                List<String> mutes = plugin.getConfig().getStringList("mutes");
                VConfig mutesConfig = new VConfig(plugin.getDataFolder().getAbsolutePath(), "mutes.yml", plugin);
                Player p = Bukkit.getPlayer(args[0]);
                if(p != null)
                {
                    if(mutes.contains(p.getName()))
                    {
                        sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+p.getName()+ChatColor.RED+" is already muted.");
                        return true;
                    }
                    mutes.add(p.getName());
                    mutesConfig.getConfig().set("mutes", mutes.toArray());
                    mutesConfig.saveConfig();
                    sender.sendMessage(ChatColor.GREEN+"Successfully muted "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+" !");
                }else
                {
                    if(mutes.contains(args[0]))
                    {
                        sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+args[0]+ChatColor.RED+" is already muted.");
                        return true;
                    }
                    mutes.add(args[0]);
                    mutesConfig.getConfig().set("mutes", mutes);
                    mutesConfig.saveConfig();
                    sender.sendMessage(ChatColor.GREEN+"Successfully muted "+ChatColor.YELLOW+args[0]+ChatColor.GREEN+" !");
                }
                return true;
            }
            
            if(args.length > 1)
            {
                sender.sendMessage(ChatColor.RED+"Error: Too many arguments.");
                sender.sendMessage(ChatColor.RED+"Usage: /mute <player>");
                return true;
            }
        }
        return false;
    }

}
