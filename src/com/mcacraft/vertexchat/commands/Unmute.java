package com.mcacraft.vertexchat.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.mcacraft.vertexchat.VertexChat;
import com.mcacraft.vertexchat.chat.ChatManager;
import com.mcacraft.vertexchat.util.MSG;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Kenny
 */
public class Unmute implements CommandExecutor
{

    private VertexChat plugin;
    protected Essentials ess;
    
    public Unmute(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    public void loadEss()
    {
        Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if(essPlugin != null && essPlugin instanceof Essentials){
        ess = (Essentials) essPlugin;
        }
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
                    loadEss();
                    User user = ess.getUser(p);
                    user.setMuted(false);
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
