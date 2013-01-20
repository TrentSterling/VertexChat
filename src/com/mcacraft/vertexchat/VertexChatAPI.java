package com.mcacraft.vertexchat;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.mcacraft.easypromote.EasyPromoteAPI;
import com.mcacraft.vertexchat.util.VConfig;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Kenny
 */
public class VertexChatAPI 
{
    private VertexChat plugin;
    
    public static Permission permission = null;
    
    //private String[] colorCodes = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    
    public VertexChatAPI(){}
    
    public VertexChatAPI(VertexChat instance)
    {
        this.plugin = instance;
    }
    
    public String parseFormat(String message, Player p, String channel)
    {
        VConfig channelConfig = new VConfig(plugin.getDataFolder()+File.separator+"channels", channel+".yml", plugin);
        
        String output = "";
        
        if(channelConfig.getConfig().getString("format").equalsIgnoreCase("%default%"))
        {
            output = plugin.getConfig().getString("format.default");
        }else
        {
            output = channelConfig.getConfig().getString("format");
        }
        
        String color = channelConfig.getConfig().getString("color");
        String focusedChannel = plugin.getChatManager().getFocusedChannelName(p.getName());
        String cNick = plugin.getChatManager().getNick(focusedChannel);
        
        String temp = output.replaceAll("%player%", p.getName()).replaceAll("%prefix%", getPrefix(p.getName())).replaceAll("%suffix%", getSuffix(p.getName())).replaceAll("%color%", color).replaceAll("%channel%", focusedChannel).replaceAll("%faction%", this.parseFaction(p)).replaceAll("%nick%", cNick);
        
        if(p.hasPermission("vertexchat.color"))
        {
            return ChatColor.translateAlternateColorCodes('&', temp+" "+message);
        }else
        {
            return ChatColor.translateAlternateColorCodes('&', temp)+" "+message;
        }
        
    }
    
    public String logParseFormat(String message, Player p, String channel)
    {
        VConfig channelConfig = new VConfig(plugin.getDataFolder()+File.separator+"channels", channel+".yml", plugin);
        
        String output = "";
        
        //Check for default formatting
        if(channelConfig.getConfig().getString("format").equalsIgnoreCase("%default%"))
        {
            output = plugin.getConfig().getString("format.default");
        }else
        {
            output = channelConfig.getConfig().getString("format");
        }
        
        //Some variahbles so the parsing line isnt so long
        String color = channelConfig.getConfig().getString("color");
        String focusedChannel = plugin.getChatManager().getFocusedChannelName(p.getName());
        String cNick = plugin.getChatManager().getNick(focusedChannel);
        
        //There has to be a better way to do this..
        String temp = output.replaceAll("%player%", p.getName()).replaceAll("%prefix%", getGroup(p.getName())).replaceAll("%suffix%", getSuffix(p.getName())).replaceAll("%color%", color).replaceAll("%channel%", focusedChannel).replaceAll("%faction%", this.parseFaction(p)).replaceAll("%nick%", cNick);
        String replaceAll = temp+" "+message;
        
        return this.removeColorCodes(replaceAll);
        
    }
    
    public String removeColorCodes(String input)
    {
        return input.replaceAll("&[a-fk-r0-9]", "");
    }
    
    public String getGroup(String player)
    {
        return EasyPromoteAPI.getGroup(player);
    }
    
    public String getPrefix(String player)
    {
        VConfig groups = new VConfig(Bukkit.getPluginManager().getPlugin("bPermissions").getDataFolder().getAbsolutePath(), "groups.yml", Bukkit.getPluginManager().getPlugin("bPermissions"));
        if(!groups.getConfig().contains("groups"))
        {
            Bukkit.getLogger().log(Level.WARNING, "bPermisisons groups.yml located in bPermissions/groups.yml is not formatted properly or is empty.");
            return "#pError#";
        }
        return groups.getConfig().getString("groups."+getGroup(player)+".meta.prefix");
    }
    
    public String getSuffix(String player)
    {
        VConfig groups = new VConfig(Bukkit.getPluginManager().getPlugin("bPermissions").getDataFolder().getAbsolutePath(), "groups.yml", Bukkit.getPluginManager().getPlugin("bPermissions"));
        if(!groups.getConfig().contains("groups"))
        {
            Bukkit.getLogger().log(Level.WARNING, "bPermisisons groups.yml located in bPermissions/groups.yml is not formatted properly or is empty.");
            return "#sError#";
        }
        return groups.getConfig().getString("groups."+getGroup(player)+".meta.suffix");
    }
    
    private String parseFaction(Player p)
    {
        if(Bukkit.getPluginManager().getPlugin("Factions") == null)
        {
            return "";
        }else
        {
            FPlayer fplayer = FPlayers.i.get(p);
            if(fplayer.hasFaction())
            {
                return plugin.getConfig().getString("format.parse-faction").replaceAll("%ftag%", fplayer.getChatTag());
            }else
            {
                return "";
            }
        }
    }
    
    public void reloadConfiguration()
    {
        plugin.reloadConfig();
        VConfig groups = new VConfig(plugin.getDataFolder().getPath(), "groups.yml", plugin);
        groups.reloadConfig();
        for(String s : plugin.getChatManager().getAvaliableChannels())
        {
            VConfig temp = new VConfig(plugin.getDataFolder()+File.separator+"channels", s+".yml", plugin);
            temp.reloadConfig();
        }
    }
    
    public void logToFile(String message)
    {
        File logFile = new File(plugin.getDataFolder(), "log.txt");
        FileWriter fw;
        try {
            fw = new FileWriter(logFile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getLocalizedMessage());
        }
    }
}