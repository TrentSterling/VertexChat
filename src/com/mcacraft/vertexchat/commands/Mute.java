package com.mcacraft.vertexchat.commands;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
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
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Kenny
 */
public class Mute implements CommandExecutor
{
    private VertexChat plugin;
    protected Essentials ess;
    
    public Mute(VertexChat instance)
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
                Player p = Bukkit.getPlayer(args[0]);
                if(p != null)
                {
                    if(ChatManager.getMuted().contains(p.getName()))
                    {
                        sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+p.getName()+ChatColor.RED+" is already muted.");
                        return true;
                    }
                    loadEss();
                    User user = ess.getUser(p);
                    user.setMuted(true);
                    
                    ChatManager.mute(p.getName());
                    sender.sendMessage(ChatColor.GREEN+"Successfully muted "+ChatColor.YELLOW+p.getName()+ChatColor.GREEN+" !");
                    p.sendMessage(ChatColor.RED+"Uh oh, you have been muted..");
                    return true;
                }else
                {
                    sender.sendMessage(ChatColor.RED+"Error: Player "+ChatColor.YELLOW+args[0]+ChatColor.RED+" must be online!");
                    return true;
                }
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
